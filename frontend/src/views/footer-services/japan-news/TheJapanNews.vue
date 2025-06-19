<template>
  <div class="japan-news-page">
    <div class="japan-news-container">
      <div class="japan-news-header">
        <h1>Tin Tức Thị Trường Nhật Bản</h1>
        <p class="last-updated">Cập nhật lần cuối: {{ lastUpdated }}</p>
      </div>

      <div class="japan-news-content">
        <section class="news-section featured-news">
          <h2>Tin Tức Nổi Bật</h2>
          <div class="featured-news-grid">
            <div class="featured-news-item" v-for="(news, index) in featuredNews" :key="index">
              <div class="news-image">
                <img :src="news.image" :alt="news.title">
              </div>
              <div class="news-content">
                <span class="news-category">{{ news.category }}</span>
                <h3>{{ news.title }}</h3>
                <p class="news-excerpt">{{ news.excerpt }}</p>
                <div class="news-meta">
                  <span class="news-date">{{ news.date }}</span>
                  <span class="news-source">{{ news.source }}</span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section class="news-section market-trends">
          <h2>Xu Hướng Thị Trường</h2>
          <div class="market-trends-grid">
            <div class="trend-item" v-for="(trend, index) in marketTrends" :key="index">
              <div class="trend-icon">
                <i :class="trend.icon"></i>
              </div>
              <div class="trend-content">
                <h3>{{ trend.title }}</h3>
                <p>{{ trend.description }}</p>
                <div class="trend-stats">
                  <span class="trend-value">{{ trend.value }}</span>
                  <span class="trend-change" :class="trend.changeType">
                    <i :class="trend.changeIcon"></i>
                    {{ trend.change }}
                  </span>
                </div>
              </div>
            </div>
          </div>
        </section>

        <section class="news-section job-opportunities">
          <h2>Cơ Hội Việc Làm</h2>
          <div class="job-categories">
            <div class="job-category" v-for="(category, index) in jobCategories" :key="index">
              <h3>{{ category.title }}</h3>
              <ul class="job-list">
                <li v-for="(job, jobIndex) in category.jobs" :key="jobIndex">
                  <div class="job-header">
                    <span class="job-title">{{ job.title }}</span>
                    <span class="job-salary">{{ job.salary }}</span>
                  </div>
                  <div class="job-details">
                    <span class="job-location">
                      <i class="fas fa-map-marker-alt"></i>
                      {{ job.location }}
                    </span>
                    <span class="job-company">
                      <i class="fas fa-building"></i>
                      {{ job.company }}
                    </span>
                  </div>
                </li>
              </ul>
            </div>
          </div>
        </section>

        <section class="news-section study-abroad">
          <h2>Du Học Nhật Bản</h2>
          <div class="education-updates">
            <div class="update-item" v-for="(update, index) in educationUpdates" :key="index">
              <div class="update-icon">
                <i :class="update.icon"></i>
              </div>
              <div class="update-content">
                <h3>{{ update.title }}</h3>
                <p>{{ update.description }}</p>
                <ul class="update-details">
                  <li v-for="(detail, detailIndex) in update.details" :key="detailIndex">
                    {{ detail }}
                  </li>
                </ul>
              </div>
            </div>
          </div>
        </section>

        <section class="news-section cultural-exchange">
          <h2>Giao Lưu Văn Hóa</h2>
          <div class="events-grid">
            <div class="event-item" v-for="(event, index) in culturalEvents" :key="index">
              <div class="event-image">
                <img :src="event.image" :alt="event.title">
              </div>
              <div class="event-content">
                <span class="event-date">{{ event.date }}</span>
                <h3>{{ event.title }}</h3>
                <p>{{ event.description }}</p>
                <div class="event-location">
                  <i class="fas fa-map-marker-alt"></i>
                  {{ event.location }}
                </div>
              </div>
            </div>
          </div>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const lastUpdated = ref('15/03/2024');

const featuredNews = ref([
  {
    image: '/src/assets/img/footer/japan-visa.jpg',
    category: 'Kinh Tế',
    title: 'Nhật Bản Mở Rộng Chính Sách Visa Cho Lao Động Nước Ngoài',
    excerpt: 'Chính phủ Nhật Bản vừa thông qua đề xuất mới về việc nới lỏng các điều kiện visa cho lao động nước ngoài trong các ngành nghề trọng điểm.',
    date: '15/03/2024',
    source: 'Nikkei Asia'
  },
  {
    image: '/src/assets/img/footer/japan-ai.jpg',
    category: 'Công Nghệ',
    title: 'Làn Sóng AI Tại Nhật Bản: Cơ Hội Việc Làm Mới',
    excerpt: 'Sự phát triển của AI đang tạo ra nhiều cơ hội việc làm mới trong lĩnh vực công nghệ tại Nhật Bản.',
    date: '14/03/2024',
    source: 'Tech Japan'
  }
]);

const marketTrends = ref([
  {
    icon: 'fas fa-chart-line',
    title: 'Tăng Trưởng GDP',
    description: 'Tốc độ tăng trưởng kinh tế Nhật Bản quý I/2024',
    value: '2.8%',
    change: '+0.5%',
    changeType: 'positive',
    changeIcon: 'fas fa-arrow-up'
  },
  {
    icon: 'fas fa-yen-sign',
    title: 'Tỷ Giá Yên',
    description: 'Tỷ giá JPY/USD',
    value: '148.25',
    change: '-0.3%',
    changeType: 'negative',
    changeIcon: 'fas fa-arrow-down'
  }
]);

const jobCategories = ref([
  {
    title: 'Công Nghệ Thông Tin',
    jobs: [
      {
        title: 'Software Engineer',
        salary: '¥4.5M - ¥7M/năm',
        location: 'Tokyo',
        company: 'Tech Corp Japan'
      },
      {
        title: 'Frontend Developer',
        salary: '¥4M - ¥6M/năm',
        location: 'Osaka',
        company: 'Digital Solutions'
      }
    ]
  },
  {
    title: 'Sản Xuất & Kỹ Thuật',
    jobs: [
      {
        title: 'Kỹ Sư Cơ Khí',
        salary: '¥3.8M - ¥5.5M/năm',
        location: 'Nagoya',
        company: 'Toyota Manufacturing'
      },
      {
        title: 'Kỹ Sư Điện Tử',
        salary: '¥4M - ¥6M/năm',
        location: 'Yokohama',
        company: 'Electronics Corp'
      }
    ]
  }
]);

const educationUpdates = ref([
  {
    icon: 'fas fa-graduation-cap',
    title: 'Học Bổng MEXT 2024',
    description: 'Chương trình học bổng toàn phần của chính phủ Nhật Bản',
    details: [
      'Hạn nộp hồ sơ: 30/04/2024',
      'Giá trị học bổng: ¥147,000/tháng',
      'Bao gồm vé máy bay khứ hồi',
      'Miễn học phí toàn phần'
    ]
  },
  {
    icon: 'fas fa-school',
    title: 'Kỳ Thi Du Học',
    description: 'Thông tin về kỳ thi EJU tháng 6/2024',
    details: [
      'Ngày thi: 16/06/2024',
      'Hạn đăng ký: 30/04/2024',
      'Địa điểm: Nhiều thành phố lớn',
      'Lệ phí: ¥20,000'
    ]
  }
]);

const culturalEvents = ref([
  {
    image: '/src/assets/img/footer/japan-sakura.jpg',
    date: '20/04/2024',
    title: 'Lễ Hội Hoa Anh Đào 2024',
    description: 'Lễ hội hoa anh đào lớn nhất năm tại công viên Ueno, Tokyo.',
    location: 'Công viên Ueno, Tokyo'
  },
  {
    image: '/src/assets/img/footer/japan-culture.webp',
    date: '15/05/2024',
    title: 'Triển Lãm Văn Hóa Việt-Nhật',
    description: 'Sự kiện giao lưu văn hóa giữa Việt Nam và Nhật Bản.',
    location: 'Trung tâm Văn hóa Nhật Bản, Osaka'
  }
]);
</script>

<style lang="scss" scoped>
.japan-news-page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding: 40px 0;

  .japan-news-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 20px;
  }

  .japan-news-header {
    text-align: center;
    margin-bottom: 40px;

    h1 {
      font-size: 36px;
      color: #333;
      margin-bottom: 10px;
    }

    .last-updated {
      color: #666;
      font-size: 14px;
    }
  }

  .news-section {
    background-color: white;
    border-radius: 8px;
    padding: 30px;
    margin-bottom: 30px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);

    h2 {
      font-size: 24px;
      color: #2c3e50;
      margin-bottom: 20px;
      padding-bottom: 10px;
      border-bottom: 2px solid #eee;
    }
  }

  .featured-news-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;

    .featured-news-item {
      border: 1px solid #eee;
      border-radius: 8px;
      overflow: hidden;

      .news-image {
        height: 200px;
        overflow: hidden;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .news-content {
        padding: 20px;

        .news-category {
          display: inline-block;
          padding: 4px 8px;
          background-color: #e3f2fd;
          color: #1976d2;
          border-radius: 4px;
          font-size: 12px;
          margin-bottom: 10px;
        }

        h3 {
          font-size: 18px;
          color: #333;
          margin-bottom: 10px;
          line-height: 1.4;
        }

        .news-excerpt {
          color: #666;
          font-size: 14px;
          line-height: 1.6;
          margin-bottom: 15px;
        }

        .news-meta {
          display: flex;
          justify-content: space-between;
          color: #999;
          font-size: 12px;
        }
      }
    }
  }

  .market-trends-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 20px;

    .trend-item {
      display: flex;
      align-items: flex-start;
      padding: 20px;
      background-color: #f8f9fa;
      border-radius: 8px;

      .trend-icon {
        font-size: 24px;
        color: #1976d2;
        margin-right: 15px;
      }

      .trend-content {
        flex: 1;

        h3 {
          font-size: 16px;
          color: #333;
          margin-bottom: 5px;
        }

        p {
          color: #666;
          font-size: 14px;
          margin-bottom: 10px;
        }

        .trend-stats {
          display: flex;
          align-items: center;
          gap: 10px;

          .trend-value {
            font-size: 20px;
            font-weight: bold;
            color: #333;
          }

          .trend-change {
            font-size: 14px;
            
            &.positive {
              color: #4caf50;
            }

            &.negative {
              color: #f44336;
            }

            i {
              margin-right: 4px;
            }
          }
        }
      }
    }
  }

  .job-categories {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 30px;

    .job-category {
      h3 {
        font-size: 18px;
        color: #2c3e50;
        margin-bottom: 15px;
      }

      .job-list {
        li {
          background-color: #f8f9fa;
          border-radius: 8px;
          padding: 15px;
          margin-bottom: 10px;

          .job-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 10px;

            .job-title {
              font-weight: 600;
              color: #333;
            }

            .job-salary {
              color: #1976d2;
              font-size: 14px;
            }
          }

          .job-details {
            display: flex;
            gap: 15px;
            font-size: 13px;
            color: #666;

            i {
              margin-right: 5px;
            }
          }
        }
      }
    }
  }

  .education-updates {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;

    .update-item {
      background-color: #f8f9fa;
      border-radius: 8px;
      padding: 20px;

      .update-icon {
        font-size: 24px;
        color: #1976d2;
        margin-bottom: 15px;
      }

      h3 {
        font-size: 18px;
        color: #333;
        margin-bottom: 10px;
      }

      p {
        color: #666;
        font-size: 14px;
        margin-bottom: 15px;
      }

      .update-details {
        list-style-type: none;
        padding: 0;

        li {
          color: #666;
          font-size: 14px;
          margin-bottom: 5px;
          padding-left: 20px;
          position: relative;

          &:before {
            content: "•";
            color: #1976d2;
            position: absolute;
            left: 0;
          }
        }
      }
    }
  }

  .events-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;

    .event-item {
      border: 1px solid #eee;
      border-radius: 8px;
      overflow: hidden;

      .event-image {
        height: 200px;
        overflow: hidden;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .event-content {
        padding: 20px;

        .event-date {
          display: inline-block;
          padding: 4px 8px;
          background-color: #e3f2fd;
          color: #1976d2;
          border-radius: 4px;
          font-size: 12px;
          margin-bottom: 10px;
        }

        h3 {
          font-size: 18px;
          color: #333;
          margin-bottom: 10px;
        }

        p {
          color: #666;
          font-size: 14px;
          margin-bottom: 15px;
        }

        .event-location {
          color: #666;
          font-size: 14px;

          i {
            margin-right: 5px;
            color: #1976d2;
          }
        }
      }
    }
  }
}

// Responsive styles
@media (max-width: 768px) {
  .japan-news-page {
    padding: 20px 0;

    .japan-news-container {
      padding: 0 15px;
    }

    .japan-news-header {
      h1 {
        font-size: 28px;
      }
    }

    .news-section {
      padding: 20px;

      h2 {
        font-size: 22px;
      }
    }

    .featured-news-grid,
    .market-trends-grid,
    .job-categories,
    .education-updates,
    .events-grid {
      grid-template-columns: 1fr;
    }
  }
}
</style> 