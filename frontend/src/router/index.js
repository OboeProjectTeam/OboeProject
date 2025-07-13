import { createRouter, createWebHistory } from "vue-router";
import routes from "./routes";
import { auth } from '../firebase';

// Admin layout and views


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

// Add emit function to route meta
router.beforeEach((to, from, next) => {
  // Add emit function to route meta
  to.meta.emit = (event, ...args) => {
    const appRoot = document.querySelector('#app')?.__vue__;
    if (appRoot && appRoot.emit) {
      appRoot.emit(event, ...args);
    }
  };

  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const requiresGuest = to.matched.some(record => record.meta.requiresGuest);
  const requiresAdmin = to.matched.some(record => record.meta.requiresAdmin);
  
  auth.onAuthStateChanged((user) => {
    if (requiresAuth && !user) {
      next('/login');
    } else if (requiresGuest && user) {
      next('/');
    } else if (requiresAdmin && !user) {
      next('/'); // Redirect to home if not admin
    } else {
      next();
    }
  });
});

export default router;
