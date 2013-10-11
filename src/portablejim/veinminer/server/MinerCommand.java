package portablejim.veinminer.server;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 8/10/13
 * Time: 1:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class MinerCommand extends CommandBase {
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
                else if(astring[1].equals("disable")) {
                    minerServer.setPlayerStatus(player, PlayerStatus.DISABLED);
                    icommandsender.sendChatToPlayer(LanguageRegistry.instance().getStringLocalization("command.veinminer.set.disable"));
                }
                else if(astring[1].equals("auto")) {
                    if(minerServer.playerHasClient(player)) {
                        minerServer.setPlayerStatus(player, PlayerStatus.INACTIVE);
                    }
                    else {
                        minerServer.setPlayerStatus(player, PlayerStatus.DISABLED);
                    }
                    icommandsender.sendChatToPlayer(LanguageRegistry.instance().getStringLocalization("command.veinminer.set.auto"));
                }
                else if(astring[1].equals("sneak")) {
                    minerServer.setPlayerStatus(player, PlayerStatus.SNEAK_ACTIVE);
                    icommandsender.sendChatToPlayer(LanguageRegistry.instance().getStringLocalization("command.veinminer.set.sneak"));
                }
                else if(astring[1].equals("no_sneak")) {
                    minerServer.setPlayerStatus(player, PlayerStatus.SNEAK_INACTIVE);
                    icommandsender.sendChatToPlayer(LanguageRegistry.instance().getStringLocalization("command.veinminer.set.nosneak"));
                }
            }
            else if(astring[0].equals("help")) {

            }
        }
        else
        {
            throw new WrongUsageException("command.veinminer", new Object[0]);
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String getCommandUsage(ICommandSender par1ICommandSender) {
        return par1ICommandSender.translateString("command.veinminer", new Object[0]);
    }
}
