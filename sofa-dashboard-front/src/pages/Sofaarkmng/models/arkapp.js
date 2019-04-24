import { queryArkAppList, pushCommand } from '@/services/api';

export default {
  namespace: 'arkapp',
  state: {
    list: [],
    defaultVersion: '',
    versionList: [],
  },
  effects: {
    // 获取所有模块
    *fetchArkApps({ payload }, { call, put }) {
      const response = yield call(queryArkAppList, payload);
      yield put({
        type: 'show',
        payload: response,
      });
    },

    // 推送命令
    *command({ payload }, { call }) {
      yield call(pushCommand, payload);
    },
  },

  reducers: {
    show(state, action) {
      return {
        ...state,
        list: action.payload.appList,
        defaultVersion: action.payload.defaultVersion,
        versionList: action.payload.versionList,
      };
    },
  },
};
