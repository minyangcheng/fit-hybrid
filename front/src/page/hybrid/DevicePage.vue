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
        items: ['getDeviceId', 'getVendorInfo', 'callPhone'],
      }
    },
    methods: {
      onItemClick(item, index) {
        switch (item) {
          case 'getDeviceId':
            hybrid.device.getDeviceId({
              success(result) {
                hybrid.ui.toast(JSON.stringify(result));
              },
              error(error) {
              }
            });
            break;
          case 'getVendorInfo':
            hybrid.device.getVendorInfo({
              success: function (result) {
                hybrid.ui.toast(JSON.stringify(result));
              },
              error: function (error) {
              }
            });
            break;
          case 'callPhone':
            hybrid.device.callPhone({
              success: function (result) {
                hybrid.ui.toast(JSON.stringify(result));
              },
              error: function (error) {
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
