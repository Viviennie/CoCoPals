// shims-vue.d.ts
import { ElMessage } from 'element-plus';

declare module '@vue/runtime-core' {
  interface ComponentCustomProperties {
    $message: typeof ElMessage;
  }
}
