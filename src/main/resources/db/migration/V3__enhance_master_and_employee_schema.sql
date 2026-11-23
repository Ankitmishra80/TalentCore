-- V3: Enhancements for Master Data and Complete Employee Lifecycle

-- 1. Enhance branches table with contact and email
ALTER TABLE branches
    ADD COLUMN contact_number VARCHAR(30) AFTER address,
    ADD COLUMN email VARCHAR(100) AFTER contact_number;

-- 2. Enhance departments table with department_code and description
ALTER TABLE departments
    ADD COLUMN department_code VARCHAR(50) AFTER company_id,
    ADD COLUMN description TEXT AFTER short_name;

-- 3. Enhance designations table with company_id and description
ALTER TABLE designations
    ADD COLUMN company_id BIGINT AFTER id,
    ADD COLUMN description TEXT AFTER short_name;

ALTER TABLE designations
    ADD CONSTRAINT fk_designations_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT;

-- Populate company_id in designations from parent departments
UPDATE designations d
JOIN departments dept ON d.department_id = dept.id
SET d.company_id = dept.company_id;

-- 4. Enhance employees table with company_id, employment_type, reporting_manager_id, official_email, profile_photo
ALTER TABLE employees
    ADD COLUMN company_id BIGINT AFTER user_id,
    ADD COLUMN official_email VARCHAR(150) AFTER aadhar_number,
    ADD COLUMN employment_type VARCHAR(50) NOT NULL DEFAULT 'FULL_TIME' AFTER official_email,
    ADD COLUMN reporting_manager_id BIGINT AFTER employment_type,
    ADD COLUMN profile_photo VARCHAR(255) AFTER reporting_manager_id,
    ADD COLUMN address TEXT AFTER profile_photo,
    ADD COLUMN exit_date DATE AFTER joining_date;

ALTER TABLE employees
    ADD CONSTRAINT fk_employees_company FOREIGN KEY (company_id) REFERENCES companies(id) ON DELETE RESTRICT,
    ADD CONSTRAINT fk_employees_manager FOREIGN KEY (reporting_manager_id) REFERENCES employees(id) ON DELETE SET NULL;
