import { queryDetails ,queryEnv,queryLogger,queryMapping,queryThreadDump} from '@/services/api';
export default {
    namespace: 'actuator',
    state: {
        info: [],
        health: [],
        threads: [],
        heap:[],
        nonheap: [],
        env: [],
        loggers: [],
        mappings: [],
        threaddump:[]
    },
    effects: {
        // 发起异步请求获取应用列表
        *fetch({ payload }, { call, put }) {
            const response = yield call(queryDetails, payload);
            yield put({
                type: 'refreshData',
                payload: response,
            });
        },

        *fetchEnv({ payload }, { call, put }) {
            const response = yield call(queryEnv, payload);
            yield put({
                type: 'refreshEnvData',
                payload: response,
            });
        },

        *fetchLogger({ payload }, { call, put }) {
            const response = yield call(queryLogger, payload);
            yield put({
                type: 'refreshLoggerData',
                payload: response,
            });
        },

        *fetchMapping({ payload }, { call, put }) {
            const response = yield call(queryMapping, payload);
            yield put({
                type: 'refreshMappingData',
                payload: response,
            });
        },

        *fetchThreadDump({ payload }, { call, put }) {
            const response = yield call(queryThreadDump, payload);
            yield put({
                type: 'refreshThreadDumpData',
                payload: response,
            });
        },
    },
    reducers: {
        refreshData(state, action) {
            return {
                ...state,
                info: action.payload.info,
                health: action.payload.health,
                threads: action.payload.threads,
                heap: action.payload.heap,
                nonheap: action.payload.nonheap,
            };
        },


        refreshEnvData(state, action) {
            return {
                ...state,
                env: action.payload.env,
            };
        },

        refreshLoggerData(state, action) {
            return {
                ...state,
                loggers: action.payload.loggers,
            };
        },

        refreshMappingData(state, action) {
            return {
                ...state,
                mappings: action.payload.mappings,
            };
        },

        refreshThreadDumpData(state, action) {
            return {
                ...state,
                threaddump: action.payload.threaddump,
            };
        },
    },
};
