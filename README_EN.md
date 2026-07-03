# XCode

[中文](README.md) | [English](README_EN.md)

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

`modules/chat-agent/` contains modified code derived from RikkaHub. See [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) and [modules/chat-agent/LICENSE](modules/chat-agent/LICENSE) for RikkaHub source, license, and modification notices.

The Agent currently supports:

- Full RikkaHub chat page
- Agent access from the editor sidebar
- Workspace shell command execution
- Workspace file read, write, and precise edit tools
- Uploaded files exposed to the Agent through `/upload`
- Skills exposed to the Agent through `/skills`
- XCode project directory mounted at `/workspace/XCodeProjects`
- Agent follows XCode theme and language policy: Chinese by default, English as fallback
- Uses XCode's shared Ubuntu rootfs; RikkaHub's standalone rootfs download/extract installer has been removed
- RikkaHub external Web UI / Web Server, update checking, and independent theme switching have been removed

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
- Agent and terminal use XCode's shared Ubuntu rootfs: `filesDir/home/xcode/ubuntu-base`.
- The RikkaHub Agent keeps the embedded chat, workspace, and tool capabilities; the external Web access service has been removed.
- Build cancellation stops the Android process and uses fallback cleanup for active CMake/Ninja processes.

## Contact

- Email: xiaochenzaine@qq.com

## License

This project uses a segmented dual licensing model: GNU AGPL v3 for eligible open-source/non-commercial/personal/educational/research use, and a commercial license for other use cases. See [LICENSE](LICENSE) for details.

This project integrates and modifies RikkaHub as the Agent module. See [THIRD_PARTY_NOTICES.md](THIRD_PARTY_NOTICES.md) and [modules/chat-agent/LICENSE](modules/chat-agent/LICENSE) for RikkaHub source, license, and modification notices.
