import {
  queryArkPluginList,
  queryArkAppList,
  registerPlugin,
  searchPlugins,
  registerNewVersion,
  deletePluginModel,
  relatedApp,
  cancelRelatedApp,
} from '@/services/api';

export default {
  namespace: 'arkmng',

  state: {
    list: [],
    appList: [],
    advancedOperation1: [],
    advancedOperation2: [],
  },

  effects: {
    // 获取所有模块
    *fetchPlugins({ payload }, { call, put }) {
      const response = yield call(queryArkPluginList, payload);
      yield put({
        type: 'show',
        payload: response,
      });
    },
    // 获取所当前插件应用到的应用列表
    *fetchApps(_, { call, put }) {
      const response = yield call(queryArkAppList);
      yield put({
        type: 'show',
        payload: response,
      });
    },
    // 查询模块
    *search({ payload }, { call, put }) {
      const response = yield call(searchPlugins, payload);
      yield put({
        type: 'show',
        payload: response,
      });
    },

    // 新增模块版本
    *submitVersion({ payload }, { call, put }) {
      const response = yield call(registerNewVersion, payload);
      // 成功则刷新
      if (response) {
        const refreshData = yield call(queryArkPluginList);
        yield put({
          type: 'show',
          payload: refreshData,
        });
      }
    },

    // 删除模块
    *deletePlugin({ payload }, { call, put }) {
      const response = yield call(deletePluginModel, payload);
      // 成功则刷新
      if (response) {
        const refreshData = yield call(queryArkPluginList);
        yield put({
          type: 'show',
          payload: refreshData,
        });
      }
    },

    // 注册模块
    *registerPlugins({ payload }, { call, put }) {
      const response = yield call(registerPlugin, payload);
      // 成功则刷新
      if (response) {
        const refreshData = yield call(queryArkPluginList);
        yield put({
          type: 'show',
          payload: refreshData,
        });
      }
    },

    *relatedApp({ payload }, { call, put }) {
      const response = yield call(relatedApp, payload);
      // 成功则刷新
      if (response) {
        const refreshData = yield call(queryArkPluginList);
        yield put({
          type: 'show',
          payload: refreshData,
        });
      }
    },

    *cancelRelatedApp({ payload }, { call, put }) {
      const response = yield call(cancelRelatedApp, payload);
      // 成功则刷新
      if (response) {
        const refreshData = yield call(queryArkPluginList);
        yield put({
          type: 'show',
          payload: refreshData,
        });
      }
    },
  },

  reducers: {
    save(state, action) {
      return {
        ...state,
        list: action.payload,
      };
    },
    show(state, action) {
      return {
        ...state,
        list: action.payload,
        appList: action.payload.appArkList,
      };
    },
  },
};
