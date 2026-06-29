# XCode

XCode is an Android C/C++ IDE focused on local development on mobile devices. It combines a code editor, project file tree, CMake/Ninja build workflow, clangd language features, and a proot-based toolchain runtime so C/C++ projects can be edited, configured, and built directly on Android.

## Features

- C/C++ project creation with executable, static library, and shared library templates
- CMake and Ninja project configure/build workflow
- proot-based Linux toolchain runtime integration
- clangd language server support for completion, signature help, symbol highlight, diagnostics, hover, and formatting
- Sora-based code editor with tabs, file tree, search, editor settings, and shortcut symbol bar
- Output panel for configure/build/toolchain logs
- Project configuration for ABI, C++ standard, build type, CMake arguments, and parallel jobs
- Default `.clang-format` generation for new projects
- Release build strip rules generated in template `CMakeLists.txt`

## Project Structure

```text
XCode/
├── app/                         Android application
├── modules/editor-core/         Editor models and shared editor state
├── modules/project-file-tree/   Project tree UI/components
├── modules/toolchain-runtime/   proot command/runtime integration
├── modules/clangd-lsp/          clangd language server bridge
├── modules/sora-editor/         Sora editor module
├── modules/sora-editor-lsp/     Sora LSP integration
├── modules/sora-language-textmate/
├── modules/sora-oniguruma-native/
├── modules/terminal-view/
└── modules/terminal-emulator/
```

## Requirements

- Android Studio or compatible Gradle environment
- JDK 17
- Android SDK with compile SDK 36
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

## Notes

- Existing projects keep their own `CMakeLists.txt` and `.clang-format`; template changes only affect newly created projects.
- Toolchain commands run through the app's proot runtime on device.
- Build cancellation stops the Android process and uses fallback cleanup for active CMake/Ninja processes.

## License

No license has been declared yet.
