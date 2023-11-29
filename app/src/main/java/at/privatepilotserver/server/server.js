const { startHttpServer } = require('./http');
const { startWebSocketServer } = require('./websocket');

startHttpServer();
startWebSocketServer(8080);
