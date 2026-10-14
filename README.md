# ColorOS AOD Helper

<p align="center">
  <img src="app/src/main/res/drawable/ic_launcher_foreground.webp" width="128" height="128" alt="ColorOS AOD Helper Logo">
</p>

<p align="center">
  <b>ColorOS 17 / 16 / 15 全景息屏增强与手势跟随 LSPosed 模块</b>
</p>

<p align="center">
  <a href="https://github.com/GloamingCity/ColorOS-AOD-Helper/releases"><img src="https://img.shields.io/github/v/release/GloamingCity/ColorOS-AOD-Helper?color=00F5A0&label=Release" alt="Release"></a>
  <img src="https://img.shields.io/badge/Platform-Android%2013%2B%20%7C%20ColorOS%2015--17-007AFF" alt="Platform">
  <img src="https://img.shields.io/badge/Framework-LSPosed%20(Legacy)-00E5FF" alt="LSPosed">
  <a href="LICENSE"><img src="https://img.shields.io/badge/License-GPL--3.0-blue" alt="License"></a>
</p>

---

## 📖 项目简介 (Introduction)

**ColorOS AOD Helper** 是一款专为 OPPO / OnePlus / Realme 系列机型（针对 ColorOS 15、ColorOS 16 及最新的 **ColorOS 17** 新架构适配）深度定制的息屏增强 Xposed 模块。

针对新版系统对“全景息屏”（壁纸息屏）在显示时长和手势交互上的限制，本模块重构了息屏链路，在保证系统稳定、零额外功耗的前提下，提供纯净顺畅的全景息屏体验。

本模块为**轻量化无头模块（Headless Module）**：
* ❌ 无桌面图标与无用 UI
* ❌ 无常驻后台 Service 与常驻进程
* ❌ 无多余联网与权限索取
* ⚡ 即开即用，随系统界面与息屏服务按需加载，性能损耗趋近于零。

---

## ✨ 核心功能 (Features)

### 1. 全景息屏解锁全天常亮显示 (Panoramic AOD Always-On)
* **原版痛点**：官方在全景息屏下仅允许“智能显示”或“定时显示”，无法像局部息屏（纯黑息屏）那样自由开启“全天常亮”。
* **本模块实现**：彻底解锁设置项与底层逻辑判断，允许全景息屏无限制选择全天常亮显示，带来如同智能手表般的永恒流光视觉。

### 2. 全景息屏手势行为完全跟随局部息屏 (Gesture Follow / Double-Tap Wake)
* **原版痛点**：在全景息屏状态下，双击亮屏等息屏手势容易失效或逻辑割裂，手势行为与局部息屏无法保持一致。
* **本模块实现**：针对 ColorOS 新架构重构手势分发链路，使全景息屏的手势调度逻辑完全对齐局部息屏，双击亮屏灵敏跟手，告别误触与唤醒延迟。

---

## 🛠️ 安装与使用指南 (Usage Guide)

1. **环境要求**：
   * 设备已解锁 Bootloader 并获取 Root 权限（Magisk / KernelSU / APatch）；
   * 已安装并激活 **LSPosed** 框架（推荐使用最新版 LSPosed）。
2. **安装步骤**：
   * 前往 [Releases](https://github.com/GloamingCity/ColorOS-AOD-Helper/releases) 页面下载最新的 `ColorOS_AOD_Helper_v1.0.apk` 并安装；
   * 打开 LSPosed 管理器，在模块列表中找到 **ColorOS AOD Helper**；
   * 勾选推荐作用域：
     * `com.android.systemui`（系统界面）
     * `com.oplus.aod`（息屏）
   * 重启“系统界面”与“息屏”，或直接重启手机即可生效。

---

## 💡 技术说明：为什么 LSPosed 显示 legacy？

本模块遵循经典的 Xposed 标准规范（通过 `assets/xposed_init` 反射加载入口，`xposedminversion <= 93`）。在 LSPosed 中，右上角会正确显示为蓝色的 **`legacy`** 标签。

* 与知名系统控制工具 **Scene**、**微博猪手** 保持一致；
* `legacy` 是 Xposed 生态中兼容性最高、最通用的工业级标准，LSPosed 核心引擎长期保持 100% 永久向后兼容，无需担心后续失效。

---

## 🤝 致谢与特别声明 (Credits & Disclaimer)

* **特别说明**：
  > **本项目完全由 Gemini 3.8 flash 基于“ColorOS AOD增强”与“别他妈亮了”二次开发重构**。

* **致敬原作**：
  * 向 **ColorOS AOD增强** 原作者致敬（提供了全景息屏常亮判定的 Hook 思路）；
  * 向 **别他妈亮了** 原作者（nep.timeline）致敬（提供了手势拦截与唤醒广播的思路）。

本项目在吸纳两位原作者优秀思路的基础上，针对 ColorOS 17 架构重新整合，剔除了冗余常驻服务与非必要逻辑，纯化为无头轻量架构，并全新绘制了水生美学旗舰质感图标。

---

## 📄 开源许可证 (License)

本项目采用 [GNU General Public License v3.0 (GPL-3.0)](LICENSE) 开源许可证。
