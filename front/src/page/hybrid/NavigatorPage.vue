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
        items: ['hide', 'show', 'showStatusBar', 'hideStatusBar', 'hookSysBack', 'hookBackBtn', 'setTitle', 'setRightBtn', 'setRightMenu','setLeftBtn'],
      }
    },
    methods: {
      onItemClick(item, index) {
        switch (item) {
          case 'hide':
            hybrid.navigator.hide();
            break;
          case 'show':
            hybrid.navigator.show();
            break;
          case 'showStatusBar':
            hybrid.navigator.showStatusBar();
            break;
          case 'hideStatusBar':
            hybrid.navigator.hideStatusBar();
            break;
          case 'hideBackBtn':
            hybrid.navigator.hideBackBtn();
            break;
          case 'hookSysBack':
            hybrid.navigator.hookSysBack();
            break;
          case 'hookBackBtn':
            hybrid.navigator.hookBackBtn();
            break;
          case 'setTitle':
            hybrid.navigator.setTitle("Navigator");
            break;
          case 'setRightBtn':
            hybrid.navigator.setRightBtn({
              text: '关于',
              imageUrl: '',
              isShow: 1,
              which: 0,
              success(result) {
                hybrid.ui.toast(JSON.stringify(result))
              },
              error(error){

              }
            });
            hybrid.navigator.setRightBtn({
              text: '设置',
              imageUrl: 'http://10.10.12.153:8080/static/img/setting.png',
              isShow: 1,
              // 对应哪一个按钮，一般是0, 1可选择
              which: 1,
              success(result) {
                hybrid.ui.toast(JSON.stringify(result))
              },
              error(error){

              }
            });
            break;
          case 'setRightMenu':
            hybrid.navigator.setRightMenu({
              text: '菜单',
              titleItems: ['帮助','反馈'],
              iconItems: ['http://10.10.12.153:8080/static/img/setting.png','http://10.10.12.153:8080/static/img/setting.png'],
              success(result) {
                hybrid.ui.toast(JSON.stringify(result))
              },
              error(error){

              }
            });
            break;
          case 'setLeftBtn':
            hybrid.navigator.setLeftBtn({
              text: '设置',
              isShow: 1,
              // 对应哪一个按钮，一般是0, 1可选择
              which: 1,
              success(result) {
                hybrid.ui.toast(JSON.stringify(result))
              },
              error(error){

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
