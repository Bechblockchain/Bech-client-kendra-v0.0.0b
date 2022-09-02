package temp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.List;

import Blocks.mod.StakeBlock;
import SEWS_Protocol.weightResults;

public class Static {

	//Native Bech Node data
	public static final String VERSION = "kendra 0.0.0b"; // Client/Chain version
	public static String NODE_TYPE = "Full Node" ; // Node type
	public static String START_TYPE; // Start type
	public static String START_TIME; // time node was started
	public static String NATIVE_VALIDATOR_ADDRESS; // Address of native node
	public static final String WARM_START = "WARM_START"; // Node ready to receive and validate blocks
	public static final String COLD_START = "COLD_START"; // Node not ready to receive and validate blocks
	public static String TOTAL_UP_TIME; // Node total up time
	public static String EPOCH_HEIGHT; // node synced up epoch height
	public static long NATIVE_BLOCK_HEIGHT; // Block height of native node
	public static final long COLD_START_TIME_THRESHOLD = 259200000; // time threshold to restart initial block download (72 hours)
	public static final long MAX_BLOCKS_REQUEST = 1000000; // max number of blocks per db file/batch
	public static long CLIENT_SENT_TIME; // requested check-in timestamp
	public static int NUM_EPOCH_WON; // Number of epochs won by native validator node
	public static long BLOCKS_VALIDATED_BY_NATIVE; //Number of blocks validated by native validator node
	public static int MAX_INBOUND_PEERS = 6; // Maximum number of outbound peers
	public static int MAX_OUTBOUND_PEERS = 4; // Maximum number of inbound peers
	public static int MAX_IP_SERVER_OUTBOUND = 2; // Maximum number of outbound servers to send all peers ips
	public static int NUM_OF_CORES; //machine core
	
	//Network data
	public static long NETWORK_TIME; // network time
	public static String NETWORK_BLOCK_HEIGHT; // block height of whole network
	public static final long NTP_TIME_OFFSET = 2000; // time offset from ntp servers
	public static final long NETWORK_TIME_OFFSET = 4000; // time offset from network
	
		
	//Validation Epoch
	public static String EPOCH_BLOCK_HASH; // Number of previously verified block (n-1)
	public static long EPOCH_TIMESTAMP; // Timestamp of current block being verified
	public static final String EPOCH = "Epoch"; // Epoch state is "start"
	public static final String EPOCH_ULTIMO_GRADU = "Ultimo_gradu"; // Epoch state is "in progress"
	public static final String EPOCH_COMPLETE = "Epoch_complete"; // Epoch state is "complete"
	public static final String PUNISH_PREV_VALIDATOR = "punish"; // IsEpochStart is to punish the previous validator
	public static final String REWARD_PREV_VALIDATOR = "reward"; // IsEpochStart is to reward the previous validator
	public static long COMPLETED_EPOCH_ANCESTOR_BLOCK_TIMESTAMP; // Timestamp of last block of epoch
	public static String PREVIOUS_EPOCH_VALIDATOR_ADDRESS; // Address of previous epoch validator
	public static BigDecimal STAKE_REWARD; // Epoch validator's rewards from stake and unstake txs
	public static BigDecimal CTX_REWARD; // Epoch validator's rewards from coin txs
	public static final BigDecimal FEE = new BigDecimal(0.01).setScale(2, RoundingMode.HALF_EVEN); // Transaction fees
	public static long PARENT_TIMESTAMP; // Timestamp of previous/parent block
	
	
	//Epoch Info
	
	//Previous Epoch
	public static BigInteger PREVIOUS_VALIDATOR_CONFIRMATION_TIME;
	
	//Current Epoch
	public static BigInteger CURRENT_NEXT_STAKE_PROCESSING; // current epoch time stake weighing for next epoch
	public static String CURRENT_EPOCH_START_TIME; // current epoch start time
	public static String CURRENT_EPOCH_END_TIME; // current epoch end time
	public static String CURENT_EPOCH_HEIGHT; // current epoch Height
	public static BigInteger CURRENT_VALIDATOR_FALL_POSITION; // current epoch validator position on fall list
	public static String EPOCH_VALIDATOR_ADDRESS; // Address of validating node for current epoch
	
	   //Next Epoch
	public static String NEXT_EPOCH_START_TIME; // current epoch start time
	public static String NEXT_EPOCH_END_TIME; // next epoch end time
	public static BigInteger NEXT_VALIDATOR_FALL_POSITION; // next epoch validator position on fall list
	public static String NEXT_EPOCH_VALIDATOR_ADDRESS; // Address of next epoch validating node
		
	
	
	//Coin Block Data
	public static final String GENESIS = "Genesis_block"; // Genesis block range
	public static String PREV_BLOCK_NUM; // Number of previously verified block (n-1)
	public static final BigDecimal ALLOWANCE_TIME_CONSTANT = new BigDecimal(10000); // Time allowed between last block of ended epoch and first block of new epoch
	public static long NUM_CTX_BLOCKS; // Total number of blocks in the chain
	public static long MAX_BLOCKS_FILES = 100000;
	
	// Stake Block Data
	public static StakeBlock RECEIVED_STAKE_BLOCK; // Newly verified stake block
	public static String PREV_STAKE_BLOCK_NUM; // Previous stake block number
	public static StakeBlock TEMP_STAKE_BLOCK; // Unverified stake block
	public static List<weightResults> PREVIOUS_FINAL_CONFIRMED_PROCESSED_STAKE_RESULTS; // Weights of stake objs processed during previous epoch to for current epoch
	public static long EPOCH_CONFIRMATION_TIME; // Time for confirmation of next epoch validator
	public static StakeBlock CUURENT_EPOCH_STAKE_BLOCK; // Current epoch stake block in db
	public static final BigDecimal MIN_STAKE_TX_POOL_RATIO = new BigDecimal(70).setScale(2, RoundingMode.HALF_EVEN); // minimum percentage of stakes expected in pool for each epoch
	
	
	//Transactions
	public static final String STAKE_OBJ = "Stake_obj"; // SEWS object parameter for staking and unstaking txs
	public static final String OBSOLETE_STAKE_CLEARANCE = "Obsolete_stake_obj"; // Range parameter for clearing stale stake objs 
	public static final String TYPE_UNSTAKE = "Unstake"; // Range parameter to unstake coins from stake objs
	public static final String TYPE_STAKE = "Stake"; // Range parameter to stake coins in stake objs
	public static final String REWARD_RANGE = "Reward"; // Range parameter to reward previous epoch validator
	public static final String PENALTY_RANGE = "Penalty"; // Range parameter to punish previous epoch validator
	public static final String CTX_RANGE = "Coin_tx"; // Range parameter for simple coin transaction
	public static final String VAULT_RANGE = "Vault_tx"; // Range parameter for vault txs
	public static final String VAL_REWARD_TX = "reward_tx"; // FromAddress from SEWS parameter to reward previous validator on weights list
	public static final String TOP_50_REWARD = "reward_50_tx"; // FromAddress parameter from SEWS to reward previous top 50% of stakers on weights list
	
	public static long MAX_PTX_HOLD = 50000000; // Max ptxs allowed in mempool
	public static int MAX_CTX_HOLD = 5000000; // Max ctxs allowed in mempool
	public static int MAX_STX_HOLD = 1000000; // Max stxs allowed in mempool
	
	//Vault
	public static final String MAINTENANCE_VAULT = "Maintenance_vault"; // Maintenance vault address
	public static final String BACKUP_VAULT =  "Backup_vault"; // BackUp vault address
	
	
	//SEWS Protocol
	public static final String LAST_VAL_STATUS_COMPLETE = "Epoch_complete";
	public static final String LAST_VAL_STATUS_INCOMPLETE = "Epoch_incomplete";
	public static final String LAST_VAL_STATUS_OVERTIME = "Epoch_overtime";
	public static final String NONE = "None";
	public static final long STAKE_OBJ_OBSOLETE_TIME_THRESHOLD = 259200000; // Time allowed for stakes to remain in stake objs(72hrs)
	public static final BigDecimal MIN_STAKE_VALUE = new BigDecimal(100000).setScale(2, RoundingMode.HALF_EVEN); // Minimum amount of coins allowed in stake objs

	public static final BigDecimal PROCESSING_TIME_CONSTANT = new BigDecimal(420000).setScale(0, RoundingMode.DOWN); // (4 minutes)Time constant to start weighing stake objs for next epoch before current epoch ends
	
	//Penalty Report
	public static final String VANITY_REQUEST = "vanity"; // Report to request vanity punishment for suspected validator(looses rewards for epoch)
	public static final String KORUST_REQUEST = "korust"; // Report to request korust punishment for suspected validator(looses rewards for epoch and staked coins)
	public static final String GEHENNA_REQUEST = "gehenna"; // Report to request gehenna punishment for suspected validator(looses rewards for epoch, staked coins, unstaked coins and banned from all stake objs)
	
	//Possible Validator crimes
	public static final String EPOCH_DISHONESTY = "Epoch_dishonesty"; // Timestamp-related crimes committed by suspected validator
	public static final String VALIDATOR_DISHONESTY = "Validator_dishonesty";
	public static final String COIN_BLOCK_TAMPERING = "CoinBlock_tampering";
	public static final String REWARD_TAMPERING = "Reward_tampering";
	public static final String STAKE_TAMPERING = "Stake_tampering"; // Stake objects-related crimes committed by suspected validator
	public static final String CTX_TAMPERING = "Ctx_tampering";
	

}
