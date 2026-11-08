import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../api/authService";
import requestService from "../api/requestService";
import "../styles/manager-dashboard.css";

function ManagerDashboard() {
  const navigate = useNavigate();

  const [requests, setRequests] = useState([]);
  const [selectedRequest, setSelectedRequest] = useState(null);
  const [comments, setComments] = useState("");
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const loadRequests = useCallback(async () => {
    try {
      const data = await requestService.getAllRequests();
      setError("");
      const queue = Array.isArray(data)
        ? data.filter(
            (request) =>
              request.status === "SUBMITTED" ||
              request.status === "MANAGER_REVIEW"
          )
        : [];

      setRequests(queue);

      setSelectedRequest((current) =>
        current
          ? queue.find((request) => request.id === current.id) || null
          : null
      );
    } catch (err) {
      setError(
        err.response?.data?.message ||
          "Unable to load manager requests. Check your manager account access."
      );
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    let active = true;

    const fetchInitialRequests = async () => {
      try {
        const data = await requestService.getAllRequests();

        if (!active) return;

        const queue = Array.isArray(data)
          ? data.filter(
              (request) =>
                request.status === "SUBMITTED" ||
                request.status === "MANAGER_REVIEW"
            )
          : [];

        setRequests(queue);
        setError("");
      } catch (err) {
        if (!active) return;

        setError(
          err.response?.data?.message ||
            "Unable to load requests. Check your account access."
        );
      } finally {
        if (active) setLoading(false);
      }
    };

    fetchInitialRequests();

    return () => {
      active = false;
    };
  }, []);

  const handleLogout = () => {
    authService.logout();
    navigate("/login");
  };

  const handleStartReview = async () => {
    if (!selectedRequest) return;

    try {
      setActionLoading(true);
      setError("");
      setMessage("");

      const updated = await requestService.startManagerReview(
        selectedRequest.id
      );

      setSelectedRequest(updated);
      setRequests((current) =>
        current.map((request) =>
          request.id === updated.id ? updated : request
        )
      );

      setMessage("Request moved to manager review.");
    } catch (err) {
      setError(
        err.response?.data?.message || "Unable to start manager review."
      );
    } finally {
      setActionLoading(false);
    }
  };

  const handleDecision = async (decision) => {
    if (!selectedRequest) return;

    const trimmedComments = comments.trim();

    if (
      (decision === "reject" || decision === "return") &&
      !trimmedComments
    ) {
      setError("Comments are required for rejection or return.");
      return;
    }

    try {
      setActionLoading(true);
      setError("");
      setMessage("");

      if (decision === "approve") {
        await requestService.approveManagerRequest(
          selectedRequest.id,
          trimmedComments
        );
      } else if (decision === "reject") {
        await requestService.rejectManagerRequest(
          selectedRequest.id,
          trimmedComments
        );
      } else {
        await requestService.returnManagerRequest(
          selectedRequest.id,
          trimmedComments
        );
      }

      setMessage(
        decision === "approve"
          ? "Request approved and forwarded to Admin."
          : decision === "reject"
            ? "Request rejected."
            : "Request returned to the employee."
      );

      setSelectedRequest(null);
      setComments("");
      await loadRequests();
    } catch (err) {
      setError(
        err.response?.data?.message ||
          `Unable to ${decision} this request.`
      );
    } finally {
      setActionLoading(false);
    }
  };

  const formatDate = (value) =>
    value ? new Date(value).toLocaleString() : "—";

  return (
    <main className="dashboard-page manager-dashboard">
      <header className="dashboard-header">
        <div>
          <span className="section-label">MANAGER WORKSPACE</span>
          <h1>Manager Dashboard</h1>
          <p>Review employee requests and make approval decisions.</p>
        </div>

        <div className="hero-actions">
          <button
            type="button"
            className="button secondary"
            onClick={loadRequests}
            disabled={loading || actionLoading}
          >
            Refresh
          </button>
          <button
            type="button"
            className="button primary"
            onClick={handleLogout}
          >
            Logout
          </button>
        </div>
      </header>

      <section className="dashboard-stats">
        <div className="stat-card">
          <h3>{requests.length}</h3>
          <p>Requests awaiting manager action</p>
        </div>
        <div className="stat-card">
          <h3>
            {requests.filter((r) => r.status === "SUBMITTED").length}
          </h3>
          <p>New submissions</p>
        </div>
        <div className="stat-card">
          <h3>
            {requests.filter((r) => r.status === "MANAGER_REVIEW").length}
          </h3>
          <p>Under review</p>
        </div>
      </section>

      {error && <p role="alert" className="error-message">{error}</p>}
      {message && <p role="status" className="success-message">{message}</p>}

      <section className="requests-section">
        <div className="dashboard-panel">
          <h2>Manager Approval Queue</h2>

          {loading ? (
            <p>Loading requests...</p>
          ) : requests.length === 0 ? (
            <p>No requests are waiting for manager action.</p>
          ) : (
            <div style={{ overflowX: "auto" }}>
              <table className="dashboard-table" style={{ width: "100%", borderCollapse: "collapse" }}>
                <thead>
                  <tr>
                    <th align="left">Request</th>
                    <th align="left">Employee</th>
                    <th align="left">Type</th>
                    <th align="left">Priority</th>
                    <th align="left">Status</th>
                    <th align="left">Action</th>
                  </tr>
                </thead>
                <tbody>
                  {requests.map((request) => (
                    <tr key={request.id}>
                      <td>{request.requestNumber}</td>
                      <td>
                        {request.requesterFirstName}{" "}
                        {request.requesterLastName}
                      </td>
                      <td>{request.requestTypeName || "—"}</td>
                      <td>
                        <span className={`priority-badge priority-${String(request.priority || "medium").toLowerCase()}`}>
                          {request.priority || "—"}
                        </span>
                      </td>
                      <td>
                        <span className={`status-badge status-${String(request.status || "unknown").toLowerCase().replaceAll("_", "-")}`}>
                          {String(request.status || "UNKNOWN").replaceAll("_", " ")}
                        </span>
                      </td>
                      <td>
                        <button
                          type="button"
                          className="button secondary"
                          onClick={() => {
                            setSelectedRequest(request);
                            setComments("");
                            setError("");
                            setMessage("");
                          }}
                        >
                          Review
                        </button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </section>

      {selectedRequest && (
        <section className="requests-section">
          <div className="dashboard-panel">
            <h2>Request Review</h2>
            <p><strong>Request number:</strong> {selectedRequest.requestNumber}</p>
            <p><strong>Title:</strong> {selectedRequest.title}</p>
            <p>
              <strong>Employee:</strong>{" "}
              {selectedRequest.requesterFirstName}{" "}
              {selectedRequest.requesterLastName}
            </p>
            <p><strong>Email:</strong> {selectedRequest.requesterEmail}</p>
            <p><strong>Request type:</strong> {selectedRequest.requestTypeName}</p>
            <p><strong>Priority:</strong> {selectedRequest.priority}</p>
            <p><strong>Status:</strong> {selectedRequest.status}</p>
            <p><strong>Submitted:</strong> {formatDate(selectedRequest.submittedAt)}</p>
            <p><strong>Description:</strong></p>
            <p>{selectedRequest.description || "No description provided."}</p>

            {selectedRequest.status === "SUBMITTED" ? (
              <button
                type="button"
                className="button primary"
                disabled={actionLoading}
                onClick={handleStartReview}
              >
                {actionLoading ? "Processing..." : "Start Review"}
              </button>
            ) : (
              <>
                <label htmlFor="manager-comments">Comments</label>
                <textarea
                  id="manager-comments"
                  value={comments}
                  onChange={(event) => setComments(event.target.value)}
                  maxLength={1000}
                  rows={4}
                  placeholder="Add review comments..."
                  style={{ width: "100%", marginBottom: "16px" }}
                />

                <div className="hero-actions">
                  <button
                    type="button"
                    className="button primary"
                    disabled={actionLoading}
                    onClick={() => handleDecision("approve")}
                  >
                    Approve
                  </button>
                  <button
                    type="button"
                    className="button secondary"
                    disabled={actionLoading}
                    onClick={() => handleDecision("return")}
                  >
                    Return to Employee
                  </button>
                  <button
                    type="button"
                    className="button secondary"
                    disabled={actionLoading}
                    onClick={() => handleDecision("reject")}
                  >
                    Reject
                  </button>
                </div>
              </>
            )}

            <button
              type="button"
              className="button secondary"
              style={{ marginTop: "12px" }}
              disabled={actionLoading}
              onClick={() => setSelectedRequest(null)}
            >
              Close
            </button>
          </div>
        </section>
      )}
    </main>
  );
}

export default ManagerDashboard;
