INSERT INTO product_tags (products_id, tags_id)
SELECT
    p.id AS products_id,
    tag_ids.tag_id
FROM generate_series(1, 1000) AS p(id)
JOIN LATERAL (
    SELECT (tag_id)
    FROM (
        SELECT DISTINCT (floor(random() * 2000) + 1)::bigint AS tag_id
        FROM generate_series(1, 50)
    ) tmp
    LIMIT 5
) AS tag_ids ON TRUE;