// App Constants
export const APP_NAME = 'Oboe';
export const APP_DESCRIPTION = 'Nền tảng học tiếng Nhật thông minh';

// API Endpoints
// export const API = {
//   AUTH: {
//     LOGIN: '/api/auth/login',
//     REGISTER: '/api/auth/register',
//     LOGOUT: '/api/auth/logout'
//   },
//   USER: {
//     PROFILE: '/api/user/profile',
//     UPDATE: '/api/user/update',
//     CHANGE_PASSWORD: '/api/user/change-password'
//   },
//   FLASHCARD: {
//     CREATE: '/api/flashcard/create',
//     LIST: '/api/flashcard/list',
//     DELETE: '/api/flashcard/delete'
//   }
// };

// Pricing Constants
export const PRICING = {
  FREE: {
    NAME: 'Miễn phí',
    PRICE: 0,
    CURRENCY: 'đ',
    DESCRIPTION: 'Các tính năng cơ bản để bắt đầu hành trình học tập',
    FEATURES: [
      'Truy cập tất cả tài liệu',
      'Tạo học liệu cơ bản',
      'Hỗ trợ qua email',
    ],
    NOT_FEATURES: [
      'Dịch tự động bằng AI',
      'Tạo học liệu bằng AI',
      'AI đánh giá và lời khuyên quiz',
    ]
  },
  PRO: {
    NAME: 'Oboe Pro',
    PRICE: 99000,
    CURRENCY: 'đ',
    PERIOD: '/tháng',
    DESCRIPTION: 'Trải nghiệm học tập toàn diện với công nghệ AI',
    FEATURES: [
      'Truy cập không giới hạn',
      'Tạo học liệu bằng AI',
      'Hỗ trợ ưu tiên 24/7',
      'AI Dịch tự động',
      'AI đánh giá và lời khuyên quiz',
      'Truy cập sớm các tính năng mới',
    ]
  }
};


// Form Validation
export const VALIDATION = {
  PASSWORD: {
    MIN_LENGTH: 8,
    MAX_LENGTH: 32,
    PATTERN: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)[a-zA-Z\d]{8,}$/
  },
  USERNAME: {
    MIN_LENGTH: 3,
    MAX_LENGTH: 20,
    PATTERN: /^[a-zA-Z0-9_-]+$/
  },
  EMAIL: {
    PATTERN: /^[^\s@]+@[^\s@]+\.[^\s@]+$/
  }
};

// Common Messages
export const MESSAGES = {
  SUCCESS: {
    LOGIN: 'Đăng nhập thành công',
    REGISTER: 'Đăng ký tài khoản thành công',
    SAVE: 'Lưu thành công',
    UPDATE: 'Cập nhật thành công',
    DELETE: 'Xóa thành công'
  },
  ERROR: {
    LOGIN: 'Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin',
    REGISTER: 'Đăng ký thất bại. Vui lòng thử lại',
    NETWORK: 'Lỗi kết nối. Vui lòng thử lại sau',
    VALIDATION: 'Vui lòng kiểm tra lại thông tin nhập'
  },
  CONFIRM: {
    DELETE: 'Bạn có chắc chắn muốn xóa?',
    LOGOUT: 'Bạn có chắc chắn muốn đăng xuất?'
  }
};

// Footer Constants
export const FOOTER = {
  CONTACT_INFO: [
    {
      icon: "fas fa-mobile-screen-button",
      title: "0775751954"
    },
    {
      icon: "far fa-envelope",
      title: "bophanchamsoc@oboe.com"
    },
    {
      icon: "far fa-map",
      title: "Hoài Đức, Hà Nội, Việt Nam"
    },
    {
      icon: "far fa-clock",
      title: "Thứ 2 - CN: 8:00 - 24:00"
    }
  ],
  SOCIAL_LINKS: [
    { icon: "fab fa-facebook", src: "https://www.facebook.com" },
    { icon: "fab fa-instagram", src: "https://www.instagram.com" },
    { icon: "fab fa-twitter", src: "https://www.twitter.com" },
    { icon: "fab fa-youtube", src: "https://www.youtube.com" }
  ],
  SERVICES: [
    {
      title: "DỊCH VỤ KHÁCH HÀNG",
      service: [
        { title: "Điều khoản dịch vụ", link: "/footer-services" },
        { title: "Chính sách quyền riêng tư", link: "/footer-services/privacy" },
        { title: "Hướng dẫn tự học", link: "/footer-services/study-guide" },
        { title: "Hướng dẫn thanh toán", link: "/footer-services/payment-guide" }
      ]
    },
    {
      title: "MẸO SỬ DỤNG",
      service: [
        { title: "Tại sao nên học flashcard", link: "/footer-services/why-flashcard" },
        { title: "Phân biệt hiragana, katakana", link: "/footer-services/writing-guide" },
        { title: "Lợi ích của học Tiếng Nhật", link: "/footer-services/benefits-japanese" },
        { title: "Cách nhận diện chữ Hán", link: "/footer-services/kanji-guide" }
      ]
    },
    {
      title: "THÔNG TIN CHUNG",
      service: [
        { title: "Tin tức thị trường Nhật Bản", link: "/footer-services/japan-news" },
        { title: "Quyền lợi nâng cấp tài khoản", link: "/upgrade" },
        { title: "Liên hệ chúng tôi", link: "/footer-services/contact" }
      ]
    },
    {
      title: "GÓP Ý",
      service: [
        {
          title: "Oboe rất tôn trọng và trân trọng từng góp ý của bạn để nâng cao chất lượng dịch vụ.",
          link: ""
        },
        {
          title: "Xin hãy chia sẻ với Oboe nhé. Oboe Xin chân thành cảm ơn!",
          link: ""
        }
      ]
    }
  ]
};
