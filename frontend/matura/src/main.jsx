import React from 'react'
import ReactDOM from 'react-dom/client'

import {createBrowserRouter, Navigate, RouterProvider} from "react-router-dom";
import LoginForm from "./LoginForm.jsx";
import {ChakraProvider} from "@chakra-ui/react";

const router = createBrowserRouter([
    {
        path: "/",
        element: <Navigate to={'/login'}/>
    },
    {
        path: "/login",
        element: <LoginForm/>,
    },
]);

ReactDOM
    .createRoot(document.getElementById('root'))
    .render(
        <React.StrictMode>
            <ChakraProvider>
                    <RouterProvider router={router}/>
            </ChakraProvider>
        </React.StrictMode>,
    )
