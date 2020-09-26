[toc]

***

### CoFiX Hedge program

#### 准备

1. 钱包私钥：做市商账户私钥。
2. 以太坊节点URL：可通过 https://infura.io 免费申请。

#### 运行启动

1. 运行项目：windows:双击项目路径下run.bat文件(Linux/MacOS:执行./run.sh)，运行项目，会弹出窗口，请勿关闭，可在窗口查看日志信息，也可以在logs文件夹下查看历史日志信息。
2. 登录：
   * 浏览器输入http://127.0.0.1，会进入登录页面，默认用户名cofix，密码&UJM6yhn。
   * 如需修改密码，可修改co-fi-x-hedging/Hedging/src/main/resourcesapplication.properties中的cofix.user.name（用户名）、confix.user.password（密码）。
3. 如自行打包，需先运行 lib文件夹下的 install-huobi-jar.bar或者install-huobi-jar.sh 文件。


#### 相关设置

1. 必须优先设置节点地址（必填）。
2. 设置做市商账户私钥（必填）。
3. 设置网络代理ip地址和端口（非必填）。
4. 设置火币API-KEY和API-SECRET（必填）,交易所对冲必须填写API-KEY和API-SECRET。 
5. 设置资产对冲轮询时间间隔（非必填，默认10秒）;
6. 设置对冲阈值，根据需要自行设置。
7. 开启对冲，以上配置完成后，点击 Hedging 可开启对冲，如需关闭，再点击一次即可。


#### 核心代码

[核心代码说明](./Hedging/README.md)
