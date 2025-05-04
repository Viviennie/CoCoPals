<template>
    <StickyNavbar />
    <div class="wrapper-el">
      <div class="wrapper-el2">
        <div class="title">
          <h1>班级列表</h1>
          <div>
            <el-button
              v-if="identity === 'TEACHER'"
              class="add-button"
              size="large"
              type="primary"
              :icon="Plus"
              @click="openDialog_create()"
            >
              创建班级
            </el-button>
            <el-button
              v-else
              class="add-button"
              size="large"
              type="primary"
              :icon="Plus"
              @click="openDialog_join()"
            >
              加入班级
            </el-button>
          </div>
        </div>
  
        <!-- 创建班级弹窗 -->
        <el-dialog
          title="创建班级"
          v-model="isDialogVisible_create"
          width="30%"
        >
          <el-input v-model="classname" placeholder="请输入班级名称"></el-input>
          <template #footer>
            <div class="dialog-footer">
              <el-button @click="isDialogVisible_create = false">取消</el-button>
              <el-button type="primary" @click="handleCreate()">保存</el-button>
            </div>
          </template>
        </el-dialog>
  
        <!-- 加入班级弹窗 -->
        <el-dialog
          title="加入班级"
          v-model="isDialogVisible_join"
          width="30%"
        >
          <el-input v-model="joinCode" placeholder="请粘贴邀请码"></el-input>
          <template #footer>
            <div class="dialog-footer">
              <el-button @click="isDialogVisible_join = false">取消</el-button>
              <el-button type="primary" @click="handleJoin()">保存</el-button>
            </div>
          </template>
        </el-dialog>
  
        <div class="table">
          <el-table :data="filterTableData" stripe="true" style="width: 100%">
            <el-table-column label="班级ID" prop="id" />
            <el-table-column label="班级名称" prop="name" />
            <el-table-column label="教师" prop="teacher" />
            <el-table-column label="创建时间" prop="createTime" />
            <el-table-column align="right">
              <template #header>
                <el-input v-model="search" size="small" placeholder="输入搜索内容" />
              </template>
              <template #default="scope">
                <el-button 
                  size="small" 
                  type="primary"
                  @click="handleEnter(scope.$index, scope.row)"
                >
                  进入
                </el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </template>
  
  <script lang="ts" setup>
  import { computed, ref, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import StickyNavbar from '../components/Navbar.vue'
  import { Plus } from '@element-plus/icons-vue'
  import { ElMessage } from 'element-plus';
  import axios from 'axios'
  
  const router = useRouter()
  const identity = localStorage.getItem('role')
  
  interface Class {
    id: number
    name: string
    teacher: string
    createTime: string
  }
  
  const tableData = ref<Class[]>([])
  const search = ref('')
  const filterTableData = computed(() =>
    tableData.value.filter(
      (data: any) =>
        !search.value ||
        data.name.toLowerCase().includes(search.value.toLowerCase()) ||
        data.teacher.toLowerCase().includes(search.value.toLowerCase())
    )
  )
  
  // 获取班级列表
  const fetchClassList = async () => {
    try {
      const response = await axios.get('http://localhost:8048/class/getclasslist', {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      })
      tableData.value = response.data.map((item: any) => ({
        id: item.id,
        name: item.className,
        teacher: item.teacher,
        createTime: item.createTime
      }))
    } catch (error) {
      console.error('获取班级列表失败:', error)
      ElMessage.error('获取班级列表失败')
    }
  }
  
  // 进入班级
  const handleEnter = (index: number, row: any) => {
    router.push({
      path: '/classdetail',
      query: { classId: row.id.toString() }
    })
  }
  
  // 加入班级相关
  const isDialogVisible_join = ref(false)
  const joinCode = ref('')
  
  const openDialog_join = () => {
    isDialogVisible_join.value = true
  }
  
  const handleJoin = async () => {
    if (!joinCode.value.trim()) {
      ElMessage.warning('邀请码不能为空！')
      return
    }
  
    try {
      const response = await axios.post(
        `http://localhost:8048/class/join`,
        {
          classCode: joinCode.value
        },
        {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        }
      )
      ElMessage.success('加入成功！')
      router.push({
        path: '/classdetail',
        query: { classId: response.data }
      })
    } catch (error: any) {
      console.error("错误:", error.response || error.message)
      ElMessage.error('加入班级失败')
    } finally {
      isDialogVisible_join.value = false
    }
  }
  
  // 创建班级相关
  const isDialogVisible_create = ref(false)
  const classname = ref('')
  
  const openDialog_create = () => {
    isDialogVisible_create.value = true
  }
  
  const handleCreate = async () => {
    if (!classname.value.trim()) {
      ElMessage.warning('班级名称不能为空！')
      return
    }
  
    try {
      const response = await axios.post(
        `http://localhost:8048/class/create?className=${classname.value}`,
        {},
        {
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          }
        }
      )
      ElMessage.success('创建成功！')
      router.push({
        path: '/classdetail',
        query: { classId: response.data }
      })
    } catch (error: any) {
      console.error("错误:", error.response || error.message)
      ElMessage.error('创建班级失败')
    } finally {
      isDialogVisible_create.value = false
    }
  }
  
  onMounted(async () => {
    fetchClassList()
  })
  </script>
  
  <style scoped>
  .wrapper-el {
    display: flex;
    flex-direction: column;
  }
  
  .wrapper-el2 {
    margin: 12vh 12vw 12vh 12vw;
  }
  
  .title {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: 0px;
  }
  
  .table {
    padding: 10px;
    background-color: white;
    border-radius: 10px;
    /*box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);*/
  }
  
  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    padding: 10px 0;
    border-top: 1px solid #ebeef5;
    margin-top: 20px;
  }
  
  .dialog-footer .el-button {
    margin-left: 10px;
  }
  </style>