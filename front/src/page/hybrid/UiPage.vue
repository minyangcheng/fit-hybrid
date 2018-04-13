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
        items: ['toast','alert','pickDate','pickMonth','pickTime'],
      }
    },
    methods: {
      onItemClick(item, index) {
        switch (item) {
          case 'toast':
            hybrid.ui.toast({
              message:'this is toast test',
              success(result){
                hybrid.ui.toast(JSON.stringify(result));
              },
              error(err){
                hybrid.ui.toast(JSON.stringify(err));
              }
            });
            break;
          case 'alert':
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
            break;
          case 'pickDate':
            hybrid.ui.pickDate({
              datetime:'2018-03-01',
              success(result){
                hybrid.ui.toast(JSON.stringify(result));
              },
              error(err){
                hybrid.ui.toast(JSON.stringify(err));
              }
            });
            break;
          case 'pickMonth':
            hybrid.ui.pickMonth({
              datetime:'2018-07',
              success(result){
                hybrid.ui.toast(JSON.stringify(result));
              },
              error(err){
                hybrid.ui.toast(JSON.stringify(err));
              }
            });
            break;
          case 'pickTime':
            hybrid.ui.pickTime({
              time:'18:07',
              success(result){
                hybrid.ui.toast(JSON.stringify(result));
              },
              error(err){
                hybrid.ui.toast(JSON.stringify(err));
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
