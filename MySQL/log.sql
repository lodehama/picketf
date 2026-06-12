# MAU 측정
SELECT COUNT(DISTINCT visitor_key) AS mau
FROM visit_log
WHERE visit_date >= '2026-06-01'
  AND visit_date < '2026-07-01';

# 접속 기기 유형  
SELECT device_type, COUNT(DISTINCT visitor_key) AS unique_visitors
FROM page_view_log
WHERE viewed_at >= '2026-06-01'
  AND viewed_at < '2026-07-01'
GROUP BY device_type
ORDER BY unique_visitors DESC;

# 페이지 뷰 조회 (같은 사람이 여러 번 보면 전부 카운트)
SELECT path, COUNT(*) AS view_count
FROM page_view_log
WHERE viewed_at >= '2026-06-01'
  AND viewed_at < '2026-07-01'
GROUP BY path
ORDER BY view_count DESC;

# 중복 제거한 방문자 수 (같은 사람이 여러 번 봐도 1명으로 카운트)
SELECT path, COUNT(DISTINCT visitor_key) AS unique_visitors
FROM page_view_log
WHERE viewed_at >= '2026-06-01'
  AND viewed_at < '2026-07-01'
GROUP BY path
ORDER BY unique_visitors DESC;