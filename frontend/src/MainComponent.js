import DataService from "./service/DataService";
import React, {useContext, useEffect, useState} from 'react';
import UserContext from "./service/UserContext";
import DomainList from "./components/DomainList";
import {useHistory} from "react-router";
import InfoComponent from "./components/InfoComponent";

class DomainListData {
    static data;
    static maxResults;
    static lastMaxResult;
}

function MainComponent() {
    const userContext = useContext(UserContext);
    let history = useHistory();

    const [watchers, setWatchers] = useState([]);
    const [loggedDomains, setLoggedDomains] = useState([]);
    const [maxResults, setMaxResults] = useState(50);
    const [numResults, setNumResults] = useState(0);

    DomainListData.maxResults = maxResults;
    DomainListData.lastMaxResult = maxResults;
    DomainListData.data = loggedDomains;

    const predefinednumResults = [10, 50, 100, 500, 1000, 2000, 5000];

    const handleNewDomain = (message) => {
        let newDomain = JSON.parse(message.body);
        let arr = [];
        arr.push(newDomain);
        arr = arr.concat(DomainListData.data);
        if (arr.length > DomainListData.maxResults) {
            arr.splice(DomainListData.maxResults);
        }
        console.log(DomainListData.maxResults);
        setLoggedDomains(arr);
        DomainListData.data = arr;
    };

    const fetchLoggedDomains = () => {
        DomainListData.data = [];
        if (watchers.length <= 0) {
            return;
        }
        DataService.getLoggedDomains(maxResults).then(res => {
            DomainListData.data = res.content;
            setLoggedDomains(res.content);
            setNumResults(res.totalElements);
        }).catch(err => {
        });
    };

    useEffect(() => {
        fetchLoggedDomains();
    }, [userContext, watchers]);

    useEffect(() => {
        if (userContext.user) {
            DataService.getWatchers().then(res => {
                setWatchers(res);
            }).catch(err => {
            });
            let websocketSubscription = null;
            websocketSubscription = DataService.getWebsocket().subscribe("/domain/" + userContext.user.id, handleNewDomain);
            return function cleanup() {
                if (websocketSubscription !== null) {
                    websocketSubscription.unsubscribe();
                }
            };
        }
    }, [userContext]);

    useEffect(() => {
            console.log(maxResults);
            DomainListData.maxResults = maxResults;
            if (DomainListData.data.length > maxResults) {
                let arr = [];
                Array.prototype.push.apply(arr, DomainListData.data);
                arr.splice(maxResults);
                setLoggedDomains(arr);
            } else if (DomainListData.lastMaxResult >= DomainListData.data.length) {
                console.log("FEtch more data");
                fetchLoggedDomains();
            }
            console.log(DomainListData.data.length, DomainListData.lastMaxResult);
            DomainListData.lastMaxResult = maxResults;
        }
        , [maxResults]);
    const changeActiveWatcher = (watcher, active) => {
        DataService.setWatcherActive(userContext.user.id, watcher.id, active).then(res => {
            let index = watchers.findIndex(obj => obj.id === res.id);
            console.log("index: " + index);
            if (index < 0) {
                return;
            }
            let arr = watchers;
            arr[index] = res;
            setWatchers([]);
            setWatchers(arr);
        }).catch(err => {

        });
    };

    return (
        <>
            {userContext.user && <div>
                <h4>Watchers</h4>
                <button type="button" onClick={() => history.push('/watcher/new')}>Watcher erstellen</button>
                {watchers.length > 0 && <p>
                    <ul>
                        {watchers.map(watcher => {
                            return <li>Such Pattern: [{watcher.searchTerm}] {watcher.regex && <>(Regex)</>}
                                <input type="checkbox" checked={watcher.active} onChange={e => {
                                    changeActiveWatcher(watcher, e.target.checked);
                                }
                                }/>Aktiv&nbsp;
                                <button onClick={() => history.push('/watcher/' + watcher.id)}>Bearbeiten</button>
                                <button>Löschen</button>
                            </li>
                        })}
                    </ul>
                </p>}
                <h4>Logged Domains</h4>
                <div>
                    Anzahl Resultate:
                    <select onChange={val => {
                        setMaxResults(predefinednumResults[val.target.value]);
                    }}>
                        {predefinednumResults.map((v, i) => <option selected={i === 1} value={i}>{v}</option>)}
                    </select>
                </div>
                {loggedDomains.length > 0 && <p>
                    <DomainList size={numResults} domains={loggedDomains}/>
                </p>}
            </div>}
            {!userContext.user && <InfoComponent/>}
        </>);
}

export default MainComponent;