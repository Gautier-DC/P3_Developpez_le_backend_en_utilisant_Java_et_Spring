# Database Setup - ChatOp

## Prerequisites
- MySQL installed and running

## Setup
1. Create database: `CREATE DATABASE chatop_db;`
2. Run script: `mysql -u root -p chatop_db < script.sql`

## Reset database (if needed)
```bash
mysql -u root -p
DROP DATABASE IF EXISTS chatop_db;
CREATE DATABASE chatop_db;
exit;
mysql -u root -p chatop_db < script.sql