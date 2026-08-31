import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import api from "../api/axios.js";
import { APP_META } from "../data/demo.js";

export default function RegisterPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [role, setRole] = useState("USER");
  const [error, setError] = useState("");
  const [ok, setOk] = useState("");
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setOk("");
    try {
      await api.post("/api/auth/register", { username, password, role });
      setOk("가입되었습니다. 로그인해주세요.");
      setTimeout(() => navigate("/login"), 800);
    } catch (err) {
      setError(err.response?.data?.message ?? "회원가입에 실패했습니다.");
    }
  };

  return (
    <div className="page">
      <div className="auth-wrap panel">
        <span className="badge">{APP_META.name}</span>
        <h1>회원가입</h1>
        <p className="muted">데모용 계정 생성. 실서비스에서는 기업 초대 흐름으로 대체 예정입니다.</p>
        <form className="form-stack" onSubmit={handleSubmit}>
          <label>
            아이디
            <input value={username} onChange={(e) => setUsername(e.target.value)} required />
          </label>
          <label>
            비밀번호
            <input
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </label>
          <label>
            역할 (데모)
            <select value={role} onChange={(e) => setRole(e.target.value)}>
              <option value="USER">고객</option>
              <option value="ADMIN">관리자</option>
            </select>
          </label>
          {error && <p className="error-text">{error}</p>}
          {ok && <p className="muted">{ok}</p>}
          <button type="submit" className="btn btn-primary">
            가입하기
          </button>
        </form>
        <p className="muted" style={{ marginTop: 14 }}>
          이미 계정이 있나요? <Link to="/login">로그인</Link>
        </p>
      </div>
    </div>
  );
}
