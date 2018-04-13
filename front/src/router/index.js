import Vue from "vue";
import Router from "vue-router";
import SamplePage from '../page/hybrid/SamplePage';
import AuthPage from '../page/hybrid/AuthPage';
import ActivityPage from '../page/hybrid/ActivityPage';
import NavigatorPage from '../page/hybrid/NavigatorPage';
import DevicePage from '../page/hybrid/DevicePage';
import RuntimePage from '../page/hybrid/RuntimePage';
import UiPage from '../page/hybrid/UiPage';
import UtilPage from '../page/hybrid/UtilPage';
import HttpPage from '../page/HttpPage';
import SassPage from '../page/SassPage';

Vue.use(Router)

let router=new Router({
  routes: [
    {
      path: "/",
      redirect: '/SamplePage',
    },
    {
      path: "/SamplePage",
      component: SamplePage,
      meta:{title:'SamplePage'}
    },
    {
      path: "/AuthPage",
      component: AuthPage,
      meta:{title:'AuthPage'}
    },
    {
      path: "/ActivityPage",
      component: ActivityPage,
      meta:{title:'ActivityPage'}
    },
    {
      path: "/NavigatorPage",
      component: NavigatorPage,
      meta:{title:'NavigatorPage'}
    },
    {
      path: "/DevicePage",
      component: DevicePage,
      meta:{title:'DevicePage'}
    },
    {
      path: "/RuntimePage",
      component: RuntimePage,
      meta:{title:'RuntimePage'}
    },
    {
      path: "/UtilPage",
      component: UtilPage,
      meta:{title:'UtilPage'}
    },
    {
      path: "/UiPage",
      component: UiPage,
      meta:{title:'UiPage'}
    },
    {
      path: "/HttpPage",
      component: HttpPage,
      meta:{title:'HttpPage'}
    },
    {
      path: "/SassPage",
      component: SassPage,
      meta:{title:'SassPage'}
    }
  ]
});

router.beforeEach((to, from, next) => {
  //根据路由中的meta动态显示title
  if(to.meta&&to.meta.title){
    hybrid.navigator.setTitle(to.meta.title);
  }
  next();
});

export default router;
