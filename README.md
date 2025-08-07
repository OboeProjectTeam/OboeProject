# Oboe - Nền tảng học tiếng Nhật thông minh

Oboe là một nền tảng học tiếng Nhật toàn diện, được thiết kế để giúp người học tiếp cận ngôn ngữ này một cách hiệu quả và cá nhân hóa.

## Tính năng chính

### 🎯 **Học từ vựng & Kanji**
- **Tra cứu thông minh**: Từ vựng, kanji, ngữ pháp với cách đọc tiếng Việt
- **Flashcard thông minh**: Hệ thống thẻ ghi nhớ với nhiều chế độ học
  - Chế độ học cơ bản
  - Chế độ kiểm tra (Test)
  - Chế độ ghép thẻ (Match)
- **Tạo học liệu**: Tự tạo flashcard và quiz cá nhân
- **Thống kê học tập**: Theo dõi tiến độ và hiệu suất học tập

### 🤖 **Tính năng AI thông minh**
- **Tự động tạo Quiz**: AI tạo bài kiểm tra từ nội dung học
- **Đánh giá và gợi ý**: AI phân tích và đưa ra lời khuyên học tập
- **Dịch thuật tự động**: Hỗ trợ dịch chi tiết và chính xác
- **Học liệu được đề xuất**: AI gợi ý nội dung phù hợp với trình độ

### 👥 **Cộng đồng & Diễn đàn**
- **Chia sẻ học liệu**: Đăng và chia sẻ flashcard, quiz với cộng đồng
- **Diễn đàn thảo luận**: Hỏi đáp và thảo luận về tiếng Nhật
- **Hệ thống tin nhắn**: Chat trực tiếp với người dùng khác
- **Trang cá nhân**: Profile cá nhân với thống kê và hoạt động
- **Hệ thống báo cáo**: Báo cáo nội dung vi phạm

### 💳 **Hệ thống thanh toán**
- **Tích hợp đa nền tảng**: MoMo, PayOS
- **Nâng cấp tài khoản**: Các gói premium với tính năng mở rộng
- **Theo dõi giao dịch**: Lịch sử thanh toán chi tiết

### ⚙️ **Quản trị & Bảo mật**
- **Dashboard admin**: Quản lý người dùng, nội dung, thống kê
- **Phân quyền người dùng**: Hệ thống role-based access control
- **Xác thực đa phương thức**: Email, Firebase Authentication
- **Quản lý báo cáo**: Xử lý báo cáo vi phạm từ cộng đồng

### 📱 **Trải nghiệm người dùng**
- **Giao diện responsive**: Tối ưu cho mọi thiết bị
- **Tìm kiếm thông minh**: Tìm kiếm toàn cục với nhiều bộ lọc
- **Thông báo real-time**: Cập nhật tin nhắn và hoạt động ngay lập tức
- **Hướng dẫn chi tiết**: Trang hướng dẫn học tập và sử dụng

### 🛡️ **Bảo mật & Infrastructure**
- **Ẩn địa chỉ IP**: Server không thể truy cập trực tiếp qua IP
- **CloudFront CDN**: Domain trỏ về CloudFront thay vì EC2
- **AWS WAF Protection**: 
  - Chống DDoS attacks
  - Hạn chế truy cập `/admin` endpoint
  - Giới hạn số request/phút cho từng trang
- **Auto Security Groups**: Lambda tự động cập nhật IP CloudFront hàng ngày
- **Auto Scaling**: Tự động tạo EC2 mới khi CPU > 70%
- **Monitoring & Alerts**: CloudWatch + SNS gửi email khi server quá tải

### 💾 **Lưu trữ & Database**
- **MySQL trên EC2**: Database chính với remote access
- **AWS S3**: Lưu trữ file upload, hình ảnh, tài liệu học tập
- **S3 Backup**: Tự động backup database và files
- **CDN Integration**: S3 files được serve qua CloudFront

## Công nghệ sử dụng

### Frontend
- **Vue 3** + **Vite** - Framework chính và build tool
- **Vuex** + **Vuex Persisted State** - Quản lý state toàn cục
- **Vue Router** - Điều hướng SPA
- **TailwindCSS** - Framework CSS utility-first
- **SCSS/Sass** - CSS preprocessor
- **Axios** - HTTP client
- **Swiper** - Carousel/slider components
- **Firebase SDK** + **VueFire** - Tích hợp Firebase
- **FontAwesome** - Icon library
- **STOMP.js** + **SockJS** - WebSocket real-time communication
- **Sonner** - Toast notifications
- **JWT Decode** - JWT token handling

### Backend
- **Spring Boot 3.2.3** - Framework chính
- **Spring Security** + **JWT** - Xác thực và phân quyền
- **Spring Data JPA** - ORM và database access
- **MySQL 8.0+** - Database trên EC2 với remote access
- **AWS S3** - File storage và static assets
- **Spring WebSocket** - Real-time communication
- **Spring Mail** - Gửi email
- **Gemini AI API** - Tích hợp AI
- **MoMo API** + **PayOS API** - Cổng thanh toán
- **Firebase Admin SDK** - Xác thực Firebase

### DevOps & Deployment
- **Docker** - Containerization
- **GitHub Actions** - CI/CD pipeline
- **AWS EC2** - Backend hosting với Auto Scaling + MySQL Database
- **AWS S3** - File storage và backup
- **AWS CloudFront** - CDN và reverse proxy
- **AWS WAF** - Web Application Firewall
- **AWS Lambda** - Serverless functions
- **AWS CloudWatch** - Monitoring và logging
- **AWS SNS** - Notification service
- **Firebase Hosting** - Frontend deployment

## Kiến trúc hệ thống

### 🏗️ **Architecture Overview**
```
Internet → CloudFront CDN → AWS WAF → Application Load Balancer → Auto Scaling Group (EC2) → MySQL (EC2)
                                                                                    ↓                ↓
                                                                              AWS S3 Storage    CloudWatch Monitoring
                                                                                    ↓                ↓
                                                                              File Backup      SNS Notifications
```

### 🔄 **Data Flow**
1. **User Request** → CloudFront (CDN)
2. **WAF Filtering** → DDoS protection, rate limiting
3. **Load Balancer** → Phân phối traffic đến EC2 instances
4. **Auto Scaling** → Tự động tạo/xóa instances theo load
5. **Monitoring** → CloudWatch theo dõi metrics
6. **Alerts** → SNS gửi email khi có vấn đề

### 🛡️ **Security Layers**
- **Layer 1**: CloudFront + WAF (DDoS, rate limiting)
- **Layer 2**: Security Groups (chỉ cho phép CloudFront)
- **Layer 3**: Application-level authentication (JWT)
- **Layer 4**: Database access control (VPC, private subnets)

## Cấu trúc dự án

```
oboe-project/
├── frontend/                    # Vue 3 frontend application
│   ├── src/
│   │   ├── api/                # API modules và HTTP clients
│   │   │   ├── modules/        # Các module API riêng biệt
│   │   │   │   ├── adminApi.js      # API quản trị
│   │   │   │   ├── aiApi.js         # API tích hợp AI
│   │   │   │   ├── authApi.js       # API xác thực
│   │   │   │   ├── flashcardApi.js  # API flashcard
│   │   │   │   ├── paymentApi.js    # API thanh toán
│   │   │   │   ├── forumApi.js      # API diễn đàn
│   │   │   │   └── ...              # Các API khác
│   │   │   ├── axios.js        # Cấu hình Axios
│   │   │   └── index.js        # Export tổng hợp
│   │   ├── components/         # Vue components tái sử dụng
│   │   │   └── layout/         # Layout components
│   │   ├── views/              # Các trang chính
│   │   │   ├── admin/          # Trang quản trị
│   │   │   ├── auth/           # Đăng nhập/đăng ký
│   │   │   ├── flashcard/      # Học flashcard
│   │   │   │   ├── flashcard-learn/   # Chế độ học
│   │   │   │   ├── flashcard-test/    # Chế độ kiểm tra
│   │   │   │   └── flashcard-match/   # Chế độ ghép thẻ
│   │   │   ├── forum/          # Diễn đàn cộng đồng
│   │   │   ├── search-japanese/# Tra cứu tiếng Nhật
│   │   │   ├── create-learn/   # Tạo học liệu
│   │   │   ├── pay-fee/        # Thanh toán
│   │   │   └── self/           # Trang cá nhân
│   │   ├── store/              # Vuex store modules
│   │   ├── router/             # Vue router configuration
│   │   └── firebase.js         # Cấu hình Firebase
│   ├── public/                 # Static assets
│   └── package.json            # Dependencies và scripts
│
├── backend/                    # Spring Boot backend
│   └── Oboe/
│       ├── src/main/java/com/example/Oboe/
│       │   ├── Controller/     # REST API endpoints
│       │   │   ├── AdminController.java     # API quản trị
│       │   │   ├── AIController.java        # API AI
│       │   │   ├── FlashCardController.java # API flashcard
│       │   │   ├── PaymentController.java   # API thanh toán
│       │   │   ├── SearchController.java    # API tìm kiếm
│       │   │   └── ...                      # Các controller khác
│       │   ├── Service/        # Business logic layer
│       │   │   ├── AdminService.java        # Logic quản trị
│       │   │   ├── GeminiService.java       # Tích hợp AI
│       │   │   ├── FlashCardService.java    # Logic flashcard
│       │   │   └── ...                      # Các service khác
│       │   ├── Repository/     # Data access layer
│       │   ├── Entity/         # JPA entities
│       │   ├── DTOs/           # Data transfer objects
│       │   └── Config/         # Configuration classes
│       ├── src/main/resources/ # Application properties
│       └── pom.xml             # Maven dependencies
│
└── .github/workflows/          # CI/CD automation
    └── deploy.yml              # Deployment pipeline
```

## Cài đặt và Chạy

### Yêu cầu hệ thống
- **Node.js** 18+ và **npm**
- **Java** 21 (JDK)
- **MySQL** 8.0+
- **Maven** 3.6+
- **Git** để clone repository

### Cài đặt Frontend
```bash
# Clone repository
git clone <repository-url>
cd oboe-project/frontend

# Cài đặt dependencies
npm install

# Chạy development server
npm run dev

# Build cho production
npm run build

# Preview production build
npm run preview
```

### Cài đặt Backend
```bash
# Di chuyển đến thư mục backend
cd backend/Oboe

# Chạy với Maven wrapper (Linux/Mac)
./mvnw spring-boot:run

# Hoặc trên Windows
mvnw.cmd spring-boot:run

# Build JAR file
./mvnw clean package

# Chạy JAR file
java -jar target/oboe-*.jar
```

### Cài đặt Database (MySQL trên EC2)
```sql
-- Tạo database MySQL
CREATE DATABASE oboe_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Tạo user với remote access
CREATE USER 'oboe_user'@'%' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON oboe_db.* TO 'oboe_user'@'%';
FLUSH PRIVILEGES;

-- Cấu hình MySQL cho remote access
-- Trong /etc/mysql/mysql.conf.d/mysqld.cnf:
-- bind-address = 0.0.0.0

-- Mở port 3306 trong Security Group
-- Source: Application servers only
```

## Biến môi trường

### Frontend (.env)
```bash
# Firebase Configuration
VITE_FIREBASE_API_KEY=your_firebase_api_key
VITE_FIREBASE_AUTH_DOMAIN=your_project.firebaseapp.com
VITE_FIREBASE_PROJECT_ID=your_project_id
VITE_FIREBASE_STORAGE_BUCKET=your_project.appspot.com
VITE_FIREBASE_MESSAGING_SENDER_ID=your_sender_id
VITE_FIREBASE_APP_ID=your_app_id

# API Base URL
VITE_API_BASE_URL=http://localhost:8080
```

### Backend (application.properties)
```properties
# Database Configuration (MySQL trên EC2)
spring.datasource.url=jdbc:mysql://your-ec2-mysql-host:3306/oboe_db
spring.datasource.username=oboe_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# AWS S3 Configuration
aws.access.key.id=your_access_key
aws.secret.access.key=your_secret_key
aws.region=ap-southeast-1
aws.s3.bucket.name=oboe-file-storage

# Mail Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your_email@gmail.com
spring.mail.password=your_app_password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# Firebase Configuration
firebase.credentials.path=path/to/firebase-service-account.json

# AI Configuration (Gemini)
gemini.api.key=your_gemini_api_key
gemini.api.url=https://generativelanguage.googleapis.com

# Payment Configuration
# MoMo
momo.partner.code=your_partner_code
momo.access.key=your_access_key
momo.secret.key=your_secret_key
momo.endpoint=https://test-payment.momo.vn

# PayOS
payos.client.id=your_payos_client_id
payos.api.key=your_payos_api_key
payos.checksum.key=your_payos_checksum_key

# JWT Configuration
jwt.secret=your_jwt_secret_key
jwt.expiration=86400000

# Application Configuration
app.domain=http://localhost:5173
server.port=8080
```

## API Documentation

### Endpoints chính

#### Authentication
- `POST /api/auth/login` - Đăng nhập
- `POST /api/auth/register` - Đăng ký
- `POST /api/auth/logout` - Đăng xuất
- `POST /api/auth/refresh` - Refresh token

#### Flashcard
- `GET /api/flashcards` - Lấy danh sách flashcard
- `POST /api/flashcards` - Tạo flashcard mới
- `PUT /api/flashcards/{id}` - Cập nhật flashcard
- `DELETE /api/flashcards/{id}` - Xóa flashcard

#### AI Features
- `POST /api/ai/generate-quiz` - Tạo quiz từ AI
- `POST /api/ai/analyze` - Phân tích học tập
- `POST /api/ai/translate` - Dịch thuật

#### Payment
- `POST /api/payment/momo` - Tạo thanh toán MoMo
- `POST /api/payment/payos` - Tạo thanh toán PayOS
- `GET /api/payment/status` - Kiểm tra trạng thái thanh toán

#### Search
- `GET /api/search/vocabulary` - Tìm kiếm từ vựng
- `GET /api/search/kanji` - Tìm kiếm kanji
- `GET /api/search/grammar` - Tìm kiếm ngữ pháp

### Swagger Documentation
API documentation chi tiết có sẵn tại `/swagger-ui.html` khi chạy backend server.

## Deployment

### Frontend (Firebase Hosting)
```bash
# Build production
npm run build

# Deploy to Firebase
firebase deploy --only hosting
```

### Backend (AWS Infrastructure)

#### EC2 Deployment
```bash
# Build Docker image
docker build -t oboe-backend .

# Push to ECR (Amazon Container Registry)
aws ecr get-login-password --region ap-southeast-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.ap-southeast-1.amazonaws.com
docker tag oboe-backend:latest <account-id>.dkr.ecr.ap-southeast-1.amazonaws.com/oboe-backend:latest
docker push <account-id>.dkr.ecr.ap-southeast-1.amazonaws.com/oboe-backend:latest
```

#### AWS Services Configuration

**CloudFront Distribution:**
- Origin: EC2 Load Balancer
- Caching: Static assets cached, API requests forwarded
- Custom domain với SSL certificate

**AWS WAF Rules:**
```json
{
  "DDoSProtection": "Rate limiting 1000 requests/5min",
  "AdminAccess": "Block /admin/* except whitelisted IPs",
  "GeneralRateLimit": "100 requests/min per IP"
}
```

**Auto Scaling Group:**
- Min instances: 1
- Max instances: 5
- Scale out when CPU > 70%
- Scale in when CPU < 30%

**Lambda Security Updater:**
```python
# Tự động cập nhật Security Groups với IP CloudFront
# Chạy hàng ngày qua EventBridge
```

**AWS S3 Configuration:**
```json
{
  "Bucket": "oboe-file-storage",
  "Region": "ap-southeast-1",
  "PublicAccess": "Blocked",
  "Versioning": "Enabled",
  "Encryption": "AES-256",
  "LifecyclePolicy": "Archive after 90 days"
}
```

**CloudWatch Alarms:**
- CPU Utilization > 70%
- Memory Usage > 80%
- Error Rate > 5%
- Response Time > 2s
- S3 Storage Usage
- Database Connection Pool

## Contributing

1. Fork repository
2. Tạo feature branch (`git checkout -b feature/TenTinhNang`)
3. Commit changes (`git commit -m 'Thêm tính năng mới'`)
4. Push to branch (`git push origin feature/TenTinhNang`)
5. Tạo Pull Request

### Coding Standards
- **Frontend**: Vue 3 Composition API, ESLint, Prettier
- **Backend**: Java Code Conventions, Spring Boot best practices
- **Database**: Tuân thủ naming conventions MySQL

## License

Dự án này được phát triển cho mục đích học tập và nghiên cứu.

## Contact & Links

- **Website**: [https://oboeru.me/](https://oboeru.me/)
- **Demo**: [https://oboe-demo.web.app/](https://oboe-demo.web.app/)
- **Documentation**: [Wiki](https://github.com/your-repo/wiki)

---

**Oboe** - Nền tảng học tiếng Nhật thông minh với AI 🤖🇯🇵