import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src') // Đảm bảo alias cho thư mục src
    }
  },
  server: {
    hmr: false,
    host: true,
    allowedHosts: [
      '13be-2001-ee0-4041-ccc9-a84e-90fd-4648-9aa.ngrok-free.app'
    ],
    headers: {
      'Cross-Origin-Opener-Policy': 'unsafe-none',
      'Cross-Origin-Embedder-Policy': 'unsafe-none'
    },
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use '@/assets/css/index.scss' as *;`, // Sử dụng alias @ cho đường dẫn
      },
    },
  },
})
