import keycloak from "./keycloak";
import {
    createContext,
    useContext,
    useState,
    useEffect
} from "react";

// create auth context
const AuthContext = createContext(null);

// auth provider
export const AuthProvider = ({ children }) => {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [token, setToken] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        keycloak
            .init({
                onLoad: "login-required",
                checkLoginIframe: false,
                pkceMethod: "S256",
            })
            .then((authenticated) => {
                setIsAuthenticated(authenticated);
                setToken(keycloak.token);
                setLoading(false);
            })
            .catch(() => {
                setLoading(false);
            });
    }, []);


    if (loading) {
        return <div>Loading authentication...</div>;
    }

    return (
        <AuthContext.Provider
            value={{
                isAuthenticated,
                token,
                keycloak,
                logout: () => keycloak.logout(),
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};

// custom hook
export const useAuth = () => useContext(AuthContext);
