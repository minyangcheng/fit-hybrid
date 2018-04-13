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
        items: ['getQuickVersion'],
      }
    },
    methods: {
      onItemClick(item, index) {
        switch (item) {
          case 'getQuickVersion':
            hybrid.runtime.getQuickVersion({
              success: function (result) {
                hybrid.ui.toast(JSON.stringify(result));
              },
              error: function (error) {
              }
            });
            break;
          case 'getAppVersion':
            hybrid.runtime.getAppVersion({
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
