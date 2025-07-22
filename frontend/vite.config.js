import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  base: '/',
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
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
    proxy: {
      '/oauth2': {
        target: 'https://oboeru.me',
        changeOrigin: true,
      },
      '/login/oauth2': {
        target: 'https://oboeru.me',
        changeOrigin: true,
      }
    }
  },
  css: {
    preprocessorOptions: {
      scss: {
        additionalData: `@use '@/assets/css/index.scss' as *;`,
      },
    },
  },
})
