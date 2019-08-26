import { stringify } from 'qs';
import request from '../utils/request';

export async function queryApplicationByKeyword (keyword) {
  return request(`/api/application?keyword=${keyword}`);
}

export async function queryInstance (params) {
  return request(`/api/instance?${ stringify(params) }`);
}

export async function queryInstanceEnv (instanceId) {
  return request(`/api/instance/${ instanceId }/env`);
}

export async function queryInstanceHealth (instanceId) {
  return request(`/api/instance/${ instanceId }/health`);
}

export async function queryInstanceLoggers (instanceId) {
  return request(`/api/instance/${ instanceId }/loggers`);
}

export async function queryInstanceMappings (instanceId) {
  return request(`/api/instance/${ instanceId }/mappings`);
}

export async function queryInstanceInfo (instanceId) {
  return request(`/api/instance/${ instanceId }/info`);
}

export async function queryThreadInfo (instanceId) {
  return request(`/api/instance/${ instanceId }/thread`)
}

export async function queryMemoryInfo (instanceId) {
  return request(`/api/instance/${ instanceId }/memory`)
}
