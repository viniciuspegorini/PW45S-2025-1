import { ChangeEvent, useContext, useEffect, useState } from "react";
import { Link, useLocation, useParams } from "react-router-dom";
import { UserLogin } from "../../commons/types";
import { ButtonWithProgress } from "../../components/ButtonWithProgress";
import { Input } from "../../components/Input";
import { AuthContext } from "../../context/AuthContext";
import { CredentialResponse, GoogleLogin } from "@react-oauth/google";
import googleLogo from "../../assets/google-logo.png";
import "./style.css";

export function LoginPage() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [apiError, setApiError] = useState("");
  const [pendingApiCall, setPendingApiCall] = useState(false);
  const { handleLogin, handleLoginSocialServer, loading } =
    useContext(AuthContext);
  const { search } = useLocation();

  useEffect(() => {
    const token = new URLSearchParams(search).get("token");
    if (token) {
      handleLoginSocialServer(token);
    }
  }, []);

  useEffect(() => {
    setApiError("");
  }, [username, password]);

  useEffect(() => {
    setPendingApiCall(loading);
    if (!loading && username) {
      setApiError("Usuário ou senha inválidos!");
    }
  }, [loading]);

  const onClickLogin = () => {
    const userLogin: UserLogin = {
      username: username,
      password: password,
    };

    handleLogin(userLogin);
  
  };

  let disableSubmit = false;
  if (username === "") {
    disableSubmit = true;
  }
  if (password === "") {
    disableSubmit = true;
  }

  return (
    <div className="container  text-center">
      <div className="text-center">
        <h1>Login</h1>
      </div>
      <div className="col-12 mb-3">
        <Input
          label="Informe o seu nome"
          className="form-control"
          type="text"
          placeholder="Your username"
          value={username}
          onChange={(event: ChangeEvent<HTMLInputElement>) =>
            setUsername(event.target.value)
          }
          name="username"
          hasError={false}
          error=""
        />
      </div>
      <div className="col-12 mb-3">
        <Input
          label="Informe a sua senha"
          className="form-control"
          type="password"
          placeholder="Your password"
          value={password}
          onChange={(event: ChangeEvent<HTMLInputElement>) =>
            setPassword(event.target.value)
          }
          name="password"
          hasError={false}
          error=""
        />
      </div>

      <div className="container">
        <div className="row align-items-center">
          <div className="col text-end">
            <ButtonWithProgress
              className="btn btn-primary"
              onClick={onClickLogin}
              disabled={pendingApiCall || disableSubmit}
              text="Login"
              pendingApiCall={pendingApiCall}
            />
          </div>

          <div className="col text-start">
            <a
              className="btn btn-outline-dark social-btn"
              href="http://localhost:8080/oauth2/authorize/google?redirect_uri=http://localhost:5173/login"
            >
              <img src={googleLogo} alt="Google" /> Login with google
            </a>
          </div>
        </div>
      </div>

      {apiError && (
        <div className="col-12 mb-3">
          <div className="alert alert-danger">{apiError}</div>
        </div>
      )}
      <div className="text-center">
        não possui cadastro? <Link to="/signup">Cadastrar-se</Link>
      </div>
    </div>
  );
}
