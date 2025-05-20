// filepath: src/router/index.js
import TheHome from "../views/home/TheHome.vue";
import TheIntro from "../views/intro/TheIntro.vue";
import TheLogin from "../views/login/TheLogin.vue";
import TheRegister from "../views/register/TheRegister.vue";
const routes = [
  {
    path: "/",
    name: "Home",
    component: TheHome,
  },
  {
    path: "/intro",
    name: "Intro",
    component: TheIntro,
  },
  {
    path: "/login",
    name: "Login",
    component: TheLogin,
  },
  {
    path: "/register",
    name: "Register",
    component: TheRegister,
  },
  
];

export default routes;