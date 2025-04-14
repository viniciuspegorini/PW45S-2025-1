import { AuthProvider } from "./context/AuthContext";
import { BaseRoutes } from "./routes/BaseRoutes";
import { ChakraProvider } from "@chakra-ui/react";

export function App() {

  return (
    <ChakraProvider >
      <AuthProvider>
        <BaseRoutes />
      </AuthProvider>
    </ChakraProvider>
  );
}
