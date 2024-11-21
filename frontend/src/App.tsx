import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import { AuthProvider } from 'src/context/auth';
import Login from './pages/Login';
import AuctionList from './pages/AuctionList';
import { PrivateRoute } from './PrivateRoute';

function App() {
    return (
      <Router>
        <AuthProvider>
          <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/auction-list" element={
              <PrivateRoute>
                <AuctionList />
              </PrivateRoute>
            } />
            {/* <Route path="/auction/:id" element={
              <PrivateRoute>
                <Auction />
              </PrivateRoute>
            } /> */}
            {/* Redirect root to login if not authenticated */}
            <Route path="/" element={<Navigate to="/login" replace />} />
          </Routes>
        </AuthProvider>
      </Router>
    );
  }

export default App;