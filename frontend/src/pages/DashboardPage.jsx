import { useCallback, useEffect, useState } from "react";
import api from "../api/axios.js";
import { useOrderStore } from "../store/orderStore.js";
import { useWebSocket } from "../hooks/useWebSocket.js";
import OrderStatusBadge from "../components/OrderStatusBadge.jsx";

const STATUSES = ["CREATED", "IN_PROGRESS", "COMPLETED", "CANCELLED"];
const STATUS_KO = {
  CREATED: "접수",
  IN_PROGRESS: "처리중",
  COMPLETED: "완료",
  CANCELLED: "취소"
};

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
    <div className="page">
      {toast && (
        <div
          className="panel"
          style={{
            position: "fixed",
            top: 72,
            left: "50%",
            transform: "translateX(-50%)",
            zIndex: 1000,
            background: "#0b1c17",
            color: "#f4fff9",
            padding: "0.85rem 1.2rem"
          }}
        >
          {toast}
        </div>
      )}

      <section className="section" style={{ marginTop: "1rem" }}>
        <div style={{ display: "flex", justifyContent: "space-between", gap: 12, flexWrap: "wrap" }}>
          <div>
            <h2>관리자 대시보드</h2>
            <p className="lead">주문 접수·상태 관리 · 실시간 알림(WebSocket) 연결</p>
          </div>
          <span className="badge badge-wip">예약 캘린더 관리 뷰 — 다음 스프린트</span>
        </div>

        <div className="panel" style={{ marginBottom: "1rem" }}>
          <div className="cta-row">
            <select value={filterStatus} onChange={(e) => setFilterStatus(e.target.value)}>
              <option value="">전체 상태</option>
              {STATUSES.map((s) => (
                <option key={s} value={s}>
                  {STATUS_KO[s]}
                </option>
              ))}
            </select>
            <input
              placeholder="상품명 검색"
              value={filterMenu}
              onChange={(e) => setFilterMenu(e.target.value)}
            />
            <button
              type="button"
              className="btn btn-primary"
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
            <button type="button" className="btn btn-secondary" onClick={reload}>
              새로고침
            </button>
          </div>
        </div>

        {loading && <p className="muted">로딩 중…</p>}
        {error && <p className="error-text">{error}</p>}

        <div className="panel table-wrap">
          <table className="data">
            <thead>
              <tr>
                <th>ID</th>
                <th>상품</th>
                <th>수량</th>
                <th>고객</th>
                <th>상태</th>
                <th>액션</th>
              </tr>
            </thead>
            <tbody>
              {orders.map((o) => (
                <tr key={o.id}>
                  <td>{o.id}</td>
                  <td>{o.menuName}</td>
                  <td>{o.quantity}</td>
                  <td>{o.username}</td>
                  <td>
                    <OrderStatusBadge status={o.status} />
                  </td>
                  <td>
                    <div className="cta-row">
                      {STATUSES.filter((s) => s !== o.status).map((s) => (
                        <button
                          key={s}
                          type="button"
                          className="btn btn-secondary"
                          style={{ padding: "0.35rem 0.65rem", fontSize: "0.8rem" }}
                          onClick={() => changeStatus(o.id, s)}
                        >
                          →{STATUS_KO[s]}
                        </button>
                      ))}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="cta-row" style={{ marginTop: 12 }}>
          <button
            type="button"
            className="btn btn-secondary"
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
          <span className="muted">
            {currentPage + 1} / {Math.max(totalPages, 1)}
          </span>
          <button
            type="button"
            className="btn btn-secondary"
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
        </div>
      </section>
    </div>
  );
}
