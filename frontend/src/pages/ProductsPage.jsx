import { useState } from "react";
import { DEMO_PRODUCTS } from "../data/demo.js";

export default function ProductsPage() {
  const [category, setCategory] = useState("전체");
  const cats = ["전체", ...new Set(DEMO_PRODUCTS.map((p) => p.category))];
  const list =
    category === "전체" ? DEMO_PRODUCTS : DEMO_PRODUCTS.filter((p) => p.category === category);

  return (
    <div className="page">
      <section className="section" style={{ marginTop: "1rem" }}>
        <div style={{ display: "flex", justifyContent: "space-between", gap: 12, flexWrap: "wrap" }}>
          <div>
            <h2>상품 카탈로그</h2>
            <p className="lead">데모 데이터입니다. 실제 테넌트별 상품 API는 연동 개발 중.</p>
          </div>
          <span className="badge badge-wip">UI 시연 · API 연동 예정</span>
        </div>

        <div className="cta-row" style={{ marginBottom: "1rem" }}>
          {cats.map((c) => (
            <button
              key={c}
              type="button"
              className={`btn ${category === c ? "btn-primary" : "btn-secondary"}`}
              onClick={() => setCategory(c)}
            >
              {c}
            </button>
          ))}
        </div>

        <div className="grid-3">
          {list.map((p) => (
            <article key={p.id} className="panel product-card">
              <div className="product-thumb">
                <span className="badge">{p.category}</span>
              </div>
              <h3>{p.name}</h3>
              <p>{p.desc}</p>
              <div className="price">{p.priceLabel ?? "견적"}</div>
              <button type="button" className="btn btn-secondary" disabled>
                장바구니 (준비 중)
              </button>
            </article>
          ))}
        </div>

        <div className="wip-note">
          장바구니·결제·재고 차감은 백엔드 Product/Cart 도메인 추가 후 연결 예정입니다.
        </div>
      </section>
    </div>
  );
}
