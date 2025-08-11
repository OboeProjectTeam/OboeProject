# Oboe - Nền tảng học tiếng Nhật thông minh

<div align="center">

![Vue.js](https://img.shields.io/badge/Vue.js-4FC08D?style=for-the-badge&logo=vue.js&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![AWS](https://img.shields.io/badge/AWS-232F3E?style=for-the-badge&logo=amazon-aws&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)

![Build Status](https://img.shields.io/github/actions/workflow/status/hoangdu999/oboe-project/deploy.yml?style=flat-square&logo=github)
![License](https://img.shields.io/badge/license-MIT-blue.svg?style=flat-square)
![Version](https://img.shields.io/badge/version-1.0.0-green.svg?style=flat-square)
![Contributors](https://img.shields.io/badge/contributors-5-orange.svg?style=flat-square)
![Last Commit](https://img.shields.io/github/last-commit/hoangdu999/oboe-project?style=flat-square)

[![Live Demo](https://img.shields.io/badge/🌐_Live_Demo-oboeru.me-blue?style=for-the-badge)](https://oboeru.me)
[![Documentation](https://img.shields.io/badge/📚_Documentation-Wiki-green?style=for-the-badge)](https://github.com/hoangdu999/oboe-project/wiki)

</div>

Oboe là một nền tảng học tiếng Nhật toàn diện, được thiết kế để giúp người học tiếp cận ngôn ngữ này một cách hiệu quả và cá nhân hóa.

## 📋 Mục lục

- [✨ Tính năng nổi bật](#-tính-năng-nổi-bật)
- [🚀 Tech Stack](#-tech-stack)
- [🏗️ Tổng quan hạ tầng hệ thống](#️-tổng-quan-hạ-tầng-hệ-thống)
- [📁 Cấu trúc dự án](#-cấu-trúc-dự-án)
- [🚀 Quick Start](#-quick-start)
  - [Prerequisites](#prerequisites)
  - [Frontend](#frontend)
  - [Backend](#backend)
  - [Database](#database)
- [⚙️ Environment Variables](#️-environment-variables)
  - [Frontend (.env)](#frontend-env)
  - [Backend (application.properties)](#backend-applicationproperties)
- [📚 API Endpoints](#-api-endpoints)
- [🚀 Deployment](#-deployment)
- [Team & Roles](#team--roles)
  - [👥 Đội ngũ phát triển](#-đội-ngũ-phát-triển)
  - [🎯 Phân công chi tiết](#-phân-công-chi-tiết)
  - [📊 Contribution Statistics](#-contribution-statistics)
- [Contributing](#contributing)
- [License](#license)
- [Contact & Links](#contact--links)

## ✨ Tính năng nổi bật

- 🎯 **Học từ vựng & Kanji** - Tra cứu thông minh, flashcard đa chế độ, tạo học liệu cá nhân
- 🤖 **AI thông minh** - Tự động tạo quiz, đánh giá học tập, dịch thuật chính xác
- 👥 **Cộng đồng** - Diễn đàn thảo luận, chia sẻ học liệu, chat trực tiếp
- 💳 **Thanh toán** - Tích hợp PayOS, nâng cấp tài khoản premium
- ⚙️ **Quản trị** - Dashboard admin, phân quyền người dùng, báo cáo vi phạm
- 🛡️ **Bảo mật** - AWS WAF, Auto Scaling, CloudFront CDN

## 🚀 Tech Stack

**Frontend:** Vue 3, Vite, Vuex, TailwindCSS, Firebase  
**Backend:** Spring Boot, MySQL, AWS S3, Gemini AI  
**DevOps:** Docker, GitHub Actions, AWS (EC2, CloudFront, WAF)

## 🏗️ Tổng quan hạ tầng hệ thống

```
                                    S3/MySQL
                                   (IAM Role)
                                        ↑
Internet → CloudFront → Application → EC2 → CloudWatch
           (WAF)        Load Balancer   AutoScaling     ↓
                                        Group      SNS Notifications
                                          ↓
                                    Security Groups
                                          ↓
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

## 🚀 Quick Start

### Prerequisites
Node.js 18+, Java 21, MySQL 8.0+, Maven 3.6+

### Frontend
```bash
cd frontend
npm install
npm run dev        # Development
npm run build      # Production
```

### Backend
```bash
cd backend/Oboe
./mvnw spring-boot:run    # Linux/Mac
mvnw.cmd spring-boot:run  # Windows
```

### Database
```sql
CREATE DATABASE oboe_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'oboe_user'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON oboe_db.* TO 'oboe_user'@'%';
```

## ⚙️ Environment Variables

### Frontend (.env)
```bash
# Firebase Configuration
VITE_FIREBASE_API_KEY=your_firebase_api_key
VITE_FIREBASE_AUTH_DOMAIN=your_project.firebaseapp.com
VITE_FIREBASE_PROJECT_ID=your_project_id
VITE_FIREBASE_STORAGE_BUCKET=your_project.appspot.com
VITE_FIREBASE_MESSAGING_SENDER_ID=your_sender_id
VITE_FIREBASE_APP_ID=your_app_id

# API Configuration
VITE_API_BASE_URL=http://localhost:8080
VITE_WEBSOCKET_URL=ws://localhost:8080/ws

# Application Configuration
VITE_APP_NAME=Oboe
VITE_APP_VERSION=1.0.0
```

### Backend (application.properties)
```properties
# Database Configuration
spring.datasource.url=jdbc:mysql://your-host:3306/oboe_db
spring.datasource.username=oboe_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
spring.jpa.properties.hibernate.format_sql=true

# AWS S3 Configuration
aws.access.key.id=your_access_key
aws.secret.access.key=your_secret_key
aws.region=ap-southeast-1
aws.s3.bucket.name=oboe-file-storage

# Firebase Configuration
firebase.credentials.path=src/main/resources/firebase/firebase-service-account.json
FIREBASE_PROJECT_ID=your_project_id
FIREBASE_PRIVATE_KEY_ID=your_private_key_id
FIREBASE_PRIVATE_KEY=your_private_key
FIREBASE_CLIENT_EMAIL=your_client_email
FIREBASE_CLIENT_ID=your_client_id

# Mail Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# AI Configuration (Gemini)
gemini.api.key=your_gemini_api_key
gemini.api.url=https://generativelanguage.googleapis.com

# Payment Configuration
# PayOS
payos.client.id=your_payos_client_id
payos.api.key=your_payos_api_key
payos.checksum.key=your_payos_checksum_key

# MoMo
momo.partner.code=your_partner_code
momo.access.key=your_access_key
momo.secret.key=your_secret_key
momo.endpoint=https://test-payment.momo.vn

# JWT Configuration
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000

# Application Configuration
app.domain=http://localhost:5173
server.port=8080
spring.application.name=Oboe

# WebSocket Configuration
spring.websocket.allowed-origins=http://localhost:5173,https://oboeru.me

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Logging Configuration
logging.level.com.example.Oboe=DEBUG
logging.level.org.springframework.security=DEBUG
```

## 📚 API Endpoints

| Category | Endpoint | Description |
|----------|----------|-------------|
| **Auth** | `POST /api/auth/login` | Đăng nhập |
| | `POST /api/auth/register` | Đăng ký |
| **Flashcard** | `GET /api/flashcards` | Lấy danh sách |
| | `POST /api/flashcards` | Tạo mới |
| **AI** | `POST /api/ai/generate-quiz` | Tạo quiz |
| | `POST /api/ai/translate` | Dịch thuật |
| **Payment** | `POST /api/payment/payos` | Thanh toán |
| **Search** | `GET /api/search/vocabulary` | Tìm từ vựng |

## 🚀 Deployment

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
| **trungton08072004** | [@trungton08072004](https://github.com/trungton08072004) | **Project Manager** | • Project Management & Timeline<br>• Human Resource Management<br>• SRS (Software Requirements Specification)<br>• Use Cases & User Stories<br>• API Documentation<br>• Test Cases & Test Plans<br>• Backend Support |
| **hoangdu999** | [@hoangdu999](https://github.com/hoangdu999) | **Tech Lead** | • Frontend Development (Vue 3, TailwindCSS)<br>• DevOps & Infrastructure (AWS EC2, CloudFront, WAF)<br>• CI/CD Pipeline (GitHub Actions)<br>• Technical Support & Code Review<br>• Architecture Design |
| **HuuNghia1301** | [@HuuNghia1301](https://github.com/HuuNghia1301) | **Backend Developer** | • Core Backend Development (Spring Boot)<br>• REST API Implementation<br>• Business Logic & Services<br>• Security & Authentication (JWT)<br>• Integration with External APIs |
| **tuanct413** | [@tuanct413](https://github.com/tuanct413) | **Backend Developer** | • Backend Development (Spring Boot)<br>• Database Integration (JPA/Hibernate)<br>• Payment Integration (MoMo, PayOS)<br>• WebSocket Real-time Features<br>• Email Services |
| **wuong161104** | [@wuong161104](https://github.com/wuong161104) | **Database & AI Developer** | • Database Design & Architecture<br>• Data Modeling & Migration<br>• Sample Data Creation & Seeding<br>• AI Integration (Gemini API)<br>• Machine Learning Features |

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

### 📊 **Contribution Statistics**
Dựa trên GitHub insights, team đã có gần **700 commits** , hơn **300 workflows** , gần **70 pull requests** với sự đóng góp tích cực từ tất cả thành viên trong việc phát triển nền tảng học tiếng Nhật Oboe.

## Contributing

1. Fork repository
2. Tạo feature branch (`git checkout -b feature/TenTinhNang`)
3. Commit changes (`git commit -m 'Thêm tính năng mới'`)
4. Push to branch (`git push origin feature/TenTinhNang`)
5. Tạo Pull Request

## License

Dự án này được phát triển cho mục đích học tập và nghiên cứu.

## Contact & Links

- **Website**: [https://oboeru.me/](https://oboeru.me/)

---

**Oboe** - Nền tảng học tiếng Nhật thông minh với AI