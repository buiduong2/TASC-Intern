INSERT INTO product_tags (products_id, tags_id)
SELECT p.id, tag_id
FROM generate_series(1, 1000) AS p(id)
CROSS JOIN LATERAL (
    SELECT DISTINCT (floor(random() * 2000) + 1)::bigint AS tag_id
    FROM generate_series(1, 50)
    LIMIT 5
) AS tagset;