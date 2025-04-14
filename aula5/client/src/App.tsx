import { Route, Routes } from "react-router-dom";
import { Layout } from "./pages/Layout";
import { LoginPage } from "./pages/LoginPage";
import { UserSignupPage } from "./pages/UserSignupPage";
import { HomePage } from "./pages/HomePage";
import { RequireAuth } from "./components/RequireAuth";
import { CategoryListPage } from "./pages/CategoryListPage";
import { CategoryFormPage } from "./pages/CategoryFormPage";
import { ProductListPage } from "./pages/ProductListPage";
import { ProductFormPage } from "./pages/ProductFormPage";
import { ProductListPageV2 } from "./pages/ProductListPageV2";
import { ProductFormPageV2 } from "./pages/ProductFormPageV2";
import { NotFound } from "./pages/NotFound";
import { Unauthorized } from "./pages/Unauthorized";

const ROLES = {
  'User': 'ROLE_USER',
  'Admin': 'ROLE_ADMIN',
}

export function App() {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        {/* public routes */}
        <Route path="login" element={<LoginPage />} />
        <Route path="signup" element={<UserSignupPage />} />
        <Route path="unauthorized" element={<Unauthorized />} />

        {/* protected routes - Roles: User and Admin */}
        <Route element={<RequireAuth allowedRoles={[ROLES.User, ROLES.Admin]} />}>
          <Route path="/home" element={<HomePage />} />
          <Route path="/" element={<HomePage />} />
          <Route path="/categories" element={<CategoryListPage />} />

          <Route path="/categories/new" element={<CategoryFormPage />} />
          <Route path="/categories/:id" element={<CategoryFormPage />} />
          <Route path="/products" element={<ProductListPage />} />
          <Route path="/products/new" element={<ProductFormPage />} />
          <Route path="/products/:id" element={<ProductFormPage />} />
        </Route>

        {/* protected routes - Role: Admin */}
        <Route element={<RequireAuth allowedRoles={[ROLES.Admin]} />}>
          <Route path="/product-v2" element={<ProductListPageV2 />} />
          <Route path="/product-v2/new" element={<ProductFormPageV2 />} />
          <Route path="/product-v2/:id" element={<ProductFormPageV2 />} />
        </Route>

        {/* catch all */}
        <Route path="*" element={<NotFound />} />
      </Route>
    </Routes>
  );
}
