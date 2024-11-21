import React from "react";
import { AuthAction, AuthActionEnum } from "src/context/auth/auth_reducer";
import { User } from "src/types/User";

/**
 * Logs in a user.
 *
 * Also updates the auth context via the dispatch function to make the currently
 * logged-in user accessible to components with the auth context.
 *
 * @param email The email of the user.
 * @param password The password of the user.
 * @param dispatch The dispatch function to change the user in the auth context.
 */
export function login(
    email: string,
    password: string,
    dispatch: React.Dispatch<AuthAction>
) {
    console.log(email);

    const user = new User("Name", email);

    dispatch({
        user: user,
        token: "TOKEN FROM API CALL",
        type: AuthActionEnum.LOGIN_SUCCESS
    });
}

export function getAuthToken(): string | null {
    const now = Date.now();

    const localToken = localStorage.getItem("authToken");
    const localExpiry = localStorage.getItem("tokenExpiry");
    if (localToken && localExpiry && parseInt(localExpiry, 10) > now) {
        return localToken;
    }

    const sessionToken = sessionStorage.getItem("authToken");
    const sessionExpiry = sessionStorage.getItem("tokenExpiry");
    if (sessionToken && sessionExpiry && parseInt(sessionExpiry, 10) > now) {
        return sessionToken;
    }

    return null;
}
