<template>
    <!-- 卡片结构 -->
    <el-card class="motto-card" >
      <!-- Head 区域 -->
      <div class="head" ></div>
  
      <!-- Content 区域 -->
      <div class="content">
        <span class="motto-text" >{{ motto }}</span>
        <img 
        src="/edit_motto.svg" 
        alt="Edit" 
        class="edit-icon"
        @click="openEditDialog"
      />
      </div>
  
      <!-- 编辑弹窗 -->
      <el-dialog
        title="Edit Motto"
        v-model="isDialogVisible"
        width="30%"
      >
        <el-input v-model="newMotto" placeholder="Tpye your favorite motto here:"></el-input>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="isDialogVisible = false">cancel</el-button>
            <el-button type="primary" @click="updateMotto">save</el-button>
          </div>
        </template>
      </el-dialog>
    </el-card>
  </template>
  
  <script lang="ts" setup>
  import { defineProps, defineEmits, ref } from 'vue';
  import { ElMessageBox } from 'element-plus';
  import { Edit } from '@element-plus/icons-vue';
  
  const props = defineProps<{
    motto: string; // 格言内容
  }>();
  
  const emit = defineEmits(['update:motto']);
  
  const isDialogVisible = ref(false); // 控制弹窗显示
  const newMotto = ref(props.motto); // 用于编辑的新格言
  
  // 打开编辑弹窗
  const openEditDialog = () => {
    isDialogVisible.value = true;
  };
  
  // 更新格言并关闭弹窗
  const updateMotto = () => {
    emit('update:motto', newMotto.value); // 触发更新事件
    isDialogVisible.value = false;
  };

  </script>
  
  <style scoped>
  .motto-card {
    display:flexbox;
    width: 700px;
    height: 90px; 
    border-radius: 5px;
    overflow: hidden;
    border: none;
    padding: 0;
    margin: 5px auto;
    background:linear-gradient(45deg, #9BBCC3, #CBA5D1);
    
  }
  .head {
    position:relative;
    top:-20px;
    left:-20px;
    margin-bottom: 7px;
    width: 110%;
    height: 30px;
    border:none;
    background-color: rgb(62, 62, 62);
  }
  
  .content {
    top:-20px;
    left:-20px;
    width: 102%;
    height: 25px;
    margin:0;
    display:flex;
    align-items: center;
    padding: 15px;
    position: relative;
    background-color: hsl(0, 0%, 100%);
  }
  
  .motto-text {
    margin: 0;
    flex: 1;
    font-size: 16px;
    color:#818181
  }
  
  .edit-icon {
    background-color: #ffffff;
    cursor: pointer;
    position: absolute;
    right:20px;
    top:15px;
    margin-left: 100px;
    border-radius: 0;
    width: 15px; 
    height: 15px; /* 设置图标大小 */
  }
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
  }
  </style>
  