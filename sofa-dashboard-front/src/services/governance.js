import { stringify } from 'qs';
import request from '../utils/request';

export async function queryAll(params) {
    if (params.queryType === 'app') {
        return request(`/api/service/all-app?query=` + params.queryKey);
    } else {
        return request(`/api/service/all-service?query=` + params.queryKey);
    }
}

export async function queryServiceByAppName(params) {
    return request(`/api/service/service-app?${stringify(params)}`);
}

// 获取服务发布详情
export async function queryProviderDetails(params) {
    return request(`/api/service/query/providers?${stringify(params)}`);
}

// 获取服务消费详情
export async function queryConsumerDetails(params) {
    return request(`/api/service/query/consumers?${stringify(params)}`);
}
//获取服务配置信息
export async function queryConfigs(params) {
    return request(`/api/service/query/config?${stringify(params)}`);
}