import { FormEvent, useState } from "react";
import { login } from "../api/authApi";
import { getOrderById } from "../api/orderApi";
import { saveToken, saveEmail } from "../services/authService";

export default function LoginPage() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    const handleGetOrder = async () => {
        try {
            const order = await getOrderById(1);

            console.log("ORDER:", order);
        } catch (error) {
            console.error("GET ORDER ERROR:", error);
        }
    };

    const handleSubmit = async (event: FormEvent) => {
        event.preventDefault();

        try {
            const response = await login({
                email,
                password,
            });

            saveToken(response.token);
            saveEmail(email);

            console.log("LOGIN SUCCESS");
            console.log("TOKEN SAVED");

        } catch (error) {
            console.error("LOGIN ERROR:", error);
        }
    };

    return (
        <div>
            <h1>Login</h1>

            <form onSubmit={handleSubmit}>
                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(event) => setEmail(event.target.value)}
                />

                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(event) => setPassword(event.target.value)}
                />

                <button type="submit">
                    Login
                </button>
            </form>
        </div>
    );
}