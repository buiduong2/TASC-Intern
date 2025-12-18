// Fake Synchornized ==> Hạn chế khả năng Race conđition

function localstorageSetNx(key: string, ttlMs: number): boolean {
  const now = Date.now();
  const raw = localStorage.getItem(key);

  // Nếu key existente nhưng đã EXPIRED → được phép overwrite
  if (raw !== null) {
    try {
      const data = JSON.parse(raw);
      if (data.exp && data.exp > now) {
        // chưa expired
        return false;
      }
      // expired → cho phép override
    } catch {
      // dữ liệu hỏng → cho phép override
    }
  }

  // Step 2 — tạo fencing token + exp
  const entry = {
    token: now + ':' + Math.random().toString(16).slice(2),
    exp: now + ttlMs,
  };

  const json = JSON.stringify(entry);

  // Step 3 — tentative write
  localStorage.setItem(key, json);

  // Step 4 — verify exact match
  const verifyRaw = localStorage.getItem(key);
  if (!verifyRaw) return false;

  if (verifyRaw === json) {
    return true; // MÌNH THẮNG
  }

  return false; // BỊ TAB KHÁC ĐÈ
}
