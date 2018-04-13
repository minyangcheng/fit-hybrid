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
        items: ['scan','selectImage'],
      }
    },
    methods: {
      onItemClick(item, index) {
        switch (item) {
          case 'scan':
            hybrid.util.scan({
              success: function (result) {
                hybrid.ui.toast(JSON.stringify(result));
              },
              error: function (error) {
              }
            });
            break;
          case 'selectImage':
            hybrid.util.selectImage({
              photoCount: 3,
              showCamera: 1,
              selectedPhotos: ['/storage/emulated/0/DCIM/20180228_180147129_eb6b43c1a56cabd7f561656969eed7ca.jpg'],
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
