/* This file is part of VeinMiner.
 *
 *    VeinMiner is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU Lesser General Public License as
 *    published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *    VeinMiner is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU Lesser General Public License for more details.
 *
 *    You should have received a copy of the GNU Lesser General Public
 *    License along with VeinMiner.
 *    If not, see <http://www.gnu.org/licenses/>.
 */

package portablejim.veinminer.server;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StatCollector;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.util.BlockID;

import java.util.List;

import static portablejim.veinminer.configuration.ConfigurationSettings.ToolType;

/**
 * Command so that clients can control VeinMiner settings for their player.
 */

public class MinerCommand extends CommandBase {
    public static final int COMMAND_MODE = 0;
    public static final int COMMAND_BLOCKLIST = 1;
    public static final int COMMAND_TOOLLIST = 2;
    public static final int COMMAND_BLOCKLIMIT = 3;
    public static final int COMMAND_RANGE = 4;
    public static final int COMMAND_PER_TICK = 5;
    public static final int COMMAND_SAVE = 6;
    public static final int COMMAND_HELP = 7;
    private static final String[] commands = new String[]{"mode", "blocklist", "toollist", "blocklimit", "radius", "per_tick", "saveconfig", "help"};
    private static final String[] modes = new String[] {"disable", "auto", "sneak", "no_sneak"};

    @Override
    public String getCommandName() {
        return "veinminer";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        return par1ICommandSender instanceof EntityPlayerMP;
    }

    private ToolType commandTool(String[] commandString, String commandName) {
        if(commandString.length == 1) {
            showUsageError("command.veinminer." + commandName);
        }

        ToolType tool = ToolType.PICKAXE;
        if("pickaxe".equals(commandString[1])) {
            tool = ToolType.PICKAXE;
        }
        else if("axe".equals(commandString[1])) {
            tool = ToolType.AXE;
        }
        else if("shovel".equals(commandString[1])) {
            tool = ToolType.SHOVEL;
        }
        else {
            showUsageError("command.veinminer." + commandName);
        }

        return tool;
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        EntityPlayerMP senderPlayer;
        if(icommandsender instanceof EntityPlayerMP) {
            senderPlayer = (EntityPlayerMP) icommandsender;
        }
        else {
            String message = StatCollector.translateToLocal("command.veinminer.cannotuse");
            throw new CommandException(message);
        }

        if(astring.length > 0) {
            if(astring[0].equals(commands[COMMAND_MODE])) {
                runCommandMode(senderPlayer, astring);
            }
            else if(astring[0].equals(commands[COMMAND_BLOCKLIST])) {
                needAdmin(senderPlayer);
                runCommandBlocklist(senderPlayer, astring);
            }
            else if(astring[0].equals(commands[COMMAND_TOOLLIST])) {
                needAdmin(senderPlayer);
                runCommandToollist(senderPlayer, astring);
            }
            else if(astring[0].equals(commands[COMMAND_BLOCKLIMIT])) {
                needAdmin(senderPlayer);
                runCommandBlocklimit(senderPlayer, astring);
            }
            else if(astring[0].equals(commands[COMMAND_RANGE])) {
                needAdmin(senderPlayer);
                runCommandRange(senderPlayer, astring);
            }
            else if(astring[0].equals(commands[COMMAND_PER_TICK])) {
                needAdmin(senderPlayer);
                runCommandPerTick(senderPlayer, astring);
            }
            else if(astring[0].equals(commands[COMMAND_SAVE])) {
                needAdmin(senderPlayer);
                runCommandSave(senderPlayer);
            }
            else if(astring[0].equals(commands[COMMAND_HELP])) {
                runCommandHelp(senderPlayer, astring);
            }
            else
            {
                showUsageError("command.veinminer");
            }
        }
        else
        {
            showUsageError("command.veinminer");
        }
    }

    private void sendProperChatToPlayer(EntityPlayerMP player, String incomingMessage) {
        String message = StatCollector.translateToLocal(incomingMessage);
        player.addChatMessage(message);
    }

    private void sendProperChatToPlayer(EntityPlayerMP player, String incomingMessage, Object... params) {
        String message = StatCollector.translateToLocalFormatted(incomingMessage, params);
        player.addChatMessage(message);
    }

    private void showUsageError(String errorKey) throws WrongUsageException {
        String message = StatCollector.translateToLocal(errorKey);
        throw new WrongUsageException(message);
    }

    private void showUsageError(String errorKey, Object... params) {
        String message = StatCollector.translateToLocalFormatted(errorKey, params);
        throw new WrongUsageException(message);
    }

    private void needAdmin(EntityPlayerMP player) {
        MinecraftServer server = player.mcServer;
        if(server.isDedicatedServer() && !server.getConfigurationManager().isPlayerOpped(player.getCommandSenderName())) {
            boolean playerNoClient = !MinerServer.instance.playerHasClient(player.getEntityName());
            String message = "command.veinminer.permissionDenied";
            if(playerNoClient) {
                message = LanguageRegistry.instance().getStringLocalization(message);
            }
            throw new CommandException(message);
        }
    }

    private void commandAction(String[] commandString, String commandName) {
        if (commandString.length < 3 || (!"add".equals(commandString[2]) && !"remove".equals(commandString[2]))) {
            showUsageError("command.veinminer." + commandName + ".actionerror", commandString[1]);
        }
    }

    private void runCommandMode(EntityPlayerMP senderPlayer, String[] astring) throws WrongUsageException {
        MinerServer minerServer = MinerServer.instance;
        String player = senderPlayer.getCommandSenderName();

        if(astring.length == 1) {
            showUsageError("command.veinminer.enable");
        }
        else if(astring[1].equals(modes[0])) {
            minerServer.setPlayerStatus(player, PlayerStatus.DISABLED);
            sendProperChatToPlayer(senderPlayer, "command.veinminer.set.disable");
        }
        else if(astring[1].equals(modes[1])) {
            if(minerServer.playerHasClient(player)) {
                minerServer.setPlayerStatus(player, PlayerStatus.INACTIVE);
            }
            else {
                minerServer.setPlayerStatus(player, PlayerStatus.DISABLED);
            }
            sendProperChatToPlayer(senderPlayer, "command.veinminer.set.auto");
        }
        else if(astring[1].equals(modes[2])) {
            minerServer.setPlayerStatus(player, PlayerStatus.SNEAK_ACTIVE);
            sendProperChatToPlayer(senderPlayer, "command.veinminer.set.sneak");
        }
        else if(astring[1].equals(modes[3])) {
            minerServer.setPlayerStatus(player, PlayerStatus.SNEAK_INACTIVE);
            sendProperChatToPlayer(senderPlayer, "command.veinminer.set.nosneak");
        }
    }

    private void runCommandBlocklist(EntityPlayerMP senderPlayer, String[] astring) {
        ConfigurationSettings configSettings = MinerServer.instance.getConfigurationSettings();

        ToolType tool = commandTool(astring, "blocklist");
        String toolString = astring[1];

        commandAction(astring, "blockList");
        String action = astring[2];

        if(astring.length < 4) {
            showUsageError("command.veinminer.blocklist.itemerror", toolString, action);
        }

        BlockID blockID = new BlockID(astring[3], ":", -1);
        if(blockID.id <= 0) {
            // String is not in proper format
            showUsageError("command.veinminer.blocklist.itemerror", toolString, action);
        }

        if("add".equals(action)) {
            configSettings.addBlockToWhitelist(tool, blockID);
            sendProperChatToPlayer(senderPlayer, "command.veinminer.blocklist.add", blockID.id, blockID.metadata, toolString);
        }
        else if("remove".equals(action)) {
            configSettings.removeBlockFromWhitelist(tool, blockID);
            sendProperChatToPlayer(senderPlayer, "command.veinminer.blocklist.remove", blockID.id, blockID.metadata, toolString);
        }
    }

    private void runCommandToollist(EntityPlayerMP senderPlayer, String[] astring) {
        ConfigurationSettings configSettings = MinerServer.instance.getConfigurationSettings();

        ToolType tool = commandTool(astring, "toollist");
        String toolString = astring[1];

        commandAction(astring, "toollist");
        String action = astring[2];

        if(astring.length < 4) {
            showUsageError("command.veinminer.toollist.itemerror", toolString, action);
        }

        int toolId;
        try{
            toolId = Integer.parseInt(astring[3]);
        }
        catch(NumberFormatException e) {
            toolId = -1;
        }

        if(toolId <= 0) {
            // String is not in proper format
            showUsageError("command.veinminer.toollist.itemerror", toolString, action);
        }

        if("add".equals(action)) {
            configSettings.addTool(tool, toolId);
            sendProperChatToPlayer(senderPlayer, "command.veinminer.toollist.add", toolId, toolString);
        }
        else if("remove".equals(action)) {
            configSettings.removeTool(tool, toolId);
            sendProperChatToPlayer(senderPlayer, "command.veinminer.toollist.remove", toolId, toolString);
        }
    }

    private void runCommandBlocklimit(EntityPlayerMP senderPlayer, String[] astring) {
        if(astring.length == 1) {
            showUsageError("command.veinminer.blocklimit");
        }

        int newBlockPerTick = 0;
        try {
            newBlockPerTick = Integer.parseInt(astring[1]);
        }
        catch (NumberFormatException e) {
            showUsageError("command.veinminer.blocklimit");
        }

        MinerServer.instance.getConfigurationSettings().setBlockLimit(newBlockPerTick);

        int actualBlockPerTick = MinerServer.instance.getConfigurationSettings().getBlockLimit();
        sendProperChatToPlayer(senderPlayer, "command.veinminer.blocklimit.set", actualBlockPerTick);
    }

    private void runCommandRange(EntityPlayerMP senderPlayer, String[] astring) {
        if(astring.length == 1) {
            showUsageError("command.veinminer.range");
        }

        int newRange = 0;
        try {
            newRange = Integer.parseInt(astring[1]);
        }
        catch (NumberFormatException e) {
            showUsageError("command.veinminer.range");
        }

        MinerServer.instance.getConfigurationSettings().setRadiusLimit(newRange);

        int actualRange = MinerServer.instance.getConfigurationSettings().getRadiusLimit();
        sendProperChatToPlayer(senderPlayer, "command.veinminer.range.set", actualRange);
    }

    private void runCommandPerTick(EntityPlayerMP senderPlayer, String[] astring) {
        if(astring.length == 1) {
            showUsageError("command.veinminer.pertick");
        }

        int newRate = 0;
        try {
            newRate = Integer.parseInt(astring[1]);
        }
        catch (NumberFormatException e) {
            showUsageError("command.veinminer.pertick");
        }

        MinerServer.instance.getConfigurationSettings().setRadiusLimit(newRate);

        int actualRate = MinerServer.instance.getConfigurationSettings().getRadiusLimit();
        sendProperChatToPlayer(senderPlayer, "command.veinminer.pertick.set", actualRate);
    }

    private void runCommandSave(EntityPlayerMP senderPlayer) {
        MinerServer.instance.getConfigurationSettings().saveConfigs();
        sendProperChatToPlayer(senderPlayer, "command.veinminer.saveconfig");
    }

    private void runCommandHelp(EntityPlayerMP senderPlayer, String[] astring) {
        if(astring.length > 1) {
            if(astring[1].equals(commands[COMMAND_MODE])) {
                sendProperChatToPlayer(senderPlayer, "command.veinminer.help.enable1");
                sendProperChatToPlayer(senderPlayer, "command.veinminer.help.enable2");
                sendProperChatToPlayer(senderPlayer, "command.veinminer.help.enable3");
                sendProperChatToPlayer(senderPlayer, "command.veinminer.help.enable4");
                sendProperChatToPlayer(senderPlayer, "command.veinminer.help.enable5");
            }
        }
        else {
            sendProperChatToPlayer(senderPlayer, "command.veinminer.help1");
            sendProperChatToPlayer(senderPlayer, "command.veinminer.help2");
            sendProperChatToPlayer(senderPlayer, "command.veinminer.help3");
            sendProperChatToPlayer(senderPlayer, "command.veinminer.help4");
            sendProperChatToPlayer(senderPlayer, "command.veinminer.help5");
            sendProperChatToPlayer(senderPlayer, "command.veinminer.help6");
            sendProperChatToPlayer(senderPlayer, "command.veinminer.help7");
            sendProperChatToPlayer(senderPlayer, "command.veinminer.help8");
            sendProperChatToPlayer(senderPlayer, "command.veinminer.help9");
        }
    }

    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] arguments) {
        switch (arguments.length) {
            case 1:
                return getListOfStringsMatchingLastWord(arguments, commands);
            case 2:
                if(arguments[0].equals(commands[COMMAND_MODE])) {
                    return getListOfStringsMatchingLastWord(arguments, modes);
                }
                else if(arguments[0].equals(commands[COMMAND_BLOCKLIST]) || arguments[0].equals(commands[COMMAND_TOOLLIST])) {
                    String[] tools = { "pickaxe", "axe", "shovel" };

                    return getListOfStringsMatchingLastWord(arguments, tools);
                }
                else if(arguments[0].equals(commands[COMMAND_TOOLLIST])) {
                    return getListOfStringsMatchingLastWord(arguments, commands);
                }
            case 3:
                if(arguments[0].equals(commands[COMMAND_BLOCKLIST]) || arguments[0].equals(commands[COMMAND_TOOLLIST])) {
                    String[] actions = { "add", "remove" };

                    return getListOfStringsMatchingLastWord(arguments, actions);
                }
        }
        return null;
    }

    @Override
    public String getCommandUsage(ICommandSender par1ICommandSender) {
        return LanguageRegistry.instance().getStringLocalization("command.veinminer");
    }
}
