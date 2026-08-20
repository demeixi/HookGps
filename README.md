<h1>虚拟定位模块(Xposed模块)</h1>

<h2>📌 项目简介</h2>
<p>本项目是一款<strong>系统级 Xposed 虚拟定位模块</strong>，适配百度地图进行位置模拟。采用全局生效方案，区别于传统单应用Hook，降低Xposed注入被检测的风险。</p>

<h2>🔧 使用修改说明</h2>
<ol>
<li><strong>配置密钥</strong>：需要在 <code>AndroidManifest.xml</code> 文件中填入自己申请的百度地图 Key。</li>
<li><strong>模块激活</strong>：开启本Xposed模块，勾选推荐应用分组即可，<strong>无需勾选指定目标应用</strong>。</li>
<li><strong>重启生效</strong>：配置完成后重启设备。</li>
<li><strong>防检测特性</strong>：规避直接指定应用Hook，避免因单独勾选应用被检测到Xposed注入行为。</li>
</ol>

<h2>⚠️ 免责声明</h2>
<p>请勿违法违规使用或修改本项目。本源码只用于学术探讨和技术研究。任何违法违规的使用、修改所产生的全部后果，均由使用者自行承担，与作者无关。</p>
<p><strong>下载或使用本源码，即表示您同意以上免责声明。</strong></p>
