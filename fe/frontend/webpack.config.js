const webpack = require('webpack');
const path = require('path');
const OpenBrowserPlugin = require('open-browser-webpack-plugin');
const HtmlPlugin = require('html-webpack-plugin');

module.exports = {
  devServer: {
    historyApiFallback: true,
    hot: true,
    inline: true,
    contentBase: './assets',
    port: 8080
  },
  entry: [
    path.resolve(__dirname, 'target/scala-2.12/scalable-client-fastopt.js')
  ],
  output: {
    path: path.resolve(__dirname, 'build'),
    publicPath: '/',
    filename: 'bundle.js'
  },
  module: {
    loaders: [
      {
        test: /\.css$/,
        use: [
          {
            loader: "style-loader"
          },
          {
            loader: "css-loader",
            options: {
              includePaths: [
                path.resolve('./node_modules/normalize.css/normalize.css'),
                path.resolve('./node_modules/react-select/dist/react-select.css')
              ]
            }
          }
        ]
      }
    ]
  },
  resolve: {
    extensions: ['.js', '.jsx', '.css', '.scss', '.png']
  },
  externals: {
    API_HOST: process.env.API_HOST
  },
  plugins: [
    new HtmlPlugin(Object.assign({
      template: 'assets/index.html',
      filename: 'index.html'
    })),
    new webpack.HotModuleReplacementPlugin(),
    new OpenBrowserPlugin({ url: 'http://localhost:8080' })
  ]
};
