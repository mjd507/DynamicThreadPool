const getters = {
  sidebar: state => state.app.sidebar,
  size: state => state.app.size,
  menus: state => state.menu.routes,
  token: state => state.user.token,
  name: state => state.user.name,

}
export default getters
