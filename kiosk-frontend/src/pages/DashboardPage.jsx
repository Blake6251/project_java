import { useCallback, useEffect, useState } from "react";
import api from "../api/axios.js";
import { useOrderStore } from "../store/orderStore.js";
import { useWebSocket } from "../hooks/useWebSocket.js";
import OrderStatusBadge from "../components/OrderStatusBadge.jsx";

const STATUSES = ["CREATED", "IN_PROGRESS", "COMPLETED", "CANCELLED"];

export default function DashboardPage() {
  const fetchOrders = useOrderStore((s) => s.fetchOrders);
  const orders = useOrderStore((s) => s.orders);
  const totalPages = useOrderStore((s) => s.totalPages);
  const currentPage = useOrderStore((s) => s.currentPage);
  const loading = useOrderStore((s) => s.loading);
  const error = useOrderStore((s) => s.error);
  const setOrdersFromWs = useOrderStore((s) => s.setOrdersFromWs);
  const updateOrderInList = useOrderStore((s) => s.updateOrderInList);

  const [filterStatus, setFilterStatus] = useState("");
  const [filterMenu, setFilterMenu] = useState("");
  const [toast, setToast] = useState(null);

  const onLiveOrder = useCallback(
    (order) => {
      setOrdersFromWs(order);
      setToast(`새 주문 #${order.id} ${order.menuName}`);
      setTimeout(() => setToast(null), 4000);
      if (typeof Notification !== "undefined" && Notification.permission === "granted") {
        new Notification("새 주문", { body: `${order.menuName} x${order.quantity}` });
      }
    },
    [setOrdersFromWs]
  );

  useWebSocket(true, onLiveOrder);

  useEffect(() => {
    if (typeof Notification !== "undefined" && Notification.permission === "default") {
      Notification.requestPermission();
    }
  }, []);

  // 최초 로드 (fetchOrders 참조 변화로 이펙트 반복 방지)
  useEffect(() => {
    useOrderStore.getState().fetchOrders({ page: 0 });
  }, []);

  const reload = () =>
    fetchOrders({
      page: currentPage,
      status: filterStatus || undefined,
      menuName: filterMenu || undefined
    });

  const changeStatus = async (id, status) => {
    try {
      const { data } = await api.patch(`/api/orders/${id}/status`, { status });
      updateOrderInList(data);
    } catch (e) {
      alert(e.response?.data?.message ?? "상태 변경 실패");
    }
  };

  return (
    <div style={{ maxWidth: 960, margin: "0 auto", padding: 16 }}>
      {toast && (
        <div
          style={{
            position: "fixed",
            top: 16,
            left: "50%",
            transform: "translateX(-50%)",
            background: "#333",
            color: "#fff",
            padding: "12px 24px",
            borderRadius: 8,
            zIndex: 1000
          }}
        >
          {toast}
        </div>
      )}

      <h1>관리자 대시보드</h1>

      <div style={{ display: "flex", gap: 12, marginBottom: 16, flexWrap: "wrap" }}>
        <select
          value={filterStatus}
          onChange={(e) => setFilterStatus(e.target.value)}
          style={{ padding: 8 }}
        >
          <option value="">전체 상태</option>
          {STATUSES.map((s) => (
            <option key={s} value={s}>
              {s}
            </option>
          ))}
        </select>
        <input
          placeholder="메뉴명 검색"
          value={filterMenu}
          onChange={(e) => setFilterMenu(e.target.value)}
          style={{ padding: 8, minWidth: 160 }}
        />
        <button
          type="button"
          onClick={() =>
            fetchOrders({
              page: 0,
              status: filterStatus || undefined,
              menuName: filterMenu || undefined
            })
          }
        >
          검색
        </button>
      </div>

      {loading && <p>로딩 중…</p>}
      {error && <p style={{ color: "crimson" }}>{error}</p>}

      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr style={{ textAlign: "left", borderBottom: "2px solid #ccc" }}>
            <th>ID</th>
            <th>메뉴</th>
            <th>수량</th>
            <th>고객</th>
            <th>상태</th>
            <th>액션</th>
          </tr>
        </thead>
        <tbody>
          {orders.map((o) => (
            <tr key={o.id} style={{ borderBottom: "1px solid #eee" }}>
              <td>{o.id}</td>
              <td>{o.menuName}</td>
              <td>{o.quantity}</td>
              <td>{o.username}</td>
              <td>
                <OrderStatusBadge status={o.status} />
              </td>
              <td>
                {STATUSES.filter((s) => s !== o.status).map((s) => (
                  <button
                    key={s}
                    type="button"
                    style={{ marginRight: 4, fontSize: 11 }}
                    onClick={() => changeStatus(o.id, s)}
                  >
                    →{s}
                  </button>
                ))}
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div style={{ marginTop: 16, display: "flex", gap: 8 }}>
        <button
          type="button"
          disabled={currentPage <= 0}
          onClick={() =>
            fetchOrders({
              page: currentPage - 1,
              status: filterStatus || undefined,
              menuName: filterMenu || undefined
            })
          }
        >
          이전
        </button>
        <span>
          {currentPage + 1} / {Math.max(totalPages, 1)}
        </span>
        <button
          type="button"
          disabled={currentPage >= totalPages - 1}
          onClick={() =>
            fetchOrders({
              page: currentPage + 1,
              status: filterStatus || undefined,
              menuName: filterMenu || undefined
            })
          }
        >
          다음
        </button>
        <button type="button" onClick={reload}>
          새로고침
        </button>
      </div>
    </div>
  );
}
