CREATE TABLE users (
                       email VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       PRIMARY KEY (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE books (
                       isbn VARCHAR(255) NOT NULL,
                       title VARCHAR(255) NOT NULL,
                       author VARCHAR(255) NOT NULL,
                       genre VARCHAR(255) NOT NULL,
                       PRIMARY KEY (isbn)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE book_items (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            isbn VARCHAR(255) NOT NULL,
                            status ENUM('AVAILABLE', 'RENTED') NOT NULL,
                            version BIGINT NOT NULL DEFAULT 0,
                            PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE rentals (
                         id BIGINT NOT NULL AUTO_INCREMENT,
                         user_email VARCHAR(255) NOT NULL,
                         book_item_id BIGINT NOT NULL,
                         rented_at DATE NOT NULL,
                         due_date DATE NOT NULL,
                         returned_at DATE,
                         status ENUM('RENTED', 'OVERDUE', 'RETURNED') NOT NULL,
                         version BIGINT NOT NULL DEFAULT 0,
                         PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE shedlock (
                          name VARCHAR(64) NOT NULL,
                          lock_until TIMESTAMP(3) NOT NULL,
                          locked_at TIMESTAMP(3) NOT NULL,
                          locked_by VARCHAR(255) NOT NULL,
                          PRIMARY KEY (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;