import { createRouter, createWebHistory } from "vue-router";
import routes from "./routes";
import store from '../store/store';

// Admin layout and views
const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach(async (to, from, next) => {
  console.log(`Navigating to: ${to.path}`);
  
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth);
  const requiresGuest = to.matched.some(record => record.meta.requiresGuest);
  
  // Check if user is authenticated
  const token = localStorage.getItem('token');
  const user = localStorage.getItem('user');
  const isAuthenticated = !!(token && user);
  
  console.log(`Route requires auth: ${requiresAuth}, User authenticated: ${isAuthenticated}`);
  
  // Validate token if exists
  if (token && isAuthenticated) {
    try {
      // Check if token is expired
      store.dispatch('auth/checkTokenValidity');
      
      // If token was cleared by checkTokenValidity, user is no longer authenticated
      const stillAuthenticated = store.getters['auth/isAuthenticated'];
      
      if (!stillAuthenticated && requiresAuth) {
        console.log('Token expired, redirecting to login');
        return next('/login');
      }
    } catch (error) {
      console.error('Error validating token:', error);
      if (requiresAuth) {
        return next('/login');
      }
    }
  }
  
  // Handle authentication requirements
  if (requiresAuth && !isAuthenticated) {
    console.log('Authentication required, redirecting to login');
    return next('/login');
  }
  
  if (requiresGuest && isAuthenticated) {
    console.log('Guest route accessed by authenticated user, redirecting to home');
    return next('/');
  }
  
  next();
});

export default router;
