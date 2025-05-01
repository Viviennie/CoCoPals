<!-- src/components/ModelViewer.vue -->
<template>
    <div ref="canvasContainer" class="canvas-container"></div>
  </template>
  
  <script lang="ts" setup>
  import { ref, onMounted } from 'vue';
  import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader.js';
  import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js';
  import * as THREE from 'three';
  
  // 定义模型文件路径
  const illustrationSrc = '/unicorn.gltf'; // Vue 会自动将 / 识别为 public 文件夹
  
  // 使用 Vue 的 ref 引用 canvas 容器
  const canvasContainer = ref<HTMLDivElement | null>(null);
  
  onMounted(() => {
    if (!canvasContainer.value) return;
  
    // 初始化 Three.js 场景和渲染器
    const scene = new THREE.Scene();
    const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
    renderer.setSize(canvasContainer.value.clientWidth, canvasContainer.value.clientHeight);
    renderer.setPixelRatio(window.devicePixelRatio);
    canvasContainer.value.appendChild(renderer.domElement);
  
    const camera = new THREE.PerspectiveCamera(75, canvasContainer.value.clientWidth / canvasContainer.value.clientHeight, 0.1, 1000);
    camera.position.z = 1.8;
  
    // 添加 OrbitControls 控制器
    const controls = new OrbitControls(camera, renderer.domElement);
    controls.enableDamping = true; // 使控制具有阻尼感，更加平滑
    controls.dampingFactor = 0.1; // 阻尼系数
    controls.rotateSpeed = 0.5; // 调整旋转速度
  
    const ambientLight = new THREE.AmbientLight(0xffffff, 2.5); 
    scene.add(ambientLight);
  
    // 加载 GLTF 模型
    const loader = new GLTFLoader();
    loader.load(
      illustrationSrc,
      (gltf) => {
        scene.add(gltf.scene); // 将加载的模型添加到场景
        animate(); // 启动渲染循环
      },
      undefined,
      (error) => {
        console.error('An error occurred:', error);
      }
    );
  
    // 渲染循环
    function animate() {
      requestAnimationFrame(animate);
      controls.update(); // 每帧更新控制器状态
      renderer.render(scene, camera);
    }
  
    // 窗口大小调整时更新渲染器和相机
    window.addEventListener('resize', () => {
      if (!canvasContainer.value) return;
      const width = canvasContainer.value.clientWidth;
      const height = canvasContainer.value.clientHeight;
      renderer.setSize(width, height);
      camera.aspect = width / height;
      camera.updateProjectionMatrix();
    });
  });
  </script>
  
  <style scoped>
  .canvas-container {
    width: 150px;
    height: 150px;
  }
  </style>
  