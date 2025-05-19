import { useNavigate } from "react-router-dom";

export function Unauthorized() {
  const navigate = useNavigate();
  
  const goBack = () => navigate(-1);

  return (
    <section>
      <div className="container">
        <h1>Usuário não autorizado</h1>
        <br />
        <p>Permissão de acesso negada.</p>
        <br />
        <div className="flexGrow">
          <button className="btn btn-dark" onClick={goBack}>Voltar</button>
        </div>
      </div>
    </section>
  );
}
