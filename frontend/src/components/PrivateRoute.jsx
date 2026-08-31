import { Navigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth.js";

export default function PrivateRoute({ children, requireAdmin }) {
  const { isAuthenticated, isAdmin } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (requireAdmin && !isAdmin) {
    return <Navigate to="/shop" replace />;
  }

  if (!requireAdmin && isAdmin) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
}
