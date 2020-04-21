import React, {useContext, useEffect, useState} from 'react';
import DataService from "../service/DataService";
import UserContext from "../service/UserContext";
import {useHistory} from "react-router";


function DomainWatcherComponent(props) {

    let history = useHistory();

    const [id, setId] = useState(null);
    const [searchTerm, setSearchTerm] = useState("");
    const [regex, setRegex] = useState(false);
    const [active, setActive] = useState(true);
    const [sendMail, setSendMail] = useState(false);
    const [mailOnUpdate, setMailOnUpdate] = useState(false);

    const userContext = useContext(UserContext);
    const newOne = !props.match.params.id || props.match.params.id === "new";

    const titleType = !newOne ? "bearbeiten" : "erstellen";

    useEffect(() => {
        if (!newOne) {
            DataService.getWatcher(props.match.params.id).then(res => {
                //TODO find better way to to that
                setId(res.id);
                setSearchTerm(res.searchTerm);
                setRegex(res.regex);
                setSendMail(res.sendMail);
                setMailOnUpdate(res.mailOnUpdate);
                setActive(res.active);
            }).then(err => {

            });
        }
    }, []);

    const createWatcher = () => {
        let obj = {
            id: id,
            searchTerm: searchTerm,
            regex: regex,
            sendMail: sendMail,
            active: active,
            mailOnUpdate: mailOnUpdate
        };
        DataService.createWatcher(obj).then(res => {
            history.push("/home");
        }).catch(err => {

        })
    };

    return (
        <>
            {<p>
                <h2>Domain Watcher {titleType}</h2>
                <p>
                    Id: <input disabled={true} value={id}/>
                </p>
                <p>
                    Search Term: <input onChange={e => setSearchTerm(e.target.value)} value={searchTerm}/>
                </p>
                <p>
                    Regex: <input type="checkbox" checked={regex} onChange={e => setRegex(e.target.checked)}/>
                </p>
                <p>
                    Send Mail: <input type="checkbox" checked={sendMail} onChange={e => setSendMail(e.target.checked)}/>
                </p>
                <p>
                    Mail on Update: <input disabled={!sendMail} type="checkbox" checked={mailOnUpdate}
                                           onChange={e => setMailOnUpdate(e.target.checked)}/>
                </p>
                <p>
                    Active: <input type="checkbox" checked={active} onChange={e => setActive(e.target.checked)}/>
                </p>
                <p>
                    <button onClick={() => createWatcher()}>Speichern</button>
                    <button onClick={() => history.push("/home")}>Zurück</button>
                </p>
            </p>}
        </>
    )
}

export default DomainWatcherComponent;