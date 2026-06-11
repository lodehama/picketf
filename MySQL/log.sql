# MAU 측정
SELECT COUNT(DISTINCT visitor_key) AS mau
FROM visit_log
WHERE visit_date >= '2026-06-01'
  AND visit_date < '2026-07-01';

# 페이지 뷰 조회
SELECT path, COUNT(*) AS view_count
FROM page_view_log
WHERE viewed_at >= '2026-06-01'
  AND viewed_at < '2026-07-01'
GROUP BY path
ORDER BY view_count DESC;