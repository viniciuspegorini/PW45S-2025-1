import { ChangeEvent, useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { User } from "../../commons/types";
import { ButtonWithProgress } from "../../components/ButtonWithProgress";
import { Input } from "../../components/Input";
import AuthService from "../../services/AuthService";

export function UserSignupPage() {
  const [form, setForm] = useState({
    displayName: "",
    username: "",
    password: "",
    passwordRepeat: "",
  });
  const [errors, setErrors] = useState({
    displayName: "",
    username: "",
    password: "",
  });
  const [pendingApiCall, setPendingApiCall] = useState(false);
  const [passwordRepeatError, setPasswordRepeatError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {    
    if (form.password || form.passwordRepeat) {
      setPasswordRepeatError( (form.password === form.passwordRepeat) ? "" : "As senhas devem ser iguais");
    }
  }, [form]);

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

  const onClickSignup = () => {
    const user: User = {
      displayName: form.displayName,
      username: form.username,
      password: form.password,
    };
    setPendingApiCall(true);
    AuthService.signup(user)
      .then((response) => {
        setPendingApiCall(false);
        navigate("/");
      })
      .catch((apiError) => {
        if (apiError.response.data && apiError.response.data.validationErrors) {
          setErrors(apiError.response.data.validationErrors);
        }
        setPendingApiCall(false);
      });
  };
 
  return (
    <main className="form-signin w-100 m-auto">
      <form>
        <div className="text-center">
          <h1 className="h3 mb-3 fw-normal">Novo usuário</h1>
        </div>
        <div className="form-floating">
          <Input
            name="displayName"
            label="Informe o seu nome"
            className="form-control"
            type="text"
            placeholder="Informe seu nome"
            onChange={onChange}
            value={form.displayName}
            hasError={errors.displayName ? true : false}
            error={errors.displayName}
          />
        </div>
        <div className="form-floating">
          <Input
            name="username"
            label="Informe o usuário"
            className="form-control"
            type="text"
            placeholder="Informe o usuário"
            onChange={onChange}
            value={form.username}
            hasError={errors.username ? true : false}
            error={errors.username}
          />
        </div>
        <div className="form-floating">
          <Input
            name="password"
            label="Informe a senha"
            className="form-control"
            type="text"
            placeholder="Informe a senha"
            onChange={onChange}
            value={form.password}
            hasError={errors.password ? true : false}
            error={errors.password}
          />
        </div>
        <div className="form-floating">
          <Input
            name="passwordRepeat"
            label="Confirme sua senha"
            className="form-control"
            type="password"
            placeholder="Informe sua senha"
            onChange={onChange}
            value={form.passwordRepeat}
            hasError={passwordRepeatError ? true : false}
            error={passwordRepeatError ? passwordRepeatError : ""}
          />
        </div>
        
        <ButtonWithProgress
          className="w-100 btn btn-lg btn-primary mb-3"
          onClick={onClickSignup}
          disabled={pendingApiCall || passwordRepeatError ? true : false}
          text="Cadastrar"
          pendingApiCall={pendingApiCall}
        />

        <div className="text-center">
          Já possui cadastro?{" "}
          <Link className="nav-link" to="/">
            Login
          </Link>
        </div>
      </form>
    </main>
  );
}
