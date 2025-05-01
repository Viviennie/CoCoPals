<template>
    <div
      class="button-container"
      @click="handleClick"
      :style="{ transform: isClicked ? 'scale(0.95)' : 'scale(1)' }"
      @mousedown="isClicked = true"
      @mouseup="isClicked = false"
      @mouseleave="isClicked = false"
    >
    <el-icon v-if="icon" class="icon">
      <component :is="icon" />
    </el-icon>
      <span class="text">{{ text }}</span>
    </div>
  </template>
  
  <script lang="ts">
  import { defineComponent, PropType, ref } from 'vue';
  import { useRouter } from 'vue-router';
  import { Component } from '@vue/runtime-core';
  
  export default defineComponent({
    name: 'FeatureButton',
    props: {
      icon: {
        type: Object as PropType<Component>, // 自定义图标组件
        required: true,
      },
      text: {
        type: String,
        required: true,
      },
      route: {
        type: String,
        required: true,
      },
    },
    setup(props) {
      const router = useRouter();
      const isClicked = ref(false);
  
      const handleClick = () => {
        router.push(props.route); // 跳转到指定路由
      };
  
      return { handleClick, isClicked };
    },
  });
  </script>
  
  <style scoped>
  .button-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 200px;
    height: 130px;
    background-color: #ffffff;
    border-radius: 10px;
    box-shadow: 1px 2px 5px rgba(0, 0, 0, 0.15);
    transition: transform 0.2s ease,background-color 0.2s ease;
    cursor: pointer;
  }

  .button-container:hover {
    background-color: #ebebeb;
    transform: translateY(-10%);
  }
  
  .icon {
    background: linear-gradient(45deg, #9BBCC3, #CBA5D1);
    /* background-color: #333; */
    color: #ffffff;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }
  
  .text {
    margin-top: 10px;
    font-weight: bold;
    color: #000;
  }
  </style>
  