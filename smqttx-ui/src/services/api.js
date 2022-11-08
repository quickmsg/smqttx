//跨域代理前缀
const API_PROXY_PREFIX='/api'
const BASE_URL = process.env.NODE_ENV === 'production' ? process.env.VUE_APP_API_BASE_URL : API_PROXY_PREFIX
const IP = window.location.host
module.exports = {
  LOGIN: `http://${IP}/auth/login`,
  CLUSTERS: `http://${IP}/smqtt/cluster`,
  CONNECTIONS: `http://${IP}/smqtt/connection`,
  DELETE_CONNECTIONS: `http://${IP}/smqtt/close/connection`,
  ISCLUESTER:`http://${IP}/smqtt/is/cluster`,
  PUBLISH:`http://${IP}/smqtt/publish`,
  ROUTES: `${BASE_URL}/routes`,
  ACLACTION: `http://${IP}/smqtt/acl/`
}
