import { APP_META } from "../data/demo.js";

export default function DevBanner() {
  return (
    <div className="promo-banner">
      <strong>
        {APP_META.name} · {APP_META.tagline}
      </strong>
      <span>직접 소싱 · 최대 40% 원가 절감 지향 · 견적 문의 환영</span>
    </div>
  );
}
