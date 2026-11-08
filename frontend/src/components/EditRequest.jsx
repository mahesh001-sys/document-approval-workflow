import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import requestService from "../api/requestService";

function EditRequest() {
  const { id } = useParams();
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    title: "",
    description: "",
    requestTypeId: ""
  });

  const [requestTypes, setRequestTypes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingTypes, setLoadingTypes] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    let isMounted = true;

    const loadData = async () => {
      try {
        setLoading(true);
        setLoadingTypes(true);
        setError("");

        const [request, types] = await Promise.all([
          requestService.getRequestById(id),
          requestService.getActiveRequestTypes()
        ]);

        if (!isMounted) return;

        if (!["DRAFT", "RETURNED"].includes(request.status)) {
          setError(
            "Only draft or returned requests can be edited."
          );
          return;
        }

        setRequestTypes(Array.isArray(types) ? types : []);

        setFormData({
          title: request.title || "",
          description: request.description || "",
          requestTypeId: String(request.requestTypeId || "")
        });
      } catch (err) {
        console.error("Failed to load request:", err);

        if (isMounted) {
          setError(
            "Unable to load the request or request types."
          );
        }
      } finally {
        if (isMounted) {
          setLoading(false);
          setLoadingTypes(false);
        }
      }
    };

    loadData();

    return () => {
      isMounted = false;
    };
  }, [id]);

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
      setSaving(true);

      await requestService.updateRequest(id, {
        title: formData.title.trim(),
        description: formData.description.trim(),
        requestTypeId: Number(formData.requestTypeId)
      });

      navigate(`/requests/${id}`);
    } catch (err) {
      console.error("Failed to update request:", err);

      if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError("Unable to update the request.");
      }
    } finally {
      setSaving(false);
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

  if (error && !formData.title) {
    return (
      <div className="auth-page">
        <div className="auth-card">
          <h1>Unable to Edit</h1>
          <p>{error}</p>

          <button
            type="button"
            className="button secondary"
            onClick={() => navigate(`/requests/${id}`)}
          >
            Back to Request
          </button>
        </div>
      </div>
    );
  }

  const noRequestTypes = requestTypes.length === 0;

  return (
    <div className="auth-page">
      <div
        className="auth-card"
        style={{ maxWidth: "650px" }}
      >
        <span className="section-label">
          EDIT REQUEST
        </span>

        <h1>Update Request</h1>

        <p>
          Update your request before submitting it
          for approval.
        </p>

        {error && (
          <div
            style={{
              marginBottom: "18px",
              padding: "12px 14px",
              borderRadius: "8px",
              background: "#fef3f2",
              color: "#b42318",
              fontSize: "14px"
            }}
          >
            {error}
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
            disabled={saving || loadingTypes || noRequestTypes}
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
            disabled={saving}
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
            maxLength={5000}
            disabled={saving}
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
              disabled={saving || loadingTypes || noRequestTypes}
            >
              {saving ? "Saving..." : "Save Changes"}
            </button>

            <button
              type="button"
              className="button secondary"
              disabled={saving}
              onClick={() => navigate(`/requests/${id}`)}
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default EditRequest;
