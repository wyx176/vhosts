## Hosts
无root修改安卓hosts、实现DNS功能

支持通配符DNS记录，例如：
```
127.0.0.1 a.com     |
127.0.0.1 m.a.com   |  => 127.0.0.1 .a.com
127.0.0.1 w.m.a.com |
```   

### APP配置
#### 所有的hosts均从网络获取
#### App上的任何标题、按钮名称均可以配置
#### 如何修改请求地址?
1、找到VhostsActivity.java->onCreate方法
2、修改方法中base64加密的字符串，是一个被加密的url，修改后替换掉即可
3、接口返回的数据格式
```
{"appInfo":{"appName":"App名称","helpBtnName":"首页帮助按钮名称"},"version":{"v":"3.0","title":"更新弹窗标题！！！","confirm":"确认按钮名称","msg":"更新内容","url":"ttps://baidu.com"},"notice":{"show":false,"title":"更新内容","msg":"修复部分网络使用不生效的问题","confirm":"确定"},"dns":{"DNS4":["223.5.5.5","223.6.6.6"],"DNS6":["2400:3200::1"]},"webViewConfig":{"title":"web浏览器标题","url":"https://qq,com","allowOpenUrls":["mqqopensdkapi","weixin","intent"]},"hosts":["127.0.0.1 localhost","::1 localhost","::1 ip6-localhost","::1 ip6-loopback"]}
```
##### 说明
```
version:v 版本号、如果接口返回的版本号和软件的版本号不一致会提示强制更新
notice:show app打开时候的弹窗公告开关、true开启、fasle关闭
dns：DNS4 默认空数组、可不填使用系统自带的
dns:DNS6 同上
webViewConfig:url webview 默认打开的地址
webViewConfig:allowOpenUrls 这个是webview能唤起的app类型 默认当前就行
hosts：设置hosts 数组、多个逗号隔开
```
### 开源许可证

<ul>
    <li>LocalVPN: <a href="https://github.com/hexene/LocalVPN/blob/master/README.md">APL 2.0</a></li>
</ul>



#### LICENSE

Copyright (C) 2023  

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
