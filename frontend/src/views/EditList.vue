<template>
    <StickyNavbar />
    <div class="wrapper-el">
      <div class="wrapper-el2">
      <div class="title">
        <h1>Latest Works</h1>
        <div>
            <el-button
                class="invited-button"
                size="large"
                type="plain"
                :icon="Checked"
                @click="openDialog_join()"
            >
                Invited
            </el-button>
            <el-button
                class="add-button"
                size="large"
                type="primary"
                :icon="Plus"
                @click="openDialog_add()"
            >
                Add New
            </el-button>
        </div>
      </div>

      <!-- 新建文件弹窗 -->
      <el-dialog
        title="Create File"
        v-model="isDialogVisible_add"
        width="30%"
      >
        <el-input v-model="filename" placeholder="Tpye your filename here:"></el-input>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="isDialogVisible_add = false">cancel</el-button>
            <el-button type="primary" @click="handleAdd()">save</el-button>
          </div>
        </template>
      </el-dialog>

      <!-- 删除文件弹窗 -->
      <el-dialog
        title="Delete File"
        v-model="isDialogVisible_delete"
        width="30%"
      >
        <p>Are you sure to delete this file?</p>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="isDialogVisible_delete = false">cancel</el-button>
            <el-button type="primary" @click="handleDelete()">confirm</el-button>
          </div>
        </template>
      </el-dialog>

      <!-- 接受邀请弹窗 -->
      <el-dialog
        title="Join Work"
        v-model="isDialogVisible_join"
        width="30%"
      >
        <el-input v-model="invitationCode" placeholder="Paste your invitation code here:"></el-input>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="isDialogVisible_join = false">cancel</el-button>
            <el-button type="primary" @click="handleInvitation()">save</el-button>
          </div>
        </template>
      </el-dialog>

      <div class="table">
        <el-table :data="filterTableData" stripe="true" style="width: 100%">
          <el-table-column label="FileID" prop="id" />
          <el-table-column label="File" prop="title" />
          <el-table-column label="Create Time" prop="createTime" />
          <el-table-column label="Last Time" prop="lastModifiedTime" />
          <el-table-column label="Host" prop="ownerName" />
          <el-table-column align="right">
            <template #header>
              <el-input v-model="search" size="small" placeholder="Type to search" />
            </template>
            <template #default="scope">
              <el-button size="small" @click="handleEdit(scope.$index, scope.row)">
                Edit
              </el-button>
              <el-button
                size="small"
                type="danger"
                @click="openDialog_delete(scope.$index, scope.row)"
              >
                Delete
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
  import { Plus, Checked, Document } from '@element-plus/icons-vue'
  import { ElMessage } from 'element-plus';
  import axios from 'axios'

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
        message: 'Filename cannot be empty!',
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
        message: 'Created!',
        type: 'success',
        duration: 3000, 
      })

      // 刷新文件列表
      fetchFileList();
    } catch (error:any) {
      console.error("Error:", error.response || error.message);
      ElMessage({
        message: 'Failed to create file.',
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
        message: 'Invita tionCode cannot be empty!',
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
        message: 'Authorited!',
        type: 'success',
        duration: 3000, 
      })
      router.push({
      path: '/basecode',
      query: { documentId: response.data } 
    })
    } catch (error:any) {
      console.error("Error:", error.response || error.message);
      ElMessage({
        message: 'Failed to join the file.',
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
          message: 'Deleted!',
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
      console.error('Error deleting file:', error);
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
    } catch (error) {
      console.error('Error fetching file list:', error);
    }
  }
  
  onMounted(async () => {
    fetchFileList();
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
  