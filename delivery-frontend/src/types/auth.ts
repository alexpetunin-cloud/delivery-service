export interface LoginRequest {
    email: string;
    password: string;
}

export interface RegisterRequest {
    email: string;
    password: string;
    name: string;
    phone: string;
    address: string;
}

export interface LoginResponse {
    token: string;
}