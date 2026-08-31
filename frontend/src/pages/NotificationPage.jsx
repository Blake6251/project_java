import { useEffect, useState } from "react";
import api from "../api/axios.js";
import { useNotificationStore } from "../store/notificationStore.js";

export default function NotificationPage() {
  const fetchNotifications = useNotificationStore((s) => s.fetchNotifications);
  const fetchUnread = useNotificationStore((s) => s.fetchUnread);
  const markReadLocal = useNotificationStore((s) => s.markReadLocal);
  const notifications = useNotificationStore((s) => s.notifications);
  const totalPages = useNotificationStore((s) => s.totalPages);
  const unreadCount = useNotificationStore((s) => s.unreadCount);
  const loading = useNotificationStore((s) => s.loading);
  const error = useNotificationStore((s) => s.error);
  const [page, setPage] = useState(0);

  useEffect(() => {
    fetchNotifications(page);
    fetchUnread();
  }, [page, fetchNotifications, fetchUnread]);

  const markRead = async (id) => {
    try {
      await api.patch(`/api/notifications/${id}/read`);
      markReadLocal(id);
    } catch (e) {
      alert(e.response?.data?.message ?? "읽음 처리 실패");
    }
  };

  return (
    <div className="page">
      <section className="section" style={{ marginTop: "1rem" }}>
        <h2>알림</h2>
        <p className="lead">주문 이벤트 알림 · 미확인 {unreadCount}건</p>

        {loading && <p className="muted">로딩…</p>}
        {error && <p className="error-text">{error}</p>}

        <div className="progress-list">
          {notifications.map((n) => {
            const read = n.isRead ?? n.read;
            return (
              <article key={n.id} className="panel" style={{ opacity: read ? 0.72 : 1 }}>
                <div style={{ display: "flex", justifyContent: "space-between", gap: 12 }}>
                  <div>
                    <span className="badge">{n.type}</span>
                    <h3 style={{ marginTop: 8 }}>주문 #{n.orderId}</h3>
                    <p>{n.message}</p>
                    <div className="muted" style={{ marginTop: 6, fontSize: "0.85rem" }}>
                      {n.createdAt}
                    </div>
                  </div>
                  {!read && (
                    <button type="button" className="btn btn-secondary" onClick={() => markRead(n.id)}>
                      읽음
                    </button>
                  )}
                </div>
              </article>
            );
          })}
        </div>

        <div className="cta-row" style={{ marginTop: 12 }}>
          <button
            type="button"
            className="btn btn-secondary"
            disabled={page <= 0}
            onClick={() => setPage((p) => p - 1)}
          >
            이전
          </button>
          <span className="muted">
            {page + 1} / {Math.max(totalPages, 1)}
          </span>
          <button
            type="button"
            className="btn btn-secondary"
            disabled={page >= totalPages - 1}
            onClick={() => setPage((p) => p + 1)}
          >
            다음
          </button>
        </div>
      </section>
    </div>
  );
}
