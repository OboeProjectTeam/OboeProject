import api from '@/api';
import { isTokenExpired } from '@/api/modules/authApi';
import { jwtDecode } from 'jwt-decode';
import WebSocketService from '@/services/websocket';

const state = () => ({
  token: localStorage.getItem('token') || null,
  user: JSON.parse(localStorage.getItem('user')) || null,
  isFirstLogin: localStorage.getItem('isFirstLogin') === 'true',
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
    state.isFirstLogin = false;
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('isFirstLogin');
    // Không xóa hasVisitedBefore để giữ trạng thái đã từng đăng nhập
  },

  // Set first login state
  SET_FIRST_LOGIN(state, isFirstLogin) {
    state.isFirstLogin = isFirstLogin;
    localStorage.setItem('isFirstLogin', isFirstLogin.toString());
  },

  // Reset language popup state (for testing or admin purposes)
  RESET_LANGUAGE_POPUP_STATE(state) {
    state.isFirstLogin = true;
    localStorage.removeItem('hasSeenLanguagePopup');
    localStorage.removeItem('hasVisitedBefore');
    localStorage.setItem('isFirstLogin', 'true');
  },
};

const actions = {
      // Xử lý login Firebase: nhận token và fetch user
  async fetchCurrentUser({ commit }, { token, user }) {
    commit('SET_TOKEN', token);

    try {
      commit('SET_USER', user);
      
      // Kiểm tra xem có phải lần đầu đăng nhập không
      const hasVisitedBefore = localStorage.getItem('hasVisitedBefore');
      const hasSeenLanguagePopup = localStorage.getItem('hasSeenLanguagePopup');
      
      if (!hasVisitedBefore && !hasSeenLanguagePopup) {
        commit('SET_FIRST_LOGIN', true);
        localStorage.setItem('hasVisitedBefore', 'true');
      } else {
        commit('SET_FIRST_LOGIN', false);
      }
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
    
    // Kiểm tra xem có phải lần đầu đăng nhập không
    const hasVisitedBefore = localStorage.getItem('hasVisitedBefore');
    const hasSeenLanguagePopup = localStorage.getItem('hasSeenLanguagePopup');
    
    if (!hasVisitedBefore && !hasSeenLanguagePopup) {
      commit('SET_FIRST_LOGIN', true);
      localStorage.setItem('hasVisitedBefore', 'true');
    } else {
      commit('SET_FIRST_LOGIN', false);
    }
  },

  // Đăng ký tài khoản
  async signup(_,userData) {
    try {
      await api.auth.signup(userData);
    } catch (error) {
      console.error('Lỗi khi đăng ký:', error);
      throw error;
    }
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
    const updatedUser = { ...state.user, avatarUrl: avatarUrl };
    commit('SET_USER', updatedUser);
  },

  // Cập nhật avatar URL trong store (không gọi API)
  updateUserAvatar({ commit, state }, avatarUrl) {
    const updatedUser = { ...state.user, avatarUrl: avatarUrl };
    commit('SET_USER', updatedUser);
  },

  // Tải lại profile từ server
  async refreshProfile({ commit }) {
    try {
      const user = await api.profile.getProfile();
      commit('SET_USER', user);
      return user;
    } catch (error) {
      console.error('Lỗi khi tải lại profile:', error);
      throw error;
    }
  },

  // Đăng xuất: gọi API và xóa thông tin local
  async logout({ commit }) {
    try {
      // Disconnect WebSocket trước khi logout
      WebSocketService.disconnect();
      
      await api.auth.logout?.(); // Nếu có API logout thì gọi, không có thì bỏ qua
    } catch (e) {
      console.warn('Không gọi được logout API, vẫn xóa local.');
      console.error('Chi tiết lỗi logout API:', e);
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
  // Kiểm tra user có đăng nhập bằng Google không
  isGoogleUser: (state) => {
    if (!state.token) return false;
    try {
      const decoded = jwtDecode(state.token);
      return decoded.provider === 'GOOGLE';
    } catch (error) {
      console.error('Lỗi decode JWT token:', error);
      return false;
    }
  },
  // Kiểm tra user có role admin không
  isAdmin: (state) => {
    if (!state.token) return false;
    try {
      const decoded = jwtDecode(state.token);
      // Kiểm tra role từ JWT token hoặc từ user object
      return decoded.role === 'ROLE_ADMIN' || 
             decoded.authorities?.includes('ROLE_ADMIN') ||
             state.user?.role === 'ROLE_ADMIN';
    } catch (error) {
      console.error('Lỗi decode JWT token:', error);
      return false;
    }
  },
  // Kiểm tra có phải lần đầu đăng nhập không
  isFirstLogin: (state) => state.isFirstLogin,
};

export default {
  namespaced: true,
  state,
  mutations,
  actions,
  getters,
};
