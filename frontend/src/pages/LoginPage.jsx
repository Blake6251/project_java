import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/axios.js";
import { useAuthStore } from "../store/authStore.js";
import { useAuth } from "../hooks/useAuth.js";
import { APP_META } from "../data/demo.js";

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const login = useAuthStore((s) => s.login);
  const { isAuthenticated, isAdmin } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (isAuthenticated) {
      navigate(isAdmin ? "/dashboard" : "/shop", { replace: true });
    }
  }, [isAuthenticated, isAdmin, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const { data } = await api.post("/api/auth/login", { username, password });
      login(data.token, data.username, data.role);
      navigate(data.role === "ADMIN" ? "/dashboard" : "/shop", { replace: true });
    } catch (err) {
      const msg =
        err.response?.data?.message ??
        "로그인에 실패했습니다. 백엔드가 꺼져 있으면 상품·예약 UI만 둘러볼 수 있습니다.";
      setError(msg);
    }
  };

  return (
    <div className="page">
      <div className="auth-wrap panel">
        <span className="badge">{APP_META.name}</span>
        <h1>로그인</h1>
        <p className="muted">기업 관리자 / 고객 계정으로 접속합니다.</p>
        <form className="form-stack" onSubmit={handleSubmit}>
          <label>
            아이디
            <input
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              autoComplete="username"
              required
            />
          </label>
          <label>
            비밀번호
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              autoComplete="current-password"
              required
            />
          </label>
          {error && <p className="error-text">{error}</p>}
          <button type="submit" className="btn btn-primary">
            로그인
          </button>
        </form>
        <p className="muted" style={{ marginTop: 14 }}>
          계정이 없나요? <Link to="/register">회원가입</Link>
        </p>
      </div>
    </div>
  );
}
