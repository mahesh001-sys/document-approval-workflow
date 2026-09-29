Database Design

1. Overview

The Document Approval Workflow application uses MySQL as its relational database.

The database is designed to support:

- User management
- Role-based access control
- Business requests
- Request types
- Document management
- Multi-level approvals
- Approval history
- Comments
- Notifications
- Audit logging

---

2. Core Entities

The application contains the following core entities:

1. Role
2. User
3. Request Type
4. Request
5. Document
6. Approval Step
7. Approval History
8. Comment
9. Notification
10. Audit Log

---

3. Roles Table

Table: roles

Column| Type| Constraints| Description
id| BIGINT| PK, Auto Increment| Role ID
name| VARCHAR(50)| UNIQUE, NOT NULL| Role name
created_at| TIMESTAMP| NOT NULL| Creation timestamp

Initial Roles

- EMPLOYEE
- MANAGER
- ADMIN

---

4. Users Table

Table: users

Column| Type| Constraints| Description
id| BIGINT| PK, Auto Increment| User ID
first_name| VARCHAR(100)| NOT NULL| First name
last_name| VARCHAR(100)| NOT NULL| Last name
email| VARCHAR(150)| UNIQUE, NOT NULL| User email
password| VARCHAR(255)| NOT NULL| Encrypted password
role_id| BIGINT| FK, NOT NULL| User role
active| BOOLEAN| NOT NULL| Account status
created_at| TIMESTAMP| NOT NULL| Creation timestamp
updated_at| TIMESTAMP| NOT NULL| Last update timestamp

Relationship

ROLE 1 -------- N USER

One role can be assigned to many users.

---

5. Request Types Table

Table: request_types

Column| Type| Constraints| Description
id| BIGINT| PK, Auto Increment| Request type ID
name| VARCHAR(100)| UNIQUE, NOT NULL| Request type name
description| VARCHAR(500)| | Description
active| BOOLEAN| NOT NULL| Whether type is active
created_at| TIMESTAMP| NOT NULL| Creation timestamp

Initial Request Types

- TRAVEL
- EXPENSE_REIMBURSEMENT
- DOCUMENT_SUBMISSION

Relationship

REQUEST_TYPE 1 -------- N REQUEST

---

6. Requests Table

Table: requests

Column| Type| Constraints| Description
id| BIGINT| PK, Auto Increment| Request ID
request_number| VARCHAR(50)| UNIQUE, NOT NULL| Business reference
title| VARCHAR(200)| NOT NULL| Request title
description| TEXT| | Request description
status| VARCHAR(50)| NOT NULL| Current workflow status
priority| VARCHAR(30)| NOT NULL| Request priority
requester_id| BIGINT| FK, NOT NULL| Employee who created request
request_type_id| BIGINT| FK, NOT NULL| Request type
created_at| TIMESTAMP| NOT NULL| Creation timestamp
updated_at| TIMESTAMP| NOT NULL| Last update timestamp
submitted_at| TIMESTAMP| | Submission timestamp
completed_at| TIMESTAMP| | Completion timestamp

Relationships

USER 1 -------- N REQUEST

REQUEST_TYPE 1 -------- N REQUEST

A user can create multiple requests.

A request type can be used by multiple requests.

---

7. Documents Table

Table: documents

Column| Type| Constraints| Description
id| BIGINT| PK, Auto Increment| Document ID
request_id| BIGINT| FK, NOT NULL| Associated request
file_name| VARCHAR(255)| NOT NULL| Original file name
file_type| VARCHAR(100)| NOT NULL| MIME type
file_size| BIGINT| NOT NULL| File size in bytes
file_path| VARCHAR(500)| NOT NULL| Storage location
uploaded_by| BIGINT| FK, NOT NULL| User who uploaded
uploaded_at| TIMESTAMP| NOT NULL| Upload timestamp

Relationships

REQUEST 1 -------- N DOCUMENT

USER 1 -------- N DOCUMENT

A request can contain multiple documents.

---

8. Approval Steps Table

Table: approval_steps

Column| Type| Constraints| Description
id| BIGINT| PK, Auto Increment| Approval step ID
request_id| BIGINT| FK, NOT NULL| Associated request
approver_id| BIGINT| FK, NOT NULL| Assigned approver
step_number| INT| NOT NULL| Approval sequence
status| VARCHAR(50)| NOT NULL| Approval status
comments| VARCHAR(1000)| | Approver comments
actioned_at| TIMESTAMP| | Approval action timestamp

Example

Request #REQ-1001

Step 1 → Manager
Step 2 → Admin

Relationships

REQUEST 1 -------- N APPROVAL_STEP

USER 1 -------- N APPROVAL_STEP

---

9. Approval History Table

Table: approval_history

Column| Type| Constraints| Description
id| BIGINT| PK, Auto Increment| History ID
request_id| BIGINT| FK, NOT NULL| Associated request
approver_id| BIGINT| FK, NOT NULL| User performing action
action| VARCHAR(50)| NOT NULL| APPROVED / REJECTED / RETURNED
comments| VARCHAR(1000)| | Approval comments
previous_status| VARCHAR(50)| NOT NULL| Previous request status
new_status| VARCHAR(50)| NOT NULL| New request status
created_at| TIMESTAMP| NOT NULL| Action timestamp

Purpose

This table preserves the complete history of approval decisions.

Example:

SUBMITTED → MANAGER_REVIEW
MANAGER_REVIEW → MANAGER_APPROVED
MANAGER_APPROVED → ADMIN_REVIEW
ADMIN_REVIEW → APPROVED
APPROVED → COMPLETED

---

10. Comments Table

Table: comments

Column| Type| Constraints| Description
id| BIGINT| PK, Auto Increment| Comment ID
request_id| BIGINT| FK, NOT NULL| Associated request
user_id| BIGINT| FK, NOT NULL| Comment author
content| TEXT| NOT NULL| Comment content
created_at| TIMESTAMP| NOT NULL| Creation timestamp

Relationships

REQUEST 1 -------- N COMMENT

USER 1 -------- N COMMENT

---

11. Notifications Table

Table: notifications

Column| Type| Constraints| Description
id| BIGINT| PK, Auto Increment| Notification ID
user_id| BIGINT| FK, NOT NULL| Notification recipient
request_id| BIGINT| FK| Related request
title| VARCHAR(200)| NOT NULL| Notification title
message| VARCHAR(500)| NOT NULL| Notification message
read_status| BOOLEAN| NOT NULL| Whether notification is read
created_at| TIMESTAMP| NOT NULL| Creation timestamp

Examples

- New request submitted
- Approval required
- Request approved
- Request rejected
- Request returned
- Final approval completed

---

12. Audit Logs Table

Table: audit_logs

Column| Type| Constraints| Description
id| BIGINT| PK, Auto Increment| Audit ID
user_id| BIGINT| FK| User performing action
action| VARCHAR(100)| NOT NULL| Action performed
entity_type| VARCHAR(100)| NOT NULL| Affected entity
entity_id| BIGINT| NOT NULL| Affected entity ID
description| VARCHAR(1000)| | Action description
created_at| TIMESTAMP| NOT NULL| Action timestamp

Example

User: Mahesh
Action: REQUEST_SUBMITTED
Entity: REQUEST
Entity ID: 1042
Description: Travel request submitted for manager approval

---

13. Entity Relationships

ROLE
  |
  | 1:N
  ↓
USER
  |
  | 1:N
  ↓
REQUEST
  |
  ├── 1:N → DOCUMENT
  |
  ├── 1:N → APPROVAL_STEP
  |
  ├── 1:N → APPROVAL_HISTORY
  |
  └── 1:N → COMMENT

REQUEST_TYPE
  |
  | 1:N
  ↓
REQUEST

USER
  |
  ├── 1:N → NOTIFICATION
  |
  └── 1:N → AUDIT_LOG

---

14. Request Status

The request status will initially support:

DRAFT
SUBMITTED
MANAGER_REVIEW
MANAGER_APPROVED
ADMIN_REVIEW
APPROVED
RETURNED
REJECTED
COMPLETED

---

15. Approval Status

Approval steps can have:

PENDING
APPROVED
REJECTED
RETURNED

---

16. Request Priority

Requests can have:

LOW
MEDIUM
HIGH
URGENT

---

17. Important Business Rules

Rule 1

Only the employee who created a request can modify it while it is in DRAFT status.

Rule 2

An employee cannot approve their own request.

Rule 3

A manager can process only requests assigned to them.

Rule 4

An administrator can perform final approval after manager approval.

Rule 5

A rejected request cannot continue through the approval workflow.

Rule 6

A returned request can be modified by the employee and resubmitted.

Rule 7

Every approval action must be recorded in approval history.

Rule 8

Important user actions must be recorded in the audit log.

---

18. Database Design Principles

The database should follow these principles:

1. Every table should have a primary key.
2. Foreign keys should maintain relationships between entities.
3. User email and request number should be unique.
4. Important business events should have timestamps.
5. Passwords must never be stored in plain text.
6. Approval history should be separate from the current request status.
7. Important user actions should be auditable.
8. Relationships should be used instead of unnecessary data duplication.
9. The design should allow additional request types in the future.
10. The design should support additional approval levels in the future.

---

19. Future Database Enhancements

The database may later be extended with:

- SLA configuration
- Workflow templates
- Department management
- Teams
- File versioning
- Email notification history
- Soft deletion
- Request escalation
- Approval delegation
- Advanced reporting
  id="4a9fd2"
