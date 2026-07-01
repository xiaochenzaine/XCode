# XCode

XCode 是一款运行在 Android 上的 C/C++ IDE，目标是在移动设备上完成本地 C/C++ 开发。它集成了代码编辑器、项目文件树、CMake/Ninja 构建流程、clangd 智能代码能力、终端、Agent 模块，以及基于 proot 的工具链运行环境，可以直接在 Android 设备上编辑、配置和构建 C/C++ 项目。

## 功能特性

- 创建 C/C++ 项目，支持可执行程序、静态库、动态库模板
- CMake 和 Ninja 项目配置、构建流程
- 基于 proot 的 Linux 工具链运行环境
- clangd 语言服务，支持补全、参数提示、符号高亮、诊断、悬浮提示和格式化
- 基于 Sora 的代码编辑器，支持标签页、文件树、搜索、编辑器设置和快捷符号栏
- 输出面板显示配置、构建和工具链日志
- 集成终端
- Agent 模块，用于后续接入面向工作区的代码助手能力
- 项目配置支持 ABI、C++ 标准、构建类型、CMake 参数和并行任务数
- 新建项目自动生成默认 `.clang-format`
- 新建项目模板会在 `CMakeLists.txt` 中生成 Release 构建后的 strip 规则

## 项目结构

```text
XCode/
├── app/                         Android 应用壳
├── modules/agent/               Agent UI 与后续 Agent 功能
├── modules/editor-core/         编辑器模型和共享状态
├── modules/project-file-tree/   项目文件树 UI/组件
├── modules/toolchain-runtime/   proot 命令和运行环境集成
├── modules/clangd-lsp/          clangd 语言服务桥接
├── modules/sora-editor/         Sora 编辑器模块
├── modules/sora-editor-lsp/     Sora LSP 集成
├── modules/sora-language-textmate/
├── modules/sora-oniguruma-native/
├── modules/terminal-view/
└── modules/terminal-emulator/
```

## 环境要求

- Android Studio 或兼容的 Gradle 构建环境
- JDK 17
- Android SDK，compile SDK 36
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

## Agent

Agent 模块位于 `modules/agent`。它作为独立 Gradle 模块维护，后续可以继续加入 provider、tool、session、工作区上下文等能力，避免直接耦合到 app 壳层。

## 说明

- 已有项目会保留自己的 `CMakeLists.txt` 和 `.clang-format`；模板变更只影响新建项目。
- 工具链命令会通过应用内的 proot 运行环境在设备上执行。
- 停止构建时会停止 Android 进程，并对活动的 CMake/Ninja 进程做兜底清理。

## 联系方式

- 邮箱：xiaochenzaine@qq.com

## 许可证

本项目采用用户分段双重许可模式：符合条件的开源、非商业、个人、教育、研究用途可基于 GNU AGPL v3 使用；其他使用场景需要取得商业授权。详见 [LICENSE](LICENSE)。

英文文档请见 [README.md](README.md)。
