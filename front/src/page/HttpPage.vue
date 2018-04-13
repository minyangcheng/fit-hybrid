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
        items: ['usePromise', 'useAwait', 'fetch'],
      }
    },
    methods: {
      onItemClick(item, index) {
        switch (item) {
          case 'usePromise':
            this.usePromise();
            break;
          case 'useAwait':
            this.useAwait();
            break;
          case 'fetch':
            fetch('http://localhost:8080/static/updateJson',{
              headers:{
                'content-type':'application/x-www-form-urlencoded'
              },
              method:'get',
              // mode:"no-cors",
              // body:"name=lulingniu&age=40"
            }).then(response => {
              console.log(response)
                return response.text();
              }).then(value => {
                console.log(value);
            }).catch(error => {
              console.log(error);
            })
            break;
        }
      },
      usePromise() {
        let getUserInfoPromise = function () {
          return new Promise(function (resolve, reject) {
            //模拟网络请求
            setTimeout(function () {
              resolve({username: 'minych', age: 18});
            }, 3000);
          });
        };
        getUserInfoPromise()
          .then(value => {
            console.log(value);
          }).catch(error => {
          console.log(error);
        });
      },
      useAwait() {
        this.getUserInfo()
          .then(value => {
            console.log(value);
          }).catch(error => {
          console.log(error);
        })
      },
      async getUserInfo() {
        let user = await Promise.resolve({username: 'minych', age: 18});
        let account = await Promise.resolve({accountId: 124, amount: 1090});
        // let account = await Promise.reject(new Error('unexpected error when get accountInfo'))
        let info = Object.assign({}, user, account);
        return info;
      },
    }
  }
</script>

<style scoped>

</style>
