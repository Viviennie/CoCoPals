<template>
    <div class="markdown-renderer" v-html="renderedMarkdown"></div>
  </template>
  
  <script setup lang="ts">
  import { computed } from 'vue';
  import MarkdownIt from 'markdown-it';
  import highlightjs from 'markdown-it-highlightjs';
  
  // 定义 props
  const props = defineProps({
    markdownText: {
      type: String,
      required: true,
    },
  });
  
  // 初始化 markdown-it 实例
  const md = new MarkdownIt().use(highlightjs);
  
  // 计算属性：将 markdown 文本渲染为 HTML
  const renderedMarkdown = computed(() => {
    return md.render(props.markdownText);
  });
  </script>
  
  <style scoped>
  .markdown-renderer {
    font-family: Arial, sans-serif;
    line-height: 1.6;
  }
  
  .markdown-renderer :deep(h1) {
    font-size: 2em;
    margin: 0.67em 0;
  }
  
  .markdown-renderer :deep(h2) {
    font-size: 1.5em;
    margin: 0.83em 0;
  }
  
  .markdown-renderer :deep(p) {
    margin: 1em 0;
  }
  
  .markdown-renderer :deep(a) {
    color: #0366d6;
    text-decoration: none;
  }
  
  .markdown-renderer :deep(a:hover) {
    text-decoration: underline;
  }
  
  .markdown-renderer :deep(code) {
    font-family: monospace;
    background-color: #f6f8fa;
    padding: 0.2em 0.4em;
    border-radius: 3px;
  }
  
  .markdown-renderer :deep(pre) {
    background-color: #f6f8fa;
    padding: 1em;
    border-radius: 4px;
    overflow-x: auto;
  }
  
  .markdown-renderer :deep(blockquote) {
    margin: 0;
    padding: 0 1em;
    color: #6a737d;
    border-left: 0.25em solid #dfe2e5;
  }
  </style>