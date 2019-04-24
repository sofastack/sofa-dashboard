import {
  queryServiceList,
  querySpecifyService,
  queryConsumerDetails,
  queryProviderDetails,
} from '@/services/api';

export default {
  namespace: 'servicemng',

  state: {
    serviceInfos: [],
    providerInfos: [],
    consumerInfos: [],
  },

  effects: {
    *fetch({ payload }, { call, put }) {
      const response = yield call(queryServiceList, payload);
      yield put({
        type: 'queryServices',
        payload: response,
      });
    },

    *fetchSpecialService({ payload }, { call, put }) {
      const response = yield call(querySpecifyService, payload);
      yield put({
        type: 'queryServices',
        payload: response,
      });
    },

    *fetchProviderDetails({ payload }, { call, put }) {
      const response = yield call(queryProviderDetails, payload);
      yield put({
        type: 'showProviders',
        payload: response,
      });
    },

    *fetchConsumerDetails({ payload }, { call, put }) {
      const response = yield call(queryConsumerDetails, payload);
      yield put({
        type: 'showConsumers',
        payload: response,
      });
    },
  },

  reducers: {
    queryServices(state, action) {
      return {
        ...state,
        serviceInfos: action.payload,
      };
    },
    showProviders(state, action) {
      return {
        ...state,
        providerInfos: action.payload,
      };
    },

    showConsumers(state, action) {
      return {
        ...state,
        consumerInfos: action.payload,
      };
    },
  },
};
