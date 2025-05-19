# React (front-end) - Upload de arquivos

## Introdução

O *upload* de arquivos no lado cliente sofreu poucas modificações em relação ao projeto **client** da pasta **aula7**, pois continua-se o uso do componente *input* do tipo *file* e o envio dos dados no formato **FormData**, contendo o JSON de um *product* e o arquivo como partes do corpo.

## Modificações no código na aplicação *client*

As alterações estão nos arquivos **types.ts** e **ProductFormPage.tsx**. No arquivo *types.ts* foi ajustado a *interface* que representa um produto, ficando com o seguinte código:
```ts
//...
export interface Product {
  id?: number;
  name: string;
  description: string;
  price: number;
  category: Category;
  imageName?: string;
  contentType?: string;
}
//...
```

Já o componente **ProductFormPage** sofreu modificação apenas na exibição da imagem do produto. Entretando será exibido todo o código da classe.

```jsx
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
    contentType: "",
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
                  /*  UPLOAD:
					    Aqui é recuperado o nome da imagem salva para exibição da mesma
				  */
                  imageName: response.data.imageName,
                  contentType: response.data.contentType,
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

  /*  UPLOAD:
	    Aqui é tratada a seleção de um novo arquivo no input file:
	    o arquivo é adicionado no State por meio da função setImage()
  */
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
    /*  UPLOAD:
	    Aqui foi criado um objeto do tipo FormData, contendo duas partes,
	    1 -  o product no formato JSON
	    2 - a imagem que veio do componente input do tipo file
    */
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
        {
        /*  NEW:
	        Adicionado o caminho para imagem armazenada no serviço MINIO: 
	        http://localhost:9000/commons/${form.imageName}
        */
        }
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
```