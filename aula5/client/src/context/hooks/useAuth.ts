import { useState, useEffect } from "react";
import { api } from "../../lib/axios";
import { AuthenticatedUser, AuthenticationResponse } from "../../commons/types";
import { useNavigate } from "react-router-dom";

export function useAuth() {
  const [authenticated, setAuthenticated] = useState(false);
  const [authenticatedUser, setAuthenticatedUser] =
    useState<AuthenticatedUser>();
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    const token = localStorage.getItem("token");
    const user = localStorage.getItem("user");

    if (token && user) {
      api.defaults.headers.common["Authorization"] = `Bearer ${JSON.parse(
        token
      )}`;
      setAuthenticatedUser(JSON.parse(user));
      setAuthenticated(true);
      navigate("/");
    }

    setLoading(false);
  }, []);

  function handleLogout() {
    setAuthenticated(false);
    localStorage.removeItem("token");
    api.defaults.headers.common["Authorization"] = "";
    setAuthenticatedUser(undefined);
  }

  function handleLogin(response: AuthenticationResponse) {
    localStorage.setItem("token", JSON.stringify(response.token));
    api.defaults.headers.common["Authorization"] = `Bearer ${response.token}`;
    console.log(response.user);
    setAuthenticatedUser(response.user);
    setAuthenticated(true);
  }

  return {
    authenticated,
    authenticatedUser,
    loading,
    handleLogin,
    handleLogout,
  };
}
