package portablejim.veinminer.event;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.nbt.NBTTagCompound;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.api.ToolType;
import portablejim.veinminer.util.BlockID;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 26/04/14
 * Time: 5:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class IMCEvent {
    public IMCEvent() {
        FMLCommonHandler.instance().bus().register(this);
    }

    public void imcCallback(FMLInterModComms.IMCEvent event) {
        for(final FMLInterModComms.IMCMessage message : event.getMessages()) {
            if("whitelist".equalsIgnoreCase(message.key) && message.isNBTMessage()) {
                NBTTagCompound nbtMessage = message.getNBTValue();

                String whitelistName = nbtMessage.getString("whitelistName");
                ToolType toolType = ToolType.values()[nbtMessage.getShort("toolType")];
                String toolName = nbtMessage.getString("toolName");

                if("block".equalsIgnoreCase(whitelistName)) {
                    BlockID blockName = new BlockID(toolName);
                    VeinMiner.instance.configurationSettings.addBlockToWhitelist(toolType, blockName);
                }
                else if("item".equalsIgnoreCase(whitelistName)) {
                    VeinMiner.instance.configurationSettings.addTool(toolType, toolName);
                }
            }
            if("forceTool".equalsIgnoreCase(message.key) && message.isStringMessage()) {

            }
        }
    }
}
