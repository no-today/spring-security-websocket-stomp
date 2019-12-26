import Vue from 'vue'
import App from './App.vue'
import './plugins/iview.js'

Vue.config.productionTip = false

new Vue({
  render: function (h) { return h(App) },
}).$mount('#app')
