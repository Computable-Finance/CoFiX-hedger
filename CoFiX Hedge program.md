## CoFiX Hedge program
**Background**
The realization of CoFiX as a calculable financial transaction model requires the market makers to provide liquidity, and the market maker takes a certain risk of price fluctuations to obtain the benefits in the transaction process. The market making, redemption, and exchange transactions defined in CoFiX may affect the proportion of funds in the CoFiX fund pool, which is the source of price fluctuation risks. For the risk of price fluctuations, market makers can eliminate the risk by hedging *in OTC*(在场外) . The basic idea is to calculate the part the market makers can obtain based on the structure of funds in the fund pool, their own share ratio, and the comparisons with a set reference point, in order to eliminate negative increments, thereby ensuring that the total assets are in a state of growth at any price.

**Description**
According to the previous description, there can be multiple specific hedging strategies. This hedging program implements a strategy for automated hedging according to the basic idea described above. The specific logic is as follows:

1. The program defines a reference state of the asset structure, and then polls to detect state changes to get the state change increment.
2. When the increment of an asset is less than 0, and the absolute value exceeds a set threshold,  the other asset will be sold to fill up the negative growth of assets (due to the CoFiX trading mechanism, it can be ensured that the amount of assets needed to sell is less than its increment, thereby to achieve profitability)
3. After completing the hedging operation described in 2, update the reference status to the new status. 
4. When the program is running, the state obtained for the first time is used as the reference state.

**Preparation**

1. Market maker account address: Mainly used to query personal shares.
2. Ethereum node URL: You can apply for free via https://infura.io.

**Run start**

1. Run the project:  windows: double-click the **run.bat** file (Linux/MacOS: execute **./run.sh**) under the project path. When running the project, a window will pop up, and please do not close it. You can view the log information in the window, or in the log files under the logs folder .
2. log in:
- Enter http://127.0.0.1 in the browser to enter the login page, with the default user name **cofix**, and password **&UJM6yhn**.
- If you need to modify the password, you can modify **cofix.user.name** (user name) and **confix.user.password** (password) in the configuration file: **co-fi-x-hedging/Hedging/src/main/resourcesapplication.properties**.
3. If you package it yourself, you need to run the **install-huobi-jar.bar** or **install-huobi-jar.sh** file in the lib folder first.

**Related settings**

1. The node address must be set first (required).
2. Set the market maker account address (required).
3. Set the network proxy IP address and port (not required).
4. Set Huobi **API-KEY** and **API-SECRET** (required), because exchange hedging requires filling in API-KEY and API-SECRET.
5. Set the polling interval for asset hedging (not required, 10 seconds by default);
6. Set the hedging threshold and set it yourself as needed.
7. Enable hedging.  After the above configuration is completed, click **Hedging** to enable hedging. If you need to close it, click it again.

**Core code**
[Core code description](https://github.com/Computable-Finance/CoFiX-hedger/blob/master/Hedging/README.md)