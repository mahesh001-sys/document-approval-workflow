import { Navigate, useLocation } from "react-router-dom";
import authService from "../api/authService";

function ProtectedRoute({ children, allowedRoles = [] }) {
  const location = useLocation();

  if (!authService.isAuthenticated()) {
    return (
      <Navigate
        to="/login"
        replace
        state={{ from: location }}
      />
    );
  }

  const user = authService.getUser();
  const rawRole =
    typeof user?.role === "string"
      ? user.role
      : user?.role?.name;

  const role = rawRole
    ?.trim()
    .replace(/^ROLE_/, "")
    .toUpperCase();

  const homePath =
    role === "ADMIN"
      ? "/admin"
      : role === "MANAGER"
        ? "/manager"
        : role === "USER"
          ? "/dashboard"
          : "/login";

  if (!role) {
    authService.logout();
    return <Navigate to="/login" replace />;
  }

  if (allowedRoles.length > 0 && !allowedRoles.includes(role)) {
    return <Navigate to={homePath} replace />;
  }

  return children;
}

export default ProtectedRoute;
