# fit-hybrid

fit-hybrid是一套H5混合开发工程化框架，可以让你的项目轻松使用采用混合开发模式。

在对渲染速度和性能要求度不高，且需要迭代比较频繁的项目，特别适合使用混合开发模式，这样能够采用纯前端技术进行开发，原生端只提供一些必要的接口供前端使用即可。

## 大致实现功能思路

1. 用原生代码做一个webview容器，该容器对前端开放的接口具有模块性（按照功能种类区分api）、扩展性（外部能够自由添加api）、一致性（所有api接口一致、所有接口回调方式一致），使得前端开发者能够更加方便、简单的调用原生功能
2. 前端代码用指定脚本打包成一个zip文件，zip文件中有预先生成的前端构建信息，包括签名、版本号等等。将zip包放在app中，使得app能够从本地加载前端文件
3. 原生通过比对zip包中签名信息，判断该zip包是否能运行在容器中
4. 事件通知处理
5. 开发过程中更好的调试功能
6. 希望能够像类似于叮叮一样的开放平台,外部能够轻松的开发一些功能运行在容器上面<https://open-doc.dingtalk.com/docs/doc.htm?spm=a219a.7629140.0.0.hliggq&treeId=171&articleId=104910&docType=1>

## 如何使用？

### android端使用

* 配置Hybrid
```
HybridConfiguration configuration = new HybridConfiguration(this)
                .setPageHostUrl("http://10.10.12.153:8080")
                .setCheckApiHandler(new CheckApiHandler() {
                    @Override
                    public void checkRequest(ResourceCheck resourceCheck) {
                        checkApiRequest(resourceCheck);
                    }
                });
HybridManager.getInstance()
        .init(configuration);
```
* 检查是否更新
```
HybridManager.getInstance().checkVersion();
```
* 将zip包中的内容解压到本地
```
HybridManager.getInstance().prepareJsBundle(SplashActivity.this);
```
* 直接打开页面
```
private void toHome() {
        WebViewContainerActivity.startActivity(this, "http://10.10.12.153:8080/");
        finish();
    }
```

### front端使用

* 引入js脚本模块
```
import Hybrid from '../hybrid-dist/hybrid-5853b623af96ea1336ca'
```
* 配置脚本
```
hybrid.error(function(error) {
  console.log('global error-->%s',JSON.stringify(error));
});
hybrid.ready(function () {
  console.log('权限校验成功');
});
hybrid.config({
  jsApiList:['pay']
});
```
* 调用api
```
//打开新页面
hybrid.page.open({pageUrl: '/AuthPage'});
//toast
hybrid.ui.toast('this is toast test');
//弹出alert对话框
hybrid.ui.alert({
              title: '提示',
              message: '要么进化，要么去死',
              buttonLabels: ['取消','去进化'],
              cancelable: 1,
              success(result){
                hybrid.ui.toast(JSON.stringify(result));
              },
              error(err){
                hybrid.ui.toast(JSON.stringify(err));
              }
            });
//日期选择对话框
hybrid.ui.pickDate({
              datetime:'2018-03-01',
              success(result){
                hybrid.ui.toast(JSON.stringify(result));
              },
              error(err){
                hybrid.ui.toast(JSON.stringify(err));
              }
            });
```

### 打包操作

* 运行`npm run build` ，在output目录下会生成一个如bundle-1.0.6-2018-03-05-144651.zip的文件，然后将该文件拷贝到app中即可

# JsBridge(js和java交互部分)
* *js调用java消息格式`QuickHybridJSBridge://ui:1882271515/pickTime?{"time":"18:07","title":"请选择时间","timeFormat":"HH:mm"}`*
* *java调用js消息格式`callJs-->javascript:JSBridge._handleMessageFromNative({"responseData":{"msg":"","result":{"time":"18:08"},"code":1},"responseId":"1882271515"});`*

### js

* 定义api
```
hybridJs.extendModule('ui', [{
    namespace: 'toast',
    os: ['quick'],
    defaultParams: {
      message: '',
    },
    runCode(...rest) {
      // 兼容字符串形式
      const args = innerUtil.compatibleStringParamsToObject.call(
        this,
        rest,
        'message');
      hybridJs.callInner.apply(this, args);
    },
  }])
```
* 代理定义的api
```
function proxyApiNamespace(apiParent, apiName, finalNameSpace, api) {
        // 代理API，将apiParent里的apiName代理到Proxy执行
        Object.defineProperty(apiParent, apiName, {
            configurable: true,
            enumerable: true,
            get: function proxyGetter() {
                // 确保get得到的函数一定是能执行的
                const nameSpaceApi = proxysApis[finalNameSpace];
                // 得到当前是哪一个环境，获得对应环境下的代理对象
                const proxyObj = nameSpaceApi[getCurrProxyApiOs(os)] || nameSpaceApi.h5;

                if (proxyObj) {
                    /**
                     * 返回代理对象，所以所有的api都会通过这个代理函数
                     * 注意引用问题，如果直接返回原型链式的函数对象，由于是在getter中，里面的this会被改写
                     * 所以需要通过walk后主动返回
                     */
                    return proxyObj.walk();
                }

                // 正常情况下走不到，除非预编译的时候在walk里手动抛出
                const osErrorTips = api.os ? (api.os.join('或')) : '"非法"';
                const msg = `${api.namespace}要求的os环境为:${osErrorTips}`;

                showError(globalError.ERROR_TYPE_APIOS.code, msg);

                return noop;
            },
            set: function proxySetter() {
                showError(globalError.ERROR_TYPE_APIMODIFY.code,
                    globalError.ERROR_TYPE_APIMODIFY.msg);
            },
        });
    }
```
* 当调用api的时候，其实是调用到Proxy.walk返回的函数
```
Proxy.prototype.walk = function walk() {
        // 实时获取promise
        const Promise = hybridJs.getPromise();

        // 返回一个闭包函数
        return (...rest) => {
            let args = rest;

            args[0] = args[0] || {};

            // 默认参数的处理
            if (this.api.defaultParams && (args[0] instanceof Object)) {
                Object.keys(this.api.defaultParams).forEach((item) => {
                    if (args[0][item] === undefined) {
                        args[0][item] = this.api.defaultParams[item];
                    }
                });
            }

            // 决定是否使用Promise
            let finallyCallback;

            if (this.callback) {
                // 将this指针修正为proxy内部，方便直接使用一些api关键参数
                finallyCallback = this.callback;
            }

            if (Promise) {
                return finallyCallback && new Promise((resolve, reject) => {
                    // 拓展 args
                    args = args.concat([resolve, reject]);
                    finallyCallback.apply(this, args);
                });
            }

            return finallyCallback && finallyCallback.apply(this, args);
        };
    };
```
* 回调到定义api的runCode方法中
```
runCode(...rest) {
      // 兼容字符串形式
      const args = innerUtil.compatibleStringParamsToObject.call(
        this,
        rest,
        'message');
      hybridJs.callInner.apply(this, args);
    }
```
* 将参数组成消息格式发送给原生端
```
/**
 * JS调用原生方法前,会先send到这里进行处理
 * @param {String} proto 这个属于协议头的一部分
 * @param {JSON} message 调用的方法详情,包括方法名,参数
 * @param {Object} responseCallback 调用完方法后的回调,或者长期回调的id
 */
function doSend(proto, message, responseCallback) {
  const newMessage = message;

  if (typeof responseCallback === 'function') {
    // 如果传入的回调时函数，需要给它生成id
    // 取到一个唯一的callbackid
    const callbackId = getCallbackId();
    // 回调函数添加到短期集合中
    responseCallbacks[callbackId] = responseCallback;
    // 方法的详情添加回调函数的关键标识
    newMessage.callbackId = callbackId;
  } else {
    // 如果传入时已经是id，代表已经在回调池中了，直接使用即可
    newMessage.callbackId = responseCallback;
  }

  // 获取 触发方法的url scheme
  const uri = getUri(proto, newMessage);

  if (os.quick) {
    // 依赖于os判断
    if (os.ios) {
      // ios采用
      window.webkit.messageHandlers.WKWebViewJavascriptBridge.postMessage(uri);
    } else {
      window.top.prompt(uri, '');
    }
  } else {
    // 浏览器
    warn(`浏览器中jsbridge无效,对应scheme:${uri}`);
  }
}
```
* 原生回调处理
```
/**
 * 原生调用H5页面注册的方法,或者调用回调方法
 * @param {String} messageJSON 对应的方法的详情,需要手动转为json
 */
JSBridge._handleMessageFromNative = function _handleMessageFromNative(messageJSON) {
  /**
   * 处理原生过来的方法
   */
  function doDispatchMessageFromNative() {
    let message;
    try {
      if (typeof messageJSON === 'string') {
        message = decodeURIComponent(messageJSON);
        message = JSON.parse(message);
      } else {
        message = messageJSON;
      }
    } catch (e) {
      showError(
        globalError.ERROR_TYPE_NATIVECALL.code,
        globalError.ERROR_TYPE_NATIVECALL.msg);

      return;
    }

    // 回调函数
    const responseId = message.responseId;
    const responseData = message.responseData;
    let responseCallback;

    if (responseId) {
      // 这里规定,原生执行方法完毕后准备通知h5执行回调时,回调函数id是responseId
      responseCallback = responseCallbacks[responseId];
      // 默认先短期再长期
      responseCallback = responseCallback || responseCallbacksLongTerm[responseId];
      // 执行本地的回调函数
      responseCallback && responseCallback(responseData);

      delete responseCallbacks[responseId];
    } else {
      /**
       * 否则,代表原生主动执行h5本地的函数
       * 从本地注册的函数中获取
       */
      let keyArr = Object.keys(messageHandlers);
      keyArr.forEach(function (value, index) {
        if (value === message.handlerName || value.indexOf(message.handlerName, value.length - message.handlerName.length) !== -1) {
          const handler = messageHandlers[value];
          const data = message.data;
          // 执行本地函数,按照要求传入数据和回调
          handler && handler(data);
        }
      })
    }
  }

  // 使用异步
  setTimeout(doDispatchMessageFromNative);
};
```
### android
* 大致流程(以调用选择时间对话框为例子)
  1. 预先定义好的ui模块
  2. 在ui模块钟寻找pickTime方法
  3. 进行数据处理，然后回调前端一个对象上属性名为1882271515的function
* 代码
```
HashMap<String, Method> methodHashMap = mExposedMethods.get(apiName);
            if (methodHashMap != null && methodHashMap.containsKey(methodName)) {
                Method method = methodHashMap.get(methodName);
                HybridUtil.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (method != null) {
                        try {
                            method.invoke(null, mContainer, JSON.parseObject(param), callback);
                        } catch (Exception e) {
                            e.printStackTrace();
                            callback.applyFail(e.toString());
                        }
                    }
                }
        });
            } else {
                error = apiName + "." + methodName + "未找到";
                callback.applyFail(error);
                return error;
            }
/**
     * 弹出日期选择对话框
     * 参数：
     * title： 标题
     * datetime： 指定日期 yyyy-MM-dd
     * date： 格式：yyyy-MM-dd
     */
    public static void pickDate(WebViewContainerFragment container, JSONObject param, final BridgeCallback callback) {
        String title = param.getString("title");
        String dateFormatStr = param.getString("dateFormat");
        String date = param.getString("datetime");
        DialogUtil.showDateDialog(container.getContext(), title, dateFormatStr, date, true, new DialogUtil.OnDateDialogListener() {
            @Override
            public void onDataSelect(String dateStr) {
                JSONObject result = new JSONObject();
                result.put("date", dateStr);
                callback.applySuccess(result);
            }
        });
    }
private void apply(JSONObject responseData) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("responseId", port);
        jsonObject.put("responseData", responseData);
        String execJs = String.format(JS_FUNCTION, jsonObject.toJSONString());
        HybridWebView webView = webViewRef.get();
        if (webView != null && checkContext(webView.getContext()) && webView.getParent() != null) {
            L.d(HybridConstants.LOG_TAG, String.format("callJs-->%s", js));
            webView.loadUrl(js);
        }
    }
```

# 更新升级zip包

1. 当应用每次从后台回到前台的时候（包括首次启动），对比本地版本号和远程版本号，如果远程版本号比本地大，则下载远程包并用downloadVersion记下该版本号
2. 当应用进入程序页面的时候，首先查看是否有远程版本号，如果该远程版本号大于资源版本号，则直接使用远程版本;如果该远程版本号小于资源版本号，并且资源版本号大于本地版本号，或者本地没有版本号，则直接使用资源版本号
3. 解压zip包，根据文件夹下的文件计算singautre，对比buildCongig文件中签名，如果一致则该zip包中的内容合法
4 代码
```
String localVersion = SharePreferenceUtil.getVersion(mContext);
            if (HybridUtil.compareVersion(remoteVersion, localVersion) > 0) {
                download(remoteVersion, md5, dist);
            } else {
                mCurrentStatus = HybridConstants.Version.SLEEP;
            }
public long prepareJsBundle(Context context) {
        long startTime = new Date().getTime();
        if (SharePreferenceUtil.getInterceptorActive(context)) {
            String downloadVersion = SharePreferenceUtil.getDownLoadVersion(context);
            String assetsVersion = AssetsUtil.getAssetsVersionInfo(context);
            if (!TextUtils.isEmpty(downloadVersion) && HybridUtil.compareVersion(downloadVersion, assetsVersion) > 0) {
                File zip = FileUtil.getFileInDir(FileUtil.getTempBundleDir(context), 0);
                FileUtil.deleteFile(FileUtil.getBundleDir(context));
                FileUtil.unZip(zip, FileUtil.getBundleDir(context));
                updateVersion(context, downloadVersion);
                SharePreferenceUtil.setDownLoadVersion(context, null);
                L.d(HybridConstants.LOG_TAG, "prepare js bundle from zip file , version=%s", downloadVersion);
            } else {
                String localVersion = SharePreferenceUtil.getVersion(context);
                if (TextUtils.isEmpty(localVersion) || HybridUtil.compareVersion(assetsVersion, localVersion) > 0) {
                    transferInsideBundle(context);
                    L.d(HybridConstants.LOG_TAG, "prepare js bundle from assert");
                }
            }
        }
        long time = new Date().getTime() - startTime;
        L.d(HybridConstants.LOG_TAG, "prepare js bundle waste time=%s", time);
        return time;
    }
private boolean validateSignature(Context context) {
    String evaluateSignature = SignatureUtil.evaluateSignature(FileUtil.getBundleDir(context));
    String buildSignature = SignatureUtil.getSignatureFromBuildConfig(context);
    return !TextUtils.isEmpty(evaluateSignature) && !TextUtils.isEmpty(buildSignature)
            && evaluateSignature.equals(buildSignature);
}
```

# 打包

1. 首先运行vue自带的打包脚本命令来生成文件，放置在dist文件夹下
2. 在dist文件下生成buildConfig.json文件，文件中放置该次打包的一些信息（版本号:npm package.json文件的version字段、签名等）
3. 将dist文件夹下的内容，根据一定的规则生成如`bundle-1.0.3-2018-03-03-195448.zip`的zip包
4. 将打包脚本通过为npm script中的build添加后置钩子实现自动构建运行

* 生成签名的规则
```
function signature(dir) {
  var files = [];
  findNeedSignatureFiles(dir, files);
  files.sort(function (var1, var2) {
    return var1 > var2;
  });
  var allMd5 = '';
  files.forEach(function (file, index) {
    var md5 = mdFile(file);
    if (md5) {
      allMd5 += md5;
    }
  })
  return mdStr(allMd5);
}
function mdFile(path) {
  var data = fs.readFileSync(path);
  var hash = crypto.createHash('md5');
  var md5 = hash.update(data).digest('hex');
  // console.log('path=%s,md5=%s', path, md5);
  return md5;
}
function mdStr(data) {
  var hash = crypto.createHash('md5');
  var md5 = hash.update(data).digest('hex');
  return md5;
}
function findNeedSignatureFiles(dir, resultFiles) {
  if (fs.existsSync(dir)) {
    var files = fs.readdirSync(dir);
    files.forEach(function (file, index) {
      var curPath = dir + "/" + file;
      if (fs.statSync(curPath).isDirectory()) { // recurse
        findNeedSignatureFiles(curPath, resultFiles);
      } else {
        if (/.+\.js$/ig.test(curPath) || /.+\.css$/ig.test(curPath) || /.+\.html$/ig.test(curPath)) {
          resultFiles.push(curPath);
        }
      }
    })
  }
```

# 开发阶段调试

### 进入参数调试界面

在打开的页面单击导航栏6次即可进入调试参数设置界面（2秒内），根据开发需求，你可以更改一些参数

### 页面控制台

通过开源项目eruda，可以实现手机端页面控制台

### hybrid调试页面

1. 开发环境下webpack dev server和app端通过websocket连接起来
2. 当webpack监听到页面变化，将通过scoket.io将页面变化通知到转发到app端，app进行自动刷新

