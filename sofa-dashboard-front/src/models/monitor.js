import {
  queryInstanceEnv,
  queryInstanceHealth,
  queryInstanceInfo,
  queryInstanceLoggers,
  queryInstanceMappings,
  queryMemoryInfo,
  queryThreadInfo,
} from '../services/application';

const MonitorModel = {
  namespace: 'monitor',
  state: {
    env: {},
    health: {},
    mappings: {},
    loggers: {},
    info: {},
    thread: [],
    memory: [],
  },
  reducers: {
    loadEnv (state, action) {
      return {
        ...state,
        env: action.payload
      }
    },
    loadHealth (state, action) {
      return {
        ...state,
        health: action.payload
      }
    },
    loadMappings (state, action) {
      return {
        ...state,
        mappings: action.payload
      }
    },
    loadLoggers (state, action) {
      return {
        ...state,
        loggers: action.payload
      }
    },
    loadInfo (state, action) {
      return {
        ...state,
        info: action.payload
      }
    },
    loadThread (state, action) {
      return {
        ...state,
        thread: action.payload
      }
    },
    loadMemory (state, action) {
      return {
        ...state,
        memory: action.payload
      }
    }
  },
  effects: {
    * fetchEnv ({ payload }, { call, put }) {
      const response = yield call(queryInstanceEnv, payload.instanceId);
      yield put({
        type: 'loadEnv',
        payload: response
      })
    },
    * fetchHealth ({ payload }, { call, put }) {
      const response = yield call(queryInstanceHealth, payload.instanceId);
      yield put({
        type: 'loadHealth',
        payload: response
      })
    },
    * fetchMappings ({ payload }, { call, put }) {
      const response = yield call(queryInstanceMappings, payload.instanceId);
      yield put({
        type: 'loadMappings',
        payload: response
      })
    },
    * fetchLoggers ({ payload }, { call, put }) {
      const response = yield call(queryInstanceLoggers, payload.instanceId);
      yield put({
        type: 'loadLoggers',
        payload: response
      })
    },
    * fetchInfo ({ payload }, { call, put }) {
      const response = yield call(queryInstanceInfo, payload.instanceId);
      yield put({
        type: 'loadInfo',
        payload: response
      })
    },
    * fetchThread ({ payload }, { call, put }) {
      const response = yield call(queryThreadInfo, payload.instanceId);
      yield put({
        type: 'loadThread',
        payload: response
      })
    },
    * fetchMemory ({ payload }, { call, put }) {
      const response = yield call(queryMemoryInfo, payload.instanceId);
      yield put({
        type: 'loadMemory',
        payload: response
      })
    },
  },
};

export default MonitorModel;
