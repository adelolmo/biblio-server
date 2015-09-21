CREATE TABLE book (
  id     INT PRIMARY KEY                     NOT NULL AUTO_INCREMENT,
  title  VARCHAR(250)                        NOT NULL,
  author VARCHAR(250)                        NOT NULL,
  ctime  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  isbn   VARCHAR(20)                         NOT NULL,
  tags   VARCHAR(250)
);

CREATE TABLE lend (
  id     INT PRIMARY KEY                     NOT NULL AUTO_INCREMENT,
  bookId INTEGER                             NOT NULL,
  person TEXT                                NOT NULL,
  ctime  TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
  rtime  TIMESTAMP                                    DEFAULT '0000-00-00',
  FOREIGN KEY fk_book (bookId) REFERENCES book (id)
);