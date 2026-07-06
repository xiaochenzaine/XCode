# XCode

[中文](README.md) | [English](README_EN.md)

XCode 是一款运行在 Android 上的 C/C++ IDE，目标是在移动设备上完成本地 C/C++ 开发。它集成了代码编辑器、项目文件树、CMake/Ninja 构建流程、clangd 智能代码能力、终端、Agent 助手，以及基于 PRoot 的 Linux 工具链运行环境，可以直接在 Android 设备上编辑、配置、构建和分析 C/C++ 项目。

## 功能特性

- 创建 C/C++ 项目，支持可执行程序、静态库、动态库模板
- CMake 和 Ninja 项目配置、构建流程
- 基于 PRoot 的 Linux 工具链运行环境
- clangd 语言服务，支持补全、参数提示、符号高亮、诊断、悬浮提示和格式化
- 基于 Tree-sitter 的语法高亮，替代旧 TextMate 资源并支持语义高亮叠加
- 基于 Sora 的代码编辑器，支持标签页、文件树、搜索、编辑器设置和快捷符号栏
- 统一胶囊风格的编辑器顶部栏、侧边栏工具栏、文件搜索框和输出面板
- 输出面板显示配置、构建和工具链日志，并提供自定义构建状态动画
- 集成终端，提供与 XCode 工具链一致的 Linux shell 环境
- 集成 RikkaHub 作为 Agent 助手模块
- 编辑器侧边栏内置完整 Agent 页面，可在编辑项目时直接使用助手
- Agent / 终端 / workspace 工具固定挂载 XCode 项目目录：`/storage/emulated/0/XCodeProjects -> /workspace/XCodeProjects`
- RikkaHub 工作区文件管理器会将 `XCodeProjects` 映射到真实的 `/storage/emulated/0/XCodeProjects`
- Agent 支持 workspace 工具，可读取、编辑、写入工作区文件并执行 shell 命令
- 项目配置支持 ABI、C++ 标准、构建类型、CMake 参数和并行任务数
- 新建项目自动生成默认 `.clang-format`
- 新建项目模板会在 `CMakeLists.txt` 中生成 Release 构建后的 strip 规则

## 项目结构

```text
XCode/
├── app/                         Android 应用壳与 XCode 主界面
├── modules/chat-agent/          RikkaHub Agent 集成模块
│   ├── app/                     RikkaHub Android UI、工作区、服务与工具
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
├── modules/sora-language-textmate/
├── modules/sora-oniguruma-native/
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

## C/C++ 项目流程

XCode 会创建基于 CMake 的项目，并根据选择的模板写入默认 `CMakeLists.txt`。

新建项目默认包含：

- Android ABI 校验
- CMake 构建目录下的输出目录配置
- target include 目录配置
- 使用 `${CMAKE_STRIP}` 的 Release-only 构建后 strip 规则

保存项目根目录下的 `CMakeLists.txt` 后，可以触发 CMake configure 刷新，让 clangd 使用更新后的 `compile_commands.json`。

## clangd

clangd 通过工具链运行环境启动，用于提供 C/C++ 智能编辑能力。编辑器设置中提供补全、参数提示、符号高亮、格式化和悬浮提示等功能开关。诊断由语言服务流程统一处理。

## Agent / RikkaHub 集成

XCode 集成 RikkaHub 作为 Agent 能力，源码位于：

```text
modules/chat-agent/
```

`modules/chat-agent/` 包含源自 RikkaHub 的修改代码。RikkaHub 来源、许可证和修改说明见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) 与 [modules/chat-agent/LICENSE](modules/chat-agent/LICENSE)。

Agent 目前支持：

- 完整 RikkaHub 聊天页面
- 编辑器侧边栏直接打开助手
- 工作区 shell 命令执行
- 工作区文件读取、写入、精确编辑
- 上传文件通过 `/upload` 暴露给 Agent 读取
- 技能目录通过 `/skills` 暴露给 Agent
- XCode 项目目录固定挂载到 `/workspace/XCodeProjects`
- Agent 跟随 XCode 的主题和语言策略：默认中文，英文备用
- 使用 XCode 共享 Ubuntu rootfs，不再提供 RikkaHub 独立 rootfs 下载/解压安装
- 已移除 RikkaHub 外部 Web UI / Web Server、更新检测和独立主题切换

### XCodeProjects 映射

XCode 默认项目目录为：

```text
/storage/emulated/0/XCodeProjects
```

Agent 和终端中的路径为：

```text
/workspace/XCodeProjects
```

RikkaHub 工作区文件管理器中显示为：

```text
XCodeProjects
```

三者指向同一份真实项目文件。

## 说明

- 已有项目会保留自己的 `CMakeLists.txt` 和 `.clang-format`；模板变更只影响新建项目。
- 工具链命令会通过应用内的 PRoot 运行环境在设备上执行。
- Agent 与终端使用 XCode 共享 Ubuntu rootfs：`filesDir/home/xcode/ubuntu-base`。
- RikkaHub Agent 只保留内置聊天、workspace/tool 能力；外部 Web 访问服务已移除。
- 停止构建时会停止 Android 进程，并对活动的 CMake/Ninja 进程做兜底清理。

## 联系方式

- 邮箱：xiaochenzaine@qq.com

## 许可证

本项目采用用户分段双重许可模式：符合条件的开源、非商业、个人、教育、研究用途可基于 GNU AGPL v3 使用；其他使用场景需要取得商业授权。详见 [LICENSE](LICENSE)。

本项目集成并修改了 RikkaHub 作为 Agent 模块；RikkaHub 的来源、许可证和修改说明见 [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) 与 [modules/chat-agent/LICENSE](modules/chat-agent/LICENSE)。
