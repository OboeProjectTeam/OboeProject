// store/modules/footer.js
import { FOOTER } from '@/assets/js/constants';

export default {
  // Kích hoạt chế độ namespaced để module này được phân biệt rõ trong store
  namespaced: true,

  // State: lưu trữ dữ liệu cho phần chân trang (footer)
  state: () => ({
    // Keep social links from constants as they don't need translation
    ListCNC: FOOTER.SOCIAL_LINKS,
  }),

  // Các mutation để cập nhật dữ liệu nếu cần
  mutations: {
    setListInfor(state, newListInfor) {
      state.ListInfor = newListInfor;
    },
    setListCNC(state, newListCNC) {
      state.ListCNC = newListCNC;
    },
    setListServies(state, newListServies) {
      state.ListServies = newListServies;
    },
  },

  // Các getter để lấy dữ liệu từ state
  getters: {
    ListCNC: (state) => state.ListCNC,
  },
};
