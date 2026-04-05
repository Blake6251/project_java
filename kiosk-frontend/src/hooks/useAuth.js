import { useAuthStore } from "../store/authStore.js";

/** 로그인 여부·역할 체크 헬퍼 */
export function useAuth() {
  const token = useAuthStore((s) => s.token);
  const username = useAuthStore((s) => s.username);
  const role = useAuthStore((s) => s.role);

  const isAuthenticated = Boolean(token);
  const isAdmin = role === "ADMIN";

  return { token, username, role, isAuthenticated, isAdmin };
}
