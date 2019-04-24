import { removeAppList, queryAppList } from '@/services/api';
import { message } from 'antd';

export default {
  namespace: 'list',

  state: {
    list: [],
    totalCount: 0,
    successCount: 0,
    failCount: 0,
  },

  effects: {
    // 发起异步请求获取应用列表
    *fetch({ payload }, { call, put }) {
      const response = yield call(queryAppList, payload);
      yield put({
        type: 'queryList',
        payload: response,
      });
    },

    // 发起异步请求删除应用信息
    *delete({ payload }, { call, put }) {
      const response = yield call(removeAppList, payload); // post
      if (response) {
        yield put({
          type: '/list/fetch',
        });
      } else {
        message.error('请求失败！');
      }
      const responseApps = yield call(queryAppList, payload);
      yield put({
        type: 'queryList',
        payload: responseApps,
      });
    },
  },

  reducers: {
    queryList(state, action) {
      return {
        ...state,
        list: action.payload.data,
        totalCount: action.payload.totalCount,
        successCount: action.payload.successCount,
        failCount: action.payload.failCount,
      };
    },
  },
};
