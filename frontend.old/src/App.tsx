import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import { AuthProvider } from 'src/context/auth';
import Login from './pages/Login';
import AuctionList from './pages/AuctionList';
import CreateAuction from './pages/CreateAuction';
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
                  <Route path="/create-auction" element={
                      <PrivateRoute>
                          <CreateAuction />
                      </PrivateRoute>
                  } />
                  <Route path="/" element={<Navigate to="/login" replace />} />
              </Routes>
          </AuthProvider>
      </Router>
  );
}

export default App;