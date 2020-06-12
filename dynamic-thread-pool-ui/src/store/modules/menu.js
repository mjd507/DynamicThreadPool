import { menuRoutes, constantRoutes } from '@/router'

const state = {
  routes: menuRoutes.concat(constantRoutes)
}

const mutations = {
  SET_ROUTES: (state, routes) => {
    state.routes = routes
  }
}

const actions = {
  generateRoutes({ commit }, routes) {
    commit("SET_ROUTES", routes)
    return routes
  }
}

export default {
  namespaced: true,
  state,
  // mutations,
  // actions
}
