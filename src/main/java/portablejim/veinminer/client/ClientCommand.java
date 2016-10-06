package portablejim.veinminer.client;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.network.PacketChangeMode;
import portablejim.veinminer.util.PreferredMode;

import java.util.List;

/**
 * Created by james on 16/06/16.
 */
public class ClientCommand extends CommandBase {
    private static final String[] modes = new String[] {"disabled", "pressed", "released", "sneak", "no_sneak"};

    @Override
    public String getCommandName() {
        return "veinminerc";
    }

    @Override
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "/veinminerc disabled/pressed/released/sneak/no_sneak";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] astring) throws WrongUsageException {
        if(sender instanceof EntityPlayerSP) {
            EntityPlayerSP senderPlayer = (EntityPlayerSP) sender;
            PacketChangeMode p = null;

            if(astring.length < 1) {
                throw new WrongUsageException("/veinminerc disabled/pressed/released/sneak/no_sneak");
            }
            else if(astring[0].equals(modes[0])) {
                p = new PacketChangeMode(PreferredMode.DISABLED);
                VeinMiner.instance.currentMode = PreferredMode.DISABLED;
                senderPlayer.addChatMessage(new TextComponentTranslation("command.veinminerc.set.disabled"));
            }
            else if(astring[0].equals(modes[1])) {
                p = new PacketChangeMode(PreferredMode.PRESSED);
                VeinMiner.instance.currentMode = PreferredMode.PRESSED;
                senderPlayer.addChatMessage(new TextComponentTranslation("command.veinminerc.set.pressed"));
            }
            else if(astring[0].equals(modes[2])) {
                p = new PacketChangeMode(PreferredMode.RELEASED);
                VeinMiner.instance.currentMode = PreferredMode.RELEASED;
                senderPlayer.addChatMessage(new TextComponentTranslation("command.veinminerc.set.released"));
            }
            else if(astring[0].equals(modes[3])) {
                p = new PacketChangeMode(PreferredMode.SNEAK_ACTIVE);
                VeinMiner.instance.currentMode = PreferredMode.SNEAK_ACTIVE;
                senderPlayer.addChatMessage(new TextComponentTranslation("command.veinminerc.set.disabled"));
            }
            else if(astring[0].equals(modes[4])) {
                p = new PacketChangeMode(PreferredMode.SNEAK_INACTIVE);
                VeinMiner.instance.currentMode = PreferredMode.SNEAK_INACTIVE;
                senderPlayer.addChatMessage(new TextComponentTranslation("command.veinminerc.set.disabled"));
            }
            else {
                throw new WrongUsageException("/veinminerc disabled/pressed/released/sneak/no_sneak");
            }

            if(p != null) {
                VeinMiner.instance.networkWrapper.sendToServer(p);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getTabCompletionOptions(MinecraftServer server, ICommandSender par1ICommandSender, String[] arguments, BlockPos pos) {
        return getListOfStringsMatchingLastWord(arguments, modes);
    }

    public int compareTo(ClientCommand par1ICommand)
    {
        return this.getCommandName().compareTo(par1ICommand.getCommandName());
    }
}
