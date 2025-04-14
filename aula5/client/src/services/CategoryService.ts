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

const filterCategories = (categories: Category[], filter: string) => {
    console.log('[ARTIFICIALLY SLOW] Filtering ' + categories.length + ' categories for "' + filter + '" tab.');
    let startTime = performance.now();
    while (performance.now() - startTime < 500) {
      // Do nothing for 500 ms to emulate extremely slow code
    }
    return categories.filter(category => category.name.includes(filter));;
}

const CategoryService = {
    save,
    findAll,
    findOne,
    remove,
    filterCategories,
}

export default CategoryService;