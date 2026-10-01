-- Run manually against the local book_rental database. Never run on startup.
-- Existing books, copies and rental statuses are preserved on repeated runs.
-- Uses the current Hibernate TABLE generator (allocation size 50).
SET NAMES utf8mb4;
START TRANSACTION;

SELECT next_val INTO @demo_sequence FROM book_items_seq FOR UPDATE;
SELECT GREATEST(COALESCE(MAX(id), 0), @demo_sequence) + 1
INTO @demo_first_id FROM book_items;

INSERT INTO books (isbn, title, author, genre)
SELECT seed.isbn, seed.title, seed.author, seed.genre
FROM (
    SELECT 'TEST-001' AS isbn, '자바 기초 연습' AS title, '테스트 저자 A' AS author, '프로그래밍' AS genre
    UNION ALL SELECT 'TEST-002', '자바 객체지향 연습', '테스트 저자 A', '프로그래밍'
    UNION ALL SELECT 'TEST-003', '재고 없는 테스트 도서', '테스트 저자 B', '소설'
    UNION ALL SELECT 'TEST-004', '자바 컬렉션 연습', '테스트 저자 B', '프로그래밍'
    UNION ALL SELECT 'TEST-005', '자바 예외 처리 연습', '테스트 저자 C', '프로그래밍'
    UNION ALL SELECT 'TEST-006', '자바 스트림 연습', '테스트 저자 C', '프로그래밍'
    UNION ALL SELECT 'TEST-007', '자바 동시성 연습', '테스트 저자 D', '프로그래밍'
    UNION ALL SELECT 'TEST-008', '도서관의 하루', '테스트 저자 D', '소설'
) seed
WHERE NOT EXISTS (SELECT 1 FROM books b WHERE b.isbn = seed.isbn);

INSERT INTO book_items (id, isbn, status)
SELECT @demo_first_id + seed.offset_id, seed.isbn, 'AVAILABLE'
FROM (
    SELECT 0 AS offset_id, 'TEST-001' AS isbn
    UNION ALL SELECT 1, 'TEST-001'
    UNION ALL SELECT 2, 'TEST-002'
    UNION ALL SELECT 3, 'TEST-004'
    UNION ALL SELECT 4, 'TEST-005'
    UNION ALL SELECT 5, 'TEST-006'
    UNION ALL SELECT 6, 'TEST-007'
    UNION ALL SELECT 7, 'TEST-008'
) seed
WHERE NOT EXISTS (SELECT 1 FROM book_items bi WHERE bi.isbn = seed.isbn);

-- Reserve beyond the inserted IDs and Hibernate's next 50-ID pool.
UPDATE book_items_seq SET next_val = @demo_first_id + 100;
COMMIT;

SELECT b.isbn, b.title, COUNT(bi.id) AS total_count,
       COALESCE(SUM(bi.status = 'AVAILABLE'), 0) AS available_count
FROM books b
LEFT JOIN book_items bi ON bi.isbn = b.isbn
WHERE b.isbn LIKE 'TEST-%'
GROUP BY b.isbn, b.title
ORDER BY b.isbn;
