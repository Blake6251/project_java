import { create } from "zustand";
import api from "../api/axios.js";

/** 알림 목록·미확인 개수 */
export const useNotificationStore = create((set, get) => ({
  notifications: [],
  totalElements: 0,
  totalPages: 0,
  currentPage: 0,
  unreadCount: 0,
  loading: false,
  error: "",

  fetchNotifications: async (page = 0, size = 10) => {
    set({ loading: true, error: "" });
    try {
      const { data } = await api.get(`/api/notifications?page=${page}&size=${size}`);
      set({
        notifications: data.content ?? [],
        totalElements: data.totalElements ?? 0,
        totalPages: data.totalPages ?? 0,
        currentPage: data.currentPage ?? 0,
        loading: false
      });
    } catch (e) {
      set({
        loading: false,
        error: e.response?.data?.message ?? "알림을 불러오지 못했습니다."
      });
    }
  },

  fetchUnread: async () => {
    try {
      const { data } = await api.get("/api/notifications/unread-count");
      set({ unreadCount: typeof data === "number" ? data : 0 });
    } catch {
      set({ unreadCount: 0 });
    }
  },

  markReadLocal: (id) => {
    set({
      notifications: get().notifications.map((n) =>
        n.id === id ? { ...n, isRead: true, read: true } : n
      )
    });
    get().fetchUnread();
  }
}));
