<template>
  <StickyNavbar />
  <div class="wrapper-el">
    <div class="wrapper-el2">
    <div class="title">
      <h1>最近课堂</h1>
      <div>
          <el-button
              class="invited-button"
              size="large"
              type="plain"
              :icon="Checked"
              @click="openDialog_join()"
          >
              加入课堂
          </el-button>
          <el-button
              class="add-button"
              size="large"
              type="primary"
              :icon="Plus"
              @click="openDialog_add()"
          >
              新建课堂
          </el-button>
      </div>
    </div>

    <!-- 新建文件弹窗 -->
    <el-dialog
      title="创建课堂"
      v-model="isDialogVisible_add"
      width="30%"
    >
      <el-input v-model="filename" placeholder="请输入课堂名称"></el-input>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="isDialogVisible_add = false">取消</el-button>
          <el-button type="primary" @click="handleAdd()">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 删除文件弹窗 -->
    <el-dialog
      title="删除课堂记录"
      v-model="isDialogVisible_delete"
      width="30%"
    >
      <p>确认要删除此记录吗？</p>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="isDialogVisible_delete = false">取消</el-button>
          <el-button type="primary" @click="handleDelete()">确认</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 接受邀请弹窗 -->
    <el-dialog
      title="加入课堂"
      v-model="isDialogVisible_join"
      width="30%"
    >
      <el-input v-model="invitationCode" placeholder="请粘贴邀请码"></el-input>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="isDialogVisible_join = false">取消</el-button>
          <el-button type="primary" @click="handleInvitation()">保存</el-button>
        </div>
      </template>
    </el-dialog>

    <div class="table">
      <el-table :data="filterTableData" stripe="true" style="width: 100%">
        <el-table-column label="课堂ID" prop="id" />
        <el-table-column label="课堂名称" prop="title" />
        <el-table-column label="创建时间" prop="createTime" />
        <el-table-column label="最后修改" prop="lastModifiedTime" />
        <el-table-column label="创建者" prop="ownerName" />
        <el-table-column align="right">
          <template #header>
            <el-input v-model="search" size="small" placeholder="输入搜索内容" />
          </template>
          <template #default="scope">
            <el-button size="small" @click="handleEdit(scope.$index, scope.row)">
              编辑
            </el-button>
            <el-button
                v-if="showDeleteButton(scope.row)"
                size="small"
                type="danger"
                @click="openDialog_delete(scope.$index, scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    </div>
  </div>
</template>

<script lang="ts" setup>
import {computed, ref, onMounted, defineProps} from 'vue'
import { useRouter } from 'vue-router'
import StickyNavbar from '../components/Navbar.vue'
import { Plus, Checked, Document } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus';
import axios from 'axios'

const user = ref({
  name: localStorage.getItem('username'),
  email: localStorage.getItem('useremail'),
  nickname: localStorage.getItem('username'),
  gender: 'male',
  birthday: '2000-01-01',
  school: '同济大学',
  github: 'https://github.com/SmithTime'
});
const showDeleteButton = (doc: Document) => {
  return doc.ownerName === user.value.name;
};

const router = useRouter()


// 定义接口 Document
interface Document {
id: number;
ownerName: string;
createTime: string;
lastModifiedTime: string;
title: string;
}
// 定义表格数据类型为 Document[]
const tableData = ref<Document[]>([]);
const search = ref('')
const filterTableData = computed(() =>
  tableData.value.filter(
    (data:any) =>
      !search.value ||
      data.ownerName.toLowerCase().includes(search.value.toLowerCase()) ||
      data.title.toLowerCase().includes(search.value.toLowerCase())
  )
);

const isDialogVisible_add = ref(false);
const filename = ref("");

const openDialog_add = () => {
  isDialogVisible_add.value = true;
};

const handleAdd = async () => {
  if (!filename.value.trim()) {
    ElMessage({
      message: '课堂名不能为空！',
      type: 'warning',
      duration: 3000, 
    })
    return;
  }
  try {
    const response = await axios.post(
      `http://localhost:8048/document/create?title=${filename.value}`,
      {},
      {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    );
    console.log(response.data);
    ElMessage({
      message: '创建成功！',
      type: 'success',
      duration: 3000, 
    })

    // 刷新文件列表
    await fetchFileList();
  } catch (error:any) {
    console.error("Error:", error.response || error.message);
    ElMessage({
      message: '课堂创建失败',
      type: 'error',
      duration: 3000, 
    })
  } finally {
    isDialogVisible_add.value = false; // 关闭弹窗
  }
};

const isDialogVisible_join = ref(false);
const invitationCode = ref<string>("");

const openDialog_join = () => {
  isDialogVisible_join.value = true;
};

const handleInvitation = async () => {
  if (!invitationCode.value.trim()) {
    ElMessage({
      message: '邀请码不能为空！',
      type: 'warning',
      duration: 3000, 
    })
    return;
  }

  try {
    const response = await axios.post(
      `http://localhost:8048/document/connectServiceByInvitation`,
      {
        invitationCode: invitationCode.value
      },
      {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      }
    );
    ElMessage({
      message: '授权成功！',
      type: 'success',
      duration: 3000, 
    })
    await router.push({
      path: '/basecode',
      query: {documentId: response.data}
    })
  } catch (error:any) {
    console.error("Error:", error.response || error.message);
    ElMessage({
      message: '加入课堂失败',
      type: 'error',
      duration: 3000, 
    })
  } finally {
    isDialogVisible_join.value = false; // 关闭弹窗
  }
};

const handleEdit = (index: number, row: any) => {
  console.log(row.id);
  router.push({
    path: '/basecode',
    query: { documentId: row.id.toString() } 
  })
}

const isDialogVisible_delete = ref(false);
let row = ref();
let index = ref<number>(0);

const openDialog_delete = (index_: number, row_: any) => {
  row.value = row_;
  index.value = index_;
  isDialogVisible_delete.value = true;
};

// 删除文件的方法
const handleDelete = async () => {
  const documentId = row.value.id;  // 获取当前行的 fileId
  try {
    console.log(documentId);
    const response = await axios.post(
      `http://localhost:8048/document/delete?documentId=${documentId}`,
      {},
      {
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`  // 在请求头中添加 Authorization
        }
    });
    
    // 如果删除成功，可以移除表格中的该行
    if (response.status === 200) {
      tableData.value.splice(index.value, 1);  // 删除前端数据中的该行
      ElMessage({
        message: '删除成功！',
        type: 'success',
        duration: 3000, 
      })
    }
  } catch (error:any) {
    if(error.response){
      ElMessage({
        message: error.response.data,
        type: 'error',
        duration: 3000, 
      })
    }
    console.error('删除文件失败:', error);
  } finally {
    isDialogVisible_delete.value = false; // 关闭弹窗
  }
};

const fetchFileList = async()=> {
  try {
    const response = await axios.get('http://localhost:8048/document/getfilelist',{
        headers: {
          'Authorization': `Bearer ${localStorage.getItem('token')}`
        }
      });
    tableData.value = response.data.map((item:Document) => ({
      id: item.id,
      createTime: item.createTime,
      lastModifiedTime: item.lastModifiedTime,
      ownerName: item.ownerName,
      title: item.title
    }));
  } catch (error: any) {
    console.error('获取课堂列表失败:', error);
    // 显示具体错误信息
    if (error.response) {
      console.error('服务器返回错误:', error.response.data);
      ElMessage.error(`获取课堂列表失败: ${error.response.data.message || error.response.status}`);
    }
  }
}

onMounted(async () => {
  await fetchFileList();
});
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
    justify-content:space-between;
  }

  .invited-button .el-icon {
    color: #5d5d5d;
  }

  .add-button .el-icon {
    color: #fff;
  }
  
  .table {
    padding: 10px;
    background-color: white;
    border-radius: 10px;
  }
  </style>
  