# Database Design

## 1. Overview

The application uses MySQL as the relational database.

The database is designed to support:

- User management
- Role-based access
- Business requests
- Request types
- Document management
- Multi-level approvals
- Approval history
- Comments
- Notifications
- Audit logging

---

# 2. Entity Relationship Overview

```text
ROLE
  |
  | 1
  |
  | N
USER
  |
  | 1
  |
  | N
REQUEST
  |
  |--------------------|
  |                    |
  | 1                  | N
  |                    |
  |                 DOCUMENT
  |
  | N
  |
REQUEST_TYPE


REQUEST
  |
  | 1
  |
  | N
APPROVAL_STEP
  |
  | 1
  |
  | N
APPROVAL_HISTORY


REQUEST
  |
  | 1
  |
  | N
COMMENT


USER
  |
  | 1
  |
  | N
NOTIFICATION


USER
  |
  | 1
  |
  | N
AUDIT_LOG
