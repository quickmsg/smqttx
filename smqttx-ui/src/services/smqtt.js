import {CLUSTERS, CONNECTIONS, ISCLUESTER, PUBLISH, ACLACTION, DELETE_CONNECTIONS} from '@/services/api'
import {request, METHOD} from '@/utils/request'

export function addPolicyAction(params) {
    return request(ACLACTION+'policy/add', METHOD.POST, params)
}


export function deletePolicyAction(params) {
    return request(ACLACTION+'policy/delete', METHOD.POST, params)
}


export async function  queryPolicyAction(params) {
    return request(ACLACTION+'policy/query', METHOD.POST, params)
}

/**
 * 获取当前连接信息
 */
export async function connections(params) {
    return request(CONNECTIONS, METHOD.POST, params)

}
/**
 * 删除当前连接信息
 */
export async function deleteConnections(params) {
    return request(DELETE_CONNECTIONS, METHOD.POST, params)

}


/**
 * 获取当前集群信息
 */
export async function clusters() {
    return request(CLUSTERS, METHOD.GET, {})
}


/**
 * 是否是集群
 */
export async function isCluster() {
    return request(ISCLUESTER, METHOD.GET, {})
}

/**
 * 推送mqtt消息
 */
export async function publish(params){
    return request(PUBLISH,METHOD.POST,params)
}
