-- KBO 10개 구단 시드 데이터
INSERT INTO teams (name, short_name, stadium, logo_url) VALUES
('KIA 타이거즈', 'KIA', '광주-기아 챔피언스 필드', '/logos/kia.png'),
('삼성 라이온즈', '삼성', '대구 삼성 라이온즈 파크', '/logos/samsung.png'),
('LG 트윈스', 'LG', '잠실 야구장', '/logos/lg.png'),
('두산 베어스', '두산', '잠실 야구장', '/logos/doosan.png'),
('KT 위즈', 'KT', '수원 KT 위즈 파크', '/logos/kt.png'),
('SSG 랜더스', 'SSG', '인천 SSG 랜더스 필드', '/logos/ssg.png'),
('롯데 자이언츠', '롯데', '사직 야구장', '/logos/lotte.png'),
('한화 이글스', '한화', '한화생명 이글스 파크', '/logos/hanwha.png'),
('NC 다이노스', 'NC', '창원 NC 파크', '/logos/nc.png'),
('키움 히어로즈', '키움', '고척 스카이돔', '/logos/kiwoom.png');

-- 샘플 경기 데이터 (2025년 5월)
INSERT INTO games (home_team_id, away_team_id, game_date_time, stadium, sale_start_at, status)
VALUES
-- LG vs 두산 (잠실 더비)
(3, 4, '2025-05-10 18:30:00', '잠실 야구장', '2025-04-27 10:00:00', 'ON_SALE'),
(3, 4, '2025-05-11 14:00:00', '잠실 야구장', '2025-04-27 10:00:00', 'ON_SALE'),
-- KIA vs 삼성
(1, 2, '2025-05-10 18:00:00', '광주-기아 챔피언스 필드', '2025-04-28 10:00:00', 'SCHEDULED'),
-- SSG vs 롯데
(6, 7, '2025-05-14 18:30:00', '인천 SSG 랜더스 필드', '2025-05-01 10:00:00', 'SCHEDULED'),
-- KT vs 키움
(5, 10, '2025-05-16 18:00:00', '수원 KT 위즈 파크', '2025-05-03 10:00:00', 'SCHEDULED');

-- 잠실 경기 (game_id=1) 구역 설정
INSERT INTO sections (game_id, name, type, price, total_seats, available_seats) VALUES
(1, '1루 내야 지정석', 'FIRST_BASE', 15000, 200, 200),
(1, '3루 내야 지정석', 'THIRD_BASE', 15000, 200, 200),
(1, '외야 응원석', 'OUTFIELD', 10000, 300, 300),
(1, '프리미엄석', 'PREMIUM', 25000, 50, 50),
(1, '패밀리석', 'FAMILY', 20000, 100, 100);

-- 잠실 경기 (game_id=2) 구역 설정
INSERT INTO sections (game_id, name, type, price, total_seats, available_seats) VALUES
(2, '1루 내야 지정석', 'FIRST_BASE', 15000, 200, 200),
(2, '3루 내야 지정석', 'THIRD_BASE', 15000, 200, 200),
(2, '외야 응원석', 'OUTFIELD', 10000, 300, 300),
(2, '프리미엄석', 'PREMIUM', 25000, 50, 50),
(2, '패밀리석', 'FAMILY', 20000, 100, 100);

-- Game 1, Section 1 좌석 생성 (1루 내야 - A~D열, 1~50번)
INSERT INTO seats (section_id, row, number)
SELECT 1, r, n
FROM (VALUES ('A'),('B'),('C'),('D')) AS rows(r)
CROSS JOIN generate_series(1, 50) AS n;

-- Game 1, Section 2 좌석 생성 (3루 내야)
INSERT INTO seats (section_id, row, number)
SELECT 2, r, n
FROM (VALUES ('A'),('B'),('C'),('D')) AS rows(r)
CROSS JOIN generate_series(1, 50) AS n;

-- Game 1, Section 3 좌석 생성 (외야)
INSERT INTO seats (section_id, row, number)
SELECT 3, r, n
FROM (VALUES ('A'),('B'),('C'),('D'),('E'),('F')) AS rows(r)
CROSS JOIN generate_series(1, 50) AS n;

-- Game 1, Section 4 좌석 생성 (프리미엄)
INSERT INTO seats (section_id, row, number)
SELECT 4, r, n
FROM (VALUES ('A'),('B')) AS rows(r)
CROSS JOIN generate_series(1, 25) AS n;

-- Game 1, Section 5 좌석 생성 (패밀리)
INSERT INTO seats (section_id, row, number)
SELECT 5, r, n
FROM (VALUES ('A'),('B'),('C'),('D')) AS rows(r)
CROSS JOIN generate_series(1, 25) AS n;

-- Game 2 좌석 생성 (section_id 6~10)
INSERT INTO seats (section_id, row, number)
SELECT s_id, r, n
FROM (VALUES (6),(7),(8),(9),(10)) AS secs(s_id)
CROSS JOIN (VALUES ('A'),('B'),('C'),('D')) AS rows(r)
CROSS JOIN generate_series(1, 50) AS n
WHERE s_id IN (6, 7, 9, 10);

INSERT INTO seats (section_id, row, number)
SELECT 8, r, n
FROM (VALUES ('A'),('B'),('C'),('D'),('E'),('F')) AS rows(r)
CROSS JOIN generate_series(1, 50) AS n;
