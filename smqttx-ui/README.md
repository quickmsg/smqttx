# SMQTTX UI

SMQTTX的Web管理界面，基于Vue 3 + Element Plus构建。

## 功能特性

- 🎨 现代化的用户界面设计
- 📊 实时数据监控和图表展示
- 👥 客户端连接管理
- 📝 主题和消息管理
- ⚙️ 规则引擎配置
- 📈 系统性能监控
- 🔧 系统设置管理

## 技术栈

- **前端框架**: Vue 3
- **UI组件库**: Element Plus
- **状态管理**: Pinia
- **路由管理**: Vue Router
- **图表库**: ECharts
- **构建工具**: Vite
- **包管理**: npm

## 开发环境要求

- Node.js >= 18.0.0
- npm >= 9.0.0

## 快速开始

### 1. 安装依赖

```bash
cd smqttx-ui/src/main/frontend
npm install
```

### 2. 开发模式

```bash
npm run dev
```

开发服务器将在 `http://localhost:3000` 启动。

### 3. 构建生产版本

```bash
npm run build
```

构建结果将输出到 `dist` 目录。

### 4. Maven构建

在项目根目录执行：

```bash
mvn clean package
```

这将自动执行前端构建并将资源打包到JAR文件中。

## 项目结构

```
smqttx-ui/
├── src/main/frontend/          # 前端源码
│   ├── src/
│   │   ├── api/               # API接口
│   │   ├── components/        # 公共组件
│   │   ├── router/           # 路由配置
│   │   ├── stores/           # 状态管理
│   │   ├── utils/            # 工具函数
│   │   ├── views/            # 页面组件
│   │   ├── App.vue           # 根组件
│   │   └── main.js           # 入口文件
│   ├── index.html            # HTML模板
│   ├── package.json          # 依赖配置
│   └── vite.config.js        # Vite配置
└── pom.xml                   # Maven配置
```

## 页面说明

### 仪表盘 (Dashboard)
- 系统概览统计
- 实时性能监控图表
- 最近连接记录

### 客户端管理 (Clients)
- 在线客户端列表
- 客户端连接详情
- 连接状态管理

### 主题管理 (Topics)
- 活跃主题列表
- 订阅者统计
- 主题消息统计

### 消息管理 (Messages)
- 消息历史记录
- 消息内容查看
- 消息过滤和搜索

### 规则引擎 (Rules)
- 规则配置管理
- 规则执行状态
- 规则创建和编辑

### 监控 (Monitoring)
- 系统资源监控
- 网络流量监控
- 实时日志查看

### 系统设置 (Settings)
- 服务端口配置
- 连接参数设置
- 日志级别配置

## 集成说明

### 自动构建集成

项目使用 `frontend-maven-plugin` 实现前后端一体化构建：

1. Maven构建时自动安装Node.js和npm
2. 自动安装前端依赖
3. 自动构建前端资源
4. 将构建结果打包到JAR文件中

### 静态资源服务

前端资源通过Spring Boot的静态资源服务提供，访问路径：

- 开发环境: `http://localhost:3000`
- 生产环境: `http://localhost:60000`

### API接口

前端通过RESTful API与后端通信，主要接口包括：

- `/api/auth/*` - 认证相关
- `/api/clients/*` - 客户端管理
- `/api/topics/*` - 主题管理
- `/api/messages/*` - 消息管理
- `/api/rules/*` - 规则引擎
- `/api/monitoring/*` - 监控数据
- `/api/settings/*` - 系统设置

## 开发指南

### 添加新页面

1. 在 `src/views/` 目录下创建新的Vue组件
2. 在 `src/router/index.js` 中添加路由配置
3. 在 `src/views/Layout.vue` 中添加菜单项

### 添加新API

1. 在 `src/api/` 目录下创建API模块
2. 在对应的页面组件中调用API
3. 使用 `src/utils/request.js` 进行HTTP请求

### 样式规范

- 使用Element Plus组件库
- 遵循BEM命名规范
- 响应式设计，支持移动端

## 部署说明

### 开发环境

1. 启动后端服务
2. 运行 `npm run dev` 启动前端开发服务器
3. 访问 `http://localhost:3000`

### 生产环境

1. 执行 `mvn clean package` 构建完整项目
2. 运行生成的JAR文件
3. 访问 `http://localhost:60000`

## 常见问题

### Q: 前端构建失败怎么办？

A: 检查Node.js版本是否符合要求，清理node_modules后重新安装依赖。

### Q: 如何修改API接口地址？

A: 在 `src/utils/request.js` 中修改 `baseURL` 配置。

### Q: 如何添加新的图表？

A: 使用ECharts库，参考 `src/views/Dashboard.vue` 中的示例。

## 贡献指南

1. Fork项目
2. 创建功能分支
3. 提交代码
4. 创建Pull Request

## 许可证

本项目采用MIT许可证。