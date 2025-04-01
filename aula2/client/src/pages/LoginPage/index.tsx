import { ChangeEvent, useState } from "react";
import { Link } from 'react-router-dom';
import { IUserLogin } from "../../commons/interfaces";
import { ButtonWithProgress } from '../../components/ButtonWithProgress';
import AuthService from "../../service/AuthService";

export function LoginPage() {
  const [form, setForm] = useState({
    username: "",
    password: "",
  });
  const [pendingApiCall, setPendingApiCall] = useState(false);
  const [apiError, setApiError] = useState(false);


  const onChange = (event: ChangeEvent<HTMLInputElement>) => {
    const { value, name } = event.target;
    setForm((previousForm) => {
      return {
        ...previousForm,
        [name]: value,
      };
    });
  };

  const onClickLogin = () => {
    setPendingApiCall(true);

    const userLogin: IUserLogin = {
      username: form.username,
      password: form.password,
    };
    AuthService.login(userLogin)
      .then((response) => {
        localStorage.setItem("token", JSON.stringify(response.data.token));
        setPendingApiCall(false);
        window.location.reload();
        console.log(response);
      })
      .catch((errorResponse) => {
        setApiError(true);
        setPendingApiCall(false);
        console.log(errorResponse);
      });
  };
  return (
    <div className="container">
      <h1 className="text-center">Login</h1>

      <div className="col-12 mb-3">
        <label>Informe seu usuário</label>
        <input
          type="text"
          className="form-control"
          placeholder="Informe o seu usuário"
          onChange={onChange}
          value={form.username}
          name="username"
        />
      </div>

      <div className="col-12 mb-3">
        <label>Informe sua senha</label>
        <input
          type="password"
          className="form-control"
          placeholder="Informe a sua senha"
          onChange={onChange}
          value={form.password}
          name="password"
        />
      </div>

      {apiError && 
        <div className="alert alert-danger">Falha ao efetuar login</div>}

      <div className="text-center">
        <ButtonWithProgress
          disabled={pendingApiCall}
          className="btn btn-primary"
          onClick={onClickLogin}
          pendingApiCall={pendingApiCall}
          text="Entrar"
        />
      </div>
      <div className="text-center">
        <Link className="btn btn-outline-secondary" to="/signup">Cadastrar novo usuário</Link>
      </div>
    </div>
  );
}
