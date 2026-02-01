<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script>
export default {
  name: 'App'
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', 'Helvetica', 'Arial', sans-serif;
  background-color: #f5f5f5;
}

/* 添加响应式布局支持 */
@media (max-width: 1200px) {
  .container {
    padding: 0 10px;
  }
}

@media (max-width: 768px) {
  body {
    font-size: 14px;
  }
}
</style>
