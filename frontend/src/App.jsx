import React from "react";
import { Link, Route, Routes } from "react-router-dom";

function Home() {
  return (
    <div className="page">
      <section className="hero">
        <div className="hero-content">
          <span className="badge">DOCUMENT APPROVAL WORKFLOW</span>

          <h1>
            Manage approvals
            <br />
            <span>with confidence.</span>
          </h1>

          <p>
            A secure, role-based platform for creating, reviewing,
            approving and tracking business requests.
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
          <span className="section-label">CORE CAPABILITIES</span>
          <h2>Built for structured approvals</h2>
          <p>
            The platform provides a controlled workflow from request
            creation to final approval.
          </p>
        </div>

        <div className="feature-grid">
          <div className="feature-card">
            <div className="feature-icon">🔐</div>
            <h3>Secure Authentication</h3>
            <p>
              JWT-based authentication with Spring Security and
              role-based access control.
            </p>
          </div>

          <div className="feature-card">
            <div className="feature-icon">🔄</div>
            <h3>Multi-Level Approval</h3>
            <p>
              Requests move through Employee, Manager and Admin
              approval stages.
            </p>
          </div>

          <div className="feature-card">
            <div className="feature-icon">📋</div>
            <h3>Complete Tracking</h3>
            <p>
              Track request status, approval history, audit activity
              and workflow progress.
            </p>
          </div>
        </div>
      </section>

      <section className="workflow">
        <div className="section-heading">
          <span className="section-label">WORKFLOW</span>
          <h2>From request to completion</h2>
        </div>

        <div className="workflow-steps">
          <div className="workflow-step">
            <span>01</span>
            <h3>Create</h3>
            <p>Employee creates a business request.</p>
          </div>

          <div className="workflow-step">
            <span>02</span>
            <h3>Review</h3>
            <p>Manager reviews the submitted request.</p>
          </div>

          <div className="workflow-step">
            <span>03</span>
            <h3>Approve</h3>
            <p>Admin provides final approval.</p>
          </div>

          <div className="workflow-step">
            <span>04</span>
            <h3>Complete</h3>
            <p>The approved request is completed.</p>
          </div>
        </div>
      </section>

      <footer className="footer">
        <p>
          © 2026 Document Approval Workflow · Built by Mahesh Kumar
        </p>
      </footer>
    </div>
  );
}

function Login() {
  return (
    <div className="auth-page">
      <div className="auth-card">
        <span className="section-label">SECURE ACCESS</span>

        <h1>Welcome back</h1>

        <p>
          Sign in to access your Document Approval Workflow
          dashboard.
        </p>

        <form>
          <label htmlFor="email">Email</label>
          <input
            id="email"
            type="email"
            placeholder="Enter your email"
          />

          <label htmlFor="password">Password</label>
          <input
            id="password"
            type="password"
            placeholder="Enter your password"
          />

          <button type="button" className="button primary full">
            Sign In
          </button>
        </form>

        <Link to="/" className="back-link">
          ← Back to Home
        </Link>
      </div>
    </div>
  );
}

function App() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
    </Routes>
  );
}

export default App;
