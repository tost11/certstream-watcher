import React, {useContext, useState} from 'react'
import UserContext from "../service/UserContext";
import DataService from "../service/DataService";
import {useHistory} from "react-router";
import {Link} from "react-router-dom";

function HeaderComponent() {
    const userContext = useContext(UserContext);
    let history = useHistory();

    const logout = () => {
        DataService.logout();
        userContext.setUser(null);
    };

    const [credentials, setCredentials] = useState({
        username: "",
        password: ""
    });

    const edit = (param, value) => {
        let att = {...credentials};
        att[param] = value;
        setCredentials(att);
    };

    const LoginButton = () => (
        <button
            type='button'
            onClick={() => {
                DataService.login(credentials).then(res => {
                    userContext.setToken(res);
                    history.push('/home')
                }).catch(err => {
                    alert("Wrong credentials");
                });
            }}
        >
            Login
        </button>
    );

    return (<div className="Main">
        <span className="App-header">
            <h3>Cerstream Watcher</h3>
            {userContext.user ?
                <span>
                    <div>Angemedet als: {userContext.user.username}&nbsp;<input defaultValue="Logout" type="Button"
                                                                                onClick={logout}/></div>
                    <Link style={{color: 'blue', textDecoration: 'inherit'}} to="/home">Home</Link>&nbsp;
                    <Link style={{color: 'blue', textDecoration: 'inherit'}} to="/user">Benutzer</Link>
                </span> :
                <span>
                    <div className="Login">
                        Username:
                        <input value={credentials.username} onChange={(ev) => edit("username", ev.target.value)}/>
                        &nbsp;Password:
                        <input type="password" value={credentials.password}
                               onChange={(ev) => edit("password", ev.target.value)}/>
                        &nbsp;<LoginButton/>
                    </div>
                    <Link style={{color: 'blue', textDecoration: 'inherit'}} to="/home">Home</Link>&nbsp;
                    <Link style={{color: 'blue', textDecoration: 'inherit'}} to="/register">Registrieren</Link>
                </span>}
        </span>
    </div>);
}

export default HeaderComponent;