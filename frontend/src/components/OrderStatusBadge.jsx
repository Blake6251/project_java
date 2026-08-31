const LABEL = {
  CREATED: "접수",
  IN_PROGRESS: "처리중",
  COMPLETED: "완료",
  CANCELLED: "취소"
};

export default function OrderStatusBadge({ status }) {
  const tone =
    status === "COMPLETED"
      ? "badge-done"
      : status === "CANCELLED"
        ? "badge-plan"
        : status === "IN_PROGRESS"
          ? "badge-wip"
          : "badge";

  return <span className={`badge ${tone}`}>{LABEL[status] ?? status}</span>;
}
