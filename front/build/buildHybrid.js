const path = require('path');
const fs = require('fs');
const UglifyJSPlugin = require('uglifyjs-webpack-plugin');
const ExtractTextPlugin = require('extract-text-webpack-plugin')

let entryPath = path.resolve(__dirname, '../hybrid/index.js');

module.exports = {
  entry: entryPath,
  plugins: [
    new UglifyJSPlugin({
      uglifyOptions: {
        compress: {
          warnings: false
        }
      },
      sourceMap: true
    })
  ],
  output: {
    filename: 'hybrid-[hash].js',
    path: path.resolve(__dirname, '../hybrid-dist')
  }
};
