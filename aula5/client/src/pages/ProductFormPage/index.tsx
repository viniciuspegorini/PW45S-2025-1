import { ChangeEvent, useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { Category, Product } from "../../commons/types";
import { ButtonWithProgress } from "../../components/ButtonWithProgress";
import { Input } from "../../components/Input";
import CategoryService from "../../services/CategoryService";
import ProductService from "../../services/ProductService";

export function ProductFormPage() {
  const [form, setForm] = useState({
    id: undefined,
    name: "",
    price: 0,
    description: "",
    category: undefined,
    imageName: "",
  });
  const [image, setImage] = useState<any | null>(null);
  const [errors, setErrors] = useState({
    id: "",
    name: "",
    price: "",
    description: "",
    category: "",
  });
  const [pendingApiCall, setPendingApiCall] = useState(false);
  const [apiError, setApiError] = useState("");
  const [categories, setCategories] = useState([]);

  const navigate = useNavigate();
  const { id } = useParams();

  useEffect(() => {
    CategoryService.findAll()
      .then((response) => {
        setCategories(response.data);
        if (id) {
          ProductService.findOne(parseInt(id))
            .then((response) => {
              if (response.data) {
                setForm({
                  id: response.data.id,
                  name: response.data.name,
                  price: response.data.price,
                  description: response.data.description,
                  category: response.data.category.id,
                  imageName: response.data.imageName,
                });
                setApiError("");
              } else {
                setApiError("Falha ao carregar o produto");
              }
            })
            .catch((erro) => {
              setApiError("Falha ao carregar o produto");
            });

          if (form.category == null) {
            setForm((previousForm) => {
              return {
                ...previousForm,
                category: response.data[0].id,
              };
            });
          }
        }
        setApiError("");
      })
      .catch((erro) => {
        setApiError("Falha ao carregar a combo de categorias.");
      });
  }, [id]);

  const onChange = (
    event: ChangeEvent<
      HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement
    >
  ) => {
    const { value, name } = event.target;
    setForm((previousForm) => {
      return {
        ...previousForm,
        [name]: value,
      };
    });
    setErrors((previousErrors) => {
      return {
        ...previousErrors,
        [name]: undefined,
      };
    });
  };

  const onFileChangeHandler = (event: ChangeEvent<HTMLInputElement>) => {    
    setImage(event.target.files ? event.target.files[0] : null);
  };

  const onSubmit = () => {
    const product: Product = {
      id: form.id,
      name: form.name,
      price: form.price!,
      description: form.description,
      category: { id: form.category, name: "" },
    };
    setPendingApiCall(true);
   
    const formData = new FormData();    
    formData.append('image', image);
    const blob = new Blob([JSON.stringify(product)], {
      type: 'application/json'
    });
    formData.append('product', blob);
    ProductService.save(formData)
      .then((response) => {
        setPendingApiCall(false);
        navigate("/products");
      })
      .catch((error) => {
        if (error.response.data && error.response.data.validationErrors) {
          setErrors(error.response.data.validationErrors);
        } else {
          setApiError("Falha ao salvar o produto.");
        }
        setPendingApiCall(false);
      });
  };

  return (
    <div className="container">
      <h1 className="text-center">Cadastro de Produtos</h1>
      <div className="col-12 mb-3">
        <Input
          name="name"
          label="Name"
          type="text"
          className="form-control"
          placeholder="Informe o nome"
          value={form.name}
          onChange={onChange}
          hasError={errors.name ? true : false}
          error={errors.name}
        />
      </div>
      <div className="col-12 mb-3">
        <Input
          name="price"
          label="Preço"
          type="text"
          className="form-control"
          placeholder="Informe o preço"
          value={form.price.toString()}
          onChange={onChange}
          hasError={errors.price ? true : false}
          error={errors.price}
        />
      </div>
      <div className="col-12 mb-3">
        <label>Descrição</label>
        <textarea
          className="form-control"
          name="description"
          placeholder="Informe a descrição"
          value={form.description}
          onChange={onChange}
        ></textarea>
        {errors.description && (
          <div className="invalid-feedback d-block">{errors.description}</div>
        )}
      </div>
      <div className="col-12 mb-3">
        <label>Categoria</label>
        <select
          className="form-control"
          name="category"
          value={form.category}
          onChange={onChange}
        >
          {categories.map((category: Category) => (
            <option key={category.id} value={category.id}>
              {category.name}
            </option>
          ))}
        </select>
        {errors.category && (
          <div className="invalid-feedback d-block">{errors.category}</div>
        )}
      </div>
      <div className="col-12 mb-3">
        <label>Imagem</label>
        <input
          className="form-control"
          type="file"
          name="image"
          onChange={onFileChangeHandler}
        />
        {(form.imageName &&
        <img style={{width:'100px', height:'100px'}} src={`http://localhost:9000/commons/${form.imageName}`} />
        )}
      </div>
      <div className="text-center">
        <ButtonWithProgress
          className="btn btn-primary"
          onClick={onSubmit}
          disabled={pendingApiCall ? true : false}
          pendingApiCall={pendingApiCall}
          text="Salvar"
        />
      </div>
      {apiError && <div className="alert alert-danger">{apiError}</div>}
      <div className="text-center">
        <Link to="/products">Voltar</Link>
      </div>
    </div>
  );
}
