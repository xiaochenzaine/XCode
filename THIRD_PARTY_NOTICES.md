# Third Party Notices

This project includes third-party open-source components and modified upstream code.

## RikkaHub

- Upstream project: RikkaHub
- Source repository: https://github.com/re-ovo/rikkahub
- Integrated location: `modules/chat-agent/`
- License: Segmented Dual Licensing, GNU AGPL v3 for eligible use cases, or Commercial License for other use cases
- License file: `modules/chat-agent/LICENSE`
- Commercial licensing contact from upstream license: `re_dev@qq.com`

### Modification Notice

`modules/chat-agent/` contains code derived from RikkaHub and has been modified for integration into XCode as the Agent module.

Major integration changes include:

- Embedded RikkaHub as an in-app Agent page for XCode.
- Adapted theme, language, and app scale behavior to follow XCode settings.
- Reused XCode's shared Ubuntu rootfs and project workspace mapping.
- Removed or disabled RikkaHub standalone Web UI/Web Server, update checking, independent theme switching, and standalone rootfs download/extraction flow.
- Adjusted resources and UI strings for XCode's default Chinese and English fallback resource strategy.

## Material Color Utilities

- Location: `modules/chat-agent/material3/material-color-utilities/`
- License: Apache License 2.0
- License file: `modules/chat-agent/material3/material-color-utilities/LICENSE`

---

# 第三方声明

本项目包含第三方开源组件以及基于上游项目修改的代码。

## RikkaHub

- 上游项目：RikkaHub
- 源码仓库：https://github.com/re-ovo/rikkahub
- 集成位置：`modules/chat-agent/`
- 许可证：用户分段双重许可，符合条件的使用场景适用 GNU AGPL v3，其他使用场景需取得商业授权
- 许可证文件：`modules/chat-agent/LICENSE`
- 上游商业授权联系方式：`re_dev@qq.com`

### 修改说明

`modules/chat-agent/` 包含源自 RikkaHub 的代码，并已为集成到 XCode 作为 Agent 模块进行修改。

主要集成修改包括：

- 将 RikkaHub 嵌入为 XCode 应用内 Agent 页面。
- 主题、语言和应用缩放行为改为跟随 XCode 设置。
- 复用 XCode 共享 Ubuntu rootfs 和项目工作区映射。
- 移除或禁用 RikkaHub 独立 Web UI/Web Server、更新检测、独立主题切换和独立 rootfs 下载/解压流程。
- 调整资源和界面文本以符合 XCode 默认中文、英文备用的资源策略。

## Material Color Utilities

- 位置：`modules/chat-agent/material3/material-color-utilities/`
- 许可证：Apache License 2.0
- 许可证文件：`modules/chat-agent/material3/material-color-utilities/LICENSE`
