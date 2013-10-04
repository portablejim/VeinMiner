package portablejim.veinminer.event;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import portablejim.veinminer.core.MinerInstance;

import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 5/09/13
 * Time: 4:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class InstanceTicker implements ITickHandler {
    MinerInstance minerInstance;
    public InstanceTicker(MinerInstance caller) {
        minerInstance = caller;
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData) {
        if(type.contains(TickType.SERVER)) {
            minerInstance.mineScheduled();
        }
    }

    @Override
    public EnumSet<TickType> ticks() {
        return EnumSet.of(TickType.SERVER);
    }

    @Override
    public String getLabel() {
        return "VeinMinerInstanceTicker";
    }
}
