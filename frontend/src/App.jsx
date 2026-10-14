import { Routes, Route, Navigate } from "react-router-dom";
import DevBanner from "./components/DevBanner.jsx";
import Navbar from "./components/Navbar.jsx";
import PrivateRoute from "./components/PrivateRoute.jsx";
import LandingPage from "./pages/LandingPage.jsx";
import LoginPage from "./pages/LoginPage.jsx";
import RegisterPage from "./pages/RegisterPage.jsx";
import ProductsPage from "./pages/ProductsPage.jsx";
import ReservationsPage from "./pages/ReservationsPage.jsx";
import RoadmapPage from "./pages/RoadmapPage.jsx";
import ShopPage from "./pages/ShopPage.jsx";
import DashboardPage from "./pages/DashboardPage.jsx";
import NotificationPage from "./pages/NotificationPage.jsx";
import { APP_META, COMPANY } from "./data/demo.js";

export default function App() {
  return (
    <div className="app-shell">
      <DevBanner />
      <Navbar />
      <Routes>
        <Route path="/" element={<LandingPage />} />
        <Route path="/products" element={<ProductsPage />} />
        <Route path="/reservations" element={<ReservationsPage />} />
        <Route path="/roadmap" element={<RoadmapPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
        <Route
          path="/shop"
          element={
            <PrivateRoute requireAdmin={false}>
              <ShopPage />
            </PrivateRoute>
          }
        />
        <Route path="/order" element={<Navigate to="/shop" replace />} />
        <Route
          path="/dashboard"
          element={
            <PrivateRoute requireAdmin>
              <DashboardPage />
            </PrivateRoute>
          }
        />
        <Route
          path="/notifications"
          element={
            <PrivateRoute requireAdmin>
              <NotificationPage />
            </PrivateRoute>
          }
        />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
      <footer className="footer">
        <div className="footer-inner">
          <span>
            {APP_META.name} · {APP_META.tagline}
            <br />
            {COMPANY.address} · {COMPANY.phone}
          </span>
          <span>{COMPANY.partner}</span>
        </div>
      </footer>
    </div>
  );
}
