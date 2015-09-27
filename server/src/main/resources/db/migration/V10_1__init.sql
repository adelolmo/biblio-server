CREATE TABLE user (
  id       INT PRIMARY KEY                            NOT NULL AUTO_INCREMENT,
  username VARCHAR(255) UNIQUE                        NOT NULL,
  password VARCHAR(255)                               NOT NULL,
  salt     VARCHAR(255)                               NOT NULL,
  role     VARCHAR(15)                                NOT NULL,
  ctime    TIMESTAMP DEFAULT CURRENT_TIMESTAMP        NOT NULL
);

CREATE TABLE book (
  id       INT PRIMARY KEY                     NOT NULL AUTO_INCREMENT,
  userId   INTEGER                             NOT NULL,
  title    VARCHAR(255)                        NOT NULL,
  author   VARCHAR(255)                        NOT NULL,
  ctime    TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  isbn     VARCHAR(30) UNIQUE,
  tags     VARCHAR(255),
  imageUrl VARCHAR(400),
  FOREIGN KEY fk_user (userId) REFERENCES user (id)
);

CREATE TABLE lend (
  id     INT PRIMARY KEY                     NOT NULL AUTO_INCREMENT,
  bookId INTEGER                             NOT NULL,
  person TEXT                                NOT NULL,
  ctime  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  rtime  TIMESTAMP                                    DEFAULT '0000-00-00',
  FOREIGN KEY fk_book (bookId) REFERENCES book (id)
);