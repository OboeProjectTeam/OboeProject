import { createRouter, createWebHistory } from "vue-router";
import routes from "./routes";
import store from '../store/store';

// Admin layout and views
const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior(to, from, savedPosition) {
    // If there's a saved position (back/forward navigation), use it
    if (savedPosition) {
      return savedPosition;
    }
    // If there's a hash (anchor link), scroll to that element
    if (to.hash) {
      return {
        el: to.hash,
        behavior: 'smooth'
      };
    }
    // Otherwise, scroll to top
    return { top: 0, behavior: 'smooth' };
  }
});

router.beforeEach(async (to, from, next) => {

  
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const requiresGuest = to.matched.some(record => record.meta.requiresGuest);
  const requiresAdmin = to.matched.some(record => record.meta.requiresAdmin);
  
  // Check if user is authenticated
  const token = localStorage.getItem('token');
  const user = JSON.parse(localStorage.getItem('user') || 'null');
  const isAuthenticated = !!(token && user);
  

  
  // Validate token if exists
  if (token && isAuthenticated) {
    try {
      // Check if token is expired
      store.dispatch('auth/checkTokenValidity');
      
      // If token was cleared by checkTokenValidity, user is no longer authenticated
      const stillAuthenticated = store.getters['auth/isAuthenticated'];
      
      if (!stillAuthenticated && requiresAuth) {

        return next('/login');
      }
    } catch (error) {
      console.error('Error validating token:', error);
      if (requiresAuth) {
        return next('/login');
      }
    }
  }
  
  // Check if route requires admin access
  if (to.path.startsWith('/admin')) {
    if (!isAuthenticated) {
      return next('/login');
    }
    
    if (user && user.role !== 'ROLE_ADMIN') {
      // Redirect non-admin users to home page
      return next('/');
    }
  }
  
  // Handle authentication requirements
  if (requiresAuth && !isAuthenticated) {

    return next('/intro');
  }
  
  if (requiresGuest && isAuthenticated) {

    return next('/');
  }
  
  next();
});

export default router;
