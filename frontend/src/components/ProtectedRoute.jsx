import React from "react";
import { Navigate, useLocation } from "react-router-dom";
import authService from "../api/authService";

function ProtectedRoute({ children }) {
  const location = useLocation();

  const authenticated = authService.isAuthenticated();

  if (!authenticated) {
    return (
      <Navigate
        to="/login"
        replace
        state={{ from: location }}
      />
    );
  }

  return children;
}

export default ProtectedRoute;
