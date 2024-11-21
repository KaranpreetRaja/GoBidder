import React from "react";
import { useAuthState } from "./context/auth";
import Login from "./pages/Login";
import AuctionList from "./pages/AuctionList";

const Main = () => {
    const authState = useAuthState();

    return (
        <>
            {
                authState.loggedIn
                    ? <AuctionList />
                    : <Login />
            }
        </>
    );
};

export default Main;