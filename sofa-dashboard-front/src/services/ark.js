import { stringify } from 'qs';
import request from '../utils/request';

export async function queryAll(params) {
  return request(`/api/ark/plugin-list?${stringify(params)}`);
}

export async function searchPlugins(params) {
  return request(`/api/ark/search-plugin?${stringify(params)}`);
}

export async function registerPlugin(params) {
  return request('/api/ark/register', {
    method: 'POST',
    // 制定下 content-type ，否则会出现 415 
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify(params),
  });
}

export async function updatePlugin(params) {
  return request('/api/ark/update-plugin', {
    method: 'POST',
    // 制定下 content-type ，否则会出现 415 
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify(params),
  });
}

export async function deletePlugin(params) {
  return request(`/api/ark/delete-plugin?${stringify(params)}`);
}

export async function relatedApp(params) {
  return request(`/api/ark/related-app?${stringify(params)}`);
}

export async function cancelRelatedApp(params) {
  return request(`/api/ark/cancel-related-app?${stringify(params)}`);
}

export async function submitVersion(params) {
  return request('/api/ark/register-new-version', {
    method: 'POST',
    // 制定下 content-type ，否则会出现 415 
    headers: { 'content-type': 'application/json' },
    body: JSON.stringify(params),
  });
}
export async function deleteVersion(params) {
  return request(`/api/ark/delete-version?${stringify(params)}`);
}


