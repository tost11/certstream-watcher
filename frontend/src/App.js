import React, {useEffect, useState} from 'react';
import './App.css';
import DomainWatcherComponent from "./components/DomainWatcherComponent";
import HeaderComponent from "./components/HeaderComponent";
import DataService from "./service/DataService";
import {UserProvider} from "./service/UserContext";
import createBrowserHistory from 'history/createBrowserHistory'
import {Route, Router} from "react-router-dom";
import MainComponent from "./MainComponent";
import UserComponent from "./components/UserComponent";

const newHistory = createBrowserHistory();

function App() {

    DataService.initUrl();

    const [user, setUser] = useState(null);
    const [token, setToken] = useState(DataService.getToken());

    const userProvider = {
        user: user,
        setUser: setUser,
        token: token,
        setToken: setToken
    };

    const fetchUser = () => {
        DataService.getUser().then(res => {
            setUser(res);
        }).catch(err => {
            if (err.response.status === 401) {
                resetLogin();
            }
        });
    };

    const resetLogin = () => {
        DataService.logout();
        setToken(null);
    };

    useEffect(() => {
        if (!user) {
            if (token) {
                if (!DataService.isTokenExpired(token)) {
                    fetchUser();
                } else {
                    resetLogin();
                }
            }
        } else {
            if (!token || DataService.isTokenExpired(token)) {
                setUser(null);
            }
        }
    }, [token]);

    const [inited, setInited] = useState(false);

    const wsOk = () => {
        setInited(true);
    };

    const swError = () => {
        setInited(false);
        initWebsocket();
    };

    const initWebsocket = () => {
        if (DataService.getWebsocket() === null) {
            DataService.connectWebSocket(wsOk, swError);
        } else {
            wsOk();
        }
    };

    useEffect(() => {
        initWebsocket();
    }, []);

    return (
        inited ?
            <UserProvider value={userProvider}>
                <Router history={newHistory}>
                    <Route path="/" component={HeaderComponent}/>
                    <Route path="/home" component={MainComponent}/>
                    <Route path="/register" component={UserComponent}/>
                    <Route path="/user" component={UserComponent}/>
                    <Route path="/watcher/:id" strict={false} component={DomainWatcherComponent}/>
                </Router>
            </UserProvider>
            :
            <div>
                Loading...
            </div>
    )
}

export default App;
