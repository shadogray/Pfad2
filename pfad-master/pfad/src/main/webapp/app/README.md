# NPM Integration Steps

- see [Angular2 Quickstart](https://angular.io/docs/ts/latest/quickstart.html#!#prereq)
- see [Angular2 Quickstart Readme](https://github.com/angular/quickstart/blob/master/README.md)

## NPM Install 

```
npm install
```

maybe it's necessary to install components individually

```
npm install lite-server 
```

## NPM Start

```
npm start
```

## NPM LiteServer 

```
npm run lite
```

## Sample Log 

### Prepare local copy of github repository [shadogray/pfad](https://github.com/shadogray/pfad)

```
$ cp pfad-master.zip /tmp/

$ cd /tmp

$ unzip pfad-master.zip
```

### Change to "app" directory of Pfad-DB and install lite-server (if necessary)

```
$ cd /tmp/pfad-master/pfad-master/pfad/src/main/webapp/app/

$ npm install lite-server
npm WARN deprecated tough-cookie@2.2.2: ReDoS vulnerability parsing Set-Cookie https://nodesecurity.io/advisories/130
pfad@1.0.0 D:\workspace-pfad\tmp\pfad-master\pfad-master\pfad\src\main\webapp\app
`-- lite-server@2.2.2 
  +-- browser-sync@2.16.0 
  | +-- browser-sync-client@2.4.2 
  | | +-- etag@1.7.0 
  | | `-- fresh@0.3.0 
  | +-- browser-sync-ui@0.6.1 
  | | +-- async-each-series@0.1.1 
  | | +-- stream-throttle@0.1.3 
<SNIP>
...
</SNIP>
  +-- connect-history-api-fallback@1.3.0 
  +-- connect-logger@0.0.1 
  | `-- moment@2.15.1 
  +-- lodash@4.16.1 
  `-- minimist@1.2.0 

npm WARN optional Skipping failed optional dependency /chokidar/fsevents:
npm WARN notsup Not compatible with your operating system or architecture: fsevents@1.0.14
npm WARN pfad@1.0.0 No repository field.
npm WARN pfad@1.0.0 No license field.
```

### Run Lite-Server - Browser will start app.html

```
$ npm run lite

> pfad@1.0.0 lite D:\tmp\pfad-master\pfad-master\pfad\src\main\webapp\app
> lite-server

Did not detect a `bs-config.json` or `bs-config.js` override file. Using lite-server defaults...
** browser-sync config **
{ injectChanges: false,
  files: [ './**/*.{html,htm,css,js}' ],
  watchOptions: { ignored: 'node_modules' },
  server: { baseDir: './', middleware: [ [Function], [Function] ] } }
[BS] Access URLs:
 -------------------------------------
       Local: http://localhost:3000
    External: http://192.168.56.1:3000
 -------------------------------------
          UI: http://localhost:3001
 UI External: http://192.168.56.1:3001
 -------------------------------------
[BS] Serving files from: ./
[BS] Watching files...
16.09.22 15:44:11 200 GET /index.html
16.09.22 15:44:11 200 GET /app.html
16.09.22 15:44:11 200 GET /node_modules/bootstrap/dist/css/bootstrap-theme.css
16.09.22 15:44:11 200 GET /node_modules/bootstrap/dist/css/bootstrap.min.css
16.09.22 15:44:11 200 GET /styles/main.css
16.09.22 15:44:11 200 GET /img/forge-logo.png
16.09.22 15:44:11 200 GET /scripts/vendor/modernizr-2.8.3.min.js
...
```