import { queryAll, queryServiceByAppName, queryProviderDetails, queryConsumerDetails } from '../services/governance';

const GovernanceModel = {
    namespace: 'governance',
    state: {
        list: [],
        providerListData: [],
        consumerListData: [],
        providerDetail: [],
        consumerDetail: []
    },
    reducers: {
        restate(state, action) {
            return {
                ...state,
                list: action.payload
            }
        },

        restateForDrawer(state, action) {
            return {
                ...state,
                providerListData: action.payload.providers,
                consumerListData: action.payload.consumers
            }
        },

        restateForProviderDetails(state, action) {
            return {
                ...state,
                providerDetail: action.payload,
            }
        },

        restateForConsumerDetails(state, action) {
            return {
                ...state,
                consumerDetail: action.payload,
            }
        },
    },
    effects: {
        *fetch({ payload }, { call, put }) {
            const response = yield call(queryAll, payload);
            yield put({
                type: 'restate',
                payload: response,
            });
        },

        *fetchServiceByMatch({ payload }, { call, put }) {
            const response = yield call(fetchServiceByMatch, payload);
            yield put({
                type: 'restate',
                payload: response,
            });
        },

        *fetchServiceByAppName({ payload }, { call, put }) {
            const response = yield call(queryServiceByAppName, payload);
            yield put({
                type: 'restateForDrawer',
                payload: response,
            });
        },


        *fetchProviderDetails({ payload }, { call, put }) {
            const response = yield call(queryProviderDetails, payload);
            yield put({
                type: 'restateForProviderDetails',
                payload: response,
            });
        },

        *fetchConsumerDetails({ payload }, { call, put }) {
            const response = yield call(queryConsumerDetails, payload);
            yield put({
                type: 'restateForConsumerDetails',
                payload: response,
            });
        },
    },
};

export default GovernanceModel;
