# Oboe - Nền tảng học tiếng Nhật thông minh

<div align="center">

[![Vue.js](https://img.shields.io/badge/Vue.js-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white)](https://vuejs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![AWS](https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white)](https://aws.amazon.com/)
[![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)](https://firebase.google.com/)

[![Build Status](https://img.shields.io/github/actions/workflow/status/OboeProjectTeam/OboeProject/deploy.yml?style=flat-square&logo=github)](https://github.com/OboeProjectTeam/OboeProject/actions)
[![License](https://img.shields.io/github/license/OboeProjectTeam/OboeProject?style=flat-square)](https://github.com/OboeProjectTeam/OboeProject/blob/main/LICENSE)
![Version](https://img.shields.io/badge/version-1.0.0-green.svg?style=flat-square)
[![Contributors](https://img.shields.io/github/contributors/OboeProjectTeam/OboeProject?style=flat-square)](https://github.com/OboeProjectTeam/OboeProject/graphs/contributors)
[![First Commit](https://img.shields.io/github/created-at/OboeProjectTeam/OboeProject?style=flat-square&label=First%20Commit)](https://github.com/OboeProjectTeam/OboeProject/commit/eb6e1c31e5ed6122fdc32d1adc20487a3ca40661)
[![Last Commit](https://img.shields.io/github/last-commit/OboeProjectTeam/OboeProject?style=flat-square)](https://github.com/OboeProjectTeam/OboeProject/commit/c3992644ed95553599aa80b8ca847e05a384074c)

[![Live Demo](https://img.shields.io/badge/🌐_Live_Demo-oboeru.me-blue?style=for-the-badge)](https://oboeru.me)
[![Documentation](https://img.shields.io/badge/📚_Documentation-GitHub-green?style=for-the-badge)](https://docs.google.com/document/d/11EFWQbEjcnAbDQ1XEmVn_tXmRUQYTJ95AEogeqGV4zA/edit?fbclid=IwY2xjawMK-bZleHRuA2FlbQIxMABicmlkETFWTzNjM2dWN0VXNW5TSUxUAR7iDreKfoapXIOQJEJWelf-dgNiN0sdRvnL2gAYALlksOn-5jfeCJprJVAg7A_aem_GNqs4xhYb19-8Zcr8869mA&tab=t.0)
</div>

Oboe là một nền tảng học tiếng Nhật toàn diện, được thiết kế để giúp người học tiếp cận ngôn ngữ này một cách hiệu quả và cá nhân hóa.

## 📄 Tài liệu SRS

📋 **Tài liệu đặc tả yêu cầu phần mềm (Software Requirements Specification)**

Tài liệu SRS chi tiết mô tả các yêu cầu chức năng, phi chức năng, use case, giao diện người dùng và kiến trúc hệ thống của dự án Oboe.

🔗 **[Xem tài liệu SRS đầy đủ](https://docs.google.com/document/d/11EFWQbEjcnAbDQ1XEmVn_tXmRUQYTJ95AEogeqGV4zA/edit?fbclid=IwY2xjawMK-bZleHRuA2FlbQIxMABicmlkETFWTzNjM2dWN0VXNW5TSUxUAR7iDreKfoapXIOQJEJWelf-dgNiN0sdRvnL2gAYALlksOn-5jfeCJprJVAg7A_aem_GNqs4xhYb19-8Zcr8869mA&tab=t.0)**

### Nội dung chính:
- 📊 Phân tích yêu cầu hệ thống
- 🎯 Danh sách Use Case chi tiết
- 🖼️ Thiết kế giao diện người dùng
- 🏗️ Kiến trúc và đặc tả chức năng
- 🧪 Kế hoạch kiểm thử

## 📋 Mục lục

- [📄 Tài liệu SRS](#-tài-liệu-srs)
- [✨ Tính năng nổi bật](#-tính-năng-nổi-bật)
- [🚀 Công nghệ sử dụng](#-công-nghệ-sử-dụng)
- [🏗️ Tổng quan hạ tầng hệ thống](#️-tổng-quan-hạ-tầng-hệ-thống)
- [📁 Cấu trúc dự án](#-cấu-trúc-dự-án)
- [🚀 Hướng dẫn khởi chạy nhanh](#-hướng-dẫn-khởi-chạy-nhanh)
  - [Yêu cầu hệ thống](#yêu-cầu-hệ-thống)
  - [Frontend](#frontend)
  - [Backend](#backend)
  - [Cơ sở dữ liệu](#cơ-sở-dữ-liệu)
- [⚙️ Biến môi trường](#️-biến-môi-trường)
  - [Frontend (.env)](#frontend-env)
  - [Backend (application.properties)](#backend-applicationproperties)
- [📚 API Endpoints](#-api-endpoints)
- [🚀 Triển khai](#-triển-khai)
- [Đội ngũ & Vai trò](#đội-ngũ--vai-trò)
  - [👥 Đội ngũ phát triển](#-đội-ngũ-phát-triển)
  - [🎯 Phân công chi tiết](#-phân-công-chi-tiết)
  - [📊 Thống kê đóng góp](#-thống-kê-đóng-góp)
- [Đóng góp](#đóng-góp)
- [Giấy phép](#giấy-phép)
- [Liên hệ & Liên kết](#liên-hệ--liên-kết)

## ✨ Tính năng nổi bật

- 🎯 **Học từ vựng & Kanji** - Tra cứu thông minh, flashcard đa chế độ, tạo học liệu cá nhân
- 🤖 **AI thông minh** - Tự động tạo quiz, đánh giá học tập, dịch thuật chính xác
- 👥 **Cộng đồng** - Diễn đàn thảo luận, chia sẻ học liệu, chat trực tiếp
- 💳 **Thanh toán** - Tích hợp PayOS, nâng cấp tài khoản premium
- ⚙️ **Quản trị** - Dashboard admin, phân quyền người dùng, báo cáo vi phạm
- 🛡️ **Bảo mật** - AWS WAF, Auto Scaling, CloudFront CDN

## 🚀 Công nghệ sử dụng

### Frontend
- **Vue 3** - Framework JavaScript tiến bộ
- **Vuex** - Quản lý trạng thái
- **Vue Router** - Định tuyến phía client
- **TailwindCSS** - Framework CSS utility-first
- **SCSS** - Bộ tiền xử lý CSS
- **Firebase** - Xác thực & cơ sở dữ liệu thời gian thực

### Backend
- **Spring Boot** - Framework ứng dụng Java
- **Spring Security** - Xác thực & phân quyền
- **Spring Data JPA** - Lưu trữ dữ liệu
- **MySQL** - Cơ sở dữ liệu quan hệ
- **JWT** - Xác thực dựa trên token
- **WebSocket** - Giao tiếp thời gian thực

### Hạ tầng & DevOps
- **AWS EC2** - Điện toán đám mây
- **AWS S3** - Lưu trữ đối tượng
- **AWS CloudFront** - Mạng phân phối nội dung
- **AWS WAF** - Tường lửa ứng dụng web
- **GitHub Actions** - Pipeline CI/CD
- **Docker** - Containerization

## 🏗️ Tổng quan hạ tầng hệ thống

```
                                          S3/MySQL
                                       (IAM Role)
                                          .
                                          .
                                          .
Internet → CloudFront → Application →     EC2      →     CloudWatch
           (WAF)        Load Balancer   AutoScaling          ↓
                                        Group          SNS Notifications
                                          ↑
                                    Security Groups
                                          ↑
                                        Lambda
```

## 📁 Cấu trúc dự án

```
oboe-project/
├── frontend/           # Vue 3 + TailwindCSS
│   ├── src/
│   │   ├── api/       # API modules
│   │   ├── components/ # Vue components
│   │   ├── views/     # Pages (admin, auth, flashcard, forum...)
│   │   ├── store/     # Vuex store
│   │   └── router/    # Vue router
│   └── package.json
│
├── backend/           # Spring Boot
│   └── Oboe/
│       ├── src/main/java/com/example/Oboe/
│       │   ├── Controller/ # REST APIs
│       │   ├── Service/    # Business logic
│       │   ├── Repository/ # Data access
│       │   └── Entity/     # JPA entities
│       └── pom.xml
│
└── .github/workflows/ # CI/CD pipeline
```

## 🚀 Hướng dẫn khởi chạy nhanh

### Yêu cầu hệ thống
Node.js 18+, Java 21, MySQL 8.0+, Maven 3.6+

### Frontend
```bash
cd frontend
npm install
npm run dev        # Phát triển
npm run build      # Sản xuất
```

### Backend
```bash
cd backend/Oboe
./mvnw spring-boot:run    # Linux/Mac
mvnw.cmd spring-boot:run  # Windows
```

### Cơ sở dữ liệu
```sql
CREATE DATABASE oboe_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'oboe_user'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON oboe_db.* TO 'oboe_user'@'%';
```

## ⚙️ Biến môi trường

### Frontend (.env)
```bash
# Cấu hình Firebase
VITE_FIREBASE_API_KEY=your_firebase_api_key
VITE_FIREBASE_AUTH_DOMAIN=your_project.firebaseapp.com
VITE_FIREBASE_PROJECT_ID=your_project_id
VITE_FIREBASE_STORAGE_BUCKET=your_project.appspot.com
VITE_FIREBASE_MESSAGING_SENDER_ID=your_sender_id
VITE_FIREBASE_APP_ID=your_app_id

# Cấu hình API
VITE_API_BASE_URL=http://localhost:8080
VITE_WEBSOCKET_URL=ws://localhost:8080/ws

# Cấu hình ứng dụng
VITE_APP_NAME=Oboe
VITE_APP_VERSION=1.0.0
```

### Backend (application.properties)
```properties
# Cấu hình cơ sở dữ liệu
spring.datasource.url=jdbc:mysql://your-host:3306/oboe_db
spring.datasource.username=oboe_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Cấu hình JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# Cấu hình AWS S3
aws.access.key.id=your_access_key
aws.secret.access.key=your_secret_key
aws.region=ap-southeast-1
aws.s3.bucket.name=oboe-file-storage

# Cấu hình Firebase
firebase.credentials.path=src/main/resources/firebase/firebase-service-account.json
FIREBASE_PROJECT_ID=your_project_id
FIREBASE_PRIVATE_KEY_ID=your_private_key_id
FIREBASE_PRIVATE_KEY=your_private_key
FIREBASE_CLIENT_EMAIL=your_client_email
FIREBASE_CLIENT_ID=your_client_id

# Cấu hình Email
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Cấu hình AI (Gemini)
gemini.api.key=your_gemini_api_key
gemini.api.url=https://generativelanguage.googleapis.com

# Cấu hình thanh toán
# PayOS
payos.client.id=your_payos_client_id
payos.api.key=your_payos_api_key
payos.checksum.key=your_payos_checksum_key

# MoMo
momo.partner.code=your_partner_code
momo.access.key=your_access_key
momo.secret.key=your_secret_key
momo.endpoint=https://test-payment.momo.vn

# Cấu hình JWT
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000

# Cấu hình ứng dụng
app.domain=http://localhost:5173
server.port=8080
spring.application.name=Oboe

# Cấu hình WebSocket
spring.websocket.allowed-origins=http://localhost:5173,https://oboeru.me

# Cấu hình tải file
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Cấu hình Logging
logging.level.com.example.Oboe=DEBUG
logging.level.org.springframework.security=DEBUG
```

## 📚 API Endpoints

| Danh mục | Endpoint | Mô tả |
|----------|----------|-------------|
| **Xác thực** | `POST /api/auth/login` | Đăng nhập |
| | `POST /api/auth/register` | Đăng ký |
| **Flashcard** | `GET /api/flashcards` | Lấy danh sách |
| | `POST /api/flashcards` | Tạo mới |
| **AI** | `POST /api/ai/generate-quiz` | Tạo quiz |
| | `POST /api/ai/translate` | Dịch thuật |
| **Thanh toán** | `POST /api/payment/payos` | Thanh toán |
| **Tìm kiếm** | `GET /api/search/vocabulary` | Tìm từ vựng |

## 🚀 Triển khai

```bash
# Frontend (Firebase)
npm run build
firebase deploy --only hosting

# Backend (Docker)
docker build -t oboe-backend .
docker run -p 8080:8080 oboe-backend
```

## Team & Roles

### 👥 **Đội ngũ phát triển**

| Thành viên | GitHub | Vai trò | Trách nhiệm |
|------------|--------|---------|-------------|
| **Tô Trung Tôn** | [@trungton08072004](https://github.com/trungton08072004) | **Project Manager** | • Project Management & Timeline<br>• Human Resource Management<br>• SRS (Software Requirements Specification)<br>• Use Cases & User Stories<br>• API Documentation<br>• Test Cases & Test Plans<br>• Backend Support |
| **Hoàng Công Du** | [@hoangdu999](https://github.com/hoangdu999) | **Tech Lead** | • Frontend Development (Vue 3, TailwindCSS)<br>• DevOps & Infrastructure (AWS EC2, CloudFront, WAF)<br>• CI/CD Pipeline (GitHub Actions)<br>• Technical Support & Code Review<br>• Architecture Design |
| **Nguyễn Hữu Nghĩa** | [@HuuNghia1301](https://github.com/HuuNghia1301) | **Backend Developer** | • Core Backend Development (Spring Boot)<br>• REST API Implementation<br>• Business Logic & Services<br>• Security & Authentication (JWT)<br>• Integration with External APIs<br>• Email Services<br>• Payment Integration (MoMo, PayOS) |
| **Nguyễn Minh Tuấn** | [@tuanct413](https://github.com/tuanct413) | **Backend Developer** | • Backend Development (Spring Boot)<br>• Database Integration <br>• WebSocket Real-time Features |
| **Bùi Thành Vương** | [@wuong161104](https://github.com/wuong161104) | **Database & AI Developer** | • Database Design & Architecture<br>• Data Modeling & Migration<br>• Sample Data Creation & Seeding<br>• AI Integration (Gemini API)<br>• Machine Learning Features |

### 🎯 **Phân công chi tiết**

#### **Project Management** - trungton08072004  
- **Project Planning**: Sprint planning, milestone tracking
- **Documentation**: Requirements analysis, API specs, test documentation
- **Quality Assurance**: Test case creation, testing coordination
- **Team Coordination**: Daily standups, progress tracking
- **Backend Contribution**: Supporting backend development tasks

#### **Frontend & Infrastructure** - hoangdu999
- **Frontend Development**: Vue 3, Vuex, Vue Router, SCSS
- **Component Architecture**: Reusable components, layout system
- **AWS Infrastructure**: EC2, S3, CloudFront, WAF, Auto Scaling
- **DevOps**: CI/CD pipeline
- **Technical Leadership**: Code review, architecture decisions, team support

#### **Core Backend Development** - HuuNghia1301 & tuanct413
- **API Development**: RESTful services, endpoint implementation
- **Authentication**: JWT, Firebase integration, security layers
- **Business Logic**: Core application features, data processing
- **Third-party Integration**: Payment gateways, external APIs
- **Real-time Features**: WebSocket implementation, notifications

#### **Database & AI** - wuong161104
- **Database Design**: MySQL schema, relationships, optimization
- **Data Management**: Sample data creation, migration scripts
- **AI Integration**: Gemini API, intelligent features
- **Data Analysis**: Learning analytics, user behavior insights

### 📊 **Thống kê đóng góp**
Dựa trên GitHub insights, team đã có gần **700 commits** , hơn **300 workflows** , gần **70 pull requests** với sự đóng góp tích cực từ tất cả thành viên trong việc phát triển nền tảng học tiếng Nhật Oboe.

## Đóng góp

1. Fork repository
2. Tạo feature branch (`git checkout -b feature/TenTinhNang`)
3. Commit changes (`git commit -m 'Thêm tính năng mới'`)
4. Push to branch (`git push origin feature/TenTinhNang`)
5. Tạo Pull Request

## Giấy phép

Dự án này được phát triển cho mục đích học tập và nghiên cứu.

## Liên hệ & Liên kết

- **Website**: [https://oboeru.me/](https://oboeru.me/)

---

**Oboe** - Nền tảng học tiếng Nhật thông minh với AI
