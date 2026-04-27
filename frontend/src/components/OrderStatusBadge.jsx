const colors = {
  CREATED: "#1976d2",
  IN_PROGRESS: "#ed6c02",
  COMPLETED: "#2e7d32",
  CANCELLED: "#9e9e9e"
};

/** 주문 상태 뱃지 (인라인 스타일) */
export default function OrderStatusBadge({ status }) {
  const bg = colors[status] ?? "#666";
  return (
    <span
      style={{
        display: "inline-block",
        padding: "2px 8px",
        borderRadius: 4,
        fontSize: 12,
        color: "#fff",
        background: bg
      }}
    >
      {status}
    </span>
  );
}
