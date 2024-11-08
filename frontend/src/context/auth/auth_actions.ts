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
    // ==================================
    // TODO: Get user data from API call.
    // ----------------------------------
    const user = new User("Name", email);
    // ==================================

    dispatch({
        user: user,
        token: "TOKEN FROM API CALL",
        type: AuthActionEnum.LOGIN_SUCCESS
    });
}
