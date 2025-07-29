import './index.css'
import { createApp } from "vue";
import App from "./App.vue";
import router from "./router/index";
import store from "./store/store";
import { auth } from "./firebase"; 

const app = createApp(App);

auth.onAuthStateChanged((user) => {
  if (user) {
    // Nếu đang ở trong domain firebaseapp → redirect về domain thật sự
    const currentUrl = window.location.href;
    if (currentUrl.includes("firebaseapp.com")) {
      window.location.href = "https://oboeru.me";
    }
  }
});

app.use(router);
app.use(store);
app.mount("#app");
