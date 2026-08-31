import { APP_META } from "../data/demo.js";

export default function DevBanner() {
  return (
    <div className="dev-banner">
      <strong>
        {APP_META.name} {APP_META.version} · {APP_META.status}
      </strong>
      <span>COSTOOL 실증용 로컬 프로토타입 · 일부 기능은 UI 시연 단계입니다</span>
    </div>
  );
}
