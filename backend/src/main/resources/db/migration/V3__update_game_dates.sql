-- 경기 날짜를 현재 기준 미래로 업데이트 (데모용)
UPDATE games SET
    game_date_time = NOW() + INTERVAL '3 days',
    sale_start_at  = NOW() - INTERVAL '1 hour',
    status = 'ON_SALE'
WHERE id = 1;

UPDATE games SET
    game_date_time = NOW() + INTERVAL '4 days',
    sale_start_at  = NOW() - INTERVAL '1 hour',
    status = 'ON_SALE'
WHERE id = 2;

UPDATE games SET
    game_date_time = NOW() + INTERVAL '10 days',
    sale_start_at  = NOW() + INTERVAL '1 day',
    status = 'SCHEDULED'
WHERE id = 3;

UPDATE games SET
    game_date_time = NOW() + INTERVAL '14 days',
    sale_start_at  = NOW() + INTERVAL '3 days',
    status = 'SCHEDULED'
WHERE id = 4;

UPDATE games SET
    game_date_time = NOW() + INTERVAL '16 days',
    sale_start_at  = NOW() + INTERVAL '5 days',
    status = 'SCHEDULED'
WHERE id = 5;
