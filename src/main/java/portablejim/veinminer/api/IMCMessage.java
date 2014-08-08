package portablejim.veinminer.api;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.nbt.NBTTagCompound;

/**
 * Helper functions to send IMC messages to VeinMiner
 */
@SuppressWarnings("UnusedDeclaration")
public class IMCMessage {
    public static void addTool(String type, String itemName) {
        sendWhitelistMessage("item", type, itemName);
    }

    public static void addBlock(String type, String blockName) {
        sendWhitelistMessage("block", type, blockName);
    }

    private static void sendWhitelistMessage(String itemType, String toolType, String blockName) {
        NBTTagCompound message = new NBTTagCompound();
        message.setString("whitelistType", itemType);
        message.setString("toolType", toolType);
        message.setString("blockName", blockName);
        FMLInterModComms.sendMessage("VeinMiner", "whitelist", message);
    }

    public static void addToolType(String type, String name, String icon) {
        NBTTagCompound message = new NBTTagCompound();
        message.setString("toolType", type);
        message.setString("toolName", name);
        message.setString("toolIcon", icon);
        FMLInterModComms.sendMessage("VeinMiner", "addTool", message);
    }

    public static void addBlockEquivalence(String existingBlock, String newBlock) {
        NBTTagCompound message = new NBTTagCompound();
        message.setString("existingBlock", existingBlock);
        message.setString("newBlock", newBlock);
        FMLInterModComms.sendMessage("VeinMiner", "addEqualBlocks", message);
    }
}
