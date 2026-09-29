Approval Workflow

1. Overview

The Document Approval Workflow uses a controlled state-transition model.

A request cannot move directly from one arbitrary status to another.

Every status change must follow a predefined workflow rule.

This prevents invalid workflow operations and provides a reliable approval process.

---

2. Request Lifecycle

The standard request lifecycle is:

DRAFT
  |
  ↓
SUBMITTED
  |
  ↓
MANAGER_REVIEW
  |
  ↓
MANAGER_APPROVED
  |
  ↓
ADMIN_REVIEW
  |
  ↓
APPROVED
  |
  ↓
COMPLETED

A request may also be:

MANAGER_REVIEW
      |
      ├──→ REJECTED
      |
      └──→ RETURNED
                |
                ↓
              DRAFT
                |
                ↓
            SUBMITTED

---

3. Status Definitions

DRAFT

The employee is creating or editing the request.

Allowed actions:

- Edit request
- Upload documents
- Remove documents
- Save changes
- Submit request

---

SUBMITTED

The employee has submitted the request.

The request is ready to enter the approval workflow.

Allowed actions:

- System assigns manager approval
- Employee can view the request
- Employee cannot modify the submitted data

---

MANAGER_REVIEW

The assigned manager is reviewing the request.

The manager can:

- Approve
- Reject
- Return for changes
- Add comments
- View supporting documents

---

MANAGER_APPROVED

The manager has approved the request.

The system moves the request to the next approval level.

Next state:

ADMIN_REVIEW

---

ADMIN_REVIEW

The administrator performs the final review.

The administrator can:

- Approve
- Reject
- Add comments
- View request details
- View approval history

---

APPROVED

The request has received final approval.

The system records the final approval.

Next state:

COMPLETED

---

COMPLETED

The complete approval process has finished successfully.

No further workflow approval actions are allowed.

---

RETURNED

The request has been sent back to the employee for corrections.

The employee can:

- Edit the request
- Add or replace documents
- Respond to comments
- Resubmit the request

After correction:

RETURNED
   ↓
DRAFT
   ↓
SUBMITTED
   ↓
MANAGER_REVIEW

---

REJECTED

The request has been rejected.

The rejection must include:

- Rejecting user
- Reason
- Timestamp
- Previous status
- New status

A rejected request cannot continue through the normal approval workflow.

---

4. Allowed State Transitions

The application will enforce the following transitions.

Current Status| Action| Next Status| Performed By
DRAFT| Submit| SUBMITTED| Employee
SUBMITTED| Start Review| MANAGER_REVIEW| System
MANAGER_REVIEW| Approve| MANAGER_APPROVED| Manager
MANAGER_REVIEW| Reject| REJECTED| Manager
MANAGER_REVIEW| Return| RETURNED| Manager
RETURNED| Edit| DRAFT| Employee
DRAFT| Resubmit| SUBMITTED| Employee
MANAGER_APPROVED| Start Admin Review| ADMIN_REVIEW| System
ADMIN_REVIEW| Approve| APPROVED| Admin
ADMIN_REVIEW| Reject| REJECTED| Admin
APPROVED| Complete| COMPLETED| System

---

5. Invalid Transitions

The system must prevent invalid state changes.

Examples:

COMPLETED → MANAGER_REVIEW

Not allowed.

REJECTED → APPROVED

Not allowed.

APPROVED → DRAFT

Not allowed.

MANAGER_REVIEW → COMPLETED

Not allowed.

SUBMITTED → APPROVED

Not allowed.

The service layer must validate every transition before changing the request status.

---

6. Employee Workflow

Employee Login
      |
      ↓
Create Request
      |
      ↓
Save as Draft
      |
      ↓
Upload Documents
      |
      ↓
Submit
      |
      ↓
Manager Review

If returned:

Manager
   |
   ↓
Return Request
   |
   ↓
Employee
   |
   ↓
Edit Request
   |
   ↓
Resubmit

---

7. Manager Workflow

Manager Login
      |
      ↓
View Pending Requests
      |
      ↓
Select Request
      |
      ├──→ Approve
      |
      ├──→ Reject
      |
      └──→ Return

If approved:

MANAGER_APPROVED
       |
       ↓
ADMIN_REVIEW

---

8. Admin Workflow

Admin Login
     |
     ↓
View Pending Final Approvals
     |
     ↓
Review Request
     |
     ├──→ Approve
     |
     └──→ Reject

If approved:

ADMIN_REVIEW
      |
      ↓
APPROVED
      |
      ↓
COMPLETED

---

9. Approval History

Every workflow action must create an approval history record.

Example:

Request: REQ-1001

1. Employee submitted request
   SUBMITTED

2. Manager started review
   MANAGER_REVIEW

3. Manager approved
   MANAGER_APPROVED

4. Admin started review
   ADMIN_REVIEW

5. Admin approved
   APPROVED

6. Request completed
   COMPLETED

The history allows users to understand how the request moved through the workflow.

---

10. Audit Logging

Important workflow actions should also generate audit log entries.

Examples:

REQUEST_CREATED
REQUEST_UPDATED
REQUEST_SUBMITTED
REQUEST_APPROVED
REQUEST_REJECTED
REQUEST_RETURNED
REQUEST_RESUBMITTED
REQUEST_COMPLETED
DOCUMENT_UPLOADED
COMMENT_ADDED

---

11. Business Rules

Rule 1

Only the request owner can modify a request in DRAFT status.

Rule 2

Only an assigned manager can approve, reject, or return a request during MANAGER_REVIEW.

Rule 3

Only an administrator can perform final approval during ADMIN_REVIEW.

Rule 4

A user cannot approve their own request.

Rule 5

Every approval decision must include the user who performed the action.

Rule 6

Rejection should require a reason.

Rule 7

Returning a request should allow the approver to provide feedback.

Rule 8

Every workflow transition must be recorded.

Rule 9

Completed requests cannot be modified through normal workflow operations.

Rule 10

Rejected requests cannot automatically continue through the approval process.

---

12. Example End-to-End Scenario

Employee:

Creates Travel Request

System:

DRAFT

Employee:

Uploads hotel quotation
Submits request

System:

SUBMITTED
→
MANAGER_REVIEW

Manager:

Reviews request
Approves request

System:

MANAGER_APPROVED
→
ADMIN_REVIEW

Admin:

Reviews request
Approves request

System:

APPROVED
→
COMPLETED

Final result:

Request Status = COMPLETED

---

13. Returned Request Scenario

Employee:

Creates Expense Request

Manager:

Reviews receipt

Manager:

Returns request
Reason:
"Please upload a clearer receipt."

System:

RETURNED

Employee:

Uploads new receipt
Edits request
Resubmits

System:

DRAFT
→
SUBMITTED
→
MANAGER_REVIEW

The manager can then review the corrected request.

---

14. Workflow Design Principle

The workflow should be implemented using explicit state-transition rules rather than allowing controllers to directly modify request statuses.

Conceptually:

Controller
    ↓
Service
    ↓
Validate Transition
    ↓
Perform Business Action
    ↓
Update Request
    ↓
Create Approval History
    ↓
Create Audit Log
    ↓
Create Notification

This keeps the business logic centralized and maintainable.
