create database bibliographies;
use bibliographies;
create table entries (
	id int NOT NULL AUTO_INCREMENT, 
	author text, 
	title text, 
	year int, 
	journal text,
	PRIMARY KEY (id));