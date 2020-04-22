# certstream-watcher
The Certstream watcher is a web page to show newliy registered certificates trackt by certstream, and filter them.

## Features
- Create user account with own filters
- Matches will be updated life on the page via websockets
- Set up mail adress to inform you if a specific filter matches
- Matches for your filters will be saved in the database for late use

## Dependencies
- [Certstream](https://certstream.calidog.io/)
- [Certstream-Java](https://github.com/CaliDog/certstream-java)

## How to install
There are some Docker Containers providet to build an run the application.
Swicht to the backend container and check out the scripts
- build_backend.sh -> creates the backend jar with mave
- build_frontend.sh -> creates html and js files with npm
- docker-compose.yml -> starts the buildet aplicatioin

If needet change the mount volumens and ports in the docker-compose file.

### Configuration Paramters
Some interresting config paramters for the application for all view [Certstream](/backend/config/application.properties)
| Paramter | Description |
| --- | --- |
|jwt.secret|JWT Secret for hashing passwords|
|spring.mail.host|Host used for mail sending|
|spring.mail.port|Port used for mail sending|
|spring.mail.username|user used for mail sending|
|spring.mail.password|password used for mail sending|
|mail.confirm-url|Url used for confirming mail Adress|
|mail.redirect-url|Redirect page after mail confirming|
|ignoredExpressions|Regexes for ignoring certificates seperated by Komma|
|user.auto-activate|enables useres by registration (so everyone can register)|
|logEverything|logs every incoming certificate (DO NOT USE !!! aferter 2 days select isn't responding XD)|

The 'ignoredExpressions' Paramter is interresting for filtering uninterresting certificates befor checking hits to take some load from the application.  f.E. .*\\.keenetic.io,.*\\.plex\\.direct,.*\\.amazonaws\\.com

## How to use
First of all you have to create a user (maby the password restrictions are a bit to exaggerated XD).
After logging in create a watcher and wait for some results (Try not to use somthing that matches everything, that will slow down the application massive when the database gets enormouse, because you are tracking the howl Internet XD).
Whenn sending mails is wanted add it under user settings. And wait for verfication mail. 
### Watcher Paramters
| Paramter | Description |
| --- | --- |
|search term|Filters results continging term|
|regex|uses serch term as regex (not implemented jet)|
|active|En-Disables showing and updating results on main page|
|send mail|En-Disables sending mail to mail|
|mail on update|Also sends you a mail on an updated certificate (resend timeout is 3 days)|

## TODO
- Implement search term with regex
- add admin user role for enabling user accounts
- implement delete functionality for watcher
- implement cleanup job for old entries
- other notification options (f.E Telegram Bot)
- make frontend good-looking

## Images
#### Overview of all tracked domains with results
![Image 0](/images/image_1.png)
----------
#### Detail view of one Filter
![Image 1](/images/image_2.png)
----------
