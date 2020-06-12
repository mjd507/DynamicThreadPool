import Vue from 'vue'
import Cookies from 'js-cookie'
import 'normalize.css/normalize.css' // a modern alternative to CSS resets
import App from './App.vue'
import store from './store'
import router from './router'
import ElementUI from 'element-ui'
import './styles/element-variables.scss'
import '@/styles/index.scss' // global css
import './icons' // icon

Vue.use(ElementUI, {
  size: Cookies.get('size') || 'medium' // set element-ui default size
})

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App),
}).$mount('#app')
