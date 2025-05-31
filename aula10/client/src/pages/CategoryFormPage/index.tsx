import { ChangeEvent, useEffect, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { number } from "zod";
import { Category } from "../../commons/types";
import { ButtonWithProgress } from "../../components/ButtonWithProgress";
import { Input } from "../../components/Input";
import CategoryService from "../../services/CategoryService";

export function CategoryFormPage() {
  const [form, setForm] = useState({
    id: undefined,
    name: "",
  });
  const [errors, setErrors] = useState({ id: null, name: "" });
  const [pendingApiCall, setPendingApiCall] = useState(false);
  const [apiError, setApiError] = useState("");
  const navigate = useNavigate();
  const { id } = useParams();

  useEffect(() => {
    if (id) {
      CategoryService.findOne(parseInt(id))
        .then((response) => {
          if (response.data) {
            setForm({
              id: response.data.id,
              name: response.data.name,
            }); // ...response.data
            setApiError("");
          } else {
            setApiError("Falha ao carregar a categoria");
          }
        })
        .catch((erro) => {
          setApiError("Falha ao carregar a categoria");
        });
    }
  }, [id]);

  const onChange = (event: ChangeEvent<HTMLInputElement>) => {
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

  const onSubmit = () => {
    const category: Category = {
      id: form.id,
      name: form.name,
    };
    setPendingApiCall(true);
    CategoryService.save(category)
      .then((response) => {
        setPendingApiCall(false);
        navigate("/categories");
        /*setForm((previousForm) => {
                return {
                    ...previousForm,
                    id: response.data.id,
                };
            }); */
      })
      .catch((error) => {
        if (error.response.data && error.response.data.validationErrors) {
          setErrors(error.response.data.validationErrors);
        } else {
          setApiError("Falha ao salvar categoria.");
        }
        setPendingApiCall(false);
      });
  };

  return (
    <main className="container">
      <form>
        <div className="bg-light p-5 rounded">
          <h6 className="border-bottom border-gray pb-2 mb-3">
            Cadastro de Categoria
          </h6>

          <div className="form-floating">
            <Input
              className="form-control"
              name="name"
              label="Nome"
              type="text"
              placeholder="Informe o nome"
              value={form.name}
              onChange={onChange}
              hasError={errors.name ? true : false}
              error={errors.name}
            />
          </div>

          <div className="text-center mt-3">
            <ButtonWithProgress
              className="w-50 btn btn-lg btn-primary mb-3"
              onClick={onSubmit}
              disabled={pendingApiCall ? true : false}
              pendingApiCall={pendingApiCall}
              text="Salvar"
            />
          </div>

          {apiError && <div className="alert alert-danger">{apiError}</div>}
          <div className="text-center">
            <Link className="nav-link" to="/categories">Voltar</Link>
          </div>
        </div>
      </form>
    </main>
  );
}
