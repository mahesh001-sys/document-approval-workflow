
import { useCallback, useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import requestService from "../api/requestService";

function formatDate(value) {
  if (!value) return "Date unavailable";

  const date = new Date(value);

  if (Number.isNaN(date.getTime())) {
    return value;
  }

  return new Intl.DateTimeFormat("en-IN", {
    day: "2-digit",
    month: "short",
    year: "numeric",
    hour: "2-digit",
    minute: "2-digit",
    hour12: true
  }).format(date);
}

function getApproverName(approver) {
  if (!approver) return "Unknown approver";

  const fullName = [
    approver.firstName,
    approver.lastName
  ].filter(Boolean).join(" ").trim();

  return fullName || approver.email || "Unknown approver";
}

function getInitials(approver) {
  if (!approver) return "?";

  const first = approver.firstName?.[0] || "";
  const last = approver.lastName?.[0] || "";

  return `${first}${last}`.toUpperCase() || "?";
}

function getActionStyle(action) {
  switch (action) {
    case "APPROVED":
      return {
        color: "#067647",
        background: "#ecfdf3",
        border: "#abefc6",
        icon: "✓"
      };

    case "REJECTED":
      return {
        color: "#b42318",
        background: "#fef3f2",
        border: "#fecdca",
        icon: "×"
      };

    case "RETURNED":
      return {
        color: "#b54708",
        background: "#fffaeb",
        border: "#fedf89",
        icon: "↩"
      };

    default:
      return {
        color: "#475467",
        background: "#f2f4f7",
        border: "#d0d5dd",
        icon: "•"
      };
  }
}

function ApprovalHistory({ history, loading, error }) {
  return (
    <section style={styles.historySection}>
      <div style={styles.historyHeader}>
        <div>
          <span style={styles.sectionEyebrow}>
            AUDIT TRAIL
          </span>

          <h2 style={styles.historyTitle}>
            Approval History
          </h2>

          <p style={styles.historySubtitle}>
            A chronological record of decisions made on this request.
          </p>
        </div>

        {!loading && !error && (
          <span style={styles.historyCount}>
            {history.length} {history.length === 1 ? "record" : "records"}
          </span>
        )}
      </div>

      {loading && (
        <div style={styles.historyMessage}>
          <span style={styles.spinner} />
          Loading approval history...
        </div>
      )}

      {!loading && error && (
        <div style={styles.historyError}>
          <strong>Unable to load approval history</strong>
          <p>{error}</p>
        </div>
      )}

      {!loading && !error && history.length === 0 && (
        <div style={styles.emptyHistory}>
          <div style={styles.emptyIcon}>◷</div>

          <h3>No approval activity yet</h3>

          <p>
            Approval decisions will appear here once a manager or
            administrator takes action on this request.
          </p>
        </div>
      )}

      {!loading && !error && history.length > 0 && (
        <div style={styles.timeline}>
          {history.map((entry, index) => {
            const actionStyle = getActionStyle(entry.action);

            return (
              <article
                key={entry.id ?? `${entry.action}-${index}`}
                style={styles.timelineItem}
              >
                <div style={styles.timelineTrack}>
                  <div
                    style={{
                      ...styles.timelineIcon,
                      color: actionStyle.color,
                      background: actionStyle.background,
                      borderColor: actionStyle.border
                    }}
                  >
                    {actionStyle.icon}
                  </div>

                  {index !== history.length - 1 && (
                    <div style={styles.timelineLine} />
                  )}
                </div>

                <div style={styles.historyCard}>
                  <div style={styles.historyCardTop}>
                    <div style={styles.approverInfo}>
                      <div style={styles.avatar}>
                        {getInitials(entry.approver)}
                      </div>

                      <div>
                        <h3 style={styles.approverName}>
                          {getApproverName(entry.approver)}
                        </h3>

                        <p style={styles.approverRole}>
                          Workflow approver
                        </p>
                      </div>
                    </div>

                    <span
                      style={{
                        ...styles.actionBadge,
                        color: actionStyle.color,
                        background: actionStyle.background,
                        border: `1px solid ${actionStyle.border}`
                      }}
                    >
                      {entry.action || "UNKNOWN"}
                    </span>
                  </div>

                  {entry.comments && (
                    <div style={styles.commentsBox}>
                      <span style={styles.commentsLabel}>
                        COMMENTS
                      </span>

                      <p style={styles.commentsText}>
                        {entry.comments}
                      </p>
                    </div>
                  )}

                  <div style={styles.statusTransition}>
                    <div>
                      <span style={styles.statusLabel}>
                        PREVIOUS STATUS
                      </span>

                      <span style={styles.statusValue}>
                        {entry.previousStatus || "-"}
                      </span>
                    </div>

                    <span style={styles.transitionArrow}>
                      →
                    </span>

                    <div>
                      <span style={styles.statusLabel}>
                        NEW STATUS
                      </span>

                      <span style={styles.statusValue}>
                        {entry.newStatus || "-"}
                      </span>
                    </div>
                  </div>

                  <div style={styles.historyDate}>
                    <span>◷</span>
                    {formatDate(entry.createdAt)}
                  </div>
                </div>
              </article>
            );
          })}
        </div>
      )}
    </section>
  );
}

function RequestDetails() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [request, setRequest] = useState(null);
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(true);
  const [historyLoading, setHistoryLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [historyError, setHistoryError] = useState("");
  const [message, setMessage] = useState("");

  const loadApprovalHistory = useCallback(async (requestId) => {
    try {
      setHistoryLoading(true);
      setHistoryError("");

      const data = await requestService.getApprovalHistory(requestId);

      setHistory(Array.isArray(data) ? data : []);
    } catch (err) {
      console.error("Failed to load approval history:", err);

      if (err.response?.status === 401) {
        navigate("/login");
        return;
      }

      setHistory([]);
      setHistoryError(
        err.response?.data?.message ||
        "The approval history could not be retrieved. Please try again."
      );
    } finally {
      setHistoryLoading(false);
    }
  }, [navigate]);

  useEffect(() => {
    let isActive = true;

    async function loadRequestDetails() {
      try {
        setLoading(true);
        setError("");

        const data = await requestService.getRequestById(id);

        if (!isActive) return;

        setRequest(data);

        // Load history independently so a history error
        // does not prevent the request details from appearing.
        try {
          setHistoryLoading(true);
          setHistoryError("");

          const historyData =
            await requestService.getApprovalHistory(id);

          if (!isActive) return;

          setHistory(
            Array.isArray(historyData) ? historyData : []
          );
        } catch (historyErr) {
          if (!isActive) return;

          console.error(
            "Failed to load approval history:",
            historyErr
          );

          if (historyErr.response?.status === 401) {
            navigate("/login");
            return;
          }

          setHistory([]);
          setHistoryError(
            historyErr.response?.data?.message ||
            "The approval history could not be retrieved."
          );
        } finally {
          if (isActive) {
            setHistoryLoading(false);
          }
        }
      } catch (err) {
        if (!isActive) return;

        console.error("Failed to load request:", err);

        if (err.response?.status === 401) {
          navigate("/login");
          return;
        }

        setError(
          err.response?.data?.message ||
          "Unable to load request details."
        );
      } finally {
        if (isActive) {
          setLoading(false);
        }
      }
    }

    loadRequestDetails();

    return () => {
      isActive = false;
    };
  }, [id, navigate]);

  const handleSubmitRequest = async () => {
    try {
      setSubmitting(true);
      setError("");
      setMessage("");

      const updatedRequest =
        await requestService.submitRequest(id);

      setRequest(updatedRequest);

      setMessage(
        "Request submitted successfully for approval."
      );

      await loadApprovalHistory(id);
    } catch (err) {
      console.error("Failed to submit request:", err);

      if (err.response?.status === 401) {
        navigate("/login");
        return;
      }

      setError(
        err.response?.data?.message ||
        "Unable to submit the request."
      );
    } finally {
      setSubmitting(false);
    }
  };

  if (loading) {
    return (
      <div className="auth-page">
        <div className="auth-card">
          <p>Loading request...</p>
        </div>
      </div>
    );
  }

  if (error && !request) {
    return (
      <div className="auth-page">
        <div className="auth-card">
          <h1>Request Error</h1>
          <p>{error}</p>

          <button
            type="button"
            className="button secondary"
            onClick={() => navigate("/dashboard")}
          >
            Back to Requests
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="auth-page">
      <div style={styles.pageContainer}>
        <div className="auth-card" style={styles.requestCard}>
          <span className="section-label">
            REQUEST DETAILS
          </span>

          <h1 style={styles.requestHeading}>
            {request?.requestNumber || `Request #${request?.id}`}
          </h1>

          {error && (
            <div style={styles.errorMessage}>
              {error}
            </div>
          )}

          {message && (
            <div style={styles.successMessage}>
              {message}
            </div>
          )}

          <div style={styles.detailsGrid}>
            <div>
              <strong>Title</strong>
              <p style={styles.detailText}>
                {request?.title || "-"}
              </p>
            </div>

            <div>
              <strong>Description</strong>
              <p style={styles.descriptionText}>
                {request?.description || "-"}
              </p>
            </div>

            <div>
              <strong>Request Type</strong>
              <p style={styles.detailText}>
                {request?.requestType?.name ||
                  request?.requestType ||
                  "-"}
              </p>
            </div>

            <div>
              <strong>Status</strong>
              <p style={{ marginTop: "6px" }}>
                <span className="status">
                  {request?.status || "UNKNOWN"}
                </span>
              </p>
            </div>

            {request?.createdAt && (
              <div>
                <strong>Created At</strong>
                <p style={styles.detailText}>
                  {formatDate(request.createdAt)}
                </p>
              </div>
            )}
          </div>

          <div style={styles.actionButtons}>
            {request?.status === "DRAFT" && (
              <button
                type="button"
                className="button secondary"
                onClick={() =>
                  navigate(`/requests/${request.id}/edit`)
                }
              >
                Edit Draft
              </button>
            )}

            {request?.status === "DRAFT" && (
              <button
                type="button"
                className="button primary"
                onClick={handleSubmitRequest}
                disabled={submitting}
              >
                {submitting
                  ? "Submitting..."
                  : "Submit for Approval"}
              </button>
            )}

            <button
              type="button"
              className="button secondary"
              onClick={() => navigate("/dashboard")}
            >
              Back to Requests
            </button>
          </div>
        </div>

        <div style={styles.historyContainer}>
          <ApprovalHistory
            history={history}
            loading={historyLoading}
            error={historyError}
          />
        </div>
      </div>
    </div>
  );
}

const styles = {
  pageContainer: {
    width: "100%",
    maxWidth: "850px",
    margin: "0 auto",
    padding: "24px 16px 48px",
    boxSizing: "border-box"
  },

  requestCard: {
    width: "100%",
    maxWidth: "none",
    boxSizing: "border-box"
  },

  requestHeading: {
    fontSize: "clamp(20px, 4vw, 28px)",
    overflowWrap: "anywhere"
  },

  detailsGrid: {
    marginTop: "28px",
    display: "grid",
    gap: "20px"
  },

  detailText: {
    marginTop: "6px",
    color: "#667085",
    lineHeight: "1.6"
  },

  descriptionText: {
    marginTop: "6px",
    color: "#667085",
    lineHeight: "1.7",
    whiteSpace: "pre-wrap",
    overflowWrap: "anywhere"
  },

  actionButtons: {
    display: "flex",
    gap: "12px",
    marginTop: "32px",
    flexWrap: "wrap"
  },

  errorMessage: {
    marginTop: "20px",
    padding: "12px 14px",
    borderRadius: "8px",
    background: "#fef3f2",
    color: "#b42318"
  },

  successMessage: {
    marginTop: "20px",
    padding: "12px 14px",
    borderRadius: "8px",
    background: "#ecfdf3",
    color: "#027a48"
  },

  historyContainer: {
    marginTop: "22px"
  },

  historySection: {
    background: "#ffffff",
    border: "1px solid #e4e7ec",
    borderRadius: "16px",
    padding: "26px",
    boxShadow: "0 4px 16px rgba(16, 24, 40, 0.04)"
  },

  historyHeader: {
    display: "flex",
    alignItems: "flex-start",
    justifyContent: "space-between",
    gap: "16px",
    flexWrap: "wrap",
    marginBottom: "26px"
  },

  sectionEyebrow: {
    color: "#5265a8",
    fontSize: "11px",
    fontWeight: "800",
    letterSpacing: "1.5px"
  },

  historyTitle: {
    color: "#182230",
    fontSize: "23px",
    fontWeight: "750",
    margin: "8px 0 5px"
  },

  historySubtitle: {
    color: "#667085",
    fontSize: "13px",
    lineHeight: "1.6",
    margin: 0
  },

  historyCount: {
    background: "#f2f4f7",
    color: "#344054",
    borderRadius: "20px",
    padding: "7px 12px",
    fontSize: "12px",
    fontWeight: "700",
    whiteSpace: "nowrap"
  },

  historyMessage: {
    padding: "24px 12px",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    gap: "12px",
    color: "#667085",
    fontSize: "14px"
  },

  spinner: {
    width: "17px",
    height: "17px",
    border: "2px solid #d0d5dd",
    borderTopColor: "#5265a8",
    borderRadius: "50%",
    display: "inline-block"
  },

  historyError: {
    background: "#fef3f2",
    border: "1px solid #fecdca",
    borderRadius: "10px",
    padding: "16px",
    color: "#b42318"
  },

  emptyHistory: {
    textAlign: "center",
    padding: "30px 18px",
    background: "#f9fafb",
    border: "1px dashed #d0d5dd",
    borderRadius: "12px"
  },

  emptyIcon: {
    width: "48px",
    height: "48px",
    margin: "0 auto 14px",
    borderRadius: "50%",
    background: "#eef2ff",
    color: "#5265a8",
    display: "grid",
    placeItems: "center",
    fontSize: "24px"
  },

  emptyHistoryTitle: {
    color: "#344054"
  },

  timeline: {
    display: "flex",
    flexDirection: "column"
  },

  timelineItem: {
    display: "flex",
    gap: "15px",
    minWidth: 0
  },

  timelineTrack: {
    display: "flex",
    flexDirection: "column",
    alignItems: "center",
    flexShrink: 0
  },

  timelineIcon: {
    width: "34px",
    height: "34px",
    borderRadius: "50%",
    border: "1px solid",
    display: "grid",
    placeItems: "center",
    fontSize: "17px",
    fontWeight: "800",
    zIndex: 1
  },

  timelineLine: {
    width: "2px",
    flex: 1,
    minHeight: "24px",
    background: "#e4e7ec"
  },

  historyCard: {
    flex: 1,
    minWidth: 0,
    marginBottom: "20px",
    padding: "17px",
    border: "1px solid #eaecf0",
    borderRadius: "12px",
    background: "#ffffff"
  },

  historyCardTop: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    flexWrap: "wrap",
    gap: "12px"
  },

  approverInfo: {
    display: "flex",
    alignItems: "center",
    gap: "11px"
  },

  avatar: {
    width: "39px",
    height: "39px",
    borderRadius: "50%",
    background: "#eef2ff",
    color: "#4659a5",
    display: "grid",
    placeItems: "center",
    fontSize: "12px",
    fontWeight: "800",
    flexShrink: 0
  },

  approverName: {
    color: "#182230",
    fontSize: "14px",
    fontWeight: "700",
    margin: 0
  },

  approverRole: {
    color: "#667085",
    fontSize: "12px",
    margin: "4px 0 0"
  },

  actionBadge: {
    borderRadius: "20px",
    padding: "6px 10px",
    fontSize: "10px",
    fontWeight: "800",
    letterSpacing: "0.5px"
  },

  commentsBox: {
    marginTop: "17px",
    padding: "12px",
    borderRadius: "8px",
    background: "#f9fafb"
  },

  commentsLabel: {
    color: "#667085",
    fontSize: "10px",
    fontWeight: "800",
    letterSpacing: "1px"
  },

  commentsText: {
    color: "#475467",
    fontSize: "13px",
    lineHeight: "1.6",
    margin: "6px 0 0",
    whiteSpace: "pre-wrap",
    overflowWrap: "anywhere"
  },

  statusTransition: {
    display: "flex",
    alignItems: "center",
    gap: "12px",
    flexWrap: "wrap",
    marginTop: "17px",
    paddingTop: "15px",
    borderTop: "1px solid #f0f1f3"
  },

  statusLabel: {
    display: "block",
    color: "#98a2b3",
    fontSize: "9px",
    fontWeight: "800",
    letterSpacing: "0.7px",
    marginBottom: "5px"
  },

  statusValue: {
    color: "#344054",
    fontSize: "11px",
    fontWeight: "700",
    overflowWrap: "anywhere"
  },

  transitionArrow: {
    color: "#98a2b3",
    fontSize: "18px",
    paddingTop: "10px"
  },

  historyDate: {
    display: "flex",
    alignItems: "center",
    gap: "7px",
    color: "#98a2b3",
    fontSize: "11px",
    marginTop: "15px"
  }
};

export default RequestDetails;
