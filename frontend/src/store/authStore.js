import { create } from "zustand";

/**
 * 인증 상태 (JWT + 사용자 표시 정보)
 * persist: localStorage 와 동기화
 */
export const useAuthStore = create((set) => ({
  token: localStorage.getItem("token"),
  username: localStorage.getItem("username"),
  role: localStorage.getItem("role"),

  login: (token, username, role) => {
    localStorage.setItem("token", token);
    localStorage.setItem("username", username);
    localStorage.setItem("role", role);
    set({ token, username, role });
  },

  logoutLocal: () => {
    localStorage.removeItem("token");
    localStorage.removeItem("username");
    localStorage.removeItem("role");
    set({ token: null, username: null, role: null });
  }
}));
