import { useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth.js";
import { useAuthStore } from "../store/authStore.js";
import { useNotificationStore } from "../store/notificationStore.js";
import api from "../api/axios.js";

export default function Navbar() {
  const { isAuthenticated, isAdmin, username } = useAuth();
  const logoutLocal = useAuthStore((s) => s.logoutLocal);
  const unreadCount = useNotificationStore((s) => s.unreadCount);
  const navigate = useNavigate();
  const fetchUnread = useNotificationStore((s) => s.fetchUnread);

  useEffect(() => {
    if (isAuthenticated && isAdmin) {
      fetchUnread();
      const t = setInterval(fetchUnread, 30000);
      return () => clearInterval(t);
    }
    return undefined;
  }, [isAuthenticated, isAdmin, fetchUnread]);

  const handleLogout = async () => {
    try {
      await api.post("/api/auth/logout");
    } catch {
      /* 서버 실패해도 로컬 토큰은 제거 */
    }
    logoutLocal();
    navigate("/login");
  };

  if (!isAuthenticated) return null;

  return (
    <nav
      style={{
        display: "flex",
        gap: 16,
        alignItems: "center",
        padding: "12px 20px",
        borderBottom: "1px solid #ddd",
        marginBottom: 16
      }}
    >
      <strong>{username}</strong>
      {isAdmin ? (
        <>
          <Link to="/dashboard">대시보드</Link>
          <Link to="/notifications">
            알림 {unreadCount > 0 ? `(${unreadCount})` : ""}
          </Link>
        </>
      ) : (
        <Link to="/order">주문</Link>
      )}
      <button type="button" onClick={handleLogout} style={{ marginLeft: "auto" }}>
        로그아웃
      </button>
    </nav>
  );
}
