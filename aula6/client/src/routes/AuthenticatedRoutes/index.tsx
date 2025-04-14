import { Route, Routes } from "react-router-dom";
import { NavBar } from "../../components/NavBar";
import { CategoryFormPage } from "../../pages/CategoryFormPage";
import { CategoryListPage } from "../../pages/CategoryListPage";
import { HomePage } from "../../pages/HomePage";
import { ProductFormPage } from "../../pages/ProductFormPage";
import { ProductFormPageV2 } from "../../pages/ProductFormPageV2";
import { ProductListPage } from "../../pages/ProductListPage";
import { ProductListPageV2 } from "../../pages/ProductListPageV2";

export function AuthenticatedRoutes() {
  return (
    <div>
      <NavBar />
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/categories" element={<CategoryListPage />} />
        <Route path="/categories/new" element={<CategoryFormPage />} />
        <Route path="/categories/:id" element={<CategoryFormPage />} />
        <Route path="/products" element={<ProductListPage />} />
        <Route path="/products/new" element={<ProductFormPage />} />
        <Route path="/products/:id" element={<ProductFormPage />} />

        <Route path="/product-v2" element={<ProductListPageV2 />} />
        <Route path="/product-v2/new" element={<ProductFormPageV2 />} />
        <Route path="/product-v2/:id" element={<ProductFormPageV2 />} />

        <Route path="*" element={<HomePage />} />
      </Routes>
    </div>
  );
}
