import React from "react"
import { useAuthDispatch, login } from "src/context/auth";

const Login: React.FC = () => {

    const dispatch = useAuthDispatch();

    const handleSubmit: React.FormEventHandler<HTMLFormElement> = (event) => {
        // Stop the form from being submitted via the default action
        event.preventDefault();

        // Get email and password from the form
        const email = event.currentTarget.email.value;
        const password = event.currentTarget.password.value;

        // Log in user
        login(email, password, dispatch);
    }

    return (
        <form onSubmit={handleSubmit}>
            <label htmlFor="email">
                Email:
            </label>
            <input id="email" name="email" type="email" />
            <br />
            <label htmlFor="password">
                Password:
            </label>
            <input id="password" name="password" type="password" />
            <br />
            <button type="submit">
                Submit
            </button>
        </form>
    );
};

export default Login;
