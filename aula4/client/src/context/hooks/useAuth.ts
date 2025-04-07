import { useState, useEffect } from "react";
import { api } from "../../lib/axios";
import { AuthenticatedUser, AuthenticationResponse } from "../../commons/types";

export function useAuth() {
  const [authenticated, setAuthenticated] = useState(false);
  const [authenticatedUser, setAuthenticatedUser] = useState<AuthenticatedUser>();
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
  
  function handleLogout() {
    setAuthenticated(false);
    localStorage.removeItem("token");
    api.defaults.headers.common["Authorization"] = "";
    setAuthenticatedUser(undefined);
  }

  function handleLogin(response: AuthenticationResponse) {
    localStorage.setItem("token", JSON.stringify(response.token));
      api.defaults.headers.common[
        "Authorization"
      ] = `Bearer ${response.token}`;
      
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