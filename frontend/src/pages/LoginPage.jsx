import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/axios.js";
import { useAuthStore } from "../store/authStore.js";
import { useAuth } from "../hooks/useAuth.js";

export default function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const login = useAuthStore((s) => s.login);
  const { isAuthenticated, isAdmin } = useAuth();
  const navigate = useNavigate();

  useEffect(() => {
    if (isAuthenticated) {
      navigate(isAdmin ? "/dashboard" : "/order", { replace: true });
    }
  }, [isAuthenticated, isAdmin, navigate]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    try {
      const { data } = await api.post("/api/auth/login", { username, password });
      login(data.token, data.username, data.role);
      navigate(data.role === "ADMIN" ? "/dashboard" : "/order", { replace: true });
    } catch (err) {
      const msg = err.response?.data?.message ?? "로그인에 실패했습니다.";
      setError(msg);
    }
  };

  return (
    <div style={{ maxWidth: 360, margin: "48px auto" }}>
      <h1>로그인</h1>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: 12 }}>
          <input
            placeholder="username"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            style={{ width: "100%", padding: 8 }}
            autoComplete="username"
          />
        </div>
        <div style={{ marginBottom: 12 }}>
          <input
            type="password"
            placeholder="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            style={{ width: "100%", padding: 8 }}
            autoComplete="current-password"
          />
        </div>
        {error && <p style={{ color: "crimson" }}>{error}</p>}
        <button type="submit" style={{ width: "100%", padding: 10 }}>
          로그인
        </button>
      </form>
      <p style={{ marginTop: 16 }}>
        <Link to="/register">회원가입</Link>
      </p>
    </div>
  );
}
