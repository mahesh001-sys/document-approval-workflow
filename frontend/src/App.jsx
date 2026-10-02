import React, { useState } from "react";
import { Link, Route, Routes, useNavigate } from "react-router-dom";
import authService from "./api/authService";

function Home() {
  return (
    <div className="page">
      <section className="hero">
        <div className="hero-content">
          <span className="badge">
            DOCUMENT APPROVAL WORKFLOW
          </span>

          <h1>
            Manage approvals
            <br />
            <span>with confidence.</span>
          </h1>

          <p>
            A secure, role-based platform for creating,
            reviewing, approving and tracking business requests.
          </p>

          <div className="hero-actions">
            <Link to="/login" className="button primary">
              Get Started
            </Link>

            <a href="#features" className="button secondary">
              Explore Features
            </a>
          </div>
        </div>
      </section>

      <section id="features" className="features">
        <div className="section-heading">
          <span className="section-label">
            CORE CAPABILITIES
          </span>

          <h2>Built for structured approvals</h2>

          <p>
            The platform provides a controlled workflow from
            request creation to final approval.
          </p>
        </div>

        <div className="feature-grid">
          <div className="feature-card">
            <div className="feature-icon">🔐</div>

            <h3>Secure Authentication</h3>

            <p>
              JWT-based authentication with Spring Security
              and role-based access control.
            </p>
          </div>

          <div className="feature-card">
            <div className="feature-icon">🔄</div>

            <h3>Multi-Level Approval</h3>

            <p>
              Requests move through Employee, Manager and
              Admin approval stages.
            </p>
          </div>

          <div className="feature-card">
            <div className="feature-icon">📋</div>

            <h3>Complete Tracking</h3>

            <p>
              Track request status, approval history,
              audit activity and workflow progress.
            </p>
          </div>
        </div>
      </section>

      <section className="workflow">
        <div className="section-heading">
          <span className="section-label">
            WORKFLOW
          </span>

          <h2>From request to completion</h2>
        </div>

        <div className="workflow-steps">
          <div className="workflow-step">
            <span>01</span>
            <h3>Create</h3>
            <p>
              Employee creates a business request.
            </p>
          </div>

          <div className="workflow-step">
            <span>02</span>
            <h3>Review</h3>
            <p>
              Manager reviews the submitted request.
            </p>
          </div>

          <div className="workflow-step">
            <span>03</span>
            <h3>Approve</h3>
            <p>
              Admin provides final approval.
            </p>
          </div>

          <div className="workflow-step">
            <span>04</span>
            <h3>Complete</h3>
            <p>
              The approved request is completed.
            </p>
          </div>
        </div>
      </section>

      <footer className="footer">
        <p>
          © 2026 Document Approval Workflow ·
          Built by Mahesh Kumar
        </p>
      </footer>
    </div>
  );
}

function Login() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleLogin = async (event) => {
    event.preventDefault();

    setError("");

    if (!email.trim() || !password.trim()) {
      setError("Please enter your email and password.");
      return;
    }

    try {
      setLoading(true);

      await authService.login(
        email.trim(),
        password
      );

      navigate("/dashboard");
    } catch (err) {
      console.error("Login failed:", err);

      if (err.response?.status === 401) {
        setError("Invalid email or password.");
      } else if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError(
          "Unable to connect to the server. Please try again."
        );
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <span className="section-label">
          SECURE ACCESS
        </span>

        <h1>Welcome back</h1>

        <p>
          Sign in to access your Document Approval Workflow
          dashboard.
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

        <form onSubmit={handleLogin}>
          <label htmlFor="email">
            Email
          </label>

          <input
            id="email"
            type="email"
            value={email}
            onChange={(event) =>
              setEmail(event.target.value)
            }
            placeholder="Enter your email"
            autoComplete="email"
            disabled={loading}
          />

          <label htmlFor="password">
            Password
          </label>

          <input
            id="password"
            type="password"
            value={password}
            onChange={(event) =>
              setPassword(event.target.value)
            }
            placeholder="Enter your password"
            autoComplete="current-password"
            disabled={loading}
          />

          <button
            type="submit"
            className="button primary full"
            disabled={loading}
          >
            {loading ? "Signing in..." : "Sign In"}
          </button>
        </form>

        <Link to="/" className="back-link">
          ← Back to Home
        </Link>
      </div>
    </div>
  );
}

function Dashboard() {
  const navigate = useNavigate();

  const user = authService.getUser();

  const handleLogout = () => {
    authService.logout();
    navigate("/login");
  };

  return (
    <div className="auth-page">
      <div className="auth-card">
        <span className="section-label">
          DASHBOARD
        </span>

        <h1>Welcome!</h1>

        <p>
          You are successfully authenticated.
        </p>

        {user && (
          <div
            style={{
              marginTop: "20px",
              padding: "18px",
              borderRadius: "12px",
              background: "#f7f9fc",
              border: "1px solid #eaecf0"
            }}
          >
            <strong>User information</strong>

            <p
              style={{
                marginTop: "10px",
                color: "#667085"
              }}
            >
              {user.email || "Authenticated user"}
            </p>
          </div>
        )}

        <button
          type="button"
          className="button primary full"
          onClick={handleLogout}
          style={{ marginTop: "24px" }}
        >
          Logout
        </button>
      </div>
    </div>
  );
}

function App() {
  return (
    <Routes>
      <Route
        path="/"
        element={<Home />}
      />

      <Route
        path="/login"
        element={<Login />}
      />

      <Route
        path="/dashboard"
        element={<Dashboard />}
      />
    </Routes>
  );
}

export default App;
