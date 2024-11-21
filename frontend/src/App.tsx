import React from 'react';
// import logo from './logo.svg';
import './App.css';
import { AuthProvider } from 'src/context/auth';
import Main from './Main';

function App() {
    return (
        <div className="App">
            <header className="App-header">
                <AuthProvider>
                    <Main />
                </AuthProvider>
            </header>
        </div>
    );
}

export default App;
