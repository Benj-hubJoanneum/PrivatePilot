const http = require('http');
const { getPublicKey } = require('./encryption');

const BASE_DIRECTORY = 'C:/Users/lampr/Desktop/fileStorage';

function startHttpServer() {
    const server = http.createServer((req, res) => {
        if (req.url === '/public-key' && req.method === 'GET') {
            res.writeHead(200, { 'Content-Type': 'text/plain' });
            res.end(getPublicKey());
        } else {
            res.writeHead(404, { 'Content-Type': 'text/plain' });
            res.end('Not Found');
        }
    });

    const PORT = 8081;

    server.listen(PORT, () => {
        console.log(`Public Key Server is running on port ${PORT}`);
    });

    return server; // Return the server instance
}

module.exports = {
    startHttpServer,
    BASE_DIRECTORY,
};