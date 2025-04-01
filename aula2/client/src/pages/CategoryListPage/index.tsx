import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ICategory } from '../../commons/interfaces';
import CategoryService from '../../service/CategoryService';

export function CategoryListPage() {
    const [data, setData] = useState([]);
    const [apiError, setApiError] = useState("");

    useEffect(() => {
        loadData();
    }, []);

    const loadData = () => {
        CategoryService.findAll()
            .then((response) => {
                setData(response.data);
                setApiError("");
            })
            .catch((responseError) => {
                setApiError("Falha ao carregar lista de categorias.");
            });
    }

    const onClickRemove = (id?: number) => {
        if (id) {
            CategoryService.remove(id)
                .then((response) => {
                    loadData();
                    setApiError("");
                })
                .catch((responseError) => {
                    setApiError("Falha ao remover o registro.");
                });
        }
    }

    return (
        <div className="container">
            <h1 className="text-center">Lista de Categoria</h1>
            <div className="text-center">
                <Link className="btn btn-success" to="/categories/new">
                    Nova Categoria
                </Link>
            </div>

            <table className="table table-striped">
                <thead>
                    <tr>
                        <td>#</td>
                        <td>Nome</td>
                        <td>Editar</td>
                        <td>Remover</td>
                    </tr>
                </thead>
                <tbody>
                    {data.map((category: ICategory) => (
                        <tr key={category.id}>
                            <td>{category.id}</td>
                            <td>{category.name}</td>
                            <td>
                                <Link className="btn btn-primary"
                                    to={`/categories/${category.id}`}>
                                    Editar
                                </Link>
                            </td>
                            <td>
                                <button className="btn btn-danger"
                                    onClick={() => onClickRemove(category.id)}>
                                    Remover
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
            {apiError &&
<div className="alert alert-danger">{apiError}</div>
            }
        </div>
    )
}