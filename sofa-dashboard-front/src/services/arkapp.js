import { stringify } from 'qs';
import request from '../utils/request';

export async function queryArkAppList(params) {
    return request(`/api/arkapp/ark-app?${stringify(params)}`);
}

export async function queryBizState(params) {
    return request(`/api/arkapp/biz-state-detail?${stringify(params)}`);
}

export async function getBizState(params) {
    return request(`/api/arkapp/biz-state?${stringify(params)}`);
}

// 推送命令
export async function pushCommand(params) {
    return request('/api/arkapp/command', {
        method: 'POST',
        headers: { 'content-type': 'application/json' },
        body: JSON.stringify(params),
    });
}