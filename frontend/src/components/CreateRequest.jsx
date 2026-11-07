import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import requestService from "../api/requestService";

function CreateRequest() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    title: "",
    description: "",
    requestTypeId: ""
  });

  const [requestTypes, setRequestTypes] = useState([]);
  const [loadingTypes, setLoadingTypes] = useState(true);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    let isMounted = true;

    const loadRequestTypes = async () => {
      try {
        const types =
          await requestService.getActiveRequestTypes();

        if (isMounted) {
          setRequestTypes(
            Array.isArray(types) ? types : []
          );
        }
      } catch (err) {
        console.error("Failed to load request types:", err);

        if (isMounted) {
          setError(
            "Unable to load request types. Please refresh the page."
          );
        }
      } finally {
        if (isMounted) {
          setLoadingTypes(false);
        }
      }
    };

    loadRequestTypes();

    return () => {
      isMounted = false;
    };
  }, []);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setSuccess("");

    if (!formData.requestTypeId) {
      setError("Please select a request type.");
      return;
    }

    if (!formData.title.trim()) {
      setError("Request title is required.");
      return;
    }

    if (!formData.description.trim()) {
      setError("Request description is required.");
      return;
    }

    try {
      setLoading(true);

      const createdRequest =
        await requestService.createRequest({
          title: formData.title.trim(),
          description: formData.description.trim(),
          requestTypeId: Number(formData.requestTypeId)
        });

      setSuccess(
        `Request ${
          createdRequest?.requestNumber || ""
        } created successfully.`
      );

      setFormData({
        title: "",
        description: "",
        requestTypeId: ""
      });

      setTimeout(() => {
        navigate("/dashboard");
      }, 1200);
    } catch (err) {
      console.error("Failed to create request:", err);

      if (err.response?.status === 401) {
        setError(
          "Your session has expired. Please login again."
        );
        return;
      }

      if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError(
          "Unable to create the request. Please try again."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  const noRequestTypes =
    !loadingTypes && requestTypes.length === 0;

  return (
    <div className="auth-page">
      <div
        className="auth-card"
        style={{ maxWidth: "650px" }}
      >
        <span className="section-label">
          NEW REQUEST
        </span>

        <h1>Create Request</h1>

        <p>
          Enter the details of your business request.
          You can submit it for approval after creation.
        </p>

        {error && (
          <div
            style={{
              marginBottom: "18px",
              padding: "12px 14px",
              borderRadius: "8px",
              background: "#fef3f2",
              color: "#b42318",
              fontSize: "14px",
              lineHeight: "1.5"
            }}
          >
            {error}
          </div>
        )}

        {success && (
          <div
            style={{
              marginBottom: "18px",
              padding: "12px 14px",
              borderRadius: "8px",
              background: "#ecfdf3",
              color: "#027a48",
              fontSize: "14px",
              lineHeight: "1.5"
            }}
          >
            {success}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <label htmlFor="requestTypeId">
            Request Type
          </label>

          <select
            id="requestTypeId"
            name="requestTypeId"
            value={formData.requestTypeId}
            onChange={handleChange}
            disabled={loading || loadingTypes || noRequestTypes}
            required
            style={{
              width: "100%",
              padding: "14px",
              marginBottom: "18px",
              border: "1px solid #d0d5dd",
              borderRadius: "9px"
            }}
          >
            <option value="">
              {loadingTypes
                ? "Loading request types..."
                : noRequestTypes
                  ? "No active request types available"
                  : "Select a request type"}
            </option>

            {requestTypes.map((type) => (
              <option key={type.id} value={type.id}>
                {type.name}
              </option>
            ))}
          </select>

          {noRequestTypes && (
            <p style={{ color: "#b42318", fontSize: "14px" }}>
              No active request types were found.
              Please contact an administrator.
            </p>
          )}

          <label htmlFor="title">
            Request Title
          </label>

          <input
            id="title"
            name="title"
            type="text"
            value={formData.title}
            onChange={handleChange}
            placeholder="Enter request title"
            maxLength={200}
            disabled={loading}
            required
          />

          <label htmlFor="description">
            Description
          </label>

          <textarea
            id="description"
            name="description"
            value={formData.description}
            onChange={handleChange}
            placeholder="Describe your request..."
            rows={7}
            maxLength={2000}
            disabled={loading}
            required
            style={{
              width: "100%",
              padding: "14px",
              border: "1px solid #d0d5dd",
              borderRadius: "9px",
              outline: "none",
              resize: "vertical"
            }}
          />

          <div
            style={{
              display: "flex",
              gap: "12px",
              marginTop: "24px",
              flexWrap: "wrap"
            }}
          >
            <button
              type="submit"
              className="button primary"
              disabled={
                loading ||
                loadingTypes ||
                noRequestTypes
              }
            >
              {loading ? "Creating..." : "Create Request"}
            </button>

            <button
              type="button"
              className="button secondary"
              disabled={loading}
              onClick={() => navigate("/dashboard")}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default CreateRequest;
