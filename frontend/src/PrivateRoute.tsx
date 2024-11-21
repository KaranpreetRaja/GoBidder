import React from 'react';
import { Navigate } from 'react-router-dom';
import { useAuthState } from 'src/context/auth/auth_context';

export const PrivateRoute = ({ children }: { children: React.ReactNode }) => {
  const authState = useAuthState();
  
  if (!authState.loggedIn) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
};