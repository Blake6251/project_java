import { useEffect } from "react";
import { Link, NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../hooks/useAuth.js";
import { useAuthStore } from "../store/authStore.js";
import { useNotificationStore } from "../store/notificationStore.js";
import api from "../api/axios.js";
import { COMPANY } from "../data/demo.js";

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
        <img src="/logo.png" alt="COSTOOL" />
      </Link>

      <div className="nav-links">
        <NavLink to="/products">상품</NavLink>
        <NavLink to="/reservations">상담예약</NavLink>
        <a href="/#contact">문의</a>
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
          <a href={COMPANY.phoneHref} className="btn btn-ghost">
            {COMPANY.phone}
          </a>
          <Link to="/products" className="btn btn-primary">
            카탈로그
          </Link>
        </>
      )}
    </nav>
  );
}
