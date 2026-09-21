const TOKEN_KEY = "access_token";
const EMAIL_KEY = "user_email";

export const saveToken = (token: string): void => {
    localStorage.setItem(TOKEN_KEY, token);
};

export const getToken = (): string | null => {
    return localStorage.getItem(TOKEN_KEY);
};

export const removeToken = (): void => {
    localStorage.removeItem(TOKEN_KEY);
};

export const saveEmail = (email: string): void => {
    localStorage.setItem(EMAIL_KEY, email);
};

export const getEmail = (): string | null => {
    return localStorage.getItem(EMAIL_KEY);
};

export const removeEmail = (): void => {
    localStorage.removeItem(EMAIL_KEY);
};

export const isAuthenticated = (): boolean => {
    return getToken() !== null;
};