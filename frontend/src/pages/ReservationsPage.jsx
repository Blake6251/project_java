import { useMemo, useState } from "react";
import { DEMO_RESOURCES, DEMO_SLOTS } from "../data/demo.js";

export default function ReservationsPage() {
  const [resourceId, setResourceId] = useState(DEMO_RESOURCES[0].id);
  const [slotId, setSlotId] = useState(null);
  const [note, setNote] = useState("");
  const [submitted, setSubmitted] = useState(false);

  const tomorrow = useMemo(() => {
    const d = new Date();
    d.setDate(d.getDate() + 1);
    return d.toISOString().slice(0, 10);
  }, []);

  const [date, setDate] = useState(tomorrow);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!slotId) return;
    setSubmitted(true);
  };

  return (
    <div className="page">
      <section className="section" style={{ marginTop: "1rem" }}>
        <div style={{ display: "flex", justifyContent: "space-between", gap: 12, flexWrap: "wrap" }}>
          <div>
            <h2>상담 예약</h2>
            <p className="lead">기술 상담·도면 검토·방문 미팅 일정을 남겨 주세요.</p>
          </div>
          <span className="badge badge-gold">견적·상담</span>
        </div>

        <div className="grid-2">
          <form className="panel form-stack" onSubmit={handleSubmit}>
            <label>
              리소스
              <select value={resourceId} onChange={(e) => setResourceId(e.target.value)}>
                {DEMO_RESOURCES.map((r) => (
                  <option key={r.id} value={r.id}>
                    {r.name} ({r.type})
                  </option>
                ))}
              </select>
            </label>
            <label>
              날짜
              <input type="date" value={date} onChange={(e) => setDate(e.target.value)} />
            </label>
            <div>
              <div style={{ marginBottom: 8, fontWeight: 500 }}>시간 슬롯</div>
              <div className="slot-grid">
                {DEMO_SLOTS.map((s) => (
                  <button
                    key={s.id}
                    type="button"
                    className={`slot ${slotId === s.id ? "selected" : ""} ${!s.available ? "disabled" : ""}`}
                    disabled={!s.available}
                    onClick={() => setSlotId(s.id)}
                  >
                    {s.label}
                  </button>
                ))}
              </div>
            </div>
            <label>
              요청 메모
              <textarea
                rows={3}
                value={note}
                onChange={(e) => setNote(e.target.value)}
                placeholder="방문 목적, 인원 등"
              />
            </label>
            <button type="submit" className="btn btn-primary" disabled={!slotId}>
              예약 신청 (데모)
            </button>
            {submitted && (
              <div className="wip-note">
                데모 접수만 표시했습니다. 실제 저장·확정 메일·캘린더 연동은 개발 중입니다.
              </div>
            )}
          </form>

          <aside className="panel">
            <span className="badge">예정 기능</span>
            <h3>예약 모듈 로드맵</h3>
            <p style={{ marginTop: 8 }}>
              · 리소스별 가용 시간 자동 계산
              <br />
              · 중복 예약 방지·대기열
              <br />
              · 예약금 결제 / 노쇼 처리
              <br />
              · 관리자 캘린더 뷰
            </p>
            <div className="wip-note">현재는 화면·플로우 검증용입니다. DB 저장은 다음 단계.</div>
          </aside>
        </div>
      </section>
    </div>
  );
}
