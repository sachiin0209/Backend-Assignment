-- Sample Data for Finance Dashboard API
-- This script creates test users and financial records for demonstration
-- Password hashes are for: password123

-- ============================================================
-- CREATE TEST USERS
-- ============================================================

-- Admin User (password: password123)
INSERT INTO users (name, email, password, role, status, created_at, updated_at) VALUES 
('Admin User', 'admin@example.com', '$2b$10$XwlkeRPnTITB2auOA43blOkNAF6dpcOojmxPFQ2lCSMfac/VuJ.46', 'ADMIN', 'ACTIVE', NOW(), NOW());

-- Analyst User (password: password123)
INSERT INTO users (name, email, password, role, status, created_at, updated_at) VALUES 
('John Analyst', 'john.analyst@example.com', '$2b$10$XwlkeRPnTITB2auOA43blOkNAF6dpcOojmxPFQ2lCSMfac/VuJ.46', 'ANALYST', 'ACTIVE', NOW(), NOW());

-- Viewer User (password: password123)
INSERT INTO users (name, email, password, role, status, created_at, updated_at) VALUES 
('Jane Viewer', 'jane.viewer@example.com', '$2b$10$XwlkeRPnTITB2auOA43blOkNAF6dpcOojmxPFQ2lCSMfac/VuJ.46', 'VIEWER', 'ACTIVE', NOW(), NOW());

-- Another Analyst User (password: password123)
INSERT INTO users (name, email, password, role, status, created_at, updated_at) VALUES 
('Bob Smith', 'bob.smith@example.com', '$2b$10$XwlkeRPnTITB2auOA43blOkNAF6dpcOojmxPFQ2lCSMfac/VuJ.46', 'ANALYST', 'ACTIVE', NOW(), NOW());

-- Inactive User (password: password123)
INSERT INTO users (name, email, password, role, status, created_at, updated_at) VALUES 
('Inactive User', 'inactive@example.com', '$2b$10$XwlkeRPnTITB2auOA43blOkNAF6dpcOojmxPFQ2lCSMfac/VuJ.46', 'VIEWER', 'INACTIVE', NOW(), NOW());

-- ============================================================
-- CREATE SAMPLE FINANCIAL RECORDS FOR USER 2 (John Analyst)
-- ============================================================

-- Income Records
INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(5000.00, 'INCOME', 'Salary', '2024-01-15', 'January Monthly Salary', 2, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(5000.00, 'INCOME', 'Salary', '2024-02-15', 'February Monthly Salary', 2, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(5000.00, 'INCOME', 'Salary', '2024-03-15', 'March Monthly Salary', 2, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(1000.00, 'INCOME', 'Freelance', '2024-01-20', 'Freelance project payment', 2, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(500.00, 'INCOME', 'Bonus', '2024-02-28', 'Quarterly Bonus', 2, FALSE, NOW(), NOW());

-- Expense Records
INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(1500.00, 'EXPENSE', 'Rent', '2024-01-01', 'January Rent Payment', 2, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(1500.00, 'EXPENSE', 'Rent', '2024-02-01', 'February Rent Payment', 2, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(1500.00, 'EXPENSE', 'Rent', '2024-03-01', 'March Rent Payment', 2, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(150.00, 'EXPENSE', 'Utilities', '2024-01-05', 'Electric Bill', 2, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(50.00, 'EXPENSE', 'Utilities', '2024-01-10', 'Internet Bill', 2, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(300.00, 'EXPENSE', 'Groceries', '2024-01-12', 'Weekly grocery shopping', 2, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(250.00, 'EXPENSE', 'Groceries', '2024-01-25', 'Weekly grocery shopping', 2, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(200.00, 'EXPENSE', 'Entertainment', '2024-01-16', 'Movie tickets and dinner', 2, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(100.00, 'EXPENSE', 'Transportation', '2024-01-08', 'Gas refill', 2, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(80.00, 'EXPENSE', 'Entertainment', '2024-02-14', 'Concert tickets', 2, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(120.00, 'EXPENSE', 'Healthcare', '2024-02-20', 'Doctor visit', 2, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(50.00, 'EXPENSE', 'Office Supplies', '2024-03-05', 'Notebooks and pens', 2, FALSE, NOW(), NOW());

-- ============================================================
-- CREATE SAMPLE FINANCIAL RECORDS FOR USER 4 (Bob Smith)
-- ============================================================

-- Income Records
INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(6000.00, 'INCOME', 'Salary', '2024-01-15', 'January Monthly Salary', 4, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(6000.00, 'INCOME', 'Salary', '2024-02-15', 'February Monthly Salary', 4, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(6000.00, 'INCOME', 'Salary', '2024-03-15', 'March Monthly Salary', 4, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(2000.00, 'INCOME', 'Investment Returns', '2024-01-31', 'Stock dividend payment', 4, FALSE, NOW(), NOW());

-- Expense Records
INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(2000.00, 'EXPENSE', 'Rent', '2024-01-01', 'January Rent Payment', 4, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(2000.00, 'EXPENSE', 'Rent', '2024-02-01', 'February Rent Payment', 4, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(2000.00, 'EXPENSE', 'Rent', '2024-03-01', 'March Rent Payment', 4, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(400.00, 'EXPENSE', 'Groceries', '2024-01-10', 'Grocery shopping', 4, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(500.00, 'EXPENSE', 'Entertainment', '2024-01-22', 'Weekend getaway', 4, FALSE, NOW(), NOW());

INSERT INTO financial_records (amount, type, category, transaction_date, description, created_by, is_deleted, created_at, updated_at) VALUES 
(150.00, 'EXPENSE', 'Utilities', '2024-02-05', 'Electric and Internet', 4, FALSE, NOW(), NOW());

-- ============================================================
-- Sample Data Summary
-- ============================================================
-- Users Created:
--   1. admin@example.com (ADMIN)
--   2. john.analyst@example.com (ANALYST) - 15 transactions
--   3. jane.viewer@example.com (VIEWER) - 0 transactions
--   4. bob.smith@example.com (ANALYST) - 11 transactions
--   5. inactive@example.com (VIEWER - INACTIVE)
--
-- Financial Records:
--   - Total records created: 26
--   - John Analyst: 15 records
--   - Bob Smith: 11 records
--
-- Test Login Credentials:
--   Email: john.analyst@example.com
--   Password: password123
--
-- All password hashes use BCrypt with strength 10
