import App from './App'
import { createSSRApp } from 'vue'
import uView from 'uview-ui'

export function createApp() {
  const app = createSSRApp(App)
  app.use(uView)
  return {
    app
  }
}

