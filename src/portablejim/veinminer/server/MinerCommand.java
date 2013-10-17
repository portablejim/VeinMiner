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
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.util.BlockID;

import java.util.List;

import static portablejim.veinminer.configuration.ConfigurationSettings.ToolType;

/**
 * Command so that clients can control VeinMiner settings for their player.
 */

public class MinerCommand extends CommandBase {
    private static final String[] commands = new String[]{"mode", "blocklist", "toollist", "help"};
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
            throw new WrongUsageException("command.veinminer." + commandName);
        }

        ToolType tool;
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
            throw new WrongUsageException("command.veinminer." + commandName);
        }

        return tool;
    }

    private void commandAction(String[] commandString, String commandName) {
        if(commandString.length >= 3 && ("add".equals(commandString[2]) || "remove".equals(commandString[2]))) {
        }
        else {
            throw new WrongUsageException("command.veinminer." + commandName + ".actionerror", new Object[]{ commandString[1] });
        }
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        EntityPlayerMP senderPlayer;
        if(icommandsender instanceof EntityPlayerMP) {
            senderPlayer = (EntityPlayerMP) icommandsender;
        }
        else {
            throw new CommandException("Non-players cannot use veinminer commands", icommandsender);
        }

        if(astring.length > 0) {
            String player = icommandsender.getCommandSenderName();
            MinerServer minerServer = MinerServer.instance;
            if(astring[0].equals(commands[0])) {
                if(astring.length == 1) {
                    throw new WrongUsageException("command.veinminer.enable");
                }
                else if(astring[1].equals(modes[0])) {
                    minerServer.setPlayerStatus(player, PlayerStatus.DISABLED);
                    senderPlayer.addChatMessage("command.veinminer.set.disable");
                }
                else if(astring[1].equals(modes[1])) {
                    if(minerServer.playerHasClient(player)) {
                        minerServer.setPlayerStatus(player, PlayerStatus.INACTIVE);
                    }
                    else {
                        minerServer.setPlayerStatus(player, PlayerStatus.DISABLED);
                    }
                    senderPlayer.addChatMessage("command.veinminer.set.auto");
                }
                else if(astring[1].equals(modes[2])) {
                    minerServer.setPlayerStatus(player, PlayerStatus.SNEAK_ACTIVE);
                    senderPlayer.addChatMessage("command.veinminer.set.sneak");
                }
                else if(astring[1].equals(modes[3])) {
                    minerServer.setPlayerStatus(player, PlayerStatus.SNEAK_INACTIVE);
                    senderPlayer.addChatMessage("command.veinminer.set.nosneak");
                }
            }
            else if(astring[0].equals(commands[1])) {
                ToolType tool = commandTool(astring, "blocklist");
                String toolString = astring[1];

                commandAction(astring, "blockList");
                String action = astring[2];

                if(astring.length < 4) {
                    throw new WrongUsageException("command.veinminer.blocklist.itemerror", new Object[]{ toolString, action });
                }

                BlockID blockID = new BlockID(astring[3], ":", -1);
                if(blockID.id <= 0) {
                    // String is not in proper format
                    throw new WrongUsageException("command.veinminer.blocklist.itemerror", new Object[]{ toolString, action });
                }

                if("add".equals(action)) {
                    MinerServer.instance.getConfigurationSettings().addBlockToWhitelist(tool, blockID);
                    String message = LanguageRegistry.instance().getStringLocalization("command.veinminer.blocklist.add");
                    senderPlayer.addChatMessage(String.format(message, blockID.id, blockID.metadata, toolString));
                }
                else if("remove".equals(action)) {
                    MinerServer.instance.getConfigurationSettings().removeBlockFromWhitelist(tool, blockID);
                    String message = LanguageRegistry.instance().getStringLocalization("command.veinminer.blocklist.remove");
                    senderPlayer.addChatMessage(String.format(message, blockID.id, blockID.metadata, toolString));
                }
            }
            else if(astring[0].equals(commands[2])) {
                ToolType tool = commandTool(astring, "toollist");
                String toolString = astring[1];

                commandAction(astring, "toollist");
                String action = astring[2];

                if(astring.length < 4) {
                    throw new WrongUsageException("command.veinminer.toollist.itemerror", new Object[]{ toolString, action });
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
                    throw new WrongUsageException("command.veinminer.toollist.itemerror", new Object[]{ toolString, action });
                }

                if("add".equals(action)) {
                    MinerServer.instance.getConfigurationSettings().addTool(tool, toolId);
                    String message = LanguageRegistry.instance().getStringLocalization("command.veinminer.toollist.add");
                    senderPlayer.addChatMessage(String.format(message, toolId, toolString));
                }
                else if("remove".equals(action)) {
                    MinerServer.instance.getConfigurationSettings().removeTool(tool, toolId);
                    String message = LanguageRegistry.instance().getStringLocalization("command.veinminer.toollist.remove");
                    senderPlayer.addChatMessage(String.format(message, toolId, toolString));
                }
            }
            else if(astring[0].equals(commands[3])) {
                if(astring.length > 1) {
                    if(astring[1].equals(commands[0])) {
                        senderPlayer.addChatMessage("command.veinminer.help.enable");
                    }
                }
                else {
                    senderPlayer.addChatMessage("command.veinminer.help");
                }
            }
        }
        else
        {
            throw new WrongUsageException("command.veinminer");
        }
        // change body of implemented methods use File | Settings | File Templates.
    }

    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] arguments) {
        switch (arguments.length) {
            case 1:
                return getListOfStringsMatchingLastWord(arguments, commands);
            case 2:
                if(arguments[0].equals(commands[0])) {
                    return getListOfStringsMatchingLastWord(arguments, modes);
                }
                else if(arguments[0].equals(commands[1]) || arguments[0].equals(commands[2])) {
                    String[] tools = { "pickaxe", "axe", "shovel" };

                    return getListOfStringsMatchingLastWord(arguments, tools);
                }
                else if(arguments[0].equals(commands[2])) {
                    return getListOfStringsMatchingLastWord(arguments, commands);
                }
            case 3:
                if(arguments[0].equals(commands[1]) || arguments[0].equals(commands[2])) {
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
