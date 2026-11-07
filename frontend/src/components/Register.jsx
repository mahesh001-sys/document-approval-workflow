import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import authService from "../api/authService";

function Register() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: ""
  });

  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const handleChange = (event) => {
    setForm({
      ...form,
      [event.target.name]: event.target.value
    });
  };

  const handleRegister = async (event) => {
    event.preventDefault();
    setError("");
    setSuccess("");

    if (form.password.length < 8 || form.password.length > 72) {
      setError("Password must be between 8 and 72 characters.");
      return;
    }

    if (form.password !== confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    try {
      setLoading(true);

      await authService.register(
        form.firstName.trim(),
        form.lastName.trim(),
        form.email.trim(),
        form.password
      );

      setSuccess("Account created successfully! Redirecting to login...");

      setTimeout(() => {
        navigate("/login", {
          state: { registeredEmail: form.email.trim() }
        });
      }, 1200);
    } catch (err) {
      console.error("Registration failed:", err);

      if (err.response?.status === 409) {
        setError("An account with this email already exists.");
      } else if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else if (err.response?.status === 400) {
        setError("Please check your details and try again.");
      } else {
        setError("Unable to register. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <span className="section-label">CREATE ACCOUNT</span>

        <h1>Join us</h1>

        <p>
          Create your account to access the Document Approval Workflow.
        </p>

        {error && (
          <div style={{
            marginBottom: "18px",
            padding: "12px 14px",
            borderRadius: "8px",
            background: "#fef3f2",
            color: "#b42318",
            fontSize: "14px"
          }}>
            {error}
          </div>
        )}

        {success && (
          <div style={{
            marginBottom: "18px",
            padding: "12px 14px",
            borderRadius: "8px",
            background: "#ecfdf3",
            color: "#027a48",
            fontSize: "14px"
          }}>
            {success}
          </div>
        )}

        <form onSubmit={handleRegister}>
          <label htmlFor="firstName">First Name</label>
          <input
            id="firstName"
            name="firstName"
            value={form.firstName}
            onChange={handleChange}
            placeholder="Enter first name"
            required
            maxLength={100}
            disabled={loading}
          />

          <label htmlFor="lastName">Last Name</label>
          <input
            id="lastName"
            name="lastName"
            value={form.lastName}
            onChange={handleChange}
            placeholder="Enter last name"
            required
            maxLength={100}
            disabled={loading}
          />

          <label htmlFor="registerEmail">Email</label>
          <input
            id="registerEmail"
            name="email"
            type="email"
            value={form.email}
            onChange={handleChange}
            placeholder="Enter email"
            required
            maxLength={150}
            disabled={loading}
          />

          <label htmlFor="registerPassword">Password</label>
          <input
            id="registerPassword"
            name="password"
            type="password"
            value={form.password}
            onChange={handleChange}
            placeholder="Create password (8–72 characters)"
            required
            minLength={8}
            maxLength={72}
            autoComplete="new-password"
            disabled={loading}
          />

          <label htmlFor="confirmPassword">Confirm Password</label>
          <input
            id="confirmPassword"
            type="password"
            value={confirmPassword}
            onChange={(event) => setConfirmPassword(event.target.value)}
            placeholder="Re-enter password"
            required
            autoComplete="new-password"
            disabled={loading}
          />

          <button
            type="submit"
            className="button primary full"
            disabled={loading}
          >
            {loading ? "Creating account..." : "Create Account"}
          </button>
        </form>

        <p style={{ marginTop: "20px", textAlign: "center" }}>
          Already have an account?{" "}
          <Link to="/login">Sign in</Link>
        </p>

        <Link to="/" className="back-link">
          ← Back to Home
        </Link>
      </div>
    </div>
  );
}

export default Register;
