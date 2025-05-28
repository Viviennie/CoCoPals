<template>

  <!-- 批注画布容器 -->
  <div class="canvas-container">
    <!-- 可拖动的批注工具栏 -->
    <div
        v-if="showToolbar"
        class="annotation-toolbar"
        :style="toolbarStyle"
        @mousedown="startDrag"
    >

      <div class="tools-container">
        <div class="toolbar-header">批注工具</div>
        <select v-model="selectedTool" class="tool-select">
          <option value="pen">画笔</option>
          <option value="eraser">橡皮擦</option>
          <option value="rectangle">矩形</option>
          <option value="circle">圆形</option>
          <option value="line">直线</option>
          <option value="text">文字</option>
        </select>

        <input type="color" v-model="selectedColor" class="color-picker">

        <select v-model="selectedWidth" class="width-select">
          <option value="1">细</option>
          <option value="3">中</option>
          <option value="5">粗</option>
          <option value="8">特粗</option>
        </select>

        <button @click="clearCanvas" class="clear-btn">清屏</button>
      </div>
    </div>

    <canvas
        ref="annotationCanvas"
        class="annotation-canvas"
        :width="width"
        :height="height"
        @mousedown="startDrawing"
        @mousemove="draw"
        @mouseup="stopDrawing"
        @mouseleave="stopDrawing"
    ></canvas>
    <canvas
        ref="previewCanvas"
        class="annotation-canvas"
        :width="width"
        :height="height"
        style="position: absolute; top: 0; left: 0; pointer-events: none;"
    ></canvas>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch, nextTick, computed, onBeforeUnmount } from 'vue';

import {useClassStore} from "../stores/classStore";
import {ElMessageBox} from "element-plus";
const classStore = useClassStore();

const props = defineProps({
  width: {
    type: Number,
    default: 900
  },
  height: {
    type: Number,
    default: 500
  },
  isAnnotating: {
    type: Boolean,
    default: false
  },
  showToolbar: {
    type: Boolean,
    default: true
  },
  documentId: {
    type: Number
  }
});

const annotationCanvas = ref<HTMLCanvasElement | null>(null);
const previewCanvas = ref<HTMLCanvasElement | null>(null);
const ctx = ref<CanvasRenderingContext2D | null>(null);
const previewCtx = ref<CanvasRenderingContext2D | null>(null);
const isDrawing = ref(false);
const lastX = ref(0);
const lastY = ref(0);
const startX = ref(0);
const startY = ref(0);
const isDrawingShape = ref(false);

const currentClass = computed(() => {
  return classStore.classes.find(c => c.documentId === props.documentId);
});

// 批注工具
const selectedTool = ref('pen');
const selectedColor = ref('#ff0000');
const selectedWidth = ref('3');

// 工具栏拖动相关状态
const isDragging = ref(false);
const toolbarPosition = ref({ x: props.width - 400, y: props.height + 20 });
const dragOffset = ref({ x: 0, y: 0 });

const toolbarStyle = computed(() => ({
  left: `${toolbarPosition.value.x}px`,
  top: `${toolbarPosition.value.y}px`,
  transform: isDragging.value ? 'scale(1.02)' : 'none',
  cursor: isDragging.value ? 'grabbing' : 'grab'
}));

const startDrag = (e: MouseEvent) => {
  // 只有当点击的是工具栏头部或空白区域时才允许拖动
  const target = e.target as HTMLElement;
  if (target.classList.contains('toolbar-header') ||
      target.classList.contains('annotation-toolbar') ||
      target.classList.contains('tools-container')) {
    isDragging.value = true;
    dragOffset.value = {
      x: e.clientX - toolbarPosition.value.x,
      y: e.clientY - toolbarPosition.value.y
    };

    // 阻止事件冒泡，避免触发画布绘制
    e.stopPropagation();
    e.preventDefault();
  }
};

const handleDrag = (e: MouseEvent) => {
  if (isDragging.value) {
    toolbarPosition.value = {
      x: e.clientX - dragOffset.value.x,
      y: e.clientY - dragOffset.value.y
    };
  }
};

const stopDrag = () => {
  isDragging.value = false;
};

onMounted(() => {
  initCanvas();
  // 添加全局鼠标移动和松开事件监听
  window.addEventListener('mousemove', handleDrag);
  window.addEventListener('mouseup', stopDrag);
});

onBeforeUnmount(() => {
  // 移除事件监听
  window.removeEventListener('mousemove', handleDrag);
  window.removeEventListener('mouseup', stopDrag);
});

watch(() => props.width, (newWidth) => {
  // 当画布宽度变化时，调整工具栏位置保持右上角
  toolbarPosition.value.x = newWidth - 400;
});

watch(() => props.isAnnotating, (newVal) => {
  if (annotationCanvas.value) {
    annotationCanvas.value.style.pointerEvents = newVal ? 'auto' : 'none';
  }
});

watch(
    () => currentClass.value?.annotations,
    (newAnnotations) => {
      console.log("发现新批注");
      if(newAnnotations){
        const blob = new Blob([newAnnotations], { type: "image/png" });
        restoreAnnotations(blob);
      }
    },
    { deep: true }
);

const initCanvas = () => {
  nextTick(() => {
    if (annotationCanvas.value && previewCanvas.value) {
      ctx.value = annotationCanvas.value.getContext('2d');
      previewCtx.value = previewCanvas.value.getContext('2d');

      if (ctx.value && previewCtx.value) {
        ctx.value.lineJoin = 'round';
        ctx.value.lineCap = 'round';
        ctx.value.strokeStyle = selectedColor.value;
        ctx.value.lineWidth = parseInt(selectedWidth.value);

        previewCtx.value.lineJoin = 'round';
        previewCtx.value.lineCap = 'round';
        previewCtx.value.strokeStyle = selectedColor.value;
        previewCtx.value.lineWidth = parseInt(selectedWidth.value);
      }
      annotationCanvas.value.style.pointerEvents = props.isAnnotating ? 'auto' : 'none';
    }
  });
};

const startDrawing = (e: MouseEvent) => {
  if (!props.isAnnotating || !ctx.value || !previewCtx.value) return;

  isDrawing.value = true;
  [lastX.value, lastY.value] = [e.offsetX, e.offsetY];
  [startX.value, startY.value] = [e.offsetX, e.offsetY];

  if (selectedTool.value === 'text') {
    ElMessageBox.prompt('请输入文字:', '文字输入', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
    }).then(({ value }) => {
      if (value && ctx.value) {
        ctx.value.font = `${parseInt(selectedWidth.value) * 5}px Arial`;
        ctx.value.fillStyle = selectedColor.value;
        ctx.value.fillText(value, lastX.value, lastY.value);
        isDrawing.value = false;
        saveAnnotations();
      }
    }).catch(() => {
      // 用户点击取消
      isDrawing.value = false;
    });
    return;
  }

  // 对于形状工具，设置标志位
  if (['rectangle', 'circle', 'line'].includes(selectedTool.value)) {
    isDrawingShape.value = true;
  }
};

const draw = (e: MouseEvent) => {
  if (!isDrawing.value || !ctx.value || !previewCtx.value) return;

  const x = e.offsetX;
  const y = e.offsetY;
  lastX.value = x;
  lastY.value = y;

  // 清除预览画布
  previewCtx.value.clearRect(0, 0, previewCanvas.value!.width, previewCanvas.value!.height);

  if (selectedTool.value === 'pen') {
    ctx.value.strokeStyle = selectedColor.value;
    ctx.value.lineWidth = parseInt(selectedWidth.value);
    ctx.value.beginPath();
    ctx.value.moveTo(startX.value, startY.value);
    ctx.value.lineTo(x, y);
    ctx.value.stroke();
    [startX.value, startY.value] = [x, y];
  }
  else if (selectedTool.value === 'eraser') {
    ctx.value.globalCompositeOperation = 'destination-out';
    ctx.value.beginPath();
    ctx.value.arc(x, y, parseInt(selectedWidth.value) * 2, 0, Math.PI * 2);
    ctx.value.fill();
    ctx.value.globalCompositeOperation = 'source-over';
  }
  else if (isDrawingShape.value) {
    previewCtx.value.strokeStyle = selectedColor.value;
    previewCtx.value.lineWidth = parseInt(selectedWidth.value);

    switch (selectedTool.value) {
      case 'rectangle':
        previewCtx.value.beginPath();
        previewCtx.value.rect(startX.value, startY.value, x - startX.value, y - startY.value);
        previewCtx.value.stroke();
        break;

      case 'circle':
        previewCtx.value.beginPath();
        const radius = Math.sqrt(Math.pow(x - startX.value, 2) + Math.pow(y - startY.value, 2));
        previewCtx.value.arc(startX.value, startY.value, radius, 0, Math.PI * 2);
        previewCtx.value.stroke();
        break;

      case 'line':
        previewCtx.value.beginPath();
        previewCtx.value.moveTo(startX.value, startY.value);
        previewCtx.value.lineTo(x, y);
        previewCtx.value.stroke();
        break;
    }
  }
};

const stopDrawing = () => {
  if (!isDrawing.value || !ctx.value || !previewCtx.value) return;
  ctx.value.lineWidth = parseInt(selectedWidth.value);
  // 对于形状工具，只在松开鼠标时最终绘制一次
  if (isDrawingShape.value) {
    switch (selectedTool.value) {
      case 'rectangle':
        ctx.value.beginPath();
        ctx.value.rect(startX.value, startY.value, lastX.value - startX.value, lastY.value - startY.value);
        ctx.value.stroke();
        break;

      case 'circle':
        ctx.value.beginPath();
        const radius = Math.sqrt(Math.pow(lastX.value - startX.value, 2) + Math.pow(lastY.value - startY.value, 2));
        ctx.value.arc(startX.value, startY.value, radius, 0, Math.PI * 2);
        ctx.value.stroke();
        break;

      case 'line':
        ctx.value.beginPath();
        ctx.value.moveTo(startX.value, startY.value);
        ctx.value.lineTo(lastX.value, lastY.value);
        ctx.value.stroke();
        break;
    }

    // 清除预览
    previewCtx.value.clearRect(0, 0, previewCanvas.value!.width, previewCanvas.value!.height);
  }

  saveAnnotations();
  isDrawing.value = false;
  isDrawingShape.value = false;
};

const clearCanvas = async () => {
  if (!ctx.value || !annotationCanvas.value) return;
  ctx.value.clearRect(0, 0, annotationCanvas.value.width, annotationCanvas.value.height);
  saveAnnotations();
};

const saveAnnotations = () => {
  if(annotationCanvas.value){
    annotationCanvas.value.toBlob((blob) => {
      if (blob) {
        console.log('批注内容 (Blob):', blob);
        classStore.updateAnnotations(props.documentId, blob);
      }
    }, 'image/png');
  }
};

const restoreAnnotations = (blob: Blob) => {
  if (!ctx.value || !annotationCanvas.value) return;

  // 创建一个临时 URL 来读取 Blob
  const imgUrl = URL.createObjectURL(blob);
  const img = new Image();

  img.onload = () => {
    // 清除当前画布
    ctx.value?.clearRect(0, 0, annotationCanvas.value!.width, annotationCanvas.value!.height);
    // 绘制新的批注内容
    ctx.value?.drawImage(img, 0, 0);
    // 释放临时 URL
    URL.revokeObjectURL(imgUrl);
  };

  img.onerror = () => {
    console.error("Failed to load annotations image");
    URL.revokeObjectURL(imgUrl);
  };

  img.src = imgUrl;
};
</script>

<style scoped>
.canvas-container {
  position: relative;
  width: fit-content;
}

.annotation-toolbar {
  position: absolute;
  z-index: 100;
  display: flex;
  flex-direction: column;
  gap: 8px;
  padding: 10px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
  width: 350px; /* 固定宽度 */
  transition: transform 0.1s ease;
  user-select: none;
  cursor: grab;
}

.toolbar-header {
  padding: 4px 8px;
  font-weight: bold;
  text-align: center;
  white-space: nowrap; /* 防止文本换行 */
}

.tools-container {
  display: flex;
  gap: 8px;
  align-items: center;
  flex-wrap: nowrap; /* 禁止换行 */
  overflow: hidden; /* 隐藏超出部分 */
}

.annotation-canvas {
  display: block;
  pointer-events: none;
  z-index: 10;
  //border: 1px solid #d9d9d9;
}

.tool-select, .width-select {
  padding: 4px 8px;
  border: 1px solid #d9d9d9;
  border-radius: 4px;
  background-color: white;
  height: 30px;
  cursor: default;
  width: 80px; /* 固定下拉框宽度 */
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.color-picker {
  padding: 0;
  width: 30px;
  height: 30px;
  flex-shrink: 0; /* 禁止缩小 */
}

.clear-btn {
  background-color: #ff4d4f;
  color: white;
  border: none;
  padding: 4px 8px;
  border-radius: 4px;
  cursor: pointer;
  height: 30px;
  flex-shrink: 0; /* 禁止缩小 */
  white-space: nowrap;
}

.clear-btn:hover {
  background-color: #ff7875;
}

.annotation-toolbar.grabbing {
  cursor: grabbing;
}
</style>