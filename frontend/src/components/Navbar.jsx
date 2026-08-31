import { useEffect } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth.js";
import { useAuthStore } from "../store/authStore.js";
import { useNotificationStore } from "../store/notificationStore.js";
import api from "../api/axios.js";
import { APP_META } from "../data/demo.js";

export default function Navbar() {
  const { isAuthenticated, isAdmin, username } = useAuth();
  const logoutLocal = useAuthStore((s) => s.logoutLocal);
  const unreadCount = useNotificationStore((s) => s.unreadCount);
  const fetchUnread = useNotificationStore((s) => s.fetchUnread);
  const navigate = useNavigate();

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
      /* ignore */
    }
    logoutLocal();
    navigate("/");
  };

  return (
    <nav className="site-nav">
      <Link to="/" className="brand">
        <img src="/logo.png" alt="COSTOOL" style={{ height: 32, width: "auto", display: "block" }} />
      </Link>

      <div className="nav-links">
        <NavLink to="/products">상품</NavLink>
        <NavLink to="/reservations">예약</NavLink>
        <NavLink to="/roadmap">개발현황</NavLink>
        {isAuthenticated && !isAdmin && <NavLink to="/shop">내 주문</NavLink>}
        {isAuthenticated && isAdmin && (
          <>
            <NavLink to="/dashboard">관리자</NavLink>
            <NavLink to="/notifications">알림{unreadCount > 0 ? ` (${unreadCount})` : ""}</NavLink>
          </>
        )}
      </div>

      <div className="nav-spacer" />

      {isAuthenticated ? (
        <>
          <span className="badge">{username}</span>
          <button type="button" className="btn btn-secondary" onClick={handleLogout}>
            로그아웃
          </button>
        </>
      ) : (
        <>
          <Link to="/login" className="btn btn-ghost">
            로그인
          </Link>
          <Link to="/register" className="btn btn-primary">
            시작하기
          </Link>
        </>
      )}
    </nav>
  );
}
