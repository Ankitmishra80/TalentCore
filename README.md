# TalentCore — Enterprise Human Resource Management System

TalentCore is a modern, enterprise-grade Human Resource Management System (HRMS) built with Java 21 LTS, Spring Boot 3.3.x, Spring Data JPA, Hibernate, MySQL 8, and server-side rendered Thymeleaf with Bootstrap 5.

## Technology Stack

- **Runtime & Language:** Java 21 LTS
- **Backend Framework:** Spring Boot 3.3.5
- **Persistence & ORM:** Spring Data JPA, Hibernate 6
- **Database Migrations:** Flyway
- **Database:** MySQL 8.x (H2 for in-memory integration testing)
- **Presentation Layer:** Spring MVC, Thymeleaf, Bootstrap 5.3.3, Vanilla JavaScript (Fetch API)
- **Security:** Spring Security 6.3 (Form login, Session management, RBAC, BCrypt password hashing, CSRF Protection)
- **Observability:** Spring Boot Actuator

---

## Implemented Modules

### 1. Master Data Management
- **Company Management (`/companies`, `/api/v1/companies`)**: Company onboarding and legal identity management (GSTIN, PAN, CIN), status toggling, searching, and pagination. Legacy route `GET /api/v1/all-companies` preserved.
- **Branch Management (`/branches`, `/api/v1/branches`)**: Branch lifecycle management mapped to corporate entities. Enforces business rules preventing deletion of branches with active employee assignments. Legacy routes `POST /api/v1/addBranch` and `GET /api/v1/branches` preserved.
- **Branch Employee Management (`/api/v1/employees/assign-branch`)**: Dynamic workforce assignment to branches with start and end dates, auto-deactivating previous assignments upon transfer with full audit history.
- **Branch Selection (`/api/v1/branch-selection`, Dashboard Switcher)**: User session-aware active branch context switcher in the top navigation bar with multi-branch tenant isolation.
- **Department Management (`/departments`, `/api/v1/departments`)**: Department catalog per company with unique name constraints.
- **Designation Management (`/designations`, `/api/v1/designations`)**: Designation catalog mapped to departments with duplicate name prevention.
- **Employee Management (`/employees`, `/api/v1/employees`)**: Onboarding, profile updates, unique employee code generation (`TC-EMP-XXXX`), directory filters, and interactive JavaScript cascades (Company -> Department -> Designation).

---

### 2. Time & Operational Modules
- **Attendance Management (`/attendance`, `/api/v1/attendance`)**: Employee swipe check-in/out capturing client IP, user-agent, and timestamps. Real-time shift rule evaluation: late arrival tracking, total working hours, early departure, half-day/full-day calculation, duplicate swipe rejection, and HR correction workflow.
- **Leave Management & Balances (`/leaves`, `/api/v1/leaves`)**: Casual Leave (CL), Sick Leave (SL), Earned Leave (EL), Optional Leave (OL), and Unpaid Leave (LWP). Balances tracking (`total_allocated`, `used_days`, `pending_days`, `available_days`), overlap prevention, holiday/weekend exclusion, and atomic balance deduction on approval.
- **Shift Management (`/shifts`, `/api/v1/shifts`)**: Day shifts and cross-midnight overnight shifts with break durations, grace periods, and roster assignments.
- **Holiday Management (`/holidays`, `/api/v1/holidays`)**: National, Festival, and Optional holidays scoped by company or specific branch.

---

### 3. Enterprise Administration, Dashboards & Security
- **User & Access Management (`/users`, `/api/v1/users`)**: Full user lifecycle, BCrypt password hashing, account activation/deactivation, password reset, and employee account linking.
- **Role-Based Access Control (RBAC)**: Support for `ROLE_SUPER_ADMIN`, `ROLE_COMPANY_ADMIN`, `ROLE_HR_ADMIN`, `ROLE_MANAGER`, and `ROLE_EMPLOYEE` with backend endpoint and service-layer `@PreAuthorize` security checks.
- **Role-Based Dashboards (`/dashboard`)**:
  - Live statistics calculated directly from database records (no mock numbers).
  - Metrics: Total employees, active branches, departments, designations.
  - Today's live attendance breakdown: Present, Late, Half-Day, Absent counts.
  - Pending leave approval count and recent employee joining feed.
  - Active organizational announcements.
- **In-App Notifications (`/notifications`, `/api/v1/notifications`)**:
  - Top navigation notification bell with unread counter badge.
  - Events for leave approval/rejection, attendance correction, employee onboarding.
  - Mark as read and mark all as read support.
- **Announcements Management (`/announcements`)**:
  - Company and branch scoped announcements with priority badges (`NORMAL`, `HIGH`, `URGENT`).
  - Scheduled expiry and author attribution.
- **Security & Compliance Audit Trail (`/audit-logs`)**:
  - Immutable audit logging for critical administrative events: Employee changes, Branch modifications, Attendance corrections, Leave approvals, User management, and Password resets.
  - Captures actor username, action, target module, record ID, IP address, user-agent, and timestamp.

---

### 4. Operational & Management Reports (`/reports`)
- **Attendance Reports (`/reports/attendance`)**: Date range, branch, and department filters with summary metrics and one-click **CSV export** (`/reports/attendance/export`).
- **Leave Reports (`/reports/leaves`)**: Application status audits and real-time leave balance breakdown per employee.

---

## Role-Based Access Matrix

| Module / Operation | SUPER_ADMIN / ADMIN | HR_ADMIN | MANAGER | EMPLOYEE |
|---|:---:|:---:|:---:|:---:|
| System Dashboard | Organization-wide | Company/Branch | Team Scope | Self Profile & Stats |
| User & Role Management | Yes | Limited | No | No |
| Password Reset & Status Toggle | Yes | No | No | Self Password Change |
| Master Data (Company, Branch, Dept) | Full CRUD | View/Limited | View | View |
| Employee Directory | Full CRUD | Onboarding & Updates | View Team | View Self |
| Attendance Check-In / Out | Yes | Yes | Yes | Yes (Self) |
| Attendance Correction Approvals | Yes | Yes | Team Only | Submit Request Only |
| Leave Applications | Yes | Yes | Yes | Submit / Cancel Own |
| Leave Approval Console | Yes | Yes | Direct Reports | No |
| Shifts & Holiday Management | Full CRUD | Full CRUD | View | View |
| Announcements Publishing | Yes | Yes | Departmental | View Active |
| Audit Trail Inspection | Yes | No | No | No |
| Management Reports & CSV Export | Full Export | Company Export | Team Export | No |

---

## Getting Started

### Prerequisites
- JDK 21 LTS
- MySQL 8.0+

### Database Setup
1. Create a database `talentcore_dev` in your local MySQL instance:
   ```sql
   CREATE DATABASE talentcore_dev CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. Adjust credentials in `src/main/resources/application-dev.yml` or set environment variables:
   ```bash
   export SPRING_DATASOURCE_USERNAME=root
   export SPRING_DATASOURCE_PASSWORD=your_password
   ```

### Running the Application
Using the Maven wrapper:
```cmd
mvnw.cmd spring-boot:run
```

Access the application in your browser:
- Application URL: `http://localhost:8085`
- Login Page: `http://localhost:8085/login`
- Users & Access Management: `http://localhost:8085/users`
- Announcements: `http://localhost:8085/announcements`
- Audit Logs: `http://localhost:8085/audit-logs`
- Notifications: `http://localhost:8085/notifications`
- Health Endpoint: `http://localhost:8085/api/v1/health`

Default Seed Credentials:
- **Admin**: `admin` / `Admin@123`
- **HR Admin**: `hr_admin` / `Hr@123`

### Executing Tests
```cmd
mvnw.cmd test
```
The test suite runs against an isolated in-memory H2 database under the `test` profile covering:
- Spring Boot Context Bootstrapping (`TalentCoreApplicationTests`)
- Master Data and Employee Integration Lifecycle (`MasterDataServiceIntegrationTests`)
- Attendance, Leaves, Shifts, and Holidays (`OperationalModulesIntegrationTests`)
- User Management, Notifications, Announcements, Audit Logs, and Dashboards (`EnterpriseAdminIntegrationTests`)

---

## Future Roadmap (Planned Phase 2 — Post-MVP)
- **Payroll Management & Salary Structures**: Earnings, deductions, CTC breakup, PF, ESI, TDS.
- **Payslip Generation**: Automated monthly payroll processing and PDF generation.
- **Recruitment & Applicant Tracking System (ATS)**: Job openings, candidate pipeline, interviews.
- **Performance Management**: OKRs, KPIs, 360-degree feedback, annual appraisals.
- **Asset & Expense Management**: Corporate asset allocation and employee expense reimbursements.
