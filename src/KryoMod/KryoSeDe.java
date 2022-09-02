package KryoMod;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.JavaSerializer;

import Blocks.mod.Block_obj;
import Blocks.mod.Epoch;
import Blocks.mod.PreviousBlockObj;
import Blocks.mod.StakeBlock;
import SEWS_Protocol.StakeObj;
import SEWS_Protocol.weightResults;
import connect.Mod.Request;
import node.Mod.CheqIn;
import node.Mod.NodeData;
import penalty.Report;
import transc.mod.Ctx;
import transc.mod.Ptx;
import vault.BackUpVault;
import vault.Vault_obj;
import wallets.mod.Acc_obj;

public class KryoSeDe {
	public static Kryo kryo;
	static public void register () {
		kryo = new Kryo();
		kryo.register(org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey.class);
		kryo.register(org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPrivateKey.class);
		kryo.register(java.math.BigDecimal.class);
		kryo.register(Block_obj.class, new JavaSerializer());
		kryo.register(PreviousBlockObj.class, new JavaSerializer());
		kryo.register(Request.class, new JavaSerializer());
		kryo.register(Report.class, new JavaSerializer());
		kryo.register(NodeData.class, new JavaSerializer());
		kryo.register(Integer.class);
		kryo.register(ArrayList.class);
		kryo.register(CopyOnWriteArrayList.class);
		kryo.register(BlockListReg.class, new JavaSerializer());
		kryo.register(StringListReg.class, new JavaSerializer());
		kryo.register(StakeObjListReg.class, new JavaSerializer());
		kryo.register(InetAddressListReg.class, new JavaSerializer());
		kryo.register(CtxListReg.class, new JavaSerializer());
		kryo.register(PtxListReg.class, new JavaSerializer());
		kryo.register(Acc_obj.class, new JavaSerializer());
		kryo.register(StakeBlock.class, new JavaSerializer());
		kryo.register(weightResults.class, new JavaSerializer());
		kryo.register(StakeObj.class, new JavaSerializer());
		kryo.register(Ctx.class, new JavaSerializer());
		kryo.register(Ptx.class, new JavaSerializer());
		kryo.register(byte[].class);
		kryo.register(Object.class);
		kryo.register(BackUpVault.class, new JavaSerializer());
		kryo.register(Vault_obj.class, new JavaSerializer());
		kryo.register(CheqIn.class, new JavaSerializer());
		kryo.register(Epoch.class, new JavaSerializer());
	}
	
	
	
}
