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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.LanguageRegistry;

/**
 * Allow MinerCommand to work with Players
 */
public class CommandSenderPlayer implements ICustomCommandSender{
    private MinerServer minerServer = null;
    private EntityPlayerMP player;

    public CommandSenderPlayer(MinerServer minerServerInstace, EntityPlayerMP player) {
        minerServer = minerServerInstace;
        this.player = player;
    }

    public EntityPlayerMP getPlayer() {
        return this.player;
    }

    @Override
    public void sendProperChat(String incomingMessage, Object... params) {
        ITextComponent message;
        if(minerServer.playerHasClient(player.getPersistentID())) {
            message = new TextComponentTranslation(incomingMessage, params);
        }
        else {
            String rawMessage = I18n.translateToLocal(incomingMessage);
            message = new TextComponentString(String.format(rawMessage, params));
        }
        player.addChatMessage(message);
    }

    @Override
    public boolean canRunCommands() {
        return !player.mcServer.isDedicatedServer() || player.canCommandSenderUseCommand(0, "veinminer"); //|| player..mcServer.getConfigurationManager().canSendCommands(player.getGameProfile());
    }

    @Override
    public String localise(String input) {
        if(!minerServer.playerHasClient(player.getPersistentID())) {
            return LanguageRegistry.instance().getStringLocalization(input);
        }
        return input;
    }
}
