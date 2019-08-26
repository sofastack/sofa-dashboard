import { queryInstance } from '../services/application';

const IntsnaceModel = {
  namespace: 'instance',
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
    *fetch({ payload }, { call, put }) {
      const response = yield call(queryInstance, payload);
      yield put({
        type: 'restate',
        payload: response,
      });
    },
  },
};

export default IntsnaceModel;
