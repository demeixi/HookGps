VirtualLocation Xposed Module
简介
VirtualLocation 是一款基于 Xposed 框架实现的系统级虚拟定位模块，可配合百度地图完成位置模拟调试。
使用说明
请在 AndroidManifest.xml 中配置你自己申请的百度地图 Key。
激活本 Xposed 模块，勾选推荐应用分组即可，无需手动勾选目标应用，配置完成后重启设备生效。
该方案规避直接勾选单个目标应用，以此降低因 Xposed 注入被应用检测的风险。
⚠️ 注意：本方案仅为降低检测风险，不能保证 100% 绕过所有应用的检测机制，不同应用防护策略存在差异。
免责声明
本项目源码仅用于 Android 安全技术学习、学术研究与技术交流目的，仅供合法调试场景使用。
禁止将本项目用于任何违反法律法规、侵犯他人权益的行为。
任何使用者因滥用本项目产生的一切后果，均由使用者本人自行承担，与本项目作者无关。
下载、编译、使用本项目源代码，即代表你已完整阅读并同意以上全部免责声明。
英文版本（适合 GitHub 双语 README，可选）
VirtualLocation Xposed Module
Introduction
VirtualLocation is a system‑level virtual location module built on the Xposed framework, supporting location simulation for BaiduMap.
Usage
Configure your own Baidu Map key in AndroidManifest.xml.
Enable this Xposed module. Check the recommended application group instead of manually selecting target apps, then reboot your device.
This approach avoids hooking specified apps directly to reduce the risk of Xposed injection detection.
Note: This can only reduce detection risk and cannot guarantee full bypass against all application detection mechanisms.
Disclaimer
This source code is for academic research and technical communication only, for legitimate debugging purposes.
Any illegal or improper use is strictly prohibited.
All consequences arising from the use or modification of this project shall be borne solely by the end‑user, and shall have nothing to do with the author.
By downloading, compiling or using this source code, you agree to the above disclaimer.
