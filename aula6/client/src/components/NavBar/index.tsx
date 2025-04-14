import { useContext } from "react";
import { Link, NavLink } from "react-router-dom";
import { AuthContext } from "../../context/AuthContext";
import logo from "../../assets/utfpr-logo.png";

export function NavBar() {
  const { handleLogout } = useContext(AuthContext);

  return (
    <div className="bg-white shadow-sm mb-2">
      <div className="container">
        <nav className="navbar navbar-light navbar-expand">
          <Link to="/" className="navbar-brand">
            <img src={logo} width="60" alt="UTFPR" />
          </Link>
          <ul className="navbar-nav me-auto mb-2 mb-md-0">
            <li className="nav-item">
              <NavLink
                to="/"
                className={(navData) =>
                  navData.isActive ? "nav-link active" : "nav-link"
                }
              >
                Home
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink
                to="/categories"
                className={(navData) =>
                  navData.isActive ? "nav-link active" : "nav-link"
                }
              >
                Categorias
              </NavLink>
            </li>
            <li className="nav-item">
              <NavLink
                to="/products"
                className={(navData) =>
                  navData.isActive ? "nav-link active" : "nav-link"
                }
              >
                Produtos
              </NavLink>
            </li>

            <li className="nav-item">
              <NavLink
                to="/product-v2"
                className={(navData) =>
                  navData.isActive ? "nav-link active" : "nav-link"
                }
              >
                Product V2
              </NavLink>
            </li>

            <li className="nav-item">
              <button className="btn btn-light" onClick={handleLogout}>
                &times; Sair
              </button>
            </li>
          </ul>
        </nav>
      </div>
    </div>
  );
}
