import { useState } from "react";
import {
  Link,
  Navigate,
  Route,
  Routes,
  useNavigate
} from "react-router-dom";

import authService from "./api/authService";
import ProtectedRoute from "./components/ProtectedRoute";
import EmployeeDashboard from "./components/EmployeeDashboard";
import ManagerDashboard from "./components/ManagerDashboard";
import AdminDashboard from "./components/AdminDashboard";
import CreateRequest from "./components/CreateRequest";
import RequestDetails from "./components/RequestDetails";
import EditRequest from "./components/EditRequest";
import Register from "./components/Register";

function Home() {
  return (
    <div className="landing-page">
      <header className="landing-navbar">
        <Link to="/" className="landing-brand" aria-label="DocumentFlow home">
          <span className="landing-brand-mark">D</span>
          <span>
            <strong>DocumentFlow</strong>
            <small>APPROVAL MANAGEMENT</small>
          </span>
        </Link>

        <nav className="landing-nav-links" aria-label="Main navigation">
          <a href="#features">Features</a>
          <a href="#workflow">How it works</a>
        </nav>

        <div className="landing-nav-actions">
          <Link to="/login" className="landing-signin">
            Sign in
          </Link>
          <Link to="/login" className="landing-nav-cta">
            Get started <span aria-hidden="true">→</span>
          </Link>
        </div>
      </header>

      <main>
        <section className="landing-hero">
          <div className="landing-hero-copy">
            <div className="landing-eyebrow">
              <span className="landing-eyebrow-dot" />
              SMARTER WORKFLOWS. CLEARER DECISIONS.
            </div>

            <h1>
              Approvals made
              <span> simple.</span>
              <br />
              Work made
              <span> faster.</span>
            </h1>

            <p className="landing-hero-description">
              Bring every business request into one secure workspace.
              Create, review, approve and track each step with clarity
              and confidence.
            </p>

            <div className="landing-hero-actions">
              <Link to="/login" className="landing-primary-button">
                Access your workspace <span aria-hidden="true">→</span>
              </Link>
              <a href="#workflow" className="landing-secondary-button">
                Explore the workflow
              </a>
            </div>

            <div className="landing-trust-note">
              <span className="landing-check">✓</span>
              Role-based access
              <span className="landing-note-divider">·</span>
              <span className="landing-check">✓</span>
              Secure approval process
            </div>
          </div>

          <div className="landing-hero-visual">
            <div className="landing-visual-glow" />

            <div className="landing-preview-card">
              <div className="landing-preview-topbar">
                <div>
                  <span className="landing-preview-kicker">WORKSPACE OVERVIEW</span>
                  <h2>Approval center</h2>
                </div>
                <span className="landing-preview-menu">•••</span>
              </div>

              <div className="landing-preview-summary">
                <div className="landing-summary-item">
                  <span className="landing-summary-icon blue">▤</span>
                  <div>
                    <strong>Requests</strong>
                    <small>Centralized workspace</small>
                  </div>
                </div>
                <div className="landing-summary-item">
                  <span className="landing-summary-icon teal">✓</span>
                  <div>
                    <strong>Approvals</strong>
                    <small>Structured decisions</small>
                  </div>
                </div>
              </div>

              <div className="landing-preview-divider" />

              <div className="landing-preview-section-heading">
                <strong>Recent activity</strong>
                <span>WORKFLOW PREVIEW</span>
              </div>

              <div className="landing-request-row">
                <span className="landing-request-icon">📄</span>
                <div className="landing-request-info">
                  <strong>Business request</strong>
                  <small>Employee submission</small>
                </div>
                <span className="landing-status pending">In review</span>
              </div>

              <div className="landing-request-row">
                <span className="landing-request-icon">📋</span>
                <div className="landing-request-info">
                  <strong>Department request</strong>
                  <small>Approval completed</small>
                </div>
                <span className="landing-status approved">Approved</span>
              </div>

              <div className="landing-preview-progress">
                <div className="landing-progress-heading">
                  <strong>Approval journey</strong>
                  <span>4 stages</span>
                </div>
                <div className="landing-progress-track">
                  <span />
                  <span />
                  <span />
                  <span />
                </div>
                <div className="landing-progress-labels">
                  <span>Employee</span>
                  <span>Manager</span>
                  <span>Admin</span>
                  <span>Done</span>
                </div>
              </div>
            </div>

            <div className="landing-floating-note">
              <span className="landing-floating-check">✓</span>
              <span>
                <strong>Clear accountability</strong>
                <small>Every step has an owner</small>
              </span>
            </div>
          </div>
        </section>

        <section className="landing-value-strip" aria-label="Platform benefits">
          <div>
            <span className="landing-value-icon">⌘</span>
            <span><strong>One workspace</strong><small>All requests in one place</small></span>
          </div>
          <div>
            <span className="landing-value-icon">♧</span>
            <span><strong>Role-based access</strong><small>Clear responsibilities</small></span>
          </div>
          <div>
            <span className="landing-value-icon">↗</span>
            <span><strong>Full visibility</strong><small>Follow every decision</small></span>
          </div>
        </section>

        <section id="features" className="landing-features">
          <div className="landing-section-heading">
            <span className="landing-section-eyebrow">BUILT FOR BETTER OPERATIONS</span>
            <h2>Everything your approval process needs.</h2>
            <p>
              A structured approach to business requests, designed to keep
              teams aligned and decisions moving.
            </p>
          </div>

          <div className="landing-feature-grid">
            <article className="landing-feature-card">
              <span className="landing-feature-icon icon-blue">⌑</span>
              <h3>Secure access</h3>
              <p>
                Protected sign-in and role-based permissions help ensure
                users access the right workspace.
              </p>
              <span className="landing-feature-number">01</span>
            </article>

            <article className="landing-feature-card">
              <span className="landing-feature-icon icon-indigo">⇄</span>
              <h3>Multi-level approvals</h3>
              <p>
                Move requests through employee, manager and administrator
                review in a structured sequence.
              </p>
              <span className="landing-feature-number">02</span>
            </article>

            <article className="landing-feature-card">
              <span className="landing-feature-icon icon-teal">◷</span>
              <h3>Request tracking</h3>
              <p>
                Follow request status and understand where each item stands
                throughout its approval journey.
              </p>
              <span className="landing-feature-number">03</span>
            </article>

            <article className="landing-feature-card">
              <span className="landing-feature-icon icon-navy">▤</span>
              <h3>Approval history</h3>
              <p>
                Keep decisions and workflow activity visible for a clearer
                record of request handling.
              </p>
              <span className="landing-feature-number">04</span>
            </article>
          </div>
        </section>

        <section id="workflow" className="landing-workflow">
          <div className="landing-section-heading">
            <span className="landing-section-eyebrow">A CLEAR PATH FOR EVERY REQUEST</span>
            <h2>From submission to completion.</h2>
            <p>
              Each request follows a defined approval journey, with
              responsibilities at every stage.
            </p>
          </div>

          <div className="landing-workflow-grid">
            <article className="landing-workflow-step">
              <span className="landing-step-number">01</span>
              <span className="landing-step-icon">＋</span>
              <h3>Create</h3>
              <p>Employees create and submit a business request.</p>
            </article>

            <article className="landing-workflow-step">
              <span className="landing-step-number">02</span>
              <span className="landing-step-icon">⌕</span>
              <h3>Manager review</h3>
              <p>Managers review the details and make a decision.</p>
            </article>

            <article className="landing-workflow-step">
              <span className="landing-step-number">03</span>
              <span className="landing-step-icon">✓</span>
              <h3>Admin approval</h3>
              <p>Administrators handle the final approval stage.</p>
            </article>

            <article className="landing-workflow-step">
              <span className="landing-step-number">04</span>
              <span className="landing-step-icon">↗</span>
              <h3>Completion</h3>
              <p>The request reaches its final workflow status.</p>
            </article>
          </div>
        </section>

        <section className="landing-cta-section">
          <div className="landing-cta-content">
            <span className="landing-section-eyebrow">READY TO GET STARTED?</span>
            <h2>Bring clarity to every approval.</h2>
            <p>
              Sign in to your workspace and manage requests through
              one organized approval process.
            </p>
            <Link to="/login" className="landing-cta-button">
              Go to your workspace <span aria-hidden="true">→</span>
            </Link>
          </div>
          <div className="landing-cta-decoration" aria-hidden="true">
            <span>✓</span>
          </div>
        </section>
      </main>

      <footer className="landing-footer">
        <Link to="/" className="landing-footer-brand">
          <span className="landing-brand-mark">D</span>
          <strong>DocumentFlow</strong>
        </Link>
        <p>© 2026 Document Approval Workflow. Built by Mahesh Kumar.</p>
        <Link to="/login">Sign in <span aria-hidden="true">→</span></Link>
      </footer>
    </div>
  );
}


function Login() {
  return (
    <main className="portal-selection-page">
      <div className="portal-selection-container">
        <Link to="/" className="portal-selection-brand">
          <span className="auth-brand-symbol">D</span>
          <span>
            <strong>DocumentFlow</strong>
            <small>APPROVAL MANAGEMENT</small>
          </span>
        </Link>

        <div className="portal-selection-heading">
          <span>SECURE WORKSPACE ACCESS</span>
          <h1>Welcome to DocumentFlow</h1>
          <p>Select your workspace to continue securely.</p>
        </div>

        <div className="portal-selection-grid">
          <Link to="/employee-login" className="portal-card employee-portal">
            <span className="portal-card-icon">♙</span>
            <span className="portal-card-label">EMPLOYEE WORKSPACE</span>
            <strong>Employee</strong>
            <span className="portal-card-description">
              Create requests, track progress and manage submissions.
            </span>
            <span className="portal-card-action">
              Continue as Employee <span>→</span>
            </span>
          </Link>

          <Link to="/manager-login" className="portal-card manager-portal">
            <span className="portal-card-icon">♧</span>
            <span className="portal-card-label">MANAGEMENT WORKSPACE</span>
            <strong>Manager</strong>
            <span className="portal-card-description">
              Review employee requests and manage approval decisions.
            </span>
            <span className="portal-card-action">
              Continue as Manager <span>→</span>
            </span>
          </Link>

          <Link to="/admin-login" className="portal-card admin-portal">
            <span className="portal-card-icon">⬡</span>
            <span className="portal-card-label">ADMINISTRATION</span>
            <strong>Administrator</strong>
            <span className="portal-card-description">
              Manage the platform, users and final approval workflows.
            </span>
            <span className="portal-card-action">
              Continue as Admin <span>→</span>
            </span>
          </Link>
        </div>

        <p className="portal-selection-footer">
          © 2026 DocumentFlow. Secure access for every role.
        </p>

        <Link to="/" className="portal-selection-home">
          ← Back to homepage
        </Link>
      </div>
    </main>
  );
}

function RoleLogin({ expectedRole }) {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const portalConfig = {
    USER: {
      title: "Employee",
      subtitle: "Access your employee workspace.",
      eyebrow: "EMPLOYEE PORTAL",
      description: "Submit requests, follow their progress and stay informed.",
      destination: "/dashboard",
      theme: "employee"
    },
    MANAGER: {
      title: "Manager",
      subtitle: "Access your management workspace.",
      eyebrow: "MANAGER PORTAL",
      description: "Review submissions and make informed approval decisions.",
      destination: "/manager",
      theme: "manager"
    },
    ADMIN: {
      title: "Administrator",
      subtitle: "Access your administration workspace.",
      eyebrow: "ADMINISTRATOR PORTAL",
      description: "Oversee users, requests and final approval operations.",
      destination: "/admin",
      theme: "admin"
    }
  };

  const config = portalConfig[expectedRole];

  const handleLogin = async (event) => {
    event.preventDefault();
    setError("");

    if (!email.trim() || !password) {
      setError("Please enter your email and password.");
      return;
    }

    try {
      setLoading(true);

      const data = await authService.login(email.trim(), password);

      const rawRole =
        data?.user?.role ||
        data?.role ||
        authService.getUser()?.role;

      const actualRole = (
        typeof rawRole === "string"
          ? rawRole
          : rawRole?.name || ""
      )
        .trim()
        .replace(/^ROLE_/, "")
        .toUpperCase();

      if (actualRole !== expectedRole) {
        authService.logout();
        setError(
          `This account does not have ${config.title} access. Please use the correct login portal.`
        );
        return;
      }

      navigate(config.destination);
    } catch (err) {
      console.error("Login failed:", err);

      if (err.response?.status === 401) {
        setError("Invalid email or password.");
      } else if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError("Unable to connect to the server. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className={`auth-page auth-redesign auth-portal auth-portal-${config.theme}`}>
      <div className="auth-layout">
        <section className="auth-showcase">
          <Link to="/" className="auth-brand">
            <span className="auth-brand-symbol">D</span>
            <span>
              <strong>DocumentFlow</strong>
              <small>APPROVAL MANAGEMENT</small>
            </span>
          </Link>

          <div className="auth-showcase-content">
            <span className="auth-showcase-label">
              {config.eyebrow}
            </span>

            <h1>
              Work smarter.
              <br />
              <span>Move decisions forward.</span>
            </h1>

            <p>{config.description}</p>

            <div className="auth-visual">
              <div className="auth-visual-heading">
                <div>
                  <small>YOUR WORKSPACE</small>
                  <h3>{config.title} access</h3>
                </div>
                <span className="auth-visual-badge">SECURE ACCESS</span>
              </div>

              <div className="auth-journey">
                <div className="auth-journey-step">
                  <span className="auth-journey-icon">01</span>
                  <span>
                    <strong>Secure sign in</strong>
                    <small>Verified credentials</small>
                  </span>
                  <span className="auth-journey-check">✓</span>
                </div>

                <div className="auth-journey-line" />

                <div className="auth-journey-step">
                  <span className="auth-journey-icon">02</span>
                  <span>
                    <strong>Role verification</strong>
                    <small>Authorized workspace</small>
                  </span>
                  <span className="auth-journey-check">✓</span>
                </div>

                <div className="auth-journey-line" />

                <div className="auth-journey-step">
                  <span className="auth-journey-icon">03</span>
                  <span>
                    <strong>Workspace access</strong>
                    <small>{config.title} dashboard</small>
                  </span>
                  <span className="auth-journey-pending">•••</span>
                </div>
              </div>

              <div className="auth-visual-footer">
                <span className="auth-footer-indicator" />
                Secure access. Clear responsibilities.
              </div>
            </div>
          </div>

          <div className="auth-showcase-footer">
            <span>SECURE</span>
            <span>·</span>
            <span>ROLE-BASED</span>
            <span>·</span>
            <span>TRANSPARENT</span>
          </div>
        </section>

        <section className="auth-form-section">
          <div className="auth-form-container">
            <Link to="/" className="auth-mobile-brand">
              <span className="auth-brand-symbol">D</span>
              <strong>DocumentFlow</strong>
            </Link>

            <Link to="/login" className="auth-portal-back">
              ← All login portals
            </Link>

            <div className="auth-form-heading">
              <span className="auth-form-eyebrow">
                {config.eyebrow}
              </span>
              <h2>Sign in as {config.title}</h2>
              <p>{config.subtitle}</p>
            </div>

            {error && (
              <div className="auth-error" role="alert">
                <span className="auth-error-icon">!</span>
                <span>{error}</span>
              </div>
            )}

            <form className="auth-login-form" onSubmit={handleLogin}>
              <div className="auth-field">
                <label htmlFor={`${config.theme}-email`}>Email address</label>
                <div className="auth-input-wrap">
                  <span className="auth-input-icon" aria-hidden="true">✉</span>
                  <input
                    id={`${config.theme}-email`}
                    type="email"
                    value={email}
                    onChange={(event) => setEmail(event.target.value)}
                    placeholder="you@example.com"
                    autoComplete="username"
                    disabled={loading}
                    required
                  />
                </div>
              </div>

              <div className="auth-field">
                <label htmlFor={`${config.theme}-password`}>Password</label>
                <div className="auth-input-wrap">
                  <span className="auth-input-icon" aria-hidden="true">◈</span>
                  <input
                    id={`${config.theme}-password`}
                    type={showPassword ? "text" : "password"}
                    value={password}
                    onChange={(event) => setPassword(event.target.value)}
                    placeholder="Enter your password"
                    autoComplete="current-password"
                    disabled={loading}
                    required
                  />
                  <button
                    type="button"
                    className="auth-password-toggle"
                    onClick={() => setShowPassword((current) => !current)}
                    aria-label={showPassword ? "Hide password" : "Show password"}
                    disabled={loading}
                  >
                    {showPassword ? "Hide" : "Show"}
                  </button>
                </div>
              </div>

              <button
                type="submit"
                className="auth-submit-button"
                disabled={loading}
              >
                {loading ? (
                  <>
                    <span className="auth-spinner" />
                    Signing in...
                  </>
                ) : (
                  <>
                    Sign in to {config.title} portal
                    <span aria-hidden="true">→</span>
                  </>
                )}
              </button>
            </form>

            <div className="auth-form-divider">
              <span />
              <small>SECURE WORKSPACE ACCESS</small>
              <span />
            </div>

            <p className="auth-register-prompt">
              Don&apos;t have an account?
              <Link to="/register">Create an account</Link>
            </p>

            <Link to="/" className="auth-home-link">
              ← Back to homepage
            </Link>

            <p className="auth-legal-note">
              © 2026 DocumentFlow. All rights reserved.
            </p>
          </div>
        </section>
      </div>
    </main>
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
        path="/employee-login"
        element={<RoleLogin expectedRole="USER" />}
      />

      <Route
        path="/manager-login"
        element={<RoleLogin expectedRole="MANAGER" />}
      />

      <Route
        path="/admin-login"
        element={<RoleLogin expectedRole="ADMIN" />}
      />

      <Route
        path="/register"
        element={<Register />}
      />

      <Route
        path="/dashboard"
        element={
          <ProtectedRoute allowedRoles={["USER"]}>
            <EmployeeDashboard />
          </ProtectedRoute>
        }
      />

      <Route
        path="/requests/new"
        element={
          <ProtectedRoute allowedRoles={["USER"]}>
            <CreateRequest />
          </ProtectedRoute>
        }
      />

      <Route
        path="/requests/:id"
        element={
          <ProtectedRoute allowedRoles={["USER", "MANAGER", "ADMIN"]}>
            <RequestDetails />
          </ProtectedRoute>
        }
      />

      <Route
        path="/requests/:id/edit"
        element={
          <ProtectedRoute allowedRoles={["USER"]}>
            <EditRequest />
          </ProtectedRoute>
        }
      />

      <Route
        path="/manager"
        element={
          <ProtectedRoute allowedRoles={["MANAGER"]}>
            <ManagerDashboard />
          </ProtectedRoute>
        }
      />

      <Route
        path="/admin"
        element={
          <ProtectedRoute allowedRoles={["ADMIN"]}>
            <AdminDashboard />
          </ProtectedRoute>
        }
      />

      <Route
        path="*"
        element={
          <Navigate
            to="/"
            replace
          />
        }
      />
    </Routes>
  );
}

export default App;
