import axios from "axios";

// 프록시 없이 백엔드 직접 호출 시 CORS(5173 허용) 필요. 개발 중 Vite 프록시만 쓸 때는 '' 로 두면 동일 출처.
const baseURL = import.meta.env.VITE_API_BASE ?? "http://localhost:8080";

const api = axios.create({
  baseURL,
  timeout: 15000
});

// 요청마다 JWT 자동 첨부
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// 401 시 로그아웃 처리 후 로그인 페이지로
api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem("token");
      localStorage.removeItem("username");
      localStorage.removeItem("role");
      if (!window.location.pathname.startsWith("/login")) {
        window.location.href = "/login";
      }
    }
    return Promise.reject(err);
  }
);

export default api;
