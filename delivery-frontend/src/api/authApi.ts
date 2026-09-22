import { api } from "./axios";
import type {
    LoginRequest,
    LoginResponse,
    RegisterRequest,
} from "../types/auth";

export const login = async (
    data: LoginRequest
): Promise<LoginResponse> => {
    const response = await api.post<LoginResponse>(
        "/api/auth/login",
        data
    );

    return response.data;
};

export const register = async (
    data: RegisterRequest
): Promise<string> => {
    const response = await api.post<string>(
        "/api/auth/register",
        data
    );

    return response.data;
};