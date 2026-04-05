import { useEffect, useState } from "react";
import api from "../api/axios.js";
import { useNotificationStore } from "../store/notificationStore.js";

export default function NotificationPage() {
  const fetchNotifications = useNotificationStore((s) => s.fetchNotifications);
  const fetchUnread = useNotificationStore((s) => s.fetchUnread);
  const markReadLocal = useNotificationStore((s) => s.markReadLocal);
  const notifications = useNotificationStore((s) => s.notifications);
  const totalPages = useNotificationStore((s) => s.totalPages);
  const currentPage = useNotificationStore((s) => s.currentPage);
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
    <div style={{ maxWidth: 720, margin: "0 auto", padding: 16 }}>
      <h1>알림</h1>
      <p>미확인: {unreadCount}건</p>
      {loading && <p>로딩…</p>}
      {error && <p style={{ color: "crimson" }}>{error}</p>}
      <ul style={{ listStyle: "none", padding: 0 }}>
        {notifications.map((n) => {
          const read = n.isRead ?? n.read;
          return (
          <li
            key={n.id}
            style={{
              border: "1px solid #ddd",
              padding: 12,
              marginBottom: 8,
              opacity: read ? 0.7 : 1
            }}
          >
            <div style={{ display: "flex", justifyContent: "space-between", alignItems: "start" }}>
              <div>
                <strong>{n.type}</strong> — 주문 #{n.orderId}
                <div style={{ fontSize: 14, marginTop: 4 }}>{n.message}</div>
                <div style={{ fontSize: 12, color: "#666" }}>{n.createdAt}</div>
              </div>
              {!read && (
                <button type="button" onClick={() => markRead(n.id)}>
                  읽음
                </button>
              )}
            </div>
          </li>
        );
        })}
      </ul>
      <div style={{ display: "flex", gap: 8 }}>
        <button type="button" disabled={page <= 0} onClick={() => setPage((p) => p - 1)}>
          이전
        </button>
        <span>
          {page + 1} / {Math.max(totalPages, 1)}
        </span>
        <button
          type="button"
          disabled={page >= totalPages - 1}
          onClick={() => setPage((p) => p + 1)}
        >
          다음
        </button>
      </div>
    </div>
  );
}
