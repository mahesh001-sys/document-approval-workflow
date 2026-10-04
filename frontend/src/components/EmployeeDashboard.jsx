import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../api/authService";
import requestService from "../api/requestService";

function EmployeeDashboard() {
  const navigate = useNavigate();

  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const user = authService.getUser();

  const loadRequests = async () => {
    try {
      setLoading(true);
      setError("");

      const data =
        await requestService.getAllRequests();

      setRequests(
        Array.isArray(data) ? data : []
      );
    } catch (err) {
      console.error(
        "Failed to load requests:",
        err
      );

      if (err.response?.status === 401) {
        authService.logout();
        navigate("/login");
        return;
      }

      setError(
        "Unable to load requests. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRequests();
  }, []);

  const handleLogout = () => {
    authService.logout();
    navigate("/login");
  };

  const getStatusClass = (status) => {
    if (!status) {
      return "status";
    }

    return `status ${status
      .toLowerCase()
      .replaceAll("_", "-")}`;
  };

  const getStatusLabel = (status) => {
    if (!status) {
      return "UNKNOWN";
    }

    return status
      .replaceAll("_", " ");
  };

  const draftCount = requests.filter(
    (request) =>
      request.status === "DRAFT"
  ).length;

  const submittedCount = requests.filter(
    (request) =>
      request.status === "SUBMITTED"
  ).length;

  const managerReviewCount = requests.filter(
    (request) =>
      request.status ===
      "MANAGER_REVIEW"
  ).length;

  const approvedCount = requests.filter(
    (request) =>
      request.status === "APPROVED" ||
      request.status === "COMPLETED"
  ).length;

  return (
    <div className="dashboard-page">
      <header className="dashboard-header">
        <div>
          <span className="section-label">
            EMPLOYEE WORKSPACE
          </span>

          <h1>My Requests</h1>

          <p>
            {user?.email
              ? `Signed in as ${user.email}`
              : "Manage your business requests"}
          </p>
        </div>

        <div className="dashboard-actions">
          <button
            type="button"
            className="button primary"
            onClick={() =>
              navigate("/requests/new")
            }
          >
            + New Request
          </button>

          <button
            type="button"
            className="button secondary"
            onClick={handleLogout}
          >
            Logout
          </button>
        </div>
      </header>

      <main className="dashboard-content">

        <section className="dashboard-stats">

          <div className="stat-card">
            <span>Total Requests</span>
            <strong>
              {requests.length}
            </strong>
          </div>

          <div className="stat-card">
            <span>Drafts</span>
            <strong>
              {draftCount}
            </strong>
          </div>

          <div className="stat-card">
            <span>Submitted</span>
            <strong>
              {submittedCount}
            </strong>
          </div>

          <div className="stat-card">
            <span>Manager Review</span>
            <strong>
              {managerReviewCount}
            </strong>
          </div>

          <div className="stat-card">
            <span>Approved</span>
            <strong>
              {approvedCount}
            </strong>
          </div>

        </section>

        <section className="requests-section">

          <div className="section-heading">
            <span className="section-label">
              REQUEST MANAGEMENT
            </span>

            <h2>Your Requests</h2>

            <p>
              Create, edit, submit and track your
              business requests through the approval
              workflow.
            </p>
          </div>

          {loading && (
            <div className="empty-state">
              <p>
                Loading requests...
              </p>
            </div>
          )}

          {!loading && error && (
            <div className="error-state">
              <p>{error}</p>

              <button
                type="button"
                className="button secondary"
                onClick={loadRequests}
              >
                Try Again
              </button>
            </div>
          )}

          {!loading &&
            !error &&
            requests.length === 0 && (
              <div className="empty-state">

                <div className="empty-icon">
                  📋
                </div>

                <h3>
                  No requests yet
                </h3>

                <p>
                  Create your first business
                  request to start the approval
                  workflow.
                </p>

                <button
                  type="button"
                  className="button primary"
                  onClick={() =>
                    navigate("/requests/new")
                  }
                >
                  Create Request
                </button>

              </div>
            )}

          {!loading &&
            !error &&
            requests.length > 0 && (
              <div className="request-table-wrapper">

                <table className="request-table">

                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Request Number</th>
                      <th>Title</th>
                      <th>Status</th>
                      <th>Action</th>
                    </tr>
                  </thead>

                  <tbody>

                    {requests.map(
                      (request) => (
                        <tr
                          key={request.id}
                        >

                          <td>
                            #{request.id}
                          </td>

                          <td>
                            {request.requestNumber ||
                              "-"}
                          </td>

                          <td>
                            {request.title ||
                              "Untitled Request"}
                          </td>

                          <td>
                            <span
                              className={getStatusClass(
                                request.status
                              )}
                            >
                              {getStatusLabel(
                                request.status
                              )}
                            </span>
                          </td>

                          <td>

                            <button
                              type="button"
                              className="table-button"
                              onClick={() =>
                                navigate(
                                  `/requests/${request.id}`
                                )
                              }
                            >
                              View
                            </button>

                            {request.status ===
                              "DRAFT" && (
                              <button
                                type="button"
                                className="table-button"
                                style={{
                                  marginLeft:
                                    "8px"
                                }}
                                onClick={() =>
                                  navigate(
                                    `/requests/${request.id}/edit`
                                  )
                                }
                              >
                                Edit
                              </button>
                            )}

                          </td>

                        </tr>
                      )
                    )}

                  </tbody>

                </table>

              </div>
            )}

        </section>

        <section className="workflow">

          <div className="section-heading">
            <span className="section-label">
              APPROVAL WORKFLOW
            </span>

            <h2>
              Request lifecycle
            </h2>

            <p>
              Your request moves through these
              stages after submission.
            </p>
          </div>

          <div className="workflow-steps">

            <div className="workflow-step">
              <span>01</span>
              <h3>Draft</h3>
              <p>
                Create and edit your request.
              </p>
            </div>

            <div className="workflow-step">
              <span>02</span>
              <h3>Submit</h3>
              <p>
                Submit the request for approval.
              </p>
            </div>

            <div className="workflow-step">
              <span>03</span>
              <h3>Manager Review</h3>
              <p>
                A manager reviews your request.
              </p>
            </div>

            <div className="workflow-step">
              <span>04</span>
              <h3>Admin Review</h3>
              <p>
                Final approval is handled by
                the administrator.
              </p>
            </div>

            <div className="workflow-step">
              <span>05</span>
              <h3>Completed</h3>
              <p>
                Approved requests reach completion.
              </p>
            </div>

          </div>

        </section>

      </main>
    </div>
  );
}

export default EmployeeDashboard;
