import { searchPlugins, queryAll, registerPlugin, updatePlugin, deletePlugin, relatedApp, cancelRelatedApp, submitVersion,deleteVersion } from '../services/ark';

const ArkModel = {
  namespace: 'ark',
  state: {
    list: [],
  },
  reducers: {
    restate(state, action) {
      return {
        ...state,
        list: action.payload
      }
    },
  },
  effects: {
    // 获取所有插件
    *fetchPlugins({ payload }, { call, put }) {
      const response = yield call(queryAll, payload);
      yield put({
        type: 'restate',
        payload: response,
      });
    },

    // 查询插件
    *searchPlugins({ payload }, { call, put }) {
      const response = yield call(searchPlugins, payload);
      yield put({
        type: 'restate',
        payload: response,
      });
    },
    // 注册插件
    *pluginRegister({ payload }, { call, put }) {
      const result = yield call(registerPlugin, payload);

      if (result) {
        const response = yield call(queryAll, payload);
        yield put({
          type: 'restate',
          payload: response,
        });
      }

      return result;
    },

    // 注册插件
    *updatePlugin({ payload }, { call, put }) {
      const result = yield call(updatePlugin, payload);
      if (result) {
        const response = yield call(queryAll, payload);
        yield put({
          type: 'restate',
          payload: response,
        });
      }
      return result;
    },

    // 删除插件
    *deletePlugin({ payload }, { call, put }) {
      const result = yield call(deletePlugin, payload);
      if (result) {
        const response = yield call(queryAll, payload);
        yield put({
          type: 'restate',
          payload: response,
        });
      }
      return result;
    },

    // 关联应用
    *relatedApp({ payload }, { call, put }) {
      const result = yield call(relatedApp, payload);
      if (result) {
        const response = yield call(queryAll, payload);
        yield put({
          type: 'restate',
          payload: response,
        });
      }
      return result;
    },

    // 取消关联
    *cancelRelatedApp({ payload }, { call, put }) {
      const result = yield call(cancelRelatedApp, payload);
      if (result) {
        const response = yield call(queryAll, payload);
        yield put({
          type: 'restate',
          payload: response,
        });
      }
      return result;
    },

    *submitVersion({ payload }, { call, put }) {
      const result = yield call(submitVersion, payload);
      if (result) {
        const response = yield call(queryAll, payload);
        yield put({
          type: 'restate',
          payload: response,
        });
      }
      return result;
    },

    *deleteVersion({ payload }, { call, put }) {
      const result = yield call(deleteVersion, payload);
      if (result) {
        const response = yield call(queryAll, payload);
        yield put({
          type: 'restate',
          payload: response,
        });
      }
      return result;
    },
  },
};

export default ArkModel;
