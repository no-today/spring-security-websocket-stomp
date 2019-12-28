module.exports = {
    devServer: {
        // open: true,
        overlay: {
            warnings: false,
            errors: true
        },
        proxy: {
            '/websocket': {
                target: 'http://localhost:8080',
                changeOrigin: true
            }
        }
    }
}
