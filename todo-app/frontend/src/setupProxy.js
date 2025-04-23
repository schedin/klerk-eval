const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
  console.log("Start of setupProxy");

  app.use(
    '/api',
    createProxyMiddleware({
      target: 'http://127.0.0.1:8080',
      changeOrigin: true,
      logLevel: 'debug',
    })
  );
};
