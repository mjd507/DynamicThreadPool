import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

import Layout from '@/layout'

export const menuRoutes = [{
    path: '/',
    component: Layout,
    redirect: '/',
    children: [{
      path: '',
      component: () => import('@/views/myApps'),
      name: 'myApps',
      meta: { title: '我的应用', icon: 'dashboard', affix: true }
    }]
  }

]

export const constantRoutes = [{
    path: '/login',
    component: () => import('@/views/login'),
  },
  {
    path: '/404',
    redirect: '/404',
    component: Layout,
    children: [
      {
        path: '',
        component: () => import('@/views/404'),
        name: '404',
        meta: { title: '404', icon: 'guide', noCache: true }
      }
    ]
  },
  // 404 page must be placed at the end !!!
  { path: '*', redirect: '/404', hidden: true }

]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: menuRoutes.concat(constantRoutes)
})

export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

const router = createRouter()

export default router
