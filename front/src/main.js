import Vue from 'vue'
import App from './App'
import router from './router'
import EurdaSrc from './assets/js/eruda.min'
import CommonUtil from './util/CommonUtil'
import Vuelidate from 'vuelidate'
import MintUI from 'mint-ui'
import 'mint-ui/lib/style.css'
import Hybrid from '../hybrid/index'
// import Hybrid from '../hybrid-dist/hybrid-5853b623af96ea1336ca'

if(process.env.NODE_ENV==='development'){
  //在手机上显示出调试快捷
  window.eruda.init()
}

Vue.config.productionTip = false;
Vue.use(Vuelidate);
Vue.use(MintUI);

new Vue({
  el: '#app',
  router,
  template: '<App/>',
  components: { App }
});

hybrid.error(function(error) {
  console.log('global error-->%s',JSON.stringify(error));
});
hybrid.ready(function () {
  console.log('模块安装成功');
});
hybrid.config({
  jsApiList:['pay']
});

