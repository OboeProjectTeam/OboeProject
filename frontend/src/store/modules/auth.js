import api from '@/api';
import { isTokenExpired } from '@/api/modules/authApi';

const state = () => ({
  token: localStorage.getItem('token') || null,
  user: JSON.parse(localStorage.getItem('user')) || null,
});

const mutations = {
  // Gán token mới vào state và localStorage
  SET_TOKEN(state, token) {
    state.token = token;
    localStorage.setItem('token', token);
  },

  // Gán user mới vào state và localStorage
  SET_USER(state, user) {
    state.user = user;
    localStorage.setItem('user', JSON.stringify(user));
  },

  // Xóa token, user khỏi state và localStorage
  CLEAR_AUTH(state) {
    state.token = null;
    state.user = null;
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
};

const actions = {
    // Xử lý login OAuth2: nhận token và fetch user
    async fetchCurrentUser({ commit }, { token }) {
      commit('SET_TOKEN', token);
  
      try {
        const user = await api.auth.getCurrentUser();
        commit('SET_USER', user);
      } catch (error) {
        console.error('Lỗi lấy user từ token:', error);
        commit('CLEAR_AUTH');
      }
    },
  
  // Đăng nhập: gọi API và lưu token + user
  async login({ commit }, { userName, passWord }) {
    const data = await api.auth.login(userName, passWord);
    commit('SET_TOKEN', data.token);
    commit('SET_USER', data.user);
  },

  // Đăng ký tài khoản
  async signup(_, userData) {
    await api.auth.signup(userData);
  },

  // Xác minh tài khoản qua token (email)
  async verify(_, token) {
    await api.auth.verify(token);
  },

  // Cập nhật thông tin người dùng
  async updateProfile({ commit, state }, userData) {
    const updatedUser = await api.auth.updateProfile(userData);
    commit('SET_USER', { ...state.user, ...updatedUser });
  },

  // Đổi mật khẩu
  async changePassword(_, passwordData) {
    await api.auth.changePassword(passwordData);
  },

  // Upload avatar mới và cập nhật lại thông tin
  async uploadAvatar({ commit, state }, file) {
    const avatarUrl = await api.auth.uploadAvatar(file);
    const updatedUser = { ...state.user, avatar: avatarUrl };
    commit('SET_USER', updatedUser);
  },

  // Đăng xuất: gọi API và xóa thông tin local
  async logout({ commit }) {
    try {
      await api.auth.logout?.(); // Nếu có API logout thì gọi, không có thì bỏ qua
    } catch (e) {
      console.warn('Không gọi được logout API, vẫn xóa local.');
    }
    commit('CLEAR_AUTH');
  },

  // Kiểm tra token có còn hạn không
  checkTokenValidity({ state, commit }) {
    if (!state.token || isTokenExpired(state.token)) {
      commit('CLEAR_AUTH');
    }
  },
};

const getters = {
  isAuthenticated: (state) => !!state.token && !!state.user,
  currentUser: (state) => state.user,
  accessToken: (state) => state.token,
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters,
};
