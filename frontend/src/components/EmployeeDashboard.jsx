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

      const data = await requestService.getAllRequests();

      setRequests(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Failed to load requests:", err);

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
            <strong>{requests.length}</strong>
          </div>

          <div className="stat-card">
            <span>Draft</span>
            <strong>
              {
                requests.filter(
                  (request) =>
                    request.status === "DRAFT"
                ).length
              }
            </strong>
          </div>

          <div className="stat-card">
            <span>In Review</span>
            <strong>
              {
                requests.filter(
                  (request) =>
                    request.status ===
                      "SUBMITTED" ||
                    request.status ===
                      "MANAGER_REVIEW" ||
                    request.status ===
                      "ADMIN_REVIEW"
                ).length
              }
            </strong>
          </div>

          <div className="stat-card">
            <span>Completed</span>
            <strong>
              {
                requests.filter(
                  (request) =>
                    request.status === "COMPLETED"
                ).length
              }
            </strong>
          </div>
        </section>

        <section className="requests-section">
          <div className="section-heading">
            <span className="section-label">
              REQUEST MANAGEMENT
            </span>

            <h2>Your Requests</h2>
          </div>

          {loading && (
            <div className="empty-state">
              <p>Loading requests...</p>
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

                <h3>No requests yet</h3>

                <p>
                  Create your first business request
                  to start the approval workflow.
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
                    {requests.map((request) => (
                      <tr key={request.id}>
                        <td>
                          #{request.id}
                        </td>

                        <td>
                          {request.requestNumber ||
                            "-"}
                        </td>

                        <td>
                          {request.title ||
                            request.description ||
                            "Untitled Request"}
                        </td>

                        <td>
                          <span
                            className={getStatusClass(
                              request.status
                            )}
                          >
                            {request.status ||
                              "UNKNOWN"}
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
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
        </section>
      </main>
    </div>
  );
}

export default EmployeeDashboard;
