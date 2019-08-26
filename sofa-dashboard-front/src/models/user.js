import { queryUser } from '../services/user';

const UserModel = {
  namespace: 'user',
  state: {
    userid: '123',
  },
  reducers: {
    restate: (state, action) => ({ ...state, ...action.payload }),
  },
  effects: {
    *fetchUser(_, { call, put }) {
      const response = yield call(queryUser);
      yield put({
        type: 'restate',
        payload: response,
      });
    },
  },
};

export default UserModel;
