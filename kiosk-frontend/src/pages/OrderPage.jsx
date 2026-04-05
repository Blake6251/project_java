import { useEffect, useState } from "react";
import api from "../api/axios.js";
import OrderStatusBadge from "../components/OrderStatusBadge.jsx";

export default function OrderPage() {
  const [menuName, setMenuName] = useState("");
  const [quantity, setQuantity] = useState(1);
  const [orders, setOrders] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [error, setError] = useState("");
  const [msg, setMsg] = useState("");

  const loadMine = async (p = 0) => {
    setError("");
    try {
      const { data } = await api.get(`/api/orders/mine?page=${p}&size=10&sort=createdAt,desc`);
      setOrders(data.content ?? []);
      setTotalPages(data.totalPages ?? 0);
      setPage(data.currentPage ?? 0);
    } catch (e) {
      setError(e.response?.data?.message ?? "목록 조회 실패");
    }
  };

  useEffect(() => {
    loadMine(0);
  }, []);

  const handleCreate = async (e) => {
    e.preventDefault();
    setMsg("");
    setError("");
    try {
      await api.post("/api/orders", { menuName, quantity: Number(quantity) });
      setMsg("주문이 등록되었습니다.");
      setMenuName("");
      setQuantity(1);
      loadMine(0);
    } catch (e) {
      setError(e.response?.data?.message ?? "주문 실패");
    }
  };

  return (
    <div style={{ maxWidth: 720, margin: "0 auto", padding: 16 }}>
      <h1>주문하기</h1>
      <form onSubmit={handleCreate} style={{ marginBottom: 24 }}>
        <input
          placeholder="메뉴명"
          value={menuName}
          onChange={(e) => setMenuName(e.target.value)}
          style={{ marginRight: 8, padding: 8 }}
        />
        <input
          type="number"
          min={1}
          value={quantity}
          onChange={(e) => setQuantity(e.target.value)}
          style={{ width: 64, marginRight: 8, padding: 8 }}
        />
        <button type="submit">주문</button>
      </form>
      {msg && <p style={{ color: "green" }}>{msg}</p>}
      {error && <p style={{ color: "crimson" }}>{error}</p>}

      <h2>내 주문</h2>
      <ul style={{ listStyle: "none", padding: 0 }}>
        {orders.map((o) => (
          <li
            key={o.id}
            style={{
              border: "1px solid #eee",
              padding: 12,
              marginBottom: 8,
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center"
            }}
          >
            <span>
              #{o.id} {o.menuName} ×{o.quantity}
            </span>
            <OrderStatusBadge status={o.status} />
          </li>
        ))}
      </ul>
      <div style={{ display: "flex", gap: 8 }}>
        <button type="button" disabled={page <= 0} onClick={() => loadMine(page - 1)}>
          이전
        </button>
        <span>
          {page + 1} / {Math.max(totalPages, 1)}
        </span>
        <button
          type="button"
          disabled={page >= totalPages - 1}
          onClick={() => loadMine(page + 1)}
        >
          다음
        </button>
      </div>
    </div>
  );
}
