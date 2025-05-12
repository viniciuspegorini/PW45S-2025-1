import { useState, useEffect } from "react";
import { api } from "../../lib/axios";
import { AuthenticatedUser, AuthenticationResponse } from "../../commons/types";
import { useNavigate } from "react-router-dom";
import { set } from "zod";

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

  async function handleLoginSocial(idToken: string) {
    setLoading(true);
    api.defaults.headers.common["Auth-Id-Token"] = `Bearer ${idToken}`;
    const response = await api.post("/auth-social");
    console.log(response);

    localStorage.setItem("token", JSON.stringify(response.data.token));
    localStorage.setItem("user", JSON.stringify(response.data.user));
    api.defaults.headers.common["Authorization"] = `Bearer ${response.data.token}`;
    setAuthenticatedUser(response.data.user);
    setAuthenticated(true);
    api.defaults.headers.common["Auth-Id-Token"] = '';
    setLoading(false);
    navigate("/");
  }

  return {
    authenticated,
    authenticatedUser,
    loading,
    handleLogin,
    handleLogout,
    handleLoginSocial,
  };
}
