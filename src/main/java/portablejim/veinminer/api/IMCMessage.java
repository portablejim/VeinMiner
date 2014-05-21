package portablejim.veinminer.api;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Helper functions to send IMC messages to VeinMiner
 */
public class IMCMessage {
    public static void addTool(ToolType type, String itemName) {
        sendMessage("item", type, itemName);
    }

    public static void addBlock(ToolType type, String blockName) {
        sendMessage("block", type, blockName);
    }

    private static void sendMessage(String itemType, ToolType type, String blockName) {
        int toolType = type.ordinal();

        NBTTagCompound message = new NBTTagCompound();
        message.setString("whitelistType", itemType);
        message.setShort("toolType", (short) toolType);
        message.setString("blockName", blockName);
        FMLInterModComms.sendMessage("VeinMiner", "whitelist", message);
    }

    public static void addToForceWhitelist(String toolName) {

    }
}
