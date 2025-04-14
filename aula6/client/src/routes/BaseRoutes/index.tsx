import { useContext } from "react";
import { AuthContext } from "../../context/AuthContext";
import { AuthenticatedRoutes } from "../AuthenticatedRoutes";
import { SignRoutes } from "../SignRoutes";

export function BaseRoutes() {
  const { authenticated } = useContext(AuthContext);
  return authenticated ? <AuthenticatedRoutes /> : <SignRoutes />;
}
