CREATE TABLE user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

select * from user;

CREATE TABLE Recipient (
    email VARCHAR(255) PRIMARY KEY,
    firstName VARCHAR(255) NOT NULL,
    contact VARCHAR(255)
);