import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import authService from "../api/authService";
import requestService from "../api/requestService";

function ManagerDashboard() {
  const navigate = useNavigate();

  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] =
    useState(false);

  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const [selectedRequest, setSelectedRequest] =
    useState(null);

  const [comments, setComments] = useState("");

  const user = authService.getUser();

  const approverId = user?.id;

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

  const handleStartReview = async (requestId) => {
    try {
      setActionLoading(true);
      setError("");
      setMessage("");

      const updatedRequest =
        await requestService.startManagerReview(
          requestId
        );

      setRequests((previous) =>
        previous.map((request) =>
          request.id === requestId
            ? updatedRequest
            : request
        )
      );

      setSelectedRequest(
        updatedRequest
      );

      setMessage(
        "Request moved to Manager Review."
      );
    } catch (err) {
      console.error(
        "Failed to start manager review:",
        err
      );

      if (err.response?.data?.message) {
        setError(
          err.response.data.message
        );
      } else {
        setError(
          "Unable to start manager review."
        );
      }
    } finally {
      setActionLoading(false);
    }
  };

  const handleApprove = async () => {
    if (!selectedRequest) {
      return;
    }

    if (!approverId) {
      setError(
        "Manager user ID was not found. Please log in again."
      );
      return;
    }

    try {
      setActionLoading(true);
      setError("");
      setMessage("");

      const updatedRequest =
        await requestService.approveManagerRequest(
          selectedRequest.id,
          approverId,
          comments.trim()
        );

      setRequests((previous) =>
        previous.filter(
          (request) =>
            request.id !==
            selectedRequest.id
        )
      );

      setSelectedRequest(null);
      setComments("");

      setMessage(
        "Request approved successfully."
      );
    } catch (err) {
      console.error(
        "Failed to approve request:",
        err
      );

      if (err.response?.data?.message) {
        setError(
          err.response.data.message
        );
      } else {
        setError(
          "Unable to approve the request."
        );
      }
    } finally {
      setActionLoading(false);
    }
  };

  const handleReject = async () => {
    if (!selectedRequest) {
      return;
    }

    if (!approverId) {
      setError(
        "Manager user ID was not found. Please log in again."
      );
      return;
    }

    if (!comments.trim()) {
      setError(
        "Please provide a comment before rejecting the request."
      );
      return;
    }

    try {
      setActionLoading(true);
      setError("");
      setMessage("");

      await requestService.rejectManagerRequest(
        selectedRequest.id,
        approverId,
        comments.trim()
      );

      setRequests((previous) =>
        previous.filter(
          (request) =>
            request.id !==
            selectedRequest.id
        )
      );

      setSelectedRequest(null);
      setComments("");

      setMessage(
        "Request rejected successfully."
      );
    } catch (err) {
      console.error(
        "Failed to reject request:",
        err
      );

      if (err.response?.data?.message) {
        setError(
          err.response.data.message
        );
      } else {
        setError(
          "Unable to reject the request."
        );
      }
    } finally {
      setActionLoading(false);
    }
  };

  const handleReturn = async () => {
    if (!selectedRequest) {
      return;
    }

    if (!approverId) {
      setError(
        "Manager user ID was not found. Please log in again."
      );
      return;
    }

    if (!comments.trim()) {
      setError(
        "Please provide a comment before returning the request."
      );
      return;
    }

    try {
      setActionLoading(true);
      setError("");
      setMessage("");

      await requestService.returnManagerRequest(
        selectedRequest.id,
        approverId,
        comments.trim()
      );

      setRequests((previous) =>
        previous.filter(
          (request) =>
            request.id !==
            selectedRequest.id
        )
      );

      setSelectedRequest(null);
      setComments("");

      setMessage(
        "Request returned to the employee for changes."
      );
    } catch (err) {
      console.error(
        "Failed to return request:",
        err
      );

      if (err.response?.data?.message) {
        setError(
          err.response.data.message
        );
      } else {
        setError(
          "Unable to return the request."
        );
      }
    } finally {
      setActionLoading(false);
    }
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
            disabled={loading || actionLoading}
          >
            Refresh
          </button>

          <button
            type="button"
            className="button secondary"
            onClick={handleLogout}
            disabled={actionLoading}
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

        {message && (
          <div
            style={{
              marginBottom: "20px",
              padding: "14px",
              borderRadius: "8px",
              background: "#ecfdf3",
              color: "#027a48"
            }}
          >
            {message}
          </div>
        )}

        {error && (
          <div
            style={{
              marginBottom: "20px",
              padding: "14px",
              borderRadius: "8px",
              background: "#fef3f2",
              color: "#b42318"
            }}
          >
            {error}
          </div>
        )}

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
                              disabled={
                                actionLoading
                              }
                              onClick={() =>
                                setSelectedRequest(
                                  request
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

        {selectedRequest && (
          <section
            className="requests-section"
            style={{
              marginTop: "30px"
            }}
          >

            <div className="section-heading">

              <span className="section-label">
                REQUEST REVIEW
              </span>

              <h2>
                {selectedRequest.title ||
                  "Request Details"}
              </h2>

              <p>
                Request #
                {selectedRequest.requestNumber ||
                  selectedRequest.id}
              </p>

            </div>

            <div
              style={{
                display: "grid",
                gap: "18px",
                marginBottom: "24px"
              }}
            >

              <div>
                <strong>
                  Description
                </strong>

                <p
                  style={{
                    marginTop: "6px",
                    lineHeight: "1.7",
                    whiteSpace: "pre-wrap"
                  }}
                >
                  {selectedRequest.description ||
                    "-"}
                </p>
              </div>

              <div>
                <strong>
                  Request Type
                </strong>

                <p
                  style={{
                    marginTop: "6px"
                  }}
                >
                  {selectedRequest.requestType ||
                    "-"}
                </p>
              </div>

              <div>
                <strong>
                  Current Status
                </strong>

                <p
                  style={{
                    marginTop: "8px"
                  }}
                >
                  <span
                    className={getStatusClass(
                      selectedRequest.status
                    )}
                  >
                    {getStatusLabel(
                      selectedRequest.status
                    )}
                  </span>
                </p>
              </div>

            </div>

            {selectedRequest.status ===
              "SUBMITTED" && (
              <div
                style={{
                  marginBottom: "24px"
                }}
              >

                <button
                  type="button"
                  className="button secondary"
                  onClick={() =>
                    handleStartReview(
                      selectedRequest.id
                    )
                  }
                  disabled={actionLoading}
                >
                  {actionLoading
                    ? "Processing..."
                    : "Start Manager Review"}
                </button>

              </div>
            )}

            {selectedRequest.status ===
              "MANAGER_REVIEW" && (
              <>

                <label htmlFor="manager-comments">
                  Comments
                </label>

                <textarea
                  id="manager-comments"
                  value={comments}
                  onChange={(event) =>
                    setComments(
                      event.target.value
                    )
                  }
                  placeholder="Enter review comments..."
                  maxLength={1000}
                  rows={5}
                  disabled={actionLoading}
                  style={{
                    width: "100%",
                    padding: "14px",
                    border:
                      "1px solid #d0d5dd",
                    borderRadius: "9px",
                    resize: "vertical",
                    marginTop: "8px"
                  }}
                />

                <p
                  style={{
                    fontSize: "12px",
                    color: "#667085",
                    marginTop: "6px"
                  }}
                >
                  Maximum 1000 characters.
                </p>

                <div
                  style={{
                    display: "flex",
                    gap: "12px",
                    flexWrap: "wrap",
                    marginTop: "20px"
                  }}
                >

                  <button
                    type="button"
                    className="button primary"
                    onClick={handleApprove}
                    disabled={actionLoading}
                  >
                    {actionLoading
                      ? "Processing..."
                      : "Approve"}
                  </button>

                  <button
                    type="button"
                    className="button secondary"
                    onClick={handleReturn}
                    disabled={actionLoading}
                  >
                    Return for Changes
                  </button>

                  <button
                    type="button"
                    className="button secondary"
                    onClick={handleReject}
                    disabled={actionLoading}
                  >
                    Reject
                  </button>

                </div>

              </>
            )}

            <div
              style={{
                marginTop: "20px"
              }}
            >

              <button
                type="button"
                className="button secondary"
                onClick={() => {
                  setSelectedRequest(null);
                  setComments("");
                  setError("");
                }}
                disabled={actionLoading}
              >
                Close Review
              </button>

            </div>

          </section>
        )}

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
