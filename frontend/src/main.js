import { createApp } from "vue";
import App from "./App.vue";
import router from "./router/index";
import store from "./store/store"; // Import Vuex store

const app = createApp(App);

app.use(router);
app.use(store); // Sử dụng Vuex store

app.mount("#app");
