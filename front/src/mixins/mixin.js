/**
 * vue组件生命周期监听打印工具
 */
export default {
    created() {
        console.log("created --> path:" + this.$route.path + " query:"+JSON.stringify(this.$route.query)+" param:"+JSON.stringify(this.$route.params));
    },
    mounted() {
        console.log("mounted --> path:" + this.$route.path);
    },
    activated() {
        console.log("activated --> path:" + this.$route.path);
    },
    deactivated() {
        console.log("deactivated --> path:" + this.$route.path);
    },
    destroyed() {
        console.log("destroyed --> path:" + this.$route.path);
    },
    watch: {
        '$route' (to, from) {
            console.log("watch from --> path:" + from.path + " query:"+JSON.stringify(from.query)+" param:"+JSON.stringify(from.params));
            console.log("watch to --> path:" + to.path + " query:"+JSON.stringify(to.query)+" param:"+JSON.stringify(to.params));
        }
    },
}
