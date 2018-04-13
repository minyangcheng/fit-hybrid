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
        items: ['getToken'],
      }
    },
    methods: {
      onItemClick(item, index) {
        switch (item) {
          case 'getToken':
            hybrid.auth.getToken({
              success: function (result) {
                hybrid.ui.toast(result.access_token);
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
