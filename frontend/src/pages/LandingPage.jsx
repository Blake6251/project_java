import { Link } from "react-router-dom";
import { APP_META, FEATURES, ROADMAP } from "../data/demo.js";

export default function LandingPage() {
  const done = ROADMAP.filter((r) => r.state === "done").length;
  const wip = ROADMAP.filter((r) => r.state === "wip").length;

  return (
    <div className="page">
      <section className="hero">
        <div className="hero-kicker rise">{APP_META.tagline}</div>
        <h1 className="rise rise-delay-1">{APP_META.name}</h1>
        <p className="rise rise-delay-2">
          기업이 자사 물품을 판매하고, 상담·설치·방문 예약을 받는{" "}
          <strong>범용 B2B SaaS</strong>를 구축 중입니다. 현재 로컬 프로토타입으로
          핵심 화면과 인증·주문 연동을 검증하고 있습니다.
        </p>
        <div className="cta-row rise rise-delay-3">
          <Link to="/products" className="btn btn-primary">
            상품 둘러보기
          </Link>
          <Link to="/reservations" className="btn btn-secondary">
            예약 체험
          </Link>
          <Link to="/roadmap" className="btn btn-ghost">
            개발 로드맵
          </Link>
        </div>
        <div className="cta-row">
          <span className="badge badge-wip">프로토타입 v{APP_META.version}</span>
          <span className="badge badge-done">완료 {done}</span>
          <span className="badge badge-wip">진행 {wip}</span>
        </div>
      </section>

      <section className="section">
        <h2>한 플랫폼에서</h2>
        <p className="lead">판매와 예약을 따로 두지 않고, 중소기업 운영 흐름에 맞게 묶습니다.</p>
        <div className="grid-3">
          {FEATURES.map((f) => (
            <article key={f.title} className="panel">
              <span className="badge">모듈</span>
              <h3>{f.title}</h3>
              <p>{f.body}</p>
            </article>
          ))}
        </div>
      </section>

      <section className="section">
        <h2>지금 개발 단계</h2>
        <p className="lead">서류·실증용으로 동작 가능한 껍데기 + 일부 실연동(로그인·주문 API).</p>
        <div className="panel">
          <div className="progress-list">
            {ROADMAP.slice(0, 4).map((item) => (
              <div key={item.id} className="progress-item">
                <div style={{ display: "flex", justifyContent: "space-between", gap: 8 }}>
                  <span>{item.title}</span>
                  <span className={`badge badge-${item.state === "done" ? "done" : item.state === "wip" ? "wip" : "plan"}`}>
                    {item.pct}%
                  </span>
                </div>
                <div className="progress-bar">
                  <i style={{ width: `${item.pct}%` }} />
                </div>
              </div>
            ))}
          </div>
          <div className="wip-note">
            멀티테넌트·실결제 PG·구독 과금은 다음 스프린트 예정입니다. 화면은 시연용으로 먼저 열어 두었습니다.
          </div>
        </div>
      </section>
    </div>
  );
}
