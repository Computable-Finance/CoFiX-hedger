## CoFiX Hedge program
**Background**

CoFiX is a novel type of AMM that allows market makers to provide liquidity with a calculable risk. To manage this risk, a market maker must hedge their position on an OTC service to lock in profits by mirroring the trades that occur in their CoFiX liquidity pool, proportional to their share of the pool. By doing so, a market maker can eliminate negative fluctuations and ensure that their position in a pool is always in  a state of growth, regardless of the market price.  

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

1. Run the project:  windows: double-click the **run.bat** file (Linux/MacOS: execute **./run.sh**) under the project path. When running the project, a window will pop up (please do not close it). You can view the log information in the window, or in the log files under the logs folder.
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
