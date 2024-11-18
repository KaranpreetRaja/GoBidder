import { useAuthState } from "./context/auth";
import Login from "./pages/Login";

const Main = () => {
    const authState = useAuthState();
    console.log(authState);

    return (
        <>
            {
                authState.loggedIn
                    ? (
                        <p>
                            Hello {authState.user?.name} with email {authState.user?.email}!
                        </p>
                    )
                    : <Login />
            }
        </>
    );
};

export default Main;
