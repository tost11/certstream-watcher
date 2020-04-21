import React, {useContext, useState} from 'react';
import UserContext from "../service/UserContext";
import DataService from "../service/DataService";
import {useHistory} from "react-router";

function UserComponent() {

    let history = useHistory();

    const userContext = useContext(UserContext);
    const [user, setUser] = useState({
        username: userContext.user ? userContext.user.username : "",
        password: "",
        notifyMail: userContext.user ? userContext.user.notifyMail : "",
        mailConfirmed: userContext.user ? userContext.user.mailConfirmed : false
    });

    const edit = (param, value) => {
        let att = {...user};
        att[param] = value;
        setUser(att);
    };

    const RegisterButton = () => (
        <button
            type='button'
            onClick={() => {
                DataService.createUser(user).then(res => {
                    userContext.setToken(res);
                    history.push('/')
                }).catch(err => {
                    err.response.json().then((text) => {
                        alert(text.message);
                    });
                });
            }}
        >
            Registrieren
        </button>
    );

    const ChangePasswordButton = () => (
        <button
            type='button'
            onClick={() => {
                DataService.updateUser({
                    password: user.password,
                    notifyMail: null
                }).then(res => {
                    edit("password", "");
                }).catch(err => {
                    err.response.json().then((text) => {
                        alert(text.message);
                    });
                });
            }}
        >
            Speichern
        </button>
    );

    const ChangeNotifyMail = () => (
        <button
            type='button'
            onClick={() => {
                DataService.updateUser({
                    password: null,
                    notifyMail: user.notifyMail
                }).then(res => {
                    edit("notifyMail", res.notifyMail);
                    alert("Mail saved, please check verification mail");
                }).catch(err => {
                    err.response.json().then((text) => {
                        alert(text.message);
                    });
                });
            }}
        >
            Speichern
        </button>
    );

    return (<div>
        <h4>{userContext.user ? "Edit User Settings" : "Create User"}</h4>

        <p>
            Username*:
            <input disabled={userContext.user} value={user.username}
                   onChange={(ev) => edit("username", ev.target.value)}/>
        </p>
        <p>
            Password*:
            <input type="password" value={user.password}
                   onChange={(ev) => edit("password", ev.target.value)}/>
            {userContext.user && <ChangePasswordButton/>}
        </p>
        {userContext.user &&
        <p>
            Notify Mail:
            <input value={user.notifyMail} onChange={(ev) => edit("notifyMail", ev.target.value)}/>
            {userContext.user && <ChangeNotifyMail/>}
        </p>
        }
        {userContext.user &&
        <p>
            Mail Confirmed: <input checked={user.mailConfirmed} type="checkbox" disabled={true}
                                   value={user.mailConfirmed}/>
        </p>
        }
        {!userContext.user && <RegisterButton/>}
    </div>)
}

export default UserComponent;