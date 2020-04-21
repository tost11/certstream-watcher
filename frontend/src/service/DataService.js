import Stomp from "stompjs";
import decode from 'jwt-decode';

class DataService {

    static websocket = null;

    static initUrl() {
        this.url = window.location.href;
        let arr = this.url.split("//");
        //console.log(arr);
        this.url = arr[0] + "//" + arr[1].split("/")[0];
        if (!this.url.endsWith("/")) {
            this.url += "/";
        }
        if (this.url.startsWith("https")) {
            this.wsUrl = this.url.replace("https", "wss");
        } else {
            this.wsUrl = this.url.replace("http", "ws");
        }
        this.wsUrl += "ws";
        this.url += "api/";
        console.log("Url is: " + this.url);
        console.log("WS Url is: " + this.wsUrl);
    }

    static login(credentials) {
        return this.fetch(this.url + "authenticate", {
            method: 'POST',
            body: JSON.stringify(credentials)
        }).then(res => {
            this.setToken(res.token);
            return Promise.resolve(res);
        });
    }

    static setToken(idToken) {
        // Saves user token to localStorage
        localStorage.setItem('id_token', idToken);
        //console.log(localStorage.getItem('id_token'));
    }

    static getToken() {
        // Retrieves the user token from localStorage
        return localStorage.getItem('id_token')
    }

    static loggedIn() {
        // Checks if there is a saved token and it's still valid
        const token = this.getToken();// GEtting token from localstorage
        return !!token && !this.isTokenExpired(token) // handwaiving here
    }

    static logout() {
        // Clear user token and profile data from localStorage
        localStorage.removeItem('id_token');
    }

    static isTokenExpired(token) {
        try {
            const decoded = decode(token);
            if (decoded.exp < Date.now() / 1000) { // Checking if token is expired. N
                return true;
            } else
                return false;
        } catch (err) {
            return false;
        }
    }

    static getUser() {
        console.log(this.url);
        return this.fetch(this.url + "user");
    }

    static createUser(user) {
        return this.fetch(this.url + "user/create", {
            method: 'POST',
            body: JSON.stringify(user)
        }).then(res => {
            this.setToken(res.token);
            return Promise.resolve(res);
        });
    }

    static updateUser(user) {
        return this.fetch(this.url + "user/update", {
            method: 'POST',
            body: JSON.stringify(user)
        }).then(res => {
            this.setToken(res.token);
            return Promise.resolve(res);
        });
    }

    static getWatchers() {
        return this.fetch(this.url + "watchers");
    }

    static getWatcher(watcherId) {
        return this.fetch(this.url + "watchers/" + watcherId);
    }

    static getLoggedDomains(size) {
        return this.fetch(this.url + "domains?size=" + size);
    }

    static createWatcher(watcher) {
        return this.fetch(this.url + "watchers", {
            method: 'POST',
            body: JSON.stringify(watcher)
        });
    }

    static setWatcherActive(userid, watcherId, active) {
        return this.fetch(this.url + "watchers/" + watcherId + "?active=" + active, {method: 'POST'});
    }

    static getWebsocket() {
        return this.websocket;
    }

    static connectWebSocket(okListner, errorListner) {
        let socket = new WebSocket(this.wsUrl);
        let ws = Stomp.over(socket);
        let that = this;
        ws.connect('', '', (frame) => {
            that.websocket = ws;
            okListner();
        }, function (error) {
            that.websocket = null;
            errorListner();
        });
    }

    static fetch(url, options) {
        // performs api calls sending the required authentication headers
        const headers = {
            'Accept': 'application/json',
            'Content-Type': 'application/json'
        };

        // Setting Authorization header
        // Authorization: Bearer xxxxxxx.xxxxxxxx.xxxxxx
        if (this.loggedIn()) {
            headers['Authorization'] = 'Bearer ' + this.getToken()
        }

        return fetch(url, {
            headers,
            ...options
        })
            .then(this._checkStatus)
            .then(response => response.json())
    }

    static _checkStatus(response) {
        // raises an error in case response status is not a success
        if (response.status >= 200 && response.status < 300) { // Success status lies between 200 to 300
            return response
        } else {
            var error = new Error(response.statusText);
            error.response = response;
            throw error
        }
    }


}

export default DataService;
