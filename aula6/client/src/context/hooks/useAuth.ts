import { useState, useEffect } from "react";
import { api } from "../../lib/axios";
import history from "../../config/history";
import { UserLogin } from "../../commons/types";

export function useAuth() {
  const [authenticated, setAuthenticated] = useState(false);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (token) {
      api.defaults.headers.common["Authorization"] = `Bearer ${JSON.parse(
        token
      )}`;
      setAuthenticated(true);
    }

    setLoading(false);
  }, []);

  async function handleLogin(user: UserLogin) {
    try {
      setLoading(true);
      const response = await api.post("/login", user);
      console.log(response);
      setLoading(false);

      localStorage.setItem("token", JSON.stringify(response.data.token));
      api.defaults.headers.common[
        "Authorization"
      ] = `Bearer ${response.data.token}`;
      setAuthenticated(true);

      history.push("/home");
    } catch (error) {
      setLoading(false);
    }
  }

  async function handleLoginSocial(idToken: string) {
    setLoading(true);
    api.defaults.headers.common["Auth-Id-Token"] = `Bearer ${idToken}`;
    const response = await api.post("/auth");

    api.defaults.headers.common["Auth-Id-Token"] = "";
    localStorage.setItem("token", JSON.stringify(response.data.token));
    api.defaults.headers.common[
      "Authorization"
    ] = `Bearer ${response.data.token}`;
    setAuthenticated(true);
    setLoading(false);
    history.push("/home");
  }

  function handleLoginSocialServer(token: string) {
    setAuthenticated(true);
    localStorage.setItem("token", JSON.stringify(token));
    api.defaults.headers.common["Authorization"] = `Bearer ${token}`;
    history.push("/home");
  }

  function handleLogout() {
    setAuthenticated(false);
    localStorage.removeItem("token");
    api.defaults.headers.common["Authorization"] = "";
    history.push("/login");
  }

  return {
    authenticated,
    loading,
    handleLogin,
    handleLoginSocial,
    handleLoginSocialServer,
    handleLogout,
  };
}
