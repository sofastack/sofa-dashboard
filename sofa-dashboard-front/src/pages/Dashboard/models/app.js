import { removeAppList, queryAppList, queryFakeList } from '@/services/api';
import { message } from 'antd';

export default {
  namespace: 'app',

  state: {
    list: [],
  },

  effects: {
    // 发起异步请求获取应用列表
    *fetch({ payload }, { call, put }) {
      const response = yield call(queryAppList, payload);
      yield put({
        type: 'queryList',
        payload: Array.isArray(response) ? response : [],
      });
    },
    *appendFetch({ payload }, { call, put }) {
      const response = yield call(queryFakeList, payload);
      yield put({
        type: 'appendList',
        payload: Array.isArray(response) ? response : [],
      });
    },
    // 发起异步请求删除应用信息
    *submit({ payload }, { call, put }) {
      const response = yield call(removeAppList, payload); // post
      if (response) {
        yield put({
          type: '/app/fetch',
        });
      } else {
        message.error('请求失败！');
      }
      const responseApps = yield call(queryAppList, payload);
      yield put({
        type: 'queryList',
        payload: Array.isArray(responseApps) ? responseApps : [],
      });
    },
  },

  reducers: {
    queryList(state, action) {
      return {
        ...state,
        list: action.payload,
      };
    },
    appendList(state, action) {
      return {
        ...state,
        list: state.list.concat(action.payload),
      };
    },
  },
};
