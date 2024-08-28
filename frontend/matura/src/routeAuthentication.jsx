import {Navigate} from "react-router-dom";
import {User} from './services/userService.js'

const permissionLevels = {
    'STUDENT': 1,
    'INSTRUCTOR': 2,
    'ADMIN': 3
};

const isAuthenticated = () => {
    try {
        return User.fromLocalStorage().validate()
    } catch (e) {
        // There's no user saved locally, therefore no one is logged in
        return false;
    }
}

export const withAuthentication = (Component, requiredPermission = 'STUDENT') => {
    const WithAuthenticationWrapper = (props) => {
        if (!isAuthenticated() || permissionLevels[User.fromLocalStorage().role] < permissionLevels[requiredPermission]) {
            return <Navigate to='/login' />;
        }

        return <Component {...props} />;
    };

    return WithAuthenticationWrapper;
};
