import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import requestService from "../api/requestService";

function RequestDetails() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [request, setRequest] = useState(null);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  const loadRequest = async () => {
    try {
      setLoading(true);
      setError("");

      const data =
        await requestService.getRequestById(id);

      setRequest(data);
    } catch (err) {
      console.error(
        "Failed to load request:",
        err
      );

      if (err.response?.status === 401) {
        navigate("/login");
        return;
      }

      setError(
        "Unable to load request details."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadRequest();
  }, [id]);

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
    } catch (err) {
      console.error(
        "Failed to submit request:",
        err
      );

      if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError(
          "Unable to submit the request."
        );
      }
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
            onClick={() =>
              navigate("/dashboard")
            }
          >
            Back to Requests
          </button>
        </div>
      </div>
    );
  }

  return (
    <div className="auth-page">
      <div
        className="auth-card"
        style={{ maxWidth: "700px" }}
      >
        <span className="section-label">
          REQUEST DETAILS
        </span>

        <h1>
          {request?.requestNumber ||
            `Request #${request?.id}`}
        </h1>

        {error && (
          <div
            style={{
              marginTop: "20px",
              padding: "12px 14px",
              borderRadius: "8px",
              background: "#fef3f2",
              color: "#b42318"
            }}
          >
            {error}
          </div>
        )}

        {message && (
          <div
            style={{
              marginTop: "20px",
              padding: "12px 14px",
              borderRadius: "8px",
              background: "#ecfdf3",
              color: "#027a48"
            }}
          >
            {message}
          </div>
        )}

        <div
          style={{
            marginTop: "28px",
            display: "grid",
            gap: "18px"
          }}
        >
          <div>
            <strong>Title</strong>

            <p
              style={{
                marginTop: "6px",
                color: "#667085"
              }}
            >
              {request?.title || "-"}
            </p>
          </div>

          <div>
            <strong>Description</strong>

            <p
              style={{
                marginTop: "6px",
                color: "#667085",
                lineHeight: "1.7",
                whiteSpace: "pre-wrap"
              }}
            >
              {request?.description || "-"}
            </p>
          </div>

          <div>
            <strong>Request Type</strong>

            <p
              style={{
                marginTop: "6px",
                color: "#667085"
              }}
            >
              {request?.requestType || "-"}
            </p>
          </div>

          <div>
            <strong>Status</strong>

            <p
              style={{
                marginTop: "6px"
              }}
            >
              <span className="status">
                {request?.status || "UNKNOWN"}
              </span>
            </p>
          </div>

          {request?.createdAt && (
            <div>
              <strong>Created At</strong>

              <p
                style={{
                  marginTop: "6px",
                  color: "#667085"
                }}
              >
                {request.createdAt}
              </p>
            </div>
          )}
        </div>

        <div
          style={{
            display: "flex",
            gap: "12px",
            marginTop: "32px",
            flexWrap: "wrap"
          }}
        >
          {request?.status === "DRAFT" && (
            <button
              type="button"
              className="button secondary"
              onClick={() =>
                navigate(
                  `/requests/${request.id}/edit`
                )
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
            onClick={() =>
              navigate("/dashboard")
            }
          >
            Back to Requests
          </button>
        </div>
      </div>
    </div>
  );
}

export default RequestDetails;
