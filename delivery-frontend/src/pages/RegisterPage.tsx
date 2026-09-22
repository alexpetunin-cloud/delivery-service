import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { register } from "../api/authApi";

export default function RegisterPage() {
    const navigate = useNavigate();

    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [name, setName] = useState("");
    const [phone, setPhone] = useState("");
    const [address, setAddress] = useState("");

    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (
        event: React.FormEvent<HTMLFormElement>
    ) => {
        event.preventDefault();

        setError(null);

        if (password !== confirmPassword) {
            setError("Пароли не совпадают");
            return;
        }

        setLoading(true);

        try {
            await register({
                email,
                password,
                name,
                phone,
                address,
            });

            navigate("/login");
        } catch (error) {
            console.error(error);
            setError("Не удалось зарегистрироваться");
        } finally {
            setLoading(false);
        }
    };

    return (
        <main className="auth-page">
            <div className="auth-card">
                <div className="auth-logo">🚚</div>

                <h1>Регистрация</h1>

                <p className="auth-subtitle">
                    Создайте аккаунт, чтобы продолжить
                </p>

                <form
                    className="auth-form"
                    onSubmit={handleSubmit}
                >
                    <div className="form-field">
                        <label htmlFor="email">
                            Email
                        </label>

                        <input
                            id="email"
                            type="email"
                            value={email}
                            onChange={(event) =>
                                setEmail(event.target.value)
                            }
                            placeholder="you@example.com"
                            required
                        />
                    </div>

                    <div className="form-field">
                        <label htmlFor="password">
                            Пароль
                        </label>

                        <input
                            id="password"
                            type="password"
                            value={password}
                            onChange={(event) =>
                                setPassword(event.target.value)
                            }
                            placeholder="Введите пароль"
                            required
                        />
                    </div>

                    <div className="form-field">
                        <label htmlFor="confirmPassword">
                            Повторите пароль
                        </label>

                        <input
                            id="confirmPassword"
                            type="password"
                            value={confirmPassword}
                            onChange={(event) =>
                                setConfirmPassword(event.target.value)
                            }
                            placeholder="Повторите пароль"
                            required
                        />
                    </div>

                    <div className="form-field">
                        <label htmlFor="name">
                            Имя
                        </label>

                        <input
                            id="name"
                            type="text"
                            value={name}
                            onChange={(event) =>
                                setName(event.target.value)
                            }
                            placeholder="Иван"
                            required
                        />
                    </div>

                    <div className="form-field">
                        <label htmlFor="phone">
                            Телефон
                        </label>

                        <input
                            id="phone"
                            type="tel"
                            value={phone}
                            onChange={(event) =>
                                setPhone(event.target.value)
                            }
                            placeholder="+79991234567"
                            required
                        />
                    </div>

                    <div className="form-field">
                        <label htmlFor="address">
                            Адрес доставки
                        </label>

                        <input
                            id="address"
                            type="text"
                            value={address}
                            onChange={(event) =>
                                setAddress(event.target.value)
                            }
                            placeholder="ул. Ленина, 10"
                            required
                        />
                    </div>

                    {error && (
                        <div className="auth-error">
                            {error}
                        </div>
                    )}

                    <button
                        className="auth-button"
                        type="submit"
                        disabled={loading}
                    >
                        {loading
                            ? "Регистрируем..."
                            : "Зарегистрироваться"}
                    </button>
                </form>

                <p className="auth-switch">
                    Уже есть аккаунт?{" "}
                    <Link to="/login">
                        Войти
                    </Link>
                </p>
            </div>
        </main>
    );
}