import { queryApplicationByKeyword } from '../services/application';

const ApplicationModel = {
  namespace: 'application',
  state: {
    list: [],
  },
  reducers: {
    restate (state, action) {
      return {
        ...state,
        list: action.payload
      }
    },
  },
  effects: {
    * fetch ({ payload }, { call, put }) {
      const response = yield call(queryApplicationByKeyword, payload.applicationName);
      yield put({
        type: 'restate',
        payload: response,
      });
    },
  },
};

export default ApplicationModel;
