package portablejim.veinminer.server;

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
            if(astring[0].equals("enable")) {
                if(astring.length == 1) {
                    throw new WrongUsageException("command.veinminer.enable", new Object[0]);
                }
                else if(astring[1].equals("auto")) {
                    MinerServer.instance.setPlayerStatus(player, PlayerStatus.DISABLED);
                }
                else if(astring[1].equals("shift")) {
                    MinerServer.instance.setPlayerStatus(player, PlayerStatus.SHIFT_ACTIVE);
                }
                else if(astring[1].equals("no_shift")) {
                    MinerServer.instance.setPlayerStatus(player, PlayerStatus.SHIFT_INACTIVE);
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
