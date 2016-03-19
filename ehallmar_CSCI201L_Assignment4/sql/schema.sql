CREATE DATABASE IF NOT EXISTS ehallmar_text_editor;

USE ehallmar_text_editor;

CREATE TABLE IF NOT EXISTS `ehallmar_text_editor`.`users` (
  `username` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  UNIQUE INDEX `username_UNIQUE` (`username` ASC))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;
