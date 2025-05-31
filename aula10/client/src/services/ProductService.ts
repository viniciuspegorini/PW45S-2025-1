import { api } from "../lib/axios";

const save = (formData: FormData) => {
  return api.post("/products/upload", formData);
};

const findAll = () => {
  return api.get("/products");
};

const findOne = (id: number) => {
  return api.get(`/products/${id}`);
};

const remove = (id: number) => {
  return api.delete(`/products/${id}`);
};

const ProductService = {
  save,
  findAll,
  findOne,
  remove,
};

export default ProductService;
