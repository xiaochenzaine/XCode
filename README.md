# XCode

[中文](README.md) | [English](README_EN.md)

XCode 是一款运行在 Android 上的 C/C++ IDE，目标是在移动设备上完成本地 C/C++ 开发。它集成了代码编辑器、项目文件树、CMake/Ninja 构建流程、clangd 智能代码能力、终端、面向代码编辑场景的 Agent 助手，以及基于 PRoot 的 Linux 工具链运行环境，可以直接在 Android 设备上编辑、配置、构建和分析 C/C++ 项目。

## 功能特性

- 创建 C/C++ 项目，支持可执行程序、静态库、动态库模板
- CMake 和 Ninja 项目配置、构建流程
- CMake 安装入口仅保留 `4.3.0`，新建项目模板默认生成 `cmake_minimum_required(VERSION 4.3.0)`
- 基于 PRoot 的 Linux 工具链运行环境，底层运行时、应用命令执行和工具链安装管理分层维护
- clangd 语言服务，支持补全、参数提示、符号高亮、诊断、悬浮提示和格式化
- 基于 Tree-sitter 的语法高亮，并支持语义高亮叠加；格式化和高亮切换时会跳过过期范围，避免旧语法树越界
- 基于 Sora 的代码编辑器，支持标签页、文件树、搜索、编辑器设置和快捷符号栏
- 文件树与编辑器 Tab 会跟随 Agent/工具产生的工作区文件变更同步刷新
- 格式化结果采用差量替换，减少大文件格式化时的全文删除/插入卡顿
- 统一胶囊风格的编辑器顶部栏、侧边栏工具栏、文件搜索框和输出面板
- 输出面板显示配置、构建和工具链日志，并提供自定义构建状态动画
- 集成终端，提供与 XCode 工具链一致的 Linux shell 环境
- 编辑器侧边栏内置代码 Agent，可在编辑项目时直接使用助手
- Agent / 终端 / workspace 工具固定挂载 XCode 项目目录：`/storage/emulated/0/XCodeProjects -> /workspace/XCodeProjects`
- 工作区文件管理器会将 `XCodeProjects` 映射到真实的 `/storage/emulated/0/XCodeProjects`
- Agent 支持事务级 workspace 编辑，可通过 IDE 层创建/修改多文件，保护 dirty buffer，并同步 Tab 与文件树
- Agent 仍可读取工作区文件、写入文件和执行 shell 命令
- 项目配置支持 ABI、C++ 标准、构建类型、CMake 参数和并行任务数
- 新建项目自动生成默认 `.clang-format`
- 新建项目模板会在 `CMakeLists.txt` 中生成 Release 构建后的 strip 规则

## 项目结构

```text
XCode/
├── app/                         Android 应用壳与 XCode 主界面
├── modules/chat-agent/          编辑器 Agent 集成模块，基于 RikkaHub 精简改造
│   ├── app/                     Agent UI、工作区、服务与工具
│   ├── ai/                      AI provider、消息、工具调用等核心能力
│   ├── workspace/               工作区文件系统、PRoot shell runner
│   ├── terminal-emulator/       终端模拟器
│   ├── terminal-view/           终端视图
│   └── ...
├── modules/editor-core/         编辑器模型和共享状态
├── modules/project-file-tree/   项目文件树 UI/组件
├── modules/toolchain-runtime/   PRoot 命令和工具链运行环境集成
├── modules/clangd-lsp/          clangd 语言服务桥接
├── modules/sora-editor/         Sora 编辑器模块
├── modules/sora-editor-lsp/     Sora LSP 集成
└── modules/terminal-*/          XCode 原生终端相关模块
```

## 环境要求

- Android Studio 或兼容的 Gradle 构建环境
- JDK 17
- Android SDK，compile SDK 37
- Android NDK `29.0.14206865`
- Android 8.0+ 设备或模拟器（`minSdk 26`）

当前应用 native 打包目标为 `arm64-v8a`。

## 构建

克隆仓库后使用 Gradle 构建：

```bash
./gradlew assembleDebug
```

也可以使用 Android Studio 打开仓库根目录，然后运行 `app` 配置。

> 说明：`modules/chat-agent:app` 的 release vital lint 已关闭。当前 AGP/Kotlin FIR 在分析部分 Compose/Kotlin 文件时可能触发 lint 自身崩溃，这不是业务代码的 lint violation。

## C/C++ 项目流程

XCode 会创建基于 CMake 的项目，并根据选择的模板写入默认 `CMakeLists.txt`。

新建项目默认包含：

- `cmake_minimum_required(VERSION 4.3.0)`
- Android ABI 校验
- CMake 构建目录下的输出目录配置
- target include 目录配置
- 使用 `${CMAKE_STRIP}` 的 Release-only 构建后 strip 规则

保存项目根目录下的 `CMakeLists.txt` 后，可以触发 CMake configure 刷新，让 clangd 使用更新后的 `compile_commands.json`。

## clangd

clangd 通过工具链运行环境启动，用于提供 C/C++ 智能编辑能力。编辑器设置中提供补全、参数提示、符号高亮、格式化和悬浮提示等功能开关。诊断由语言服务流程统一处理。

## Agent 集成

XCode 的 Agent 基于 RikkaHub 精简改造，源码位于：

```text
modules/chat-agent/
```

`modules/chat-agent/` 包含源自 RikkaHub 的修改代码。RikkaHub 来源、许可证和修改说明见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) 与 [modules/chat-agent/LICENSE](modules/chat-agent/LICENSE)。

Agent 当前定位为编辑器内嵌代码助手，不再作为通用聊天入口。当前保留能力包括：

- 编辑器侧边栏直接打开助手
- 基于项目根目录隔离的侧边栏输入草稿保存与恢复
- 事务级 workspace 编辑工具 `apply_workspace_edits`
- 多文件创建/修改、preview/apply、路径检查、冲突检测和 dirty buffer 保护
- 修改后同步编辑器 Tab 与项目文件树
- 工作区 shell 命令执行
- 工作区文件读取、写入、精确编辑兼容工具
- 上传文件通过 `/upload` 暴露给 Agent 读取
- 技能目录通过 `/skills` 暴露给 Agent
- XCode 项目目录固定挂载到 `/workspace/XCodeProjects`
- Agent 跟随 XCode 的主题和语言策略：默认中文，英文备用
- 使用 XCode 共享 Ubuntu rootfs，不再提供独立 rootfs 下载/解压安装

已移除或禁用的 RikkaHub 通用能力：

- 主界面独立 Agent 入口和独立 Agent Activity
- 图像生成页面、数据库实体和仓库
- AI 翻译页面、消息翻译按钮和翻译提示词
- ASR/TTS 语音输入、自动朗读和本地 TTS 工具
- MCP 管理、OAuth、Picker、设置项和工具链
- 外部 Web UI / Web Server、更新检测和独立主题切换
- `chat-agent/workspace` 中旧 Termux PTY native 实现

### Agent 数据库

Agent 本地 Room 数据库已经从精简后的结构重新作为 `version = 1` 开始，不再继承 RikkaHub 旧的 `1 -> 24` schema/迁移链。旧安装包升级到当前版本时，如果本地数据库不兼容，会通过 destructive migration 重建数据库。

### XCodeProjects 映射

XCode 默认项目目录为：

```text
/storage/emulated/0/XCodeProjects
```

Agent 和终端中的路径为：

```text
/workspace/XCodeProjects
```

工作区文件管理器中显示为：

```text
XCodeProjects
```

三者指向同一份真实项目文件。

## 说明

- 已有项目会保留自己的 `CMakeLists.txt` 和 `.clang-format`；模板变更只影响新建项目。
- 工具链命令会通过应用内的 PRoot 运行环境在设备上执行。
- Agent 与终端使用 XCode 共享 Ubuntu rootfs：`filesDir/home/xcode/ubuntu-base`。
- Agent 只保留编辑器侧边栏、workspace 和代码辅助相关能力。
- 停止构建时会停止 Android 进程，并对活动的 CMake/Ninja 进程做兜底清理。

## 联系方式

- 邮箱：xiaochenzaine@qq.com

## 许可证

本项目采用用户分段双重许可模式：符合条件的开源、非商业、个人、教育、研究用途可基于 GNU AGPL v3 使用；其他使用场景需要取得商业授权。详见 [LICENSE](LICENSE)。

本项目集成并修改了 RikkaHub 作为 Agent 模块；RikkaHub 的来源、许可证和修改说明见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) 与 [modules/chat-agent/LICENSE](modules/chat-agent/LICENSE)。
