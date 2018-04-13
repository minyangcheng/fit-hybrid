<template>
  <div>
    <div v-for="(item,index) in items" :key="index" @click="onItemClick(item,index)">
      <mt-cell :title="item" is-link></mt-cell>
    </div>
  </div>
</template>

<script>

  export default {
    data() {
      return {
        items: ['auth', 'page', 'navigator', 'ui', 'device', 'runtime', 'util', 'callApi(调用扩展的功能)'],
      }
    },
    created() {
      let version = process.env.VERSION;
      hybrid.navigator.setTitle({
        title: this.$router.currentRoute.meta.title,
        subTitle: 'v' + version
      });
    },
    mounted() {
      hybrid.on(this.$router.currentRoute.path, 'refreshType', (data) => {
        console.log(data)
        hybrid.ui.toast(JSON.stringify(data));
      });
    },
    beforeDestroy() {
      // hybrid.off(this.$router.currentRoute.path,'refreshType');
    },
    methods: {
      onItemClick(item, index) {
        switch (item) {
          case 'auth':
            hybrid.page.open('/AuthPage');
            break;
          case 'page':
            hybrid.page.open('/ActivityPage');
            break;
          case 'navigator':
            hybrid.page.open('/NavigatorPage');
            break;
          case 'ui':
            hybrid.page.open('/UiPage');
            break;
          case 'device':
            hybrid.page.open('/DevicePage');
            break;
          case 'runtime':
            hybrid.page.open('/RuntimePage');
            break;
          case 'util':
            hybrid.page.open('/UtilPage');
            break;
          case 'callApi(调用扩展的功能)':
            hybrid.callApi({
              name: 'payMoney',
              mudule: 'pay',
              success(result) {
                hybrid.ui.toast(JSON.stringify(result));
              },
              error(error) {
                hybrid.ui.toast('失败:' + JSON.stringify(error));
              }
            });
            break;
        }
      }
    }
  }
</script>

<style scoped>

</style>
