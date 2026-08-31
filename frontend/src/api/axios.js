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

// 401 시 토큰 정리. 공개 페이지는 로그인으로 강제 이동하지 않음
const PUBLIC_PATHS = ["/", "/products", "/reservations", "/roadmap", "/login", "/register"];

api.interceptors.response.use(
  (res) => res,
  (err) => {
    if (err.response?.status === 401) {
      localStorage.removeItem("token");
      localStorage.removeItem("username");
      localStorage.removeItem("role");
      const path = window.location.pathname;
      const isPublic = PUBLIC_PATHS.includes(path);
      if (!isPublic && !path.startsWith("/login")) {
        window.location.href = "/login";
      }
    }
    return Promise.reject(err);
  }
);

export default api;
