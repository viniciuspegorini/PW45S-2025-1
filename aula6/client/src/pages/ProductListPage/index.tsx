import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Product } from "../../commons/types";
import ProductService from "../../services/ProductService";

export function ProductListPage() {
  const [data, setData] = useState([]);
  const [apiError, setApiError] = useState("");

  useEffect(() => {
    loadData();
  }, []);

  const loadData = () => {
    ProductService.findAll()
      .then((response) => {
        setData(response.data);
        setApiError("");
      })
      .catch((error) => {
        setApiError("Falha ao carregar a lista de produtos");
      });
  };

  const onRemove = (id: number) => {
    ProductService.remove(id)
      .then((response) => {
        loadData();
        setApiError("");
      })
      .catch((erro) => {
        setApiError("Falha ao remover o produto");
      });
  };

  return (
    <div className="container">
      <h1 className="text-center">Lista de Produtos</h1>
      <div className="text-center">
        <Link className="btn btn-success" to="/products/new">
          Novo Produto
        </Link>
      </div>
      <table className="table table-striped">
        <thead>
          <tr>
            <th>Código</th>
            <th>Nome</th>
            <th>Preço</th>
            <th>Categoria</th>
            <th>Ações</th>
          </tr>
        </thead>
        <tbody>
          {data.map((product: Product) => (
            <tr key={product.id}>
              <td>{product.id}</td>
              <td>{product.name}</td>
              <td>{product.price}</td>
              <td>{product.category!.name}</td>
              <td>
                <Link
                  className="btn btn-primary"
                  to={`/products/${product.id}`}
                >
                  Editar
                </Link>

                <button
                  className="btn btn-danger"
                  onClick={() => onRemove(product.id!)}
                >
                  Remover
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {apiError && <div className="alert alert-danger">{apiError}</div>}
    </div>
  );
}
