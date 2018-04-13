/**
 * Created by cheguo on 2018/1/24.
 */
const Util = {};

Util.isNumber = function (val) {

  var regPos = /^\d+(\.\d+)?$/; //非负浮点数
  var regNeg = /^(-(([0-9]+\.[0-9]*[1-9][0-9]*)|([0-9]*[1-9][0-9]*\.[0-9]+)|([0-9]*[1-9][0-9]*)))$/; //负浮点数
  if (regPos.test(val) || regNeg.test(val)) {
    return true;
  } else {
    return false;
  }
}

Util.isRunInPhone = function () {
  var i = 0;
  var iOS = false;
  var iDevice = ['iPad', 'iPhone', 'iPod'];
  for (; i < iDevice.length; i++) {
    if (navigator.platform.indexOf(iDevice[i]) >= 0) {
      iOS = true;
      break;
    }
  }
  var UIWebView = /(iPhone|iPod|iPad).*AppleWebKit(?!.*Safari)/i.test(navigator.userAgent);
  var isAndroid = navigator.userAgent.toLowerCase().indexOf("android") > -1;
  return UIWebView || isAndroid || iOS;
}

Util.getAbsolutePath = function (path) {
  let url=window.location.origin+'/#'+path;
  return url;
}

export default Util;
