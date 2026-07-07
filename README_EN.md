# XCode

[中文](README.md) | [English](README_EN.md)

XCode is an Android C/C++ IDE focused on local development on mobile devices. It combines a code editor, project file tree, CMake/Ninja build workflow, clangd language features, an integrated terminal, a code-focused Agent assistant, and a PRoot-based Linux toolchain runtime so C/C++ projects can be edited, configured, built, and analyzed directly on Android.

## Features

- C/C++ project creation with executable, static library, and shared library templates
- CMake and Ninja project configure/build workflow
- Only CMake `4.3.0` is offered by the built-in installer; new project templates generate `cmake_minimum_required(VERSION 4.3.0)`
- PRoot-based Linux toolchain runtime integration with separated low-level runtime, app command execution, and toolchain installation management
- clangd language server support for completion, signature help, symbol highlight, diagnostics, hover, and formatting
- Tree-sitter based syntax highlighting with semantic highlighting overlay support; stale ranges are skipped during formatting/highlighting updates to avoid old-tree out-of-bounds reads
- Sora-based code editor with tabs, file tree, search, editor settings, and shortcut symbol bar
- File tree and editor tabs are synchronized after workspace file changes produced by Agent/tools
- Formatting results are applied with differential replacement to reduce full-document delete/insert jank on large files
- Unified capsule-style editor top bar, sidebar toolbar, file search box, and output panel
- Output panel for configure/build/toolchain logs with a custom build status animation
- Integrated terminal with the same Linux toolchain environment used by XCode
- Code Agent embedded in the editor sidebar
- Agent / terminal / workspace tools mount the XCode project directory: `/storage/emulated/0/XCodeProjects -> /workspace/XCodeProjects`
- Workspace file manager maps `XCodeProjects` to the real `/storage/emulated/0/XCodeProjects` directory
- Agent supports transaction-level workspace edits through the IDE layer, including multi-file create/modify, dirty-buffer protection, and tab/file-tree synchronization
- Agent can still read workspace files, write files, and execute shell commands
- Project configuration for ABI, C++ standard, build type, CMake arguments, and parallel jobs
- Default `.clang-format` generation for new projects
- Release build strip rules generated in template `CMakeLists.txt`

## Project Structure

```text
XCode/
├── app/                         Android application shell and XCode main UI
├── modules/chat-agent/          Editor Agent integration module, trimmed from RikkaHub
│   ├── app/                     Agent UI, workspace, services, and tools
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

> Note: release vital lint is disabled for `modules/chat-agent:app`. Current AGP/Kotlin FIR lint analysis may crash on some Compose/Kotlin files; this is a lint analyzer failure, not a business-code lint violation.

## C/C++ Project Workflow

XCode creates CMake-based projects and writes a default `CMakeLists.txt` for the selected template.

New projects include:

- `cmake_minimum_required(VERSION 4.3.0)`
- Android ABI validation
- output directories under the CMake build directory
- target include directory setup
- Release-only post-build strip rule using `${CMAKE_STRIP}`

Saving the root `CMakeLists.txt` can trigger a CMake configure refresh so clangd can use the updated `compile_commands.json`.

## clangd

clangd is started through the toolchain runtime and is used for C/C++ intelligent editing features. The editor settings expose user-facing feature switches such as completion, signature help, symbol highlight, formatting, and hover. Diagnostics are handled as part of the language server flow.

## Agent Integration

XCode's Agent is a trimmed and editor-focused integration based on RikkaHub. The source code lives in:

```text
modules/chat-agent/
```

`modules/chat-agent/` contains modified code derived from RikkaHub. See [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) and [modules/chat-agent/LICENSE](modules/chat-agent/LICENSE) for RikkaHub source, license, and modification notices.

The Agent is now positioned as an embedded coding assistant, not a general-purpose chat entry. Currently retained capabilities include:

- Agent access from the editor sidebar
- Project-root scoped sidebar input draft save/restore
- Transaction-level workspace edit tool: `apply_workspace_edits`
- Multi-file create/modify, preview/apply, path validation, conflict detection, and dirty-buffer protection
- Editor tab and project file tree synchronization after edits
- Workspace shell command execution
- Workspace file read, write, and precise-edit compatibility tools
- Uploaded files exposed to the Agent through `/upload`
- Skills exposed to the Agent through `/skills`
- XCode project directory mounted at `/workspace/XCodeProjects`
- Agent follows XCode theme and language policy: Chinese by default, English as fallback
- Uses XCode's shared Ubuntu rootfs; standalone rootfs download/extract installation has been removed

Removed or disabled RikkaHub general-purpose capabilities:

- Main-screen standalone Agent entry and standalone Agent Activity
- Image generation page, database entity, and repository
- AI translator page, message translation action, and translation prompt
- ASR/TTS voice input, auto playback, and local TTS tool
- MCP manager, OAuth, picker, settings, and tool chain
- External Web UI / Web Server, update checking, and independent theme switching
- Legacy Termux PTY native implementation under `chat-agent/workspace`

### Agent Database

The Agent local Room database has been reset to `version = 1` using the current trimmed schema. It no longer inherits RikkaHub's historical `1 -> 24` schema/migration chain. When upgrading from an older installation with an incompatible local database, destructive migration recreates the database.

### XCodeProjects Mapping

XCode's default project directory is:

```text
/storage/emulated/0/XCodeProjects
```

Inside the Agent and terminal it is available as:

```text
/workspace/XCodeProjects
```

Inside the workspace file manager it is shown as:

```text
XCodeProjects
```

All three paths point to the same real project files.

## Notes

- Existing projects keep their own `CMakeLists.txt` and `.clang-format`; template changes only affect newly created projects.
- Toolchain commands run through the app's PRoot runtime on device.
- Agent and terminal use XCode's shared Ubuntu rootfs: `filesDir/home/xcode/ubuntu-base`.
- The Agent keeps only editor-sidebar, workspace, and coding-assistance related capabilities.
- Build cancellation stops the Android process and uses fallback cleanup for active CMake/Ninja processes.

## Contact

- Email: xiaochenzaine@qq.com

## License

This project uses a segmented dual licensing model: GNU AGPL v3 for eligible open-source/non-commercial/personal/educational/research use, and a commercial license for other use cases. See [LICENSE](LICENSE) for details.

This project integrates and modifies RikkaHub as the Agent module. See [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) and [modules/chat-agent/LICENSE](modules/chat-agent/LICENSE) for RikkaHub source, license, and modification notices.
