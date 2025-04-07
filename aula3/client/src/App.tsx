import { ChakraProvider } from '@chakra-ui/react'
import './App.css'
import { BaseRoutes } from './routes/BaseRoutes'

export function App() {

  return (
    <ChakraProvider>
      <BaseRoutes />
    </ChakraProvider>
  )
}
