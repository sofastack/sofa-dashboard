import { stringify } from 'qs';
import request from '@/utils/request';

// 获取环境属性信息
export async function queryConfEnviroment() {
  return request('/api/conf/env');
}
// 获取服务列表
export async function queryServiceList() {
  return request(`/api/service/all`);
}

// 查询服务列表
export async function querySpecifyService(params) {
  return request(`/api/service/query/services?${stringify(params)}`);
}
// 获取服务发布详情
export async function queryProviderDetails(params) {
  return request(`/api/service/query/providers?${stringify(params)}`);
}

// 获取服务消费详情
export async function queryConsumerDetails(params) {
  return request(`/api/service/query/consumers?${stringify(params)}`);
}

// 获取应用信息
export async function queryAppList() {
  return request(`/api/application/list`);
}
// 删除应用信息
export async function removeAppList(params) {
  return request(`/api/application/remove?${stringify(params)}`, {
    method: 'DELETE',
  });
}

export async function fakeAccountLogin(params) {
  return request('/api/login/account', {
    method: 'POST',
    body: params,
  });
}

export async function fakeRegister(params) {
  return request('/api/register', {
    method: 'POST',
    body: params,
  });
}

export async function queryNotices() {
  return request('/api/notices');
}

// arkmng request api
export async function queryArkPluginList() {
  return request(`/api/ark/plugin-list`);
}
// export async function queryArkAppList(params) {
//   return request(`http://localhost:8099/api/ark/app-list`);
// }
export async function searchPlugins(params) {
  return request(`/api/ark/search-plugin?pluginName=${params}`);
}

export async function registerPlugin(params) {
  return request('/api/ark/register', {
    method: 'POST',
    body: params,
  });
}
export async function registerNewVersion(params) {
  return request('/api/ark/registerNewVersion', {
    method: 'POST',
    body: params,
  });
}
export async function deletePluginModel(params) {
  return request('/api/ark/deletePluginModel', {
    method: 'POST',
    body: params,
  });
}

// arkapp request api
export async function queryArkAppList(params) {
  return request(`/api/arkapp/app-list?${stringify(params)}`);
}

// 关联应用录入
export async function relatedApp(params) {
  return request(`/api/ark/related-app?${stringify(params)}`);
}

// 移除关联关系
export async function cancelRelatedApp(params) {
  return request(`/api/ark/cancel-related-app?${stringify(params)}`);
}

// 推送命令
export async function pushCommand(params) {
  return request('/api/arkapp/command', {
    method: 'POST',
    body: params,
  });
}

// 应用面版-应用详情
export async function queryDetails(params) {
    return request(`/api/actuator/details?${stringify(params)}`);
}

// 应用面版-应用详情
export async function queryEnv(params) {
    return request(`/api/actuator/env?${stringify(params)}`);
}

export async function queryLogger(params) {
    return request(`/api/actuator/loggers?${stringify(params)}`);
}

export async function queryMapping(params) {
    return request(`/api/actuator/mappings?${stringify(params)}`);
}


export async function queryThreadDump(params) {
    return request(`/api/actuator/thread-dump?${stringify(params)}`);
}
