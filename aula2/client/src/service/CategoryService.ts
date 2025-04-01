import { ICategory } from '../commons/interfaces';
import { api } from '../lib/axios'

const save = (category: ICategory) => {
    return api.post('/categories', category);
}

const findAll = () => {
    return api.get('/categories');
}

const remove = (id: number) => {
    return api.delete(`/categories/${id}`);
}

const findById = (id: number) => {
    return api.get(`/categories/${id}`);
}

const CategoryService = {
    save,
    findAll,
    remove,
    findById,
}

export default CategoryService;