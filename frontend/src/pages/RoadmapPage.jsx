import { APP_META, ROADMAP } from "../data/demo.js";

const STATE_LABEL = {
  done: "완료",
  wip: "진행중",
  plan: "예정"
};

export default function RoadmapPage() {
  return (
    <div className="page">
      <section className="section" style={{ marginTop: "1rem" }}>
        <h2>개발 현황</h2>
        <p className="lead">
          {APP_META.name} {APP_META.version} — 국가지원 사업 서류·실증용으로 공개하는 진행 현황입니다.
        </p>

        <div className="panel progress-list">
          {ROADMAP.map((item) => (
            <div key={item.id} className="progress-item">
              <div style={{ display: "flex", justifyContent: "space-between", gap: 8, flexWrap: "wrap" }}>
                <strong>
                  {item.id}. {item.title}
                </strong>
                <span
                  className={`badge badge-${item.state === "done" ? "done" : item.state === "wip" ? "wip" : "plan"}`}
                >
                  {STATE_LABEL[item.state]} · {item.pct}%
                </span>
              </div>
              <div className="progress-bar">
                <i style={{ width: `${item.pct}%` }} />
              </div>
            </div>
          ))}
        </div>

        <div className="grid-2" style={{ marginTop: "1rem" }}>
          <article className="panel">
            <h3>현재 동작</h3>
            <p>
              로그인·회원가입(JWT), 주문 API 연동, 관리자 대시보드·실시간 알림 골격, 상품/예약 UI
              프로토타입.
            </p>
          </article>
          <article className="panel">
            <h3>다음 스프린트</h3>
            <p>상품 도메인 정식화, 예약 엔티티 저장, 결제 연동, 기업(테넌트) 분리.</p>
          </article>
        </div>
      </section>
    </div>
  );
}
