import { createRouter, createWebHistory } from "vue-router";
import routes from "./routes";
import store from '../store/store';

// Admin layout and views
const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach((to, from, next) => {
  const requiresAuth = to.matched.some(record => record.meta.requiresAuth)
  const isAuthenticated = store.state.auth.user !== null

  if (requiresAuth && !isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router;
