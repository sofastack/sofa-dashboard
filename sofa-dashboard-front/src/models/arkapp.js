import { queryArkAppList, queryBizState, pushCommand, getBizState } from '../services/arkapp';

const ArkAppModel = {
    namespace: 'arkapp',
    state: {
        ipUnitList: [],
        defaultVersion: "",
        versionList: [],
        bizState: {},
        bizInfos: {}
    },
    reducers: {
        restate(state, action) {
            return {
                ...state,
                ipUnitList: action.payload.ipUnitList,
                defaultVersion: action.payload.defaultVersion,
                versionList: action.payload.versionList,
            }
        },

        setBizState(state, action) {
            return {
                ...state,
                bizState: action.payload,
            }
        },
    },

    effects: {
        // 获取所有宿主实例
        *fetchArkApps({ payload }, { call, put }) {
            const response = yield call(queryArkAppList, payload);
            yield put({
                type: 'restate',
                payload: response,
            });
        },

        *fetchBizState({ payload }, { call, put }) {
            const response = yield call(queryBizState, payload);
            yield put({
                type: 'setBizState',
                payload: response,
            });
        },

        // 推送命令
        *command({ payload }, { call }) {
            const response = yield call(pushCommand, payload);
            return response;
        },


        // 推送命令
        *getBizState({ payload }, { call }) {
            const response = yield call(getBizState, payload);
            return response;
        },
    },
};

export default ArkAppModel;
