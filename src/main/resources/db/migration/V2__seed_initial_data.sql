-- Seed Initial Data for TalentCore

INSERT INTO companies (id, code, name, short_name, contact_person_name, contact_person_email, is_active)
VALUES (1, 'TALENT-001', 'TalentCore Enterprise Solutions', 'TalentCore', 'Operations Lead', 'admin@talentcore.local', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO branches (id, company_id, branch_code, branch_name, city, state, address, is_active)
VALUES 
(1, 1, 'HQ-DEL', 'Headquarters New Delhi', 'New Delhi', 'Delhi', 'Connaught Place, Central Delhi', 1),
(2, 1, 'BR-BLR', 'Bengaluru Tech Center', 'Bengaluru', 'Karnataka', 'Electronic City, Phase 1', 1),
(3, 1, 'BR-MUM', 'Mumbai Financial Branch', 'Mumbai', 'Maharashtra', 'Bandra Kurla Complex', 1)
ON DUPLICATE KEY UPDATE branch_name=VALUES(branch_name);

INSERT INTO departments (id, company_id, department_name, short_name, is_active)
VALUES 
(1, 1, 'Information Technology', 'IT', 1),
(2, 1, 'Human Resources', 'HR', 1),
(3, 1, 'Finance & Accounting', 'FIN', 1)
ON DUPLICATE KEY UPDATE department_name=VALUES(department_name);

INSERT INTO designations (id, department_id, name, short_name, is_active)
VALUES 
(1, 1, 'Senior Software Engineer', 'SSE', 1),
(2, 1, 'Engineering Lead', 'Lead', 1),
(3, 2, 'HR Manager', 'HRM', 1),
(4, 3, 'Senior Accountant', 'SA', 1)
ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO roles (id, name, description)
VALUES 
(1, 'ROLE_ADMIN', 'System Administrator with full access'),
(2, 'ROLE_HR_ADMIN', 'Human Resource Executive and Branch Manager'),
(3, 'ROLE_EMPLOYEE', 'Standard Employee Portal User')
ON DUPLICATE KEY UPDATE description=VALUES(description);

-- Admin password is: Admin@123 (BCrypt hash)
INSERT INTO users (id, company_id, username, email, password_hash, full_name, is_active)
VALUES 
(1, 1, 'admin', 'admin@talentcore.local', '$2a$10$wK1V.E03jLwYxZk3p5K.8e8O/qRz1ZJm8Yd7X.U6QZ6Bv9kX9rV8m', 'TalentCore Administrator', 1),
(2, 1, 'hr_admin', 'hr@talentcore.local', '$2a$10$wK1V.E03jLwYxZk3p5K.8e8O/qRz1ZJm8Yd7X.U6QZ6Bv9kX9rV8m', 'HR Operations Manager', 1)
ON DUPLICATE KEY UPDATE full_name=VALUES(full_name);

INSERT INTO user_roles (user_id, role_id)
VALUES 
(1, 1),
(1, 2),
(2, 2)
ON DUPLICATE KEY UPDATE role_id=VALUES(role_id);
