[toc]

***
## CoFiX Hedge program
**Background**

CoFiX is a novel type of AMM that allows market makers to provide liquidity with a calculable risk. To manage this risk, a market maker needs to hedge their position in other market to lock in profits by mirroring the proportion changes in the liquidity pools, proportional to their share of the pool. By doing so, a market maker can eliminate negative fluctuations and ensure that their asset position is always in  a state of growth, regardless of the market price.  

This repo provides a basic tool for market makers to hedge but there can be multiple ways to do it. 

**Description**

This hedging program implements a strategy for automated hedging according to the basic idea described above. The specific logic is as follows:

1. The program defines a reference state of the asset structure, and then polls to detect state changes.
2. When the increment of an asset is less than 0, and the absolute value exceeds a set threshold,  the other asset will be sold to fill the negative growth of the assets. The CoFiX trading mechanism can ensure that the amount of assets needed to sell is less than its increment, thereby achieving profitability.
3. After completing the hedging operation described in 2, update the reference status to the new status. 
4. When the program is running, the state obtained for the first time is used as the reference state.

**Preparation**

1. Market maker account address: Mainly used to query personal shares.
2. Ethereum node URL: You can apply for free at [infura](https://infura.io).

**Run start**

1. Run the project:  windows: double-click the **run.bat** file (Linux/MacOS: execute **./run.sh**) under the project path. When running the project, a window will pop up (please do not close it). You can view the log information in the window, or in the log files under the logs folder. These files are located in [releases](https://github.com/Computable-Finance/CoFiX-hedger/releases)
2. log in:
- Enter http://127.0.0.1 in the browser to enter the login page, with the default user name **cofix**, and password **&UJM6yhn**.
- If you need to modify the password, you can modify **cofix.user.name** (user name) and **confix.user.password** (password) in the configuration file: **co-fi-x-hedging/Hedging/src/main/resources/application.properties**.
3. If you package it yourself, you need to run the **install-huobi-jar.bar** or **install-huobi-jar.sh** file in the lib folder first.

**Related settings**

1. The node address must be set (required).
2. The market maker account address must be set (required).
3. The network proxy IP address and port (not required).
4. Set the Huobi **API-KEY** and **API-SECRET** (required), because exchange hedging requires filling in an API-KEY and API-SECRET.
5. Set the polling interval for asset hedging (not required, 10 seconds by default);
6. Set the hedging threshold as needed.
7. Enable hedging.  After the above configuration is completed, click **Hedging** to enable hedging. If you need to close it, click it again.

**Core code**
[Core code description](https://github.com/Computable-Finance/CoFiX-hedger/blob/master/Hedging/README.md)

### CoFiX Hedge program

#### 背景
CoFiX作为一种可计算的金融交易模型的实现，需要做市商角色提供资金流动性，做市商通过承担一定的价格波动风险，来获取交易过程中得到的收益。CoFiX中定义的做市、赎回、兑换交易，都有可能会影响CoFiX资金池的资金比例，这是价格波动风险的来源。
对于价格波动风险，做市商可以通过在场外进行反向交易对冲来消除，基本思路是根据资金池中资金的结构和自己的份额比例，实时的计算出自己可以分得的部分，与自己选定的参考点进行比较，消除负增量，从而保证在任何价格下，总资产都是处于增长状态。

#### 说明
根据前面的描述，具体的对冲策略可以有多种，本对冲程序按照前面描述的基本思路，实现了一种策略，来进行自动化对冲，具体逻辑如下：
1. 程序定义了一个资产结构参考状态，然后轮询检测状态变更，得到状态变更增量。
2. 当一种资产的增量小于0，且绝对值超过一个设定的阈值时，就通过卖出另外一种资产，来将负增长的资产补齐（由于CoFiX交易机制，可以确保做市商需要卖出的资产数量小于其增量，从而实现盈利）
3. 完成2中描述的对冲操作后，将新状态更新为参考状态。
4. 程序运行时，将第一次获取到的状态作为参考状态。

#### 准备

1. 做市商账号地址：主要用来查询个人份额。
2. 以太坊节点URL：可通过 https://infura.io 免费申请。

#### 运行启动

1. 运行项目：windows:双击项目路径下run.bat文件(Linux/MacOS:执行./run.sh)，运行项目，会弹出窗口，请勿关闭，可在窗口查看日志信息，也可以在logs文件夹下查看历史日志信息。
2. 登录：
   * 浏览器输入http://127.0.0.1，会进入登录页面，默认用户名cofix，密码&UJM6yhn。
   * 如需修改密码，可修改co-fi-x-hedging/Hedging/src/main/resourcesapplication.properties中的cofix.user.name（用户名）、confix.user.password（密码）。
3. 如自行打包，需先运行 lib文件夹下的 install-huobi-jar.bar或者install-huobi-jar.sh 文件。


#### 相关设置

1. 必须优先设置节点地址（必填）。
2. 设置做市商账户地址（必填）。
3. 设置网络代理ip地址和端口（非必填）。
4. 设置添加交易池（必填）,可以依次添加多个。
   * 交易池ERC20代币地址（必填）；
   * 火币交易对（必填，如ethusdt）；
   * WETH对冲阈值（非必填，默认1ETH）；
   * ERC20对冲阈值（非必填，默认1ERC20）；
5. 设置火币API-KEY和API-SECRET（必填）,交易所对冲必须填写API-KEY和API-SECRET。 
6. 设置资产对冲轮询时间间隔（非必填，默认10秒）;
7. 设置对冲阈值，根据需要自行设置。
8. 开启对冲，以上配置完成后，点击 Hedging 可开启对冲，如需关闭，再点击一次即可。

#### 相关合约地址配置说明
	可以在application.properties 配置文件中进行配置，如果合约更新，地址变更，可以重新配置，然后重启程序。
```properties
## TEST WETH token contract address
cofix.weth.contract.address=0x59b8881812ac484ab78b8fc7c10b2543e079a6c3

## TEST Query the address of the trade pool contract based on the ERC20 token address
cofix.icofix-factory.contract.address=0xB19EbE64A0ca9626824abBdbdeC4a76294D460A5

## TEST Query the address of the lock pool contract based on the trade pool contract address
cofix.lock-factory.contract.address=0xB6ae9774D2C743B0886123A1C98d4fc92558BaBC
```


#### 核心代码

[核心代码说明](./Hedging/README.md)
