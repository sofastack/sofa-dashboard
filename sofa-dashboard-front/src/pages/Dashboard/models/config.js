import { queryConfEnviroment } from '@/services/api';

export default {
  namespace: 'config',

  state: {
    appProps: [],
    appSysEnv: [],
    appSysProps: [],
    advancedOperation1: [],
    advancedOperation2: [],
  },

  effects: {
    *fetchBasic(_, { call, put }) {
      const response = yield call(queryConfEnviroment);
      yield put({
        type: 'show',
        payload: response,
      });
    },
  },

  reducers: {
    show(state, { payload }) {
      return {
        ...state,
        ...payload,
      };
    },
  },
};
