import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter, Route, Routes } from "react-router-dom";
import { App } from "./App";
import "./index.css";

import { ChakraProvider } from "@chakra-ui/react";
import { AuthProvider } from "./context/AuthContext";
import { GoogleOAuthProvider } from "@react-oauth/google";

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <React.StrictMode>
    <BrowserRouter>
      <ChakraProvider>
        <AuthProvider>
          <GoogleOAuthProvider clientId="310109923674-la5thl4s4t0b2ajp6acdhq7tra74dn31.apps.googleusercontent.com">
            <Routes>
              <Route path="/*" element={<App />} />
            </Routes>
          </GoogleOAuthProvider>  
        </AuthProvider>
      </ChakraProvider>
    </BrowserRouter>
  </React.StrictMode>
);