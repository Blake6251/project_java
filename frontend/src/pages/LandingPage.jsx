import { Link } from "react-router-dom";
import { APP_META, COMPANY, HIGHLIGHTS, VALUE_PROPS, DEMO_PRODUCTS } from "../data/demo.js";

export default function LandingPage() {
  const preview = DEMO_PRODUCTS.slice(0, 6);

  return (
    <div className="page">
      <section className="hero">
        <div className="hero-visual" aria-hidden="true" />
        <div className="hero-kicker rise">{APP_META.tagline}</div>
        <h1 className="rise rise-delay-1">{APP_META.name}</h1>
        <p className="rise rise-delay-2">
          불필요한 유통 마진을 줄이고, <strong>직접 소싱으로 최대 40% 원가 절감</strong>을
          추구합니다. 금형부품·절삭공구 카탈로그를 확인하고 견적을 요청하세요.
        </p>
        <div className="cta-row rise rise-delay-3">
          <Link to="/products" className="btn btn-primary">
            상품 카탈로그
          </Link>
          <a href={COMPANY.phoneHref} className="btn btn-gold">
            전화 문의 {COMPANY.phone}
          </a>
          <Link to="/reservations" className="btn btn-secondary">
            상담 예약
          </Link>
        </div>
        <div className="cta-row">
          <span className="badge badge-gold">High Quality</span>
          <span className="badge">Reasonable Price</span>
          <span className="badge">Precision</span>
        </div>
      </section>

      <section className="section">
        <div className="grid-3">
          {HIGHLIGHTS.map((h) => (
            <article key={h.label} className="panel">
              <div className="muted">{h.label}</div>
              <div className="stat-num">{h.value}</div>
            </article>
          ))}
        </div>
      </section>

      <section className="section">
        <h2>왜 COSTOOL인가</h2>
        <p className="lead">금형 현장 납품에 맞춘 부품·공구를, 유통 단계를 줄여 제공합니다.</p>
        <div className="grid-3">
          {VALUE_PROPS.map((f) => (
            <article key={f.title} className="panel">
              <span className="badge">강점</span>
              <h3>{f.title}</h3>
              <p>{f.body}</p>
            </article>
          ))}
        </div>
      </section>

      <section className="section">
        <div style={{ display: "flex", justifyContent: "space-between", gap: 12, flexWrap: "wrap" }}>
          <div>
            <h2>주요 상품</h2>
            <p className="lead">기성품 견적과 도면 기반 가공 의뢰를 함께 안내합니다.</p>
          </div>
          <Link to="/products" className="btn btn-secondary">
            전체 보기
          </Link>
        </div>
        <div className="grid-3">
          {preview.map((p) => (
            <article key={p.id} className="panel product-card">
              <div className="product-thumb">
                <span className="badge">{p.category}</span>
              </div>
              <h3>{p.name}</h3>
              <p>{p.desc}</p>
              <div className="price">{p.priceLabel}</div>
            </article>
          ))}
        </div>
      </section>

      <section className="section" id="contact">
        <h2>문의</h2>
        <p className="lead">견적·도면·방문 상담이 필요하면 연락 주세요.</p>
        <div className="grid-2">
          <article className="panel">
            <span className="badge">Contact</span>
            <h3>{COMPANY.brand}</h3>
            <p style={{ marginTop: 8 }}>
              전화: <a href={COMPANY.phoneHref}><strong>{COMPANY.phone}</strong></a>
              <br />
              주소: {COMPANY.address}
              <br />
              운영: {COMPANY.partner}
            </p>
            <div className="cta-row" style={{ marginTop: 14 }}>
              <a href={COMPANY.phoneHref} className="btn btn-primary">
                바로 전화하기
              </a>
              <Link to="/reservations" className="btn btn-secondary">
                상담 일정 잡기
              </Link>
            </div>
          </article>
          <article className="panel">
            <span className="badge badge-gold">안내</span>
            <h3>견적 준비물</h3>
            <p style={{ marginTop: 8 }}>
              · 기성품: 규격·수량
              <br />
              · 파트리스트/외주가공: 도면 첨부
              <br />
              · 절삭공구: 브랜드·모델·수량
            </p>
          </article>
        </div>
      </section>
    </div>
  );
}
