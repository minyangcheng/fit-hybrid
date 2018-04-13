<template>
  <div>
    <input type="number" ref="input" :value="value" @input="updateValue">
  </div>
</template>

<script>
  //decimal
  import Utils from './../util/CommonUtil'
  export default {
    data () {
      return {}
    },
    props: {
      value: {
        required: false
      },
      type: {
        type: String,
        required: false,
        default: 'number'
      },
      decimalLength: {
        type: Number,
        required: false,
        default: 2
      }
    },
    // 数值且有默认值
    propD: {
      type: Number,
      default: 100
    },
    methods: {
      updateValue(event){
        var value = event.target.value
        if (this.type === 'number') {
        } else if (this.type === 'decimal') {
          value = value.slice(0, value.indexOf('.') === -1 ? value.length : value.indexOf('.') + this.decimalLength+1)
        }
        this.$refs.input.value = value
        this.$emit('input',value)
      }
    }
  }
</script>

<style scoped>
  .page {
    display: flex;
    flex-flow: column nowrap;
    align-items: center;
    width: 100%;
    height: 100%;
    background: #ffccc8;
  }
</style>
