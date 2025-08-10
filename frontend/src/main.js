import './index.css'
import 'flag-icons/css/flag-icons.min.css'
import { createApp } from "vue";
import App from "./App.vue";
import router from "./router/index";
import store from "./store/store";
import i18n from "./i18n";

const app = createApp(App);

app.use(router);
app.use(store);
app.use(i18n);
app.mount("#app");
