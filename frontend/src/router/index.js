import { createRouter, createWebHistory } from "vue-router";

import routes from "./routes";

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition; // Quay lại vị trí cuộn trước đó (nếu có)
    } else {
      return { top: 0 }; // Cuộn lên đầu trang
    }
  },
});

export default router;
