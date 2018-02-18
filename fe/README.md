## Minimal Requirements

1. IntelliJ IDEA 2017.3.4
2. SBT Version 1.1.0

## Setup

1. Clone repository
2. `cd frontend`
3. `yarn install`
4. `sbt ~fastOptJS` (hint `sbt clean run`)
5. `npm run start`
6. Open in browser `http://localhost:8080`

## Access locally running server and website from mobile device

1. Find out your local IP adress 
2. open `frontend/package.json` and edit `"scripts": { "mobile": "API_HOST=...:5000` to `API_HOST=yourlocalIPadress:5000`
3. `sbt ~fastOptJS`
4. `npm run mobile`
5. Open your mobile browser and go to `yourlocalIPadress:8080` (you might have to disable firewall first)
