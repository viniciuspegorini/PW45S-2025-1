import { ChangeEvent, useState } from 'react'
import { IUserSignUp } from '../../commons/interfaces';
import AuthService from '../../service/AuthService';
import { Input } from '../../components/Input';
import { ButtonWithProgress } from '../../components/ButtonWithProgress';
import { Link, useNavigate } from 'react-router-dom';

export function UserSignupPage() {
    const [form, setForm] = useState({
        displayName: '',
        username: '',
        password: '',
    });
    const [errors, setErrors] = useState({
        displayName: '',
        username: '',
        password: '',
    });
    const [pendingApiCall, setPendingApiCall] = useState(false);
    const navigate = useNavigate();


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

    const onClickSignUp = () => {
        setPendingApiCall(true);
        const userSignUp: IUserSignUp = {
            displayName: form.displayName,
            username: form.username,
            password: form.password,
        };
        AuthService.signup(userSignUp).then((response) => {
            setPendingApiCall(false);
            console.log(response);
            navigate('/login');
        }).catch((errorResponse) => {
            setPendingApiCall(false);
            console.log(errorResponse);
            if (errorResponse.response.data.validationErrors) {
                setErrors(errorResponse.response.data.validationErrors);
            }
        });
    }

    return (
        <div className="container">
            <h1 className="text-center">Sign Up</h1>
            <div className="col-12 mb-3">
                <Input
                    label="Informe seu nome"
                    type="text"
                    className="form-control"
                    placeholder="Informe o seu nome"
                    onChange={onChange}
                    value={form.displayName}
                    name="displayName"
                    error={errors.displayName}
                    hasError={errors.displayName ? true : false}
                />
            </div>

            <div className="col-12 mb-3">
                <Input
                    label="Informe seu usuário"
                    type="text"
                    className="form-control"
                    placeholder="Informe o seu usuário"
                    onChange={onChange}
                    value={form.username}
                    name="username"
                    error={errors.username}
                    hasError={errors.username ? true : false}
                />
            </div>

            <div className="col-12 mb-3">
                <Input
                    label="Informe sua senha"
                    type="password"
                    className="form-control"
                    placeholder="Informe a sua senha"
                    onChange={onChange}
                    value={form.password}
                    name="password"
                    error={errors.password}
                    hasError={errors.password ? true : false}
                />
            </div>

            <div className="text-center">
                <ButtonWithProgress
                    disabled={pendingApiCall}
                    className="btn btn-primary"
                    onClick={onClickSignUp}
                    pendingApiCall={pendingApiCall}
                    text="Cadastrar"
                />
            </div>
            <div className="text-center">
                <Link className="btn btn-outline-secondary"
                    to="/login">Ir para tela de Login</Link>
            </div>
        </div>
    )
}