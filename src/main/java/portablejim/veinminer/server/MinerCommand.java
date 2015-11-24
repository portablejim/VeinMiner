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

import com.google.common.base.Joiner;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.common.registry.LanguageRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.StatCollector;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.util.BlockID;
import portablejim.veinminer.util.PlayerStatus;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Command so that clients can control VeinMiner settings for their player.
 */

public class MinerCommand extends CommandBase {
    private MinerServer minerServer = null;

    public static final int COMMAND_MODE = 0;
    public static final int COMMAND_BLOCKLIST = 1;
    public static final int COMMAND_TOOLLIST = 2;
    public static final int COMMAND_BLOCKLIMIT = 3;
    public static final int COMMAND_RANGE = 4;
    public static final int COMMAND_PER_TICK = 5;
    public static final int COMMAND_SAVE = 6;
    public static final int COMMAND_RELOAD = 7;
    public static final int COMMAND_HELP = 8;
    private static final String[] commands = new String[]{"mode", "blocklist", "toollist", "blocklimit", "radius", "per_tick", "saveconfig", "reloadconfig", "help"};
    private static final String[] modes = new String[] {"auto", "sneak", "no_sneak"};

    public MinerCommand(MinerServer minerServerInstance) {
        minerServer = minerServerInstance;
    }

    @Override
    public String getCommandName() {
        return "veinminer";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        return par1ICommandSender instanceof EntityPlayerMP || par1ICommandSender instanceof DedicatedServer;
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) throws CommandException {
        ICustomCommandSender senderPlayer;
        if(icommandsender instanceof EntityPlayerMP) {
            senderPlayer = new CommandSenderPlayer(minerServer, (EntityPlayerMP)icommandsender);
        }
        else if(icommandsender instanceof DedicatedServer) {
            senderPlayer = new CommandSenderServer((DedicatedServer)icommandsender);
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
            else if(astring[0].equals(commands[COMMAND_RELOAD])) {
                needAdmin(senderPlayer);
                runCommandReload(senderPlayer);
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

    private void sendProperChatToPlayer(ICommandSender player, String incomingMessage, Object... params) {
        IChatComponent message;
        if(minerServer.playerHasClient(player.getCommandSenderEntity().getPersistentID())) {
            message = new ChatComponentTranslation(incomingMessage, params);
        }
        else {
            String rawMessage = StatCollector.translateToLocal(incomingMessage);
            message = new ChatComponentText(String.format(rawMessage, params));
        }
        player.addChatMessage(message);
    }

    private void showUsageError(String errorKey) throws WrongUsageException {
        String message = StatCollector.translateToLocal(errorKey);
        throw new WrongUsageException(message);
    }

    private void showUsageError(String errorKey, Object... params) throws WrongUsageException {
        String message = StatCollector.translateToLocalFormatted(errorKey, params);
        throw new WrongUsageException(message);
    }

    private void needAdmin(ICustomCommandSender sender) throws CommandException {
        if(sender instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP) sender;
            MinecraftServer server = player.mcServer;
            if (server.isDedicatedServer() && !server.getConfigurationManager().canSendCommands(player.getGameProfile())) {
                boolean playerNoClient = !minerServer.playerHasClient(player.getUniqueID());
                String message = "command.veinminer.permissionDenied";
                if (playerNoClient) {
                    message = LanguageRegistry.instance().getStringLocalization(message);
                }
                throw new CommandException(message);
            }
        }
    }

    private void commandAction(String[] commandString, String commandName) throws WrongUsageException {
        if (commandString.length < 3 || (!"add".equals(commandString[2]) && !"remove".equals(commandString[2]))) {
            showUsageError("command.veinminer." + commandName + ".actionerror", commandString[1]);
        }
    }

    private void runCommandMode(ICustomCommandSender sender, String[] astring) throws CommandException {
        if(sender instanceof CommandSenderPlayer) {
            CommandSenderPlayer senderPlayer = ((CommandSenderPlayer) sender);
            UUID player = senderPlayer.getPlayer().getPersistentID();

            if(astring.length == 1) {
                showUsageError("command.veinminer.enable");
            }
            else if(astring[1].equals(modes[0])) {
                minerServer.setPlayerStatus(player, PlayerStatus.INACTIVE);
                senderPlayer.sendProperChat("command.veinminer.set.auto");
            }
            else if(astring[1].equals(modes[1])) {
                minerServer.setPlayerStatus(player, PlayerStatus.SNEAK_ACTIVE);
                senderPlayer.sendProperChat("command.veinminer.set.sneak");
            }
            else if(astring[1].equals(modes[2])) {
                minerServer.setPlayerStatus(player, PlayerStatus.SNEAK_INACTIVE);
                senderPlayer.sendProperChat("command.veinminer.set.nosneak");
            }
        }
        else if(sender instanceof CommandSenderServer) {
            String message = sender.localise("command.veinminer.permissionDenied");
            throw new CommandException(message);
        }
    }

    private void runCommandBlocklist(ICustomCommandSender senderPlayer, String[] astring) throws WrongUsageException {
        ConfigurationSettings configSettings = minerServer.getConfigurationSettings();

        ConfigurationSettings settings = minerServer.getConfigurationSettings();
        Set<String> toolsSet = settings.getToolTypeNames();
        String toolsSlashed = Joiner.on("/").join(toolsSet);

        if(astring.length == 1) {
            showUsageError("command.veinminer.blocklist", toolsSlashed);
        }

        String tool;
        if(toolsSet.contains(astring[1])) {
            tool = astring[1];
        }
        else {
            showUsageError("command.veinminer.blocklist", toolsSlashed);
            return;
        }

        String toolString = settings.getToolTypeName(tool);

        commandAction(astring, "blockList");
        String action = astring[2];

        if(astring.length < 4) {
            showUsageError("command.veinminer.blocklist.itemerror", toolString, action);
        }

        int metadata = -1;
        if(astring.length >= 5) {
            try {
                metadata = Integer.parseInt(astring[4]);
            }
            catch (NumberFormatException ignored) {}
        }

        BlockID blockID = new BlockID(astring[3], metadata);
        if(blockID.name.isEmpty()) {
            // String is not in proper format
            showUsageError("command.veinminer.blocklist.itemerror", toolString, action);
        }

        if("add".equals(action)) {
            configSettings.addBlockToWhitelist(tool, blockID);
            senderPlayer.sendProperChat("command.veinminer.blocklist.add", blockID.name, blockID.metadata, toolString);
        }
        else if("remove".equals(action)) {
            configSettings.removeBlockFromWhitelist(tool, blockID);
            senderPlayer.sendProperChat("command.veinminer.blocklist.remove", blockID.name, blockID.metadata, toolString);
        }
    }

    private void runCommandToollist(ICustomCommandSender senderPlayer, String[] astring) throws WrongUsageException {
        ConfigurationSettings configSettings = minerServer.getConfigurationSettings();

        ConfigurationSettings settings = minerServer.getConfigurationSettings();
        Set<String> toolsSet = settings.getToolTypeNames();
        String toolsSlashed = Joiner.on("/").join(toolsSet);

        if(astring.length == 1) {
            showUsageError("command.veinminer.toollist", toolsSlashed);
        }

        String tool;
        if(toolsSet.contains(astring[1])) {
            tool = astring[1];
        }
        else {
            showUsageError("command.veinminer.toollist", toolsSlashed);
            return;
        }

        String toolString = settings.getToolTypeName(tool);

        commandAction(astring, "toollist");
        String action = astring[2];

        if(astring.length < 4) {
            showUsageError("command.veinminer.toollist.itemerror", toolString, action);
        }

        String toolId = astring[3];

        if(toolId.isEmpty()) {
            // String is not in proper format
            showUsageError("command.veinminer.toollist.itemerror", toolString, action);
        }

        if("add".equals(action)) {
            configSettings.addTool(tool, toolId);
            senderPlayer.sendProperChat("command.veinminer.toollist.add", toolId, toolString);
        }
        else if("remove".equals(action)) {
            configSettings.removeTool(tool, toolId);
            senderPlayer.sendProperChat("command.veinminer.toollist.remove", toolId, toolString);
        }
    }

    private void runCommandBlocklimit(ICustomCommandSender senderPlayer, String[] astring) throws WrongUsageException {
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

        minerServer.getConfigurationSettings().setBlockLimit(newBlockPerTick);

        int actualBlockPerTick = minerServer.getConfigurationSettings().getBlockLimit();
        senderPlayer.sendProperChat("command.veinminer.blocklimit.set", actualBlockPerTick);
    }

    private void runCommandRange(ICustomCommandSender senderPlayer, String[] astring) throws WrongUsageException {
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

        minerServer.getConfigurationSettings().setRadiusLimit(newRange);

        int actualRange = minerServer.getConfigurationSettings().getRadiusLimit();
        senderPlayer.sendProperChat("command.veinminer.range.set", actualRange);
    }

    private void runCommandPerTick(ICustomCommandSender senderPlayer, String[] astring) throws WrongUsageException {
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

        minerServer.getConfigurationSettings().setBlocksPerTick(newRate);

        int actualRate = minerServer.getConfigurationSettings().getBlocksPerTick();
        senderPlayer.sendProperChat("command.veinminer.pertick.set", actualRate);
    }

    private void runCommandSave(ICustomCommandSender senderPlayer) {
        minerServer.getConfigurationSettings().saveConfigs();
        senderPlayer.sendProperChat("command.veinminer.saveconfig");
    }

    private void runCommandReload(ICustomCommandSender senderPlayer) {
        minerServer.getConfigurationSettings().reloadConfigFile();
        senderPlayer.sendProperChat("command.veinminer.loadconfig");
    }

    private void runCommandHelp(ICustomCommandSender senderPlayer, String[] astring) {
        if(astring.length > 1) {
            if(astring[1].equals(commands[COMMAND_MODE])) {
                senderPlayer.sendProperChat("command.veinminer.help.enable1");
                senderPlayer.sendProperChat("command.veinminer.help.enable2");
                senderPlayer.sendProperChat("command.veinminer.help.enable3");
                senderPlayer.sendProperChat("command.veinminer.help.enable4");
                //sendProperChatToPlayer(senderPlayer, "command.veinminer.help.enable5");
            }
        }
        else {
            senderPlayer.sendProperChat("command.veinminer.help1");
            senderPlayer.sendProperChat("command.veinminer.help2");
            senderPlayer.sendProperChat("command.veinminer.help3");
            senderPlayer.sendProperChat("command.veinminer.help4");
            senderPlayer.sendProperChat("command.veinminer.help5");
            senderPlayer.sendProperChat("command.veinminer.help6");
            senderPlayer.sendProperChat("command.veinminer.help7");
            senderPlayer.sendProperChat("command.veinminer.help8");
            senderPlayer.sendProperChat("command.veinminer.help9");
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] arguments) {
        switch (arguments.length) {
            case 1:
                return getListOfStringsMatchingLastWord(arguments, commands);
            case 2:
                if(arguments[0].equals(commands[COMMAND_MODE])) {
                    return getListOfStringsMatchingLastWord(arguments, modes);
                }
                else if(arguments[0].equals(commands[COMMAND_BLOCKLIST]) || arguments[0].equals(commands[COMMAND_TOOLLIST])) {
                    Set<String> toolsSet = minerServer.getConfigurationSettings().getToolTypeNames();
                    String[] tools = new String[] {};
                    tools = toolsSet.toArray(tools);
                    Arrays.sort(tools);

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
        return StatCollector.translateToLocal("command.veinminer");
    }

    @SuppressWarnings("UnusedDeclaration")
    public int compareTo(MinerCommand par1ICommand)
    {
        return this.getCommandName().compareTo(par1ICommand.getCommandName());
    }

    @Override
    public int compareTo(Object par1Obj)
    {
        return this.compareTo((ICommand)par1Obj);
    }
}
