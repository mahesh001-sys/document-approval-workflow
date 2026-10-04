import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../api/authService";
import requestService from "../api/requestService";

function ManagerDashboard() {
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

      const managerRequests =
        Array.isArray(data)
          ? data.filter(
              (request) =>
                request.status ===
                  "SUBMITTED" ||
                request.status ===
                  "MANAGER_REVIEW"
            )
          : [];

      setRequests(managerRequests);
    } catch (err) {
      console.error(
        "Failed to load manager requests:",
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

    return status.replaceAll("_", " ");
  };

  const pendingCount = requests.length;

  const submittedCount = requests.filter(
    (request) =>
      request.status === "SUBMITTED"
  ).length;

  const managerReviewCount = requests.filter(
    (request) =>
      request.status ===
      "MANAGER_REVIEW"
  ).length;

  return (
    <div className="dashboard-page">

      <header className="dashboard-header">

        <div>
          <span className="section-label">
            MANAGER WORKSPACE
          </span>

          <h1>
            Approval Requests
          </h1>

          <p>
            {user?.email
              ? `Signed in as ${user.email}`
              : "Review and manage employee requests"}
          </p>
        </div>

        <div className="dashboard-actions">

          <button
            type="button"
            className="button secondary"
            onClick={loadRequests}
          >
            Refresh
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
            <span>
              Pending Requests
            </span>

            <strong>
              {pendingCount}
            </strong>
          </div>

          <div className="stat-card">
            <span>
              Submitted
            </span>

            <strong>
              {submittedCount}
            </strong>
          </div>

          <div className="stat-card">
            <span>
              Manager Review
            </span>

            <strong>
              {managerReviewCount}
            </strong>
          </div>

        </section>

        <section className="requests-section">

          <div className="section-heading">

            <span className="section-label">
              APPROVAL QUEUE
            </span>

            <h2>
              Requests Waiting for Review
            </h2>

            <p>
              Review requests submitted by
              employees and take the appropriate
              workflow action.
            </p>

          </div>

          {loading && (
            <div className="empty-state">
              <p>
                Loading approval requests...
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
                  ✓
                </div>

                <h3>
                  No pending requests
                </h3>

                <p>
                  There are currently no employee
                  requests waiting for manager review.
                </p>

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
                              Review
                            </button>

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
              MANAGER RESPONSIBILITIES
            </span>

            <h2>
              Approval process
            </h2>

            <p>
              Requests assigned to managers follow
              a controlled approval process.
            </p>

          </div>

          <div className="workflow-steps">

            <div className="workflow-step">
              <span>01</span>

              <h3>
                Review
              </h3>

              <p>
                Examine the employee request and
                supporting information.
              </p>
            </div>

            <div className="workflow-step">
              <span>02</span>

              <h3>
                Approve
              </h3>

              <p>
                Approve valid requests for the next
                workflow stage.
              </p>
            </div>

            <div className="workflow-step">
              <span>03</span>

              <h3>
                Return
              </h3>

              <p>
                Send incomplete requests back to the
                employee for changes.
              </p>
            </div>

            <div className="workflow-step">
              <span>04</span>

              <h3>
                Reject
              </h3>

              <p>
                Reject requests that do not meet
                approval requirements.
              </p>
            </div>

          </div>

        </section>

      </main>

    </div>
  );
}

export default ManagerDashboard;
