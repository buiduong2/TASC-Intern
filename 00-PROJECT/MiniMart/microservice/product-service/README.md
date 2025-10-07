### 🧱 Redis Cache Structure

| **Key Pattern**              | **Type**        | **Value Example / Structure**                                                                                                                                                            | **Purpose / Notes**                                                              |
| ---------------------------- | --------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------- |
| `product:detail:{productId}` | `String (JSON)` | `json { "id": 101, "name": "Áo thun basic", "imageUrl": "https://cdn.minimart.vn/p101.jpg", "description": "Cotton 100%", "salePrice": 199000, "compareAtPrice": 249000, "stock": 45 } ` | Cached product detail (excluding tag info).                                      |
| `product:tags:{productId}`   | `Set`           | `{1, 2, 5}`                                                                                                                                                                              | List of tag IDs associated with the product. Used to rebuild `ProductDetailDTO`. |
| `tag:{tagId}`                | `String (JSON)` | `json { "id": 2, "name": "Cotton" } `                                                                                                                                                    | Cached tag dictionary entry (shared by multiple products).                       |
| `product:detail:access`      | `ZSET`          | `member = productId`, `score = lastAccessTime (epoch millis)`                                                                                                                            | Tracks product recency for LRU-style eviction.                                   |
| _(optional)_ `product:ids`   | `Set`           | `{101, 102, 103}`                                                                                                                                                                        | (Optional) For debugging or monitoring cached product IDs.                       |

---

### ⚙️ Cache Workflow

| **Operation**           | **Steps**                                                                                                                                                                         |
| ----------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Cache Product**       | 1️⃣ Save product detail JSON → `product:detail:{id}` <br> 2️⃣ Save tag ID set → `product:tags:{id}` <br> 3️⃣ Update last access time → `ZADD product:detail:access {timestamp} {id}` |
| **Get Product**         | 1️⃣ `GET product:detail:{id}` <br> 2️⃣ `SMEMBERS product:tags:{id}` <br> 3️⃣ `MGET tag:{tagId...}` <br> 4️⃣ Merge → `ProductDetailDTO` <br> 5️⃣ Update `ZADD` for access time          |
| **Evict Product (LRU)** | 1️⃣ Remove product with oldest score from `product:detail:access` <br> 2️⃣ `DEL product:detail:{id}` <br> 3️⃣ `DEL product:tags:{id}` <br> 4️⃣ `ZREM product:detail:access {id}`      |
| **Tag Update**          | Refresh or invalidate only `tag:{id}` key (no need to update products).                                                                                                           |
| **Scheduled Cleanup**   | Run periodically to trim ZSET size to ≤ 50 entries.                                                                                                                               |

---

### 🧩 Naming Conventions

| **Prefix**              | **Represents**                |
| ----------------------- | ----------------------------- |
| `product:detail:`       | Cached product entity         |
| `product:tags:`         | Tag relationships per product |
| `tag:`                  | Cached tag dictionary entry   |
| `product:detail:access` | ZSET for LRU tracking         |
| `product:detail:daily:{yyyyMMdd}` | ZSET for LRU tracking         |

---

### 💡 Design Notes

-   Data model is **normalized**: product & tag stored separately.
-   Eviction handled by **time-based LRU (ZSET)**.
-   Tag updates do not invalidate product caches.
-   Cache size capped (~50 products).
-   Cache refreshed from DB when updates occur.
