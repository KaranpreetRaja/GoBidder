import React, { useReducer, useContext, createContext } from "react";
import { User } from "src/types/User";
import { AuthAction, AuthReducer } from "src/context/auth/auth_reducer";

/**
 * Representation of information about the currently logged-in user.
 */
export interface AuthState {
    loggedIn: boolean;
    user?: User;
    token?: string;
    loading?: boolean;
    errorMessage?: string;
};

const AuthStateContext = createContext<AuthState>({ loggedIn: false });

/**
 * Get the current auth state.
 *
 * @returns The current auth state.
 */
export function useAuthState(): AuthState {
    const context = useContext(AuthStateContext);
    if (context === undefined) {
        throw new Error("useAuthState must be used within AuthProvider");
    }
    return context;
}

function _defaultDispatcher(): AuthState {
    throw new Error("Default dispatcher for auth state should not be used");
}

const AuthDispatchContext = createContext<React.Dispatch<AuthAction>>(_defaultDispatcher);

/**
 * Returns the dispatcher for the auth state.
 *
 * @returns The dispatcher for the auth state.
 */
export function useAuthDispatch(): React.Dispatch<AuthAction> {
    const context = useContext(AuthDispatchContext);
    if (context === undefined) {
        throw new Error("useAuthState must be used within AuthProvider");
    }
    return context;
}

export const AuthProvider: React.FC<{children: React.ReactNode}> = ({ children }) => {
    const initialState: AuthState = { loggedIn: false };
    const [authState, authStateDispatch] = useReducer(AuthReducer, initialState);

    return (
        <AuthStateContext.Provider value={authState}>
            <AuthDispatchContext.Provider value={authStateDispatch}>
                {children}
            </AuthDispatchContext.Provider>
        </AuthStateContext.Provider>
    );
}
