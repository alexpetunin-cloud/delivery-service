import { Navigate } from "react-router-dom";
import { getToken } from "../services/authService";

interface PublicRouteProps {
    children: React.ReactNode;
}

export default function PublicRoute({
    children,
}: PublicRouteProps) {
    if (getToken()) {
        return <Navigate to="/restaurants" replace />;
    }

    return children;
}