import { Category } from "../commons/types";
import { api } from "../lib/axios";

const save = (category: Category) => {
    return api.post('/categories', category);
}

const findAll = () => {
    return api.get('/categories');
}

const findOne = (id: number) => {
    return api.get(`/categories/${id}`);
}

const remove = (id: number) => {
    return api.delete(`/categories/${id}`);
}

const CategoryService = {
    save,
    findAll,
    findOne,
    remove
}

export default CategoryService;