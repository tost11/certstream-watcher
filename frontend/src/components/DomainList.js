import React from 'react';
import moment from "moment";

function DomainList(props) {

    const formatDate = (date) => {
        let m = new moment(date);
        return m.format("DD:MM:YYYY hh:mm");
    };

    return (
        <>
            <h4>{props.size} gefunden Resultate</h4>
            <ul>
                {props.domains.map((loggedDomain, i) => {
                    return <li key={i}>[{formatDate(loggedDomain.lastUpdateDate)}] {loggedDomain.domain}</li>
                })}
            </ul>
        </>
    );
}

export default DomainList;