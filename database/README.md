# Database Setup - ChatOp

## Prerequisites
- MySQL installed and running

## Setup
```sql
cd database
mysql -u root -p
CREATE USER 'username'@'localhost' IDENTIFIED BY 'password';
GRANT ALL PRIVILEGES ON *.* TO 'chatop_user'@'localhost';
FLUSH PRIVILEGES;
CREATE DATABASE chatop_db;
USE chatop_db;
```

## Reset database (if needed)
```bash
mysql -u root -p
DROP DATABASE IF EXISTS chatop_db;
CREATE DATABASE chatop_db;
exit;
mysql -u root -p chatop_db