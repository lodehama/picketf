 DROP DATABASE picketf;
CREATE DATABASE picketf DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE picketf;

CREATE TABLE user (
    us_num INT AUTO_INCREMENT PRIMARY KEY,
    us_id VARCHAR(50),
    us_pw VARCHAR(255),
    us_nickname VARCHAR(50),
    us_authority VARCHAR(10),
    us_created DATETIME DEFAULT CURRENT_TIMESTAMP
);
