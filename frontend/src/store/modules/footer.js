// store/modules/footer.js
export default {
  // Kích hoạt chế độ namespaced để module này được phân biệt rõ trong store
  namespaced: true,

  // State: lưu trữ dữ liệu cho phần chân trang (footer)
  state: () => ({
    ListInfor: [
      {
        icon: "fas fa-mobile-screen-button",
        title: "0775751954",
      },
      {
        icon: "far fa-envelope",
        title: "bophanchamsoc@oboe.com",
      },
      {
        icon: "far fa-map",
        title: "Hoài Đức, Hà Nội, Việt Nam",
      },
      {
        icon: "far fa-clock",
        title: "Thứ 2 - CN: 8:00 - 24:00",
      },
    ],
    ListCNC: [
      { icon: "fab fa-facebook", src: "https://www.facebook.com" },
      { icon: "fab fa-instagram", src: "https://www.instagram.com" },
      { icon: "fab fa-twitter", src: "https://www.twitter.com" },
      { icon: "fab fa-youtube", src: "https://www.youtube.com" },
    ],
    ListServies: [
      {
        title: "DỊCH VỤ KHÁCH HÀNG",
        service: [
          { title: "Điều khoản dịch vụ", link: "/dieukhoan" },
          { title: "Chính sách quyền riêng tư", link: "/baomat" },
          { title: "Hướng dẫn tự học", link: "/huong-dan-hoc" },
          { title: "Hướng dẫn thanh toán", link: "/huong-dan-thanh-toan" },
        ],
      },
      {
        title: "MẸO SỬ DỤNG",
        service: [
          { title: "Tại sao nên học flashcard", link: "/flashcard" },
          { title: "Cách học Tiếng Nhật tại nhà", link: "/tu-hoc-nhat" },
          {
            title: "Phân biệt hiragana, katakana",
            link: "/hiragana-vs-katakana",
          },
          { title: "Lợi ích của học Tiếng Nhật", link: "/loi-ich-hoc-nhat" },
          { title: "Cách nhận diện chữ Hán", link: "/chu-han" },
        ],
      },
      {
        title: "THÔNG TIN CHUNG",
        service: [
          { title: "Tin tức thị trường Nhật Bản", link: "/tin-tuc" },
          { title: "Quyền lợi nâng cấp tài khoản", link: "/nang-cap" },
          { title: "Liên hệ chúng tôi", link: "/lien-he" },
        ],
      },
      {
        title: "GÓP Ý",
        service: [
          {
            title:
              "Oboe rất tôn trọng và trân trọng từng góp ý của bạn để nâng cao chất lượng dịch vụ.",
            link: "",
          },
          {
            title: "Xin hãy chia sẻ với Oboe nhé. Oboe Xin chân thành cảm ơn!",
            link: "",
          },
        ],
      },
    ],
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
    ListInfor: (state) => state.ListInfor,
    ListCNC: (state) => state.ListCNC,
    ListServies: (state) => state.ListServies,
  },
};
