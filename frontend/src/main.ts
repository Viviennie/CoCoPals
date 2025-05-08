import { createApp } from 'vue';
import App from './App.vue';
import router from './router'; // 导入路由
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import {createPinia} from "pinia";

const app = createApp(App)
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}
const pinia = createPinia()
// 全局注册 ElMessage
app.config.globalProperties.$message = ElMessage;

// 使用 Element Plus
app.use(ElementPlus);
app.use(router); // 使用路由
app.use(pinia);
app.mount('#app');
