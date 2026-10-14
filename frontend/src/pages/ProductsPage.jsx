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
            <p className="lead">금형부품·절삭공구. 견적(기성품) 및 도면 첨부 의뢰 항목을 안내합니다.</p>
          </div>
          <span className="badge badge-gold">견적 중심</span>
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
              <a href="tel:0313665576" className="btn btn-secondary">
                견적 문의
              </a>
            </article>
          ))}
        </div>

        <div className="wip-note">
          온라인 장바구니는 준비 중입니다. 당장 견적이 필요하면 전화(031-366-5576) 또는 상담예약을 이용해 주세요.
        </div>
      </section>
    </div>
  );
}
