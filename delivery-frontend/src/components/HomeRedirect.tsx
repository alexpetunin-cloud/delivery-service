import { Navigate } from "react-router-dom";
import { isAuthenticated } from "../services/authService";

export default function HomeRedirect() {
    if (isAuthenticated()) {
        return <Navigate to="/restaurants" replace />;
    }

    return <Navigate to="/login" replace />;
}