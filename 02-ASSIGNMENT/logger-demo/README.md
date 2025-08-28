## B1

Indexing:
LogIndexer → sinh LogMeta → đẩy vào LogMetaCache.

Query:
Người dùng submit QueryCondition → LogQueryEngine tìm trong cache → lấy tập offset.

Read content:
Tập offset chia cho nhiều LogFileReader thread → đọc message → ResultMerger.
