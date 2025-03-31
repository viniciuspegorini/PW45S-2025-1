import { Routes, Route } from 'react-router-dom';
import { LoginPage } from '../../pages/LoginPage';
import { UserSignupPage } from '../../pages/UserSignUpPage';

export function SignRoutes() {

    return (
        <Routes>
            <Route path="/" element={<LoginPage />} />
            <Route path="/signup" element={<UserSignupPage />} />

            <Route path="*" element={<LoginPage />} />
        </Routes>
    )
}