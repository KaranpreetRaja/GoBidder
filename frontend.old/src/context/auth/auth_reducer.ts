import React from "react";
import { AuthState } from "src/context/auth/auth_context";
import { User } from "src/types/User";

/**
 * Possible authentication actions.
 */
export enum AuthActionEnum {
    REQUEST_LOGIN,
    LOGIN_SUCCESS,
    LOGIN_ERROR,
    LOGOUT
};

/**
 * Representation of authentication actions.
 */
export interface AuthAction {
    user?: User;
    token?: string;
    type: AuthActionEnum;
};

/**
 * Transforms state according to the action.
 *
 * This function will be used by the
 * {@link https://react.dev/reference/react/useReducer|useReducer} React hook.
 *
 * @param state The current state.
 * @param action The action to apply to the state.
 *
 * @returns The new state after applying the action to it.
 */
export const AuthReducer: React.Reducer<AuthState, AuthAction> = (state, action) => {
    switch (action.type) {
        case AuthActionEnum.REQUEST_LOGIN:
            console.log('no');
            return {
                ...state,
                loggedIn: false,
                loading: true
            };
        case AuthActionEnum.LOGIN_SUCCESS:
            console.log("suceess");
            return {
                ...state,
                loggedIn: true,
                user: action.user,
                token: action.token,
                loading: false
            };
        case AuthActionEnum.LOGIN_ERROR:
            return {
                ...state,
                loggedIn: false,
                loading: false
            };
        case AuthActionEnum.LOGOUT:
            return {
                ...state,
                loggedIn: false,
                loading: false,
                user: undefined,
                token: undefined
            };
        default:
            throw new Error("Unknown auth action type");
    }
};
