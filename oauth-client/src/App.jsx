import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import toast from 'react-hot-toast'
import { useAuth } from './config/authcontext'

function App() {
  const { isAuthenticated } = useAuth();
  console.log(isAuthenticated);

  return (
    <>
      <Toaster />
      <button onClick={() => {
        toast.success("You clicked")
      }}>Click me</button>
    </>
  )
}

export default App
