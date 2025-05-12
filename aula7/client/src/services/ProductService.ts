import { Product } from "../commons/types";
import { api } from "../lib/axios";

const save = (entity: Product) => {
  return api.post("/products", entity);
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
