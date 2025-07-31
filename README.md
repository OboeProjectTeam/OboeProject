# Oboe - Nền tảng học tiếng Nhật thông minh

Oboe là một nền tảng học tiếng Nhật toàn diện, được thiết kế để giúp người học tiếp cận ngôn ngữ này một cách hiệu quả và cá nhân hóa.

## Tính năng chính

- **Học từ vựng & Kanji**
  - Tra cứu từ vựng, kanji, ngữ pháp
  - Thẻ ghi nhớ (Flashcard) thông minh
  - Bài kiểm tra tùy chỉnh
  - Ví dụ mẫu và cách sử dụng

- **Cộng đồng & Diễn đàn**
  - Chia sẻ tài liệu học tập
  - Thảo luận và hỏi đáp
  - Hệ thống tin nhắn trực tiếp
  - Trang cá nhân người dùng

- **Tính năng nâng cao**
  - Xác thực đa phương thức (Email, Firebase Authentication)
  - Hệ thống thanh toán tích hợp
  - Quản lý tài khoản và phân quyền
  - Trang quản trị cho admin
- **Tính năng AI**
  - Tự động tạo bài Quizz
  - Đưa ra đánh giá, lời khuyên
  - Tụ động dịch chi tiết

## Công nghệ sử dụng

### Frontend
- Vue 3 + Vite
- Vuex cho quản lý state
- Vue Router cho điều hướng
- SCSS cho styling
- Axios cho HTTP requests
- TailwindCSS cho UI components

### Backend
- Spring Boot 3.2.3
- Spring Security + JWT
- Spring Data JPA
- MySQL Database
- AWS S3 cho lưu trữ file
- Firebase Authentication cho xác thực

### DevOps
- Docker cho containerization
- GitHub Actions cho CI/CD
- PM2 cho process management
- Nginx cho web server

## Cấu trúc dự án

```
oboe-project/
├── frontend/                # Vue 3 frontend
│   ├── src/
│   │   ├── api/            # API modules
│   │   ├── assets/         # Static assets
│   │   ├── components/     # Vue components
│   │   ├── router/         # Vue router config
│   │   ├── store/          # Vuex store modules
│   │   └── views/          # Page components
│   └── public/             # Public assets
│
├── backend/                # Spring Boot backend
│   └── Oboe/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/
│       │   │   │   └── com/example/Oboe/
│       │   │   │       ├── Controller/    # REST endpoints
│       │   │   │       ├── Service/       # Business logic
│       │   │   │       ├── Repository/    # Data access
│       │   │   │       ├── Entity/        # Data models
│       │   │   │       └── DTOs/          # Data transfer objects
│       │   │   └── resources/
│       │   └── test/      # Unit tests
│       └── pom.xml        # Maven dependencies
│
└── .github/
    └── workflows/         # CI/CD pipelines
```

## Cài đặt và Chạy

### Yêu cầu
- Node.js 18+
- Java 21
- MySQL 8.0+
- Maven

### Frontend
```bash
cd frontend
npm install
npm run dev
```

### Backend
```bash
cd backend/Oboe
./mvnw spring-boot:run
```

## Biến môi trường

### Backend (.env)
```
SPRING_DATASOURCE_URL=
SPRING_DATASOURCE_USERNAME=
SPRING_DATASOURCE_PASSWORD=
SPRING_MAIL_USERNAME=
SPRING_MAIL_PASSWORD=
GOOGLE_CLIENT_ID=
GOOGLE_CLIENT_SECRET=
FACEBOOK_CLIENT_ID=
FACEBOOK_CLIENT_SECRET=
DOMAIN=
ACCESS_KEY=
CREDENTIALS_SECRET_KEY=
```

## API Documentation

API documentation is available at `/swagger-ui.html` when running the backend server.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request


## Contact

Project Link: [https://oboeru.me/](https://oboeru.me/)