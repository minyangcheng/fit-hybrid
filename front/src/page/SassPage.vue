<template>
  <div>
    <div class="btn-orange">
    </div>
    <div class="item">
    </div>
  </div>
</template>

<script>
  export default {
    data() {
      return {
        msg: 'MainPage'
      }
    },
    methods: {}
  }
</script>

<style lang="scss" type="text/scss" scoped>
  @import "../assets/scss/values";
  @import "../assets/scss/common";

  $nav-color: #F90;

  @function test($value:1px) {
    @return $value;
  }

  .item {
    $width: test(50px);
    width: $width;
    height: 100px;
    background: $nav-color;
  }
</style>
