const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
  console.log("Start of setupProxy");

  app.use(
    '/api',
    createProxyMiddleware({
      target: 'http://localhost:8080/api',
      changeOrigin: true,
    })
  );
};
