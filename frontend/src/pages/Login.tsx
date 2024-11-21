import React, { useState } from "react";
import { useEffect } from 'react';
import { useAuthDispatch, login } from "src/context/auth";
import { useNavigate } from "react-router-dom";
import { AuthActionEnum } from "src/context/auth/auth_reducer";
import { useAuthState } from '../context/auth/auth_context';
import "./Login.css";

const Login: React.FC = () => {
    const [isSignUp, setIsSignUp] = useState<boolean>(false);
    const [error, setError] = useState<string | null>(null);
    const [loading, setLoading] = useState<boolean>(false);
    const [rememberMe, setRememberMe] = useState<boolean>(false);
    const [signUpData, setSignUpData] = useState({
        email: "",
        password: "",
        fullName: "",
        cardNumber: "",
        csv: "",
        expirationDate: "",
        billingAddress: "",
    });

    const authState = useAuthState();
    const dispatch = useAuthDispatch();
    const navigate = useNavigate();

    useEffect(() => {
        if (authState.loggedIn) {
            navigate('/auction-list');
        }
    }, [authState.loggedIn, navigate]);

    const handleLoginSubmit: React.FormEventHandler<HTMLFormElement> = async (event) => {
        event.preventDefault();

        const email = event.currentTarget.email.value.trim();
        const password = event.currentTarget.password.value.trim();

        if (!email || !password) {
            setError("Both email and password are required.");
            return;
        }

        try {
            setError(null);
            setLoading(true);

            const response = await fetch("http://localhost:8080/api/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ email, password }),
            });

            const data = await response.json();

            if (response.status === 401) {
                setError("Invalid email or password. Please try again.");
                setLoading(false);
                return;
            }

            if (!response.ok) {
                setError(data.message || "Failed to login. Please try again.");
                setLoading(false);
                return;
            }

            const { token, expiresIn } = data;

            if (rememberMe) {
                localStorage.setItem("authToken", token);
                localStorage.setItem("tokenExpiry", (Date.now() + expiresIn).toString());
            } else {
                sessionStorage.setItem("authToken", token);
                sessionStorage.setItem("tokenExpiry", (Date.now() + expiresIn).toString());
            }

            dispatch({
                type: AuthActionEnum.LOGIN_SUCCESS,
                user: { name: email.split("@")[0], email },
                token,
            });

            login(email, password, dispatch);
            console.log(`Token expires in: ${expiresIn}ms`);
            
        } catch (err) {
            setError("An unexpected error occurred. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    const handleSignUpSubmit: React.FormEventHandler<HTMLFormElement> = async (event) => {
        event.preventDefault();

        const { email, password, fullName, cardNumber, csv, expirationDate, billingAddress } = signUpData;

        if (!email || !password || !fullName || !cardNumber || !csv || !expirationDate || !billingAddress) {
            setError("All fields are required for sign up.");
            return;
        }

        try {
            setError(null);
            setLoading(true);

            const response = await fetch("http://localhost:8080/api/auth/signup", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    email,
                    password,
                    fullName,
                    cardNumber,
                    csv,
                    expirationDate,
                    billingAddress,
                }),
            });

            const data = await response.json();

            if (!response.ok) {
                setError(data.message || "Failed to sign up. Please try again.");
                setLoading(false);
                return;
            }

            setIsSignUp(false);
            setError("Sign up successful! Please log in.");
        } catch (err) {
            setError("An unexpected error occurred. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    const handleSignUpInputChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setSignUpData((prev) => ({
            ...prev,
            [name]: value,
        }));
    };

    return (
        <div className="login-container">
            <h2>{isSignUp ? "Sign Up" : "Login"}</h2>
            {error && <div className="error-message">{error}</div>}
            {isSignUp ? (
                <form onSubmit={handleSignUpSubmit} className="login-form">
                    <div className="form-group">
                        <label htmlFor="email">Email:</label>
                        <input
                            id="email"
                            name="email"
                            type="email"
                            value={signUpData.email}
                            onChange={handleSignUpInputChange}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="password">Password:</label>
                        <input
                            id="password"
                            name="password"
                            type="password"
                            value={signUpData.password}
                            onChange={handleSignUpInputChange}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="fullName">Full Name:</label>
                        <input
                            id="fullName"
                            name="fullName"
                            type="text"
                            value={signUpData.fullName}
                            onChange={handleSignUpInputChange}
                            required
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="cardNumber">Card Number:</label>
                        <input
                            id="cardNumber"
                            name="cardNumber"
                            type="text"
                            value={signUpData.cardNumber}
                            onChange={handleSignUpInputChange}
                            required
                            maxLength={16}
                            pattern="\d{16}"
                            title="Enter a valid 16-digit card number"
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="csv">CSV:</label>
                        <input
                            id="csv"
                            name="csv"
                            type="text"
                            value={signUpData.csv}
                            onChange={handleSignUpInputChange}
                            required
                            maxLength={3}
                            pattern="\d{3}"
                            title="Enter a valid 3-digit CSV"
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="expirationDate">Expiration Date (MM/YY):</label>
                        <input
                            id="expirationDate"
                            name="expirationDate"
                            type="text"
                            value={signUpData.expirationDate}
                            onChange={handleSignUpInputChange}
                            required
                            pattern="^(0[1-9]|1[0-2])\/\d{2}$"
                            title="Enter a valid expiration date in MM/YY format"
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="billingAddress">Billing Address:</label>
                        <textarea
                            id="billingAddress"
                            name="billingAddress"
                            value={signUpData.billingAddress}
                            onChange={handleSignUpInputChange}
                            required
                        />
                    </div>
                    <button type="submit" className="login-button" disabled={loading}>
                        {loading ? "Signing up..." : "Sign Up"}
                    </button>
                    <p className="toggle-form">
                        Already have an account?{" "}
                        <button type="button" onClick={() => setIsSignUp(false)} className="toggle-button">
                            Login
                        </button>
                    </p>
                </form>
            ) : (
                <form onSubmit={handleLoginSubmit} className="login-form">
                    <div className="form-group">
                        <label htmlFor="email">Email:</label>
                        <input id="email" name="email" type="email" required />
                    </div>
                    <div className="form-group">
                        <label htmlFor="password">Password:</label>
                        <input id="password" name="password" type="password" required />
                    </div>
                    <div className="form-group remember-me">
                        <input
                            id="rememberMe"
                            name="rememberMe"
                            type="checkbox"
                            checked={rememberMe}
                            onChange={() => setRememberMe(!rememberMe)}
                        />
                        <label htmlFor="rememberMe">Remember Me</label>
                    </div>
                    <button type="submit" className="login-button" disabled={loading}>
                        {loading ? "Logging in..." : "Submit"}
                    </button>
                    <p className="toggle-form">
                        Don't have an account?{" "}
                        <button type="button" onClick={() => setIsSignUp(true)} className="toggle-button">
                            Sign Up
                        </button>
                    </p>
                </form>
            )}
        </div>
    );
};

export default Login;
