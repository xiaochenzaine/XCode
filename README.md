# XCode

[中文](#xcode) | [English](#english)

XCode 是一款运行在 Android 上的 C/C++ IDE，目标是在移动设备上完成本地 C/C++ 开发。它集成了代码编辑器、项目文件树、CMake/Ninja 构建流程、clangd 智能代码能力、终端、Agent 助手，以及基于 PRoot 的 Linux 工具链运行环境，可以直接在 Android 设备上编辑、配置、构建和分析 C/C++ 项目。

## 功能特性

- 创建 C/C++ 项目，支持可执行程序、静态库、动态库模板
- CMake 和 Ninja 项目配置、构建流程
- 基于 PRoot 的 Linux 工具链运行环境
- clangd 语言服务，支持补全、参数提示、符号高亮、诊断、悬浮提示和格式化
- 基于 Sora 的代码编辑器，支持标签页、文件树、搜索、编辑器设置和快捷符号栏
- 输出面板显示配置、构建和工具链日志
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

Agent 目前支持：

- 完整 RikkaHub 聊天页面
- 编辑器侧边栏直接打开助手
- 工作区 shell 命令执行
- 工作区文件读取、写入、精确编辑
- 上传文件通过 `/upload` 暴露给 Agent 读取
- 技能目录通过 `/skills` 暴露给 Agent
- XCode 项目目录固定挂载到 `/workspace/XCodeProjects`

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
- 停止构建时会停止 Android 进程，并对活动的 CMake/Ninja 进程做兜底清理。
- Android 构建时不会自动构建 RikkaHub Web UI；Web UI 源码保留在 `modules/chat-agent/web-ui`。

## 联系方式

- 邮箱：xiaochenzaine@qq.com

## 许可证

本项目采用用户分段双重许可模式：符合条件的开源、非商业、个人、教育、研究用途可基于 GNU AGPL v3 使用；其他使用场景需要取得商业授权。详见 [LICENSE](LICENSE)。

---

# English

XCode is an Android C/C++ IDE focused on local development on mobile devices. It combines a code editor, project file tree, CMake/Ninja build workflow, clangd language features, an integrated terminal, an Agent assistant, and a PRoot-based Linux toolchain runtime so C/C++ projects can be edited, configured, built, and analyzed directly on Android.

## Features

- C/C++ project creation with executable, static library, and shared library templates
- CMake and Ninja project configure/build workflow
- PRoot-based Linux toolchain runtime integration
- clangd language server support for completion, signature help, symbol highlight, diagnostics, hover, and formatting
- Sora-based code editor with tabs, file tree, search, editor settings, and shortcut symbol bar
- Output panel for configure/build/toolchain logs
- Integrated terminal with the same Linux toolchain environment used by XCode
- RikkaHub integrated as the Agent assistant module
- Full Agent page embedded in the editor sidebar
- Agent / terminal / workspace tools mount the XCode project directory: `/storage/emulated/0/XCodeProjects -> /workspace/XCodeProjects`
- RikkaHub workspace file manager maps `XCodeProjects` to the real `/storage/emulated/0/XCodeProjects` directory
- Agent workspace tools can read, edit, write files, and execute shell commands
- Project configuration for ABI, C++ standard, build type, CMake arguments, and parallel jobs
- Default `.clang-format` generation for new projects
- Release build strip rules generated in template `CMakeLists.txt`

## Project Structure

```text
XCode/
├── app/                         Android application shell and XCode main UI
├── modules/chat-agent/          RikkaHub Agent integration module
│   ├── app/                     RikkaHub Android UI, workspace, services, and tools
│   ├── ai/                      AI providers, messages, tool calling, and core logic
│   ├── workspace/               Workspace file system and PRoot shell runner
│   ├── terminal-emulator/       Terminal emulator
│   ├── terminal-view/           Terminal view
│   └── ...
├── modules/editor-core/         Editor models and shared editor state
├── modules/project-file-tree/   Project tree UI/components
├── modules/toolchain-runtime/   PRoot command/runtime integration
├── modules/clangd-lsp/          clangd language server bridge
├── modules/sora-editor/         Sora editor module
├── modules/sora-editor-lsp/     Sora LSP integration
├── modules/sora-language-textmate/
├── modules/sora-oniguruma-native/
└── modules/terminal-*/          XCode native terminal modules
```

## Requirements

- Android Studio or compatible Gradle environment
- JDK 17
- Android SDK with compile SDK 37
- Android NDK `29.0.14206865`
- Android device or emulator running Android 8.0+ (`minSdk 26`)

The app currently targets `arm64-v8a` native packaging.

## Build

Clone the repository and build with Gradle:

```bash
./gradlew assembleDebug
```

For Android Studio, open the repository root and run the `app` configuration.

## C/C++ Project Workflow

XCode creates CMake-based projects and writes a default `CMakeLists.txt` for the selected template.

New projects include:

- Android ABI validation
- output directories under the CMake build directory
- target include directory setup
- Release-only post-build strip rule using `${CMAKE_STRIP}`

Saving the root `CMakeLists.txt` can trigger a CMake configure refresh so clangd can use the updated `compile_commands.json`.

## clangd

clangd is started through the toolchain runtime and is used for C/C++ intelligent editing features. The editor settings expose user-facing feature switches such as completion, signature help, symbol highlight, formatting, and hover. Diagnostics are handled as part of the language server flow.

## Agent / RikkaHub Integration

XCode integrates RikkaHub as the Agent system. The source code lives in:

```text
modules/chat-agent/
```

The Agent currently supports:

- Full RikkaHub chat page
- Agent access from the editor sidebar
- Workspace shell command execution
- Workspace file read, write, and precise edit tools
- Uploaded files exposed to the Agent through `/upload`
- Skills exposed to the Agent through `/skills`
- XCode project directory mounted at `/workspace/XCodeProjects`

### XCodeProjects Mapping

XCode's default project directory is:

```text
/storage/emulated/0/XCodeProjects
```

Inside the Agent and terminal it is available as:

```text
/workspace/XCodeProjects
```

Inside the RikkaHub workspace file manager it is shown as:

```text
XCodeProjects
```

All three paths point to the same real project files.

## Notes

- Existing projects keep their own `CMakeLists.txt` and `.clang-format`; template changes only affect newly created projects.
- Toolchain commands run through the app's PRoot runtime on device.
- Build cancellation stops the Android process and uses fallback cleanup for active CMake/Ninja processes.
- RikkaHub Web UI is not built automatically during Android builds; its source remains under `modules/chat-agent/web-ui`.

## Contact

- Email: xiaochenzaine@qq.com

## License

This project uses a segmented dual licensing model: GNU AGPL v3 for eligible open-source/non-commercial/personal/educational/research use, and a commercial license for other use cases. See [LICENSE](LICENSE) for details.
