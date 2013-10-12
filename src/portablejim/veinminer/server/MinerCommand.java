package portablejim.veinminer.server;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 8/10/13
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class MinerCommand extends CommandBase {
    private static final String[] commands = new String[]{"enable", "help"};
    private static final String[] modes = new String[] {"disable", "auto", "sneak", "nosneak"};

    @Override
    public String getCommandName() {
        return "veinminer";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public void processCommand(ICommandSender icommandsender, String[] astring) {
        if(astring.length > 0) {
            String player = icommandsender.getCommandSenderName();
            MinerServer minerServer = MinerServer.instance;
            if(astring[0].equals("enable")) {
                if(astring.length == 1) {
                    throw new WrongUsageException("command.veinminer.enable", new Object[0]);
                }
                else if(astring[1].equals(modes[0])) {
                    minerServer.setPlayerStatus(player, PlayerStatus.DISABLED);
                    icommandsender.sendChatToPlayer(LanguageRegistry.instance().getStringLocalization("command.veinminer.set.disable"));
                }
                else if(astring[1].equals(modes[1])) {
                    if(minerServer.playerHasClient(player)) {
                        minerServer.setPlayerStatus(player, PlayerStatus.INACTIVE);
                    }
                    else {
                        minerServer.setPlayerStatus(player, PlayerStatus.DISABLED);
                    }
                    icommandsender.sendChatToPlayer(LanguageRegistry.instance().getStringLocalization("command.veinminer.set.auto"));
                }
                else if(astring[1].equals(modes[2])) {
                    minerServer.setPlayerStatus(player, PlayerStatus.SNEAK_ACTIVE);
                    icommandsender.sendChatToPlayer(LanguageRegistry.instance().getStringLocalization("command.veinminer.set.sneak"));
                }
                else if(astring[1].equals(modes[3])) {
                    minerServer.setPlayerStatus(player, PlayerStatus.SNEAK_INACTIVE);
                    icommandsender.sendChatToPlayer(LanguageRegistry.instance().getStringLocalization("command.veinminer.set.nosneak"));
                }
            }
            else if(astring[0].equals(commands[1])) {
                if(astring.length > 1) {
                    if(astring[1].equals(commands[0])) {
                        icommandsender.sendChatToPlayer(LanguageRegistry.instance().getStringLocalization("command.veinminer.help.enable"));
                    }
                }
                else {
                    icommandsender.sendChatToPlayer(LanguageRegistry.instance().getStringLocalization("command.veinminer.help"));
                }
            }
        }
        else
        {
            throw new WrongUsageException("command.veinminer", new Object[0]);
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] arguments) {
        switch (arguments.length) {
            case 1:
                return getListOfStringsMatchingLastWord(arguments, commands);
            case 2:
                if(arguments[1].equals(commands[0])) {
                    return getListOfStringsMatchingLastWord(arguments, modes);
                }
                else if(arguments[1].equals(commands[1])) {
                    return getListOfStringsMatchingLastWord(arguments, commands);
                }
        }
        return null;
    }

    @Override
    public String getCommandUsage(ICommandSender par1ICommandSender) {
        return par1ICommandSender.translateString("command.veinminer", new Object[0]);
    }
}
