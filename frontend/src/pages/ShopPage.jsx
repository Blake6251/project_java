import { useEffect, useState } from "react";
import api from "../api/axios.js";
import OrderStatusBadge from "../components/OrderStatusBadge.jsx";
import { DEMO_PRODUCTS } from "../data/demo.js";

export default function ShopPage() {
  const [productName, setProductName] = useState(DEMO_PRODUCTS[0]?.name ?? "");
  const [quantity, setQuantity] = useState(1);
  const [orders, setOrders] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [error, setError] = useState("");
  const [msg, setMsg] = useState("");
  const [offline, setOffline] = useState(false);

  const loadMine = async (p = 0) => {
    setError("");
    try {
      const { data } = await api.get(`/api/orders/mine?page=${p}&size=10&sort=createdAt,desc`);
      setOrders(data.content ?? []);
      setTotalPages(data.totalPages ?? 0);
      setPage(data.currentPage ?? 0);
      setOffline(false);
    } catch (e) {
      setOffline(true);
      setError(e.response?.data?.message ?? "주문 API 연결 대기 중 (UI 시연 가능)");
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
      // 백엔드는 아직 menuName 필드를 사용 (상품명으로 매핑)
      await api.post("/api/orders", { menuName: productName, quantity: Number(quantity) });
      setMsg("주문이 등록되었습니다.");
      setQuantity(1);
      loadMine(0);
    } catch (e) {
      setError(e.response?.data?.message ?? "주문 실패 — 백엔드 기동 후 다시 시도하세요.");
    }
  };

  return (
    <div className="page">
      <section className="section" style={{ marginTop: "1rem" }}>
        <div style={{ display: "flex", justifyContent: "space-between", gap: 12, flexWrap: "wrap" }}>
          <div>
            <h2>내 주문</h2>
            <p className="lead">상품 주문 연동 화면. 카탈로그 장바구니는 개발 중이라 빠른 주문 폼을 사용합니다.</p>
          </div>
          <span className={`badge ${offline ? "badge-wip" : "badge-done"}`}>
            {offline ? "API 대기" : "API 연결됨"}
          </span>
        </div>

        <div className="grid-2">
          <form className="panel form-stack" onSubmit={handleCreate}>
            <label>
              상품
              <select value={productName} onChange={(e) => setProductName(e.target.value)}>
                {DEMO_PRODUCTS.map((p) => (
                  <option key={p.id} value={p.name}>
                    {p.name}
                  </option>
                ))}
              </select>
            </label>
            <label>
              수량
              <input
                type="number"
                min={1}
                value={quantity}
                onChange={(e) => setQuantity(e.target.value)}
              />
            </label>
            <button type="submit" className="btn btn-primary">
              주문하기
            </button>
            {msg && <p className="muted">{msg}</p>}
            {error && <p className="error-text">{error}</p>}
          </form>

          <div className="panel">
            <h3>주문 목록</h3>
            <div className="table-wrap" style={{ marginTop: 12 }}>
              <table className="data">
                <thead>
                  <tr>
                    <th>#</th>
                    <th>상품</th>
                    <th>수량</th>
                    <th>상태</th>
                  </tr>
                </thead>
                <tbody>
                  {orders.length === 0 ? (
                    <tr>
                      <td colSpan={4} className="muted">
                        주문이 없거나 API 미연결 상태입니다.
                      </td>
                    </tr>
                  ) : (
                    orders.map((o) => (
                      <tr key={o.id}>
                        <td>{o.id}</td>
                        <td>{o.menuName}</td>
                        <td>{o.quantity}</td>
                        <td>
                          <OrderStatusBadge status={o.status} />
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>
            <div className="cta-row" style={{ marginTop: 12 }}>
              <button
                type="button"
                className="btn btn-secondary"
                disabled={page <= 0}
                onClick={() => loadMine(page - 1)}
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
                onClick={() => loadMine(page + 1)}
              >
                다음
              </button>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
}
