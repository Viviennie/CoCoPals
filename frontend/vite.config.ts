import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      // Proxy websockets to ws://localhost:8080 for `npm run dev`
      '/ws': {
        target: 'ws://localhost:8080',
        ws: true
      }
    },
  }
});

