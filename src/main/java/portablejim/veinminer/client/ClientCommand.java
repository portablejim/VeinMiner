package portablejim.veinminer.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.StatCollector;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.network.PacketChangeMode;
import portablejim.veinminer.server.CommandSenderPlayer;
import portablejim.veinminer.util.PlayerStatus;
import portablejim.veinminer.util.PreferredMode;

import java.util.List;
import java.util.UUID;

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
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender) {
        return true;
    }

    @Override
    public String getCommandUsage(ICommandSender p_71518_1_) {
        return "/veinminerc disabled/pressed/released/sneak/no_sneak";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] astring) {
        if(sender instanceof EntityClientPlayerMP) {
            EntityClientPlayerMP senderPlayer = (EntityClientPlayerMP) sender;
            UUID player = senderPlayer.getPersistentID();
            PacketChangeMode p = null;

            if(astring.length < 1) {
                throw new WrongUsageException("/veinminerc disabled/pressed/released/sneak/no_sneak");
            }
            else if(astring[0].equals(modes[0])) {
                p = new PacketChangeMode(PreferredMode.DISABLED);
                VeinMiner.instance.currentMode = PreferredMode.DISABLED;
                senderPlayer.addChatMessage(new ChatComponentTranslation("command.veinminerc.set.disabled"));
            }
            else if(astring[0].equals(modes[1])) {
                p = new PacketChangeMode(PreferredMode.PRESSED);
                VeinMiner.instance.currentMode = PreferredMode.PRESSED;
                senderPlayer.addChatMessage(new ChatComponentTranslation("command.veinminerc.set.pressed"));
            }
            else if(astring[0].equals(modes[2])) {
                p = new PacketChangeMode(PreferredMode.RELEASED);
                VeinMiner.instance.currentMode = PreferredMode.RELEASED;
                senderPlayer.addChatMessage(new ChatComponentTranslation("command.veinminerc.set.released"));
            }
            else if(astring[0].equals(modes[3])) {
                p = new PacketChangeMode(PreferredMode.SNEAK_ACTIVE);
                VeinMiner.instance.currentMode = PreferredMode.SNEAK_ACTIVE;
                senderPlayer.addChatMessage(new ChatComponentTranslation("command.veinminerc.set.disabled"));
            }
            else if(astring[0].equals(modes[4])) {
                p = new PacketChangeMode(PreferredMode.SNEAK_INACTIVE);
                VeinMiner.instance.currentMode = PreferredMode.SNEAK_INACTIVE;
                senderPlayer.addChatMessage(new ChatComponentTranslation("command.veinminerc.set.disabled"));
            }
            else {
                throw new WrongUsageException("/veinminerc disabled/pressed/released/sneak/no_sneak");
            }

            if(p != null) {
                VeinMiner.instance.networkWrapper.sendToServer(p);
            }
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] arguments) {
        return getListOfStringsMatchingLastWord(arguments, modes);
    }

    public int compareTo(ClientCommand par1ICommand)
    {
        return this.getCommandName().compareTo(par1ICommand.getCommandName());
    }

    @Override
    public int compareTo(Object par1Obj)
    {
        return this.compareTo((ICommand)par1Obj);
    }
}
