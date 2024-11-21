import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import './App.css';
import { AuthProvider } from 'src/context/auth';
import Main from './Main';
import Login from './pages/Login';
import AuctionList from './pages/AuctionList';

function App() {
    return (
        <div className="App">
            <header className="App-header">
                <AuthProvider>
                    <BrowserRouter>
                        <Routes>
                            <Route path="/login" element={<Login />} />
                            <Route path="/auction-list" element={<AuctionList />} />
                            <Route path="/" element={<Navigate to="/auction-list" />} />
                            <Route path="*" element={<Main />} />
                        </Routes>
                    </BrowserRouter>
                </AuthProvider>
            </header>
        </div>
    );
}

export default App;