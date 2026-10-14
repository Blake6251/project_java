/** COSTOOL 홍보·카탈로그용 프론트 데이터 */

export const APP_META = {
  name: "COSTOOL",
  version: "1.0",
  tagline: "금형부품 · 절삭공구 전문",
  status: "공식 소개"
};

export const COMPANY = {
  brand: "COSTOOL",
  partner: "바로볼트 (BARO BOLT)",
  phone: "031-366-5576",
  address: "경기도 화성시 팔탄면 온천로 1길 162호",
  phoneHref: "tel:0313665576"
};

export const VALUE_PROPS = [
  {
    title: "직접 소싱",
    body: "불필요한 유통 마진을 줄이고, 현장 납품에 맞는 가격으로 제공합니다."
  },
  {
    title: "품질·공차",
    body: "내열·직진도·열처리 품질을 기준으로 금형 부품을 선별합니다."
  },
  {
    title: "견적·가공 의뢰",
    body: "기성품 견적은 물론, 도면 첨부 기반 파트리스트·외주가공 의뢰도 받습니다."
  }
];

export const HIGHLIGHTS = [
  { label: "원가 절감", value: "최대 40%" },
  { label: "주력", value: "SKD-61 밀핀" },
  { label: "대응", value: "금형부품 · 절삭공구" }
];

export const ROADMAP = [
  { id: 1, title: "홍보 사이트·상품 카탈로그", pct: 80, state: "wip" },
  { id: 2, title: "견적 문의·도면 첨부 플로우", pct: 25, state: "plan" },
  { id: 3, title: "주문·결제 연동", pct: 40, state: "wip" },
  { id: 4, title: "예약·상담 슬롯", pct: 35, state: "wip" },
  { id: 5, title: "기업(테넌트) 분리", pct: 15, state: "plan" }
];

export const DEMO_PRODUCTS = [
  {
    id: "p1",
    name: "SKD-61 밀핀 (1단)",
    category: "금형부품",
    priceLabel: "견적 (기성품 사용)",
    desc: "내열·직진도 안정형 표준 밀핀"
  },
  {
    id: "p2",
    name: "파트리스트 전체",
    category: "금형부품",
    priceLabel: "견적 (도면 첨부)",
    desc: "금형에 사용되는 가공건 일괄 의뢰"
  },
  {
    id: "p3",
    name: "외주가공",
    category: "금형부품",
    priceLabel: "견적 (도면 첨부)",
    desc: "선반, 밀링, 와이어 등"
  },
  {
    id: "p4",
    name: "엔드밀 류",
    category: "절삭공구",
    priceLabel: "견적",
    desc: "SGSO"
  },
  {
    id: "p5",
    name: "인서트 류",
    category: "절삭공구",
    priceLabel: "견적",
    desc: "ABP"
  },
  {
    id: "p6",
    name: "척 류",
    category: "절삭공구",
    priceLabel: "견적",
    desc: "ABP"
  },
  {
    id: "p7",
    name: "사각 밀핀",
    category: "금형부품",
    priceLabel: "견적 (기성품 사용)",
    desc: "정밀 가공 사각 규격"
  },
  {
    id: "p8",
    name: "슬리브 핀",
    category: "금형부품",
    priceLabel: "견적 (기성품 사용)",
    desc: "현장 교체용 슬리브"
  },
  {
    id: "p9",
    name: "코아핀 세트",
    category: "금형부품",
    priceLabel: "견적 (기성품 사용)",
    desc: "다규격 번들 패키지"
  },
  {
    id: "p10",
    name: "이단 밀핀",
    category: "금형부품",
    priceLabel: "견적 (기성품 사용)",
    desc: "단차 가공 밀핀"
  },
  {
    id: "p11",
    name: "드릴 류",
    category: "절삭공구",
    priceLabel: "견적",
    desc: "SGSO / 일반·초경"
  },
  {
    id: "p12",
    name: "리머·탭 류",
    category: "절삭공구",
    priceLabel: "견적",
    desc: "정밀 홀 가공용"
  }
];

export const DEMO_SLOTS = [
  { id: "s1", label: "10:00", available: true },
  { id: "s2", label: "11:00", available: true },
  { id: "s3", label: "13:00", available: false },
  { id: "s4", label: "14:00", available: true },
  { id: "s5", label: "15:30", available: true },
  { id: "s6", label: "16:30", available: false },
  { id: "s7", label: "17:00", available: true }
];

export const DEMO_RESOURCES = [
  { id: "r1", name: "기술 상담", type: "상담" },
  { id: "r2", name: "도면 검토", type: "상담" },
  { id: "r3", name: "방문 미팅", type: "방문" },
  { id: "r4", name: "샘플 확인", type: "방문" }
];

export const FEATURES = VALUE_PROPS;

export function formatWon(n) {
  return new Intl.NumberFormat("ko-KR").format(n) + "원";
}
