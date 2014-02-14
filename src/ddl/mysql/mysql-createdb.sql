CREATE DATABASE northstar;
CREATE USER 'northstar'@'localhost' IDENTIFIED BY 'admin';
GRANT ALL ON northstar.* TO 'northstar'@'localhost';