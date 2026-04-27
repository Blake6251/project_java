import { create } from "zustand";
import api from "../api/axios.js";

/** 관리자 대시보드용 주문 목록 상태 */
export const useOrderStore = create((set, get) => ({
  orders: [],
  totalElements: 0,
  totalPages: 0,
  currentPage: 0,
  loading: false,
  error: "",

  setOrdersFromWs: (order) => {
    const list = [order, ...get().orders.filter((o) => o.id !== order.id)];
    set({ orders: list });
  },

  fetchOrders: async (params = {}) => {
    set({ loading: true, error: "" });
    try {
      const { page = 0, size = 10, status, menuName, sort = "createdAt,desc" } = params;
      const search = new URLSearchParams({ page, size, sort });
      if (status) search.append("status", status);
      if (menuName) search.append("menuName", menuName);
      const { data } = await api.get(`/api/orders?${search.toString()}`);
      set({
        orders: data.content ?? [],
        totalElements: data.totalElements ?? 0,
        totalPages: data.totalPages ?? 0,
        currentPage: data.currentPage ?? 0,
        loading: false
      });
    } catch (e) {
      set({
        loading: false,
        error: e.response?.data?.message ?? "주문 목록을 불러오지 못했습니다."
      });
    }
  },

  updateOrderInList: (updated) => {
    set({
      orders: get().orders.map((o) => (o.id === updated.id ? { ...o, ...updated } : o))
    });
  }
}));
