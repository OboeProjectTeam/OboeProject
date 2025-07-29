import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

export default defineConfig({
  base: '/',
  plugins: [vue()],
  define: {
    global: 'globalThis',
  },
  resolve: {
    alias: {
      '@': path.resolve(__dirname, 'src')
    }
  },
  server: {
    port: 5175, // Match current running port
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
      '/api': {
        target: 'https://oboeru.me',
        changeOrigin: true,
        secure: true,
        configure: (proxy, options) => {
          proxy.on('error', (err, req, res) => {

          });
          proxy.on('proxyReq', (proxyReq, req, res) => {

          });
          proxy.on('proxyRes', (proxyRes, req, res) => {

          });
        },
      },
      '/ws': {
        target: 'https://oboeru.me',
        changeOrigin: true,
        secure: true,
        ws: true, // Enable WebSocket proxying
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
