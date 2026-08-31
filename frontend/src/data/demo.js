/** 국가지원 서류·로컬 데모용 목 데이터 (백엔드 미연동 OK) */

export const APP_META = {
  name: "COSTOOL",
  version: "0.3.0-beta",
  tagline: "기업용 물품 판매·예약 SaaS",
  status: "개발 진행 중"
};

export const ROADMAP = [
  { id: 1, title: "회원·권한 (로그인/관리자)", pct: 90, state: "done" },
  { id: 2, title: "상품 카탈로그·재고 UI", pct: 55, state: "wip" },
  { id: 3, title: "주문·결제 연동", pct: 40, state: "wip" },
  { id: 4, title: "예약·슬롯 캘린더", pct: 35, state: "wip" },
  { id: 5, title: "멀티테넌트(기업별 스토어)", pct: 15, state: "plan" },
  { id: 6, title: "실결제 PG·정산", pct: 10, state: "plan" },
  { id: 7, title: "SaaS 구독 과금", pct: 5, state: "plan" }
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
  { id: "r1", name: "상담실 A", type: "공간" },
  { id: "r2", name: "데모 부스 B", type: "공간" },
  { id: "r3", name: "기술 지원 슬롯", type: "인력" },
  { id: "r4", name: "출장 설치팀", type: "인력" }
];

export const FEATURES = [
  {
    title: "물품 판매",
    body: "카탈로그·재고·주문 흐름을 한곳에서. B2B 납품·소량 판매 모두 대응."
  },
  {
    title: "예약·스케줄",
    body: "상담·설치·방문·장비 대여 등 시간 슬롯 예약을 웹에서 접수."
  },
  {
    title: "기업 관리자",
    body: "주문·예약 현황, 알림, 통계를 관리자 화면에서 확인 (개발 확장 중)."
  }
];

export function formatWon(n) {
  return new Intl.NumberFormat("ko-KR").format(n) + "원";
}
