"use client"

import React, { createContext, useState, useContext, ReactNode } from 'react';

interface AuthContextType {
    isAuthenticated: boolean;
    token: string | null;
    login: (token: string) => void;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

interface AuthProviderProps {
    children: ReactNode;
}

/**
 * Provider for the context (global state) of the currently-authenticated user.
 *
 * @param param0 Children components.
 *
 * @returns The authentication context provider.
 */
export const AuthProvider = ({ children }: AuthProviderProps) => {
    const [token, setToken] = useState<string | null>(localStorage.getItem('jwt'));

    const login = (token: string) => {
        setToken(token);
        localStorage.setItem('jwt', token);
    };

    const logout = () => {
        setToken(null);
        localStorage.removeItem('jwt');
    };

    return (
        <AuthContext.Provider
            value={{
                isAuthenticated: token !== null,
                token,
                login,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};

/**
 * Hook to get the state of the currently-authenticated user and functions to
 * login and logout.
 *
 * @returns The token of the currently-authenticated user, a boolean value,
 * indicating whether or not they are authenticated, a function to log in a
 * user, and a function to log out a user.
 */
export const useAuth = (): AuthContextType => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};
