import { useContext } from "react";
import { AuthContext } from "../../context/AuthContext";
import { Navigate, Outlet, useLocation } from "react-router-dom";

interface RequireAuthProps {
    allowedRoles: string[];
}

export function RequireAuth( {allowedRoles} : RequireAuthProps) {
    const {authenticated, authenticatedUser} = useContext(AuthContext);
    const location = useLocation();

    return authenticatedUser?.authorities?.find((authority) => 
        allowedRoles.includes(authority.authority)) ? (
            <Outlet />
    ) : authenticated ? (
        <Navigate to="/unauthorized" state={{ from: location }} replace />
    ) : (
        <Navigate to="/login" state={{ from: location }} replace />
    );
}