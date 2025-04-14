import { User } from "../commons/types";
import { api } from "../lib/axios";

const signup = (user: User) => {
  return api.post("/users", user);
};

const getCurrentUser = () => {
  return ''; //JSON.parse(localStorage.getItem("user"));
};

const isAuthenticated = () => {
  return localStorage.getItem("token") ? true : false;
};

const AuthService = {
  signup,
  getCurrentUser,
  isAuthenticated,
};

export default AuthService;
