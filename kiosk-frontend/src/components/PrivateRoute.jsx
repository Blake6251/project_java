import { Navigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth.js";

/**
 * 로그인 필수. requireAdmin 이면 ADMIN 만 허용, USER 는 /order 로 돌림
 */
export default function PrivateRoute({ children, requireAdmin }) {
  const { isAuthenticated, isAdmin } = useAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  if (requireAdmin && !isAdmin) {
    return <Navigate to="/order" replace />;
  }

  if (!requireAdmin && isAdmin) {
    // USER 전용 페이지에 ADMIN 이 들어온 경우 대시보드로 (선택 동작)
    return <Navigate to="/dashboard" replace />;
  }

  return children;
}
