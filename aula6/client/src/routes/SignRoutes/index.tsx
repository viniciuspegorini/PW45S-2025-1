import { Route, Routes } from "react-router-dom";
import { LoginPage } from "../../pages/LoginPage";
import { UserSignupPage } from "../../pages/UserSignupPage";

export function SignRoutes() {
  return (
    <div>
      <Routes>
        <Route path="/" element={<LoginPage />} />
        <Route path="/signup" element={<UserSignupPage />} />
        <Route path="*" element={<LoginPage />} />
      </Routes>
    </div>
  );
}
