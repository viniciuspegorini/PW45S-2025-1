import { ChangeEvent, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { ICategory } from '../../commons/interfaces';
import { ButtonWithProgress } from '../../components/ButtonWithProgress';
import { Input } from '../../components/Input';
import CategoryService from '../../service/CategoryService';


export function CategoryFormPage() {
    const [form, setForm] = useState({
        id: undefined,
        name: "",
    });
    const [errors, setErrors] = useState({
        id: undefined,
        name: "",
    });
    const [pendingApiCall, setPendingApiCall] = useState(false);
    const [apiError, setApiError] = useState(false);
    const navigate = useNavigate();
    const { id } = useParams();


    useEffect(() => {
        if (id) {
            CategoryService.findById( parseInt(id) )
                .then((response) => {
                    if (response.data) {
                        setForm({
                            id: response.data.id,
                            name: response.data.name,
                        });
                    }
                })
                .catch((responseError) => {
                    setApiError(true);
                });
        }
    },[]);


    const onChange = (event: ChangeEvent<HTMLInputElement>) => {
        const { value, name } = event.target;
        setForm((previousForm) => {
            return {
                ...previousForm,
                [name]: value,
            }
        });
        setErrors((previousErrors) => {
            return {
                ...previousErrors,
                [name]: '',
            }
        });
    }

    const onSubmit = () => {
        const category: ICategory = {
            id: form.id,
            name: form.name,
            testDate: new Date(),
        }
        setPendingApiCall(true);
        CategoryService.save(category)
            .then((response) => {
                setPendingApiCall(false);
                navigate('/categories');
            })
            .catch((responseError) => {
                if (responseError.response.data.validationErrors) {
                    setErrors(responseError.response.data.validationErrors);
                }
                setPendingApiCall(false);
                setApiError(true);
            });
    }

    return (
        <div className="container">
            <h1 className="text-center">Cadastro de Categoria</h1>

            <div className="col-12 mb-3">
                <Input
                    className="form-control"
                    name="name"
                    label="Nome"
                    placeholder="Informe o nome"
                    type="text"
                    value={form.name}
                    onChange={onChange}
                    hasError={errors.name ? true : false}
                    error={errors.name}
                />
            </div>
            {apiError &&
                <div className="alert alert-danger">Falha ao cadastrar a categoria.</div>
            }
            <div className="text-center">
                <ButtonWithProgress
                    className="btn btn-primary"
                    onClick={onSubmit}
                    disabled={pendingApiCall ? true : false}
                    pendingApiCall={pendingApiCall}
                    text="Salvar"
                />
            </div>
        </div>
    )
}