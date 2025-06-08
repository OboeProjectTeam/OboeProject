import { createRouter, createWebHistory } from "vue-router";
import routes from "./routes";
import { auth } from '../firebase';

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

// Navigation guard
router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const requiresGuest = to.matched.some(record => record.meta.requiresGuest);
  
  auth.onAuthStateChanged(user => {
    if (to.path === '/' && !user) {
      next('/intro');
    } else if (requiresAuth && !user) {
      // Chuyển hướng đến trang đăng nhập nếu chưa đăng nhập
      next('/login');
    } else if (requiresGuest && user) {
      // Chuyển hướng về trang chủ nếu đã đăng nhập
      next('/');
    } else {
      next();
    }
  });
});

export default router;
