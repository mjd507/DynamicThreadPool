<template>
  <div class="wrapper">
    <div class="header">
      <div class="description">当前应用</div>
      <el-select
        v-model="currAppId"
        @change="onAppChange(currAppId)"
        placeholder="请选择应用"
        size="medium"
      >
        <el-option v-for="item in apps" :key="item.appId" :label="item.appName" :value="item.appId"></el-option>
      </el-select>
      <div class="permission">{{permission}}</div>
    </div>
    <div class="content">
      <div style="display:flex; justify-content: flex-end; padding: 20px 0 5px">
        <el-button @click="addConfig" v-if="permission.indexOf('写') > -1" type="primary">申请线程池</el-button>
      </div>
      <el-table
        v-loading="listLoading"
        :data="tableList"
        border
        fit
        highlight-current-row
        style="width: 100%"
      >
        <el-table-column align="center" label="配置key" width="180">
          <template slot-scope="{row}">
            <span>{{ row.key }}</span>
          </template>
        </el-table-column>

        <el-table-column min-width="300px" label="配置value">
          <template slot-scope="{row}">
            <template v-if="row.edit">
              <el-input v-model="row.value" class="edit-input" size="small" />
              <el-button
                style="top: auto;"
                class="cancel-btn"
                size="small"
                icon="el-icon-refresh"
                type="warning"
                @click="cancelEdit(row)"
              >取消</el-button>
            </template>
            <span v-else>{{ row.value }}</span>
          </template>
        </el-table-column>

        <el-table-column v-if="permission.indexOf('写') > -1" align="center" label="操作" width="180">
          <template slot-scope="{row}">
            <el-button
              v-if="row.edit"
              type="success"
              size="small"
              icon="el-icon-circle-check-outline"
              @click="confirmEdit(row)"
            >确定</el-button>
            <el-button
              v-else
              type="primary"
              size="small"
              icon="el-icon-edit"
              @click="row.edit=(!row.edit)"
            >编辑</el-button>
            <el-button type="danger" size="small" @click="deleteRow(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-dialog title="新增配置" center :visible.sync="showAddCfgPanel">
      <el-form
        :model="ruleForm"
        status-icon
        :rules="rules"
        ref="ruleForm"
        label-width="100px"
        class="demo-ruleForm"
      >
        <el-form-item label="key" prop="key">
          <el-input type="textarea" v-model="ruleForm.key"></el-input>
        </el-form-item>
        <el-form-item label="val" prop="val">
          <el-input type="textarea" :autosize="{ minRows: 6}" v-model="ruleForm.val"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="cancelForm('ruleForm')">取 消</el-button>
        <el-button type="primary" @click="submitForm('ruleForm')">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getApps } from "@/api/user";
import { listConfig, writeConfig, deleteConfig } from "@/api/config";
export default {
  name: "myApp",
  data() {
    return {
      apps: [],
      currAppId: -1,
      permission: "",
      tableList: [],
      listLoading: true,
      showAddCfgPanel: false,
      ruleForm: {
        key: "",
        val: ""
      },
      rules: {
        key: [{ required: true, message: "请输入配置key", trigger: "blur" }],
        val: [{ required: true, message: "请输入配置value", trigger: "blur" }]
      }
    };
  },
  created() {
    getApps().then(res => {
      this.apps = res.data;
      if (this.apps && this.apps.length) {
        this.currAppId = this.apps[0].appId;
        this.permission = this.apps[0].permission;
      }
      this._getCfgList();
    });
  },
  methods: {
    async _getCfgList() {
      if (this.currAppId === -1) return;
      this.listLoading = true;
      const { data } = await listConfig({ appId: this.currAppId });
      const cfgs = [];
      for (let key in data) {
        cfgs.push({
          key,
          value: data[key],
          originValue: data[key],
          edit: false
        });
        // originValue will be used when user click the cancel
      }
      this.tableList = cfgs;
      this.listLoading = false;
    },
    onAppChange(appId) {
      this.currAppId = appId;
      this.permission = this.apps.filter(
        el => el.appId === appId
      )[0].permission;
      this._getCfgList();
    },
    cancelEdit(row) {
      row.value = row.originValue;
      row.edit = false;
      this.$message({
        message: "已取消",
        type: "warning"
      });
    },
    _writeCfg(cfg) {
      return writeConfig(cfg);
    },
    confirmEdit(row) {
      row.edit = false;
      const cfg = {
        appId: this.currAppId,
        key: row.key,
        val: row.value,
        isAdd: false
      };
      this._writeCfg(cfg).then(res => {
        const { data } = res;
        if (data === true) {
          row.originValue = row.value;
          this.$message({
            message: "修改成功",
            type: "success"
          });
        }
      });
    },
    deleteRow(row) {
      const req = {
        appId: this.currAppId,
        key: row.key
      };
      deleteConfig(req).then(res => {
        const { data } = res;
        if (data === true) {
          this.$message({
            message: "删除成功",
            type: "success"
          });
          this._getCfgList();
        }
      });
    },
    addConfig() {
      this.showAddCfgPanel = true;
    },
    cancelForm(formName) {
      this.$refs[formName].resetFields();
      this.showAddCfgPanel = false;
    },
    submitForm(formName) {
      this.$refs[formName].validate(valid => {
        if (valid) {
          const req = {
            appId: this.currAppId,
            key: this.ruleForm.key,
            val: this.ruleForm.val,
            isAdd: true
          };
          this._writeCfg(req).then(res => {
            const { data } = res;
            if (data === true) {
              this.$message({
                message: "添加成功",
                type: "success"
              });
              this.$refs[formName].resetFields();
              this._getCfgList();
              this.showAddCfgPanel = false;
            }
          });
        } else {
          return false;
        }
      });
    }
  }
};
</script>
<style lang="scss" scoped>
.wrapper {
  padding: 20px;
  .header {
    display: flex;
    align-items: center;
    height: 47px;
    .description {
      color: rgba(0, 0, 0, 0.8);
      padding-right: 20px;
    }
    .permission {
      color: rgba(0, 0, 0, 0.5);
      text-align: left;
      padding-left: 20px;
    }
  }
  .edit-input {
    padding-right: 100px;
  }
  .cancel-btn {
    position: absolute;
    right: 15px;
    top: 10px;
  }
}
</style>
