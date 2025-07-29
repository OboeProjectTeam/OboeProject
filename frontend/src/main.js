import './index.css'
import { createApp } from "vue";
import App from "./App.vue";
import router from "./router/index";
import store from "./store/store";

const app = createApp(App);

app.use(router);
app.use(store);
app.mount("#app");
