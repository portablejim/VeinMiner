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

import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

/**
 * Allow MinerCommand to work with server consoles.
 */
public class CommandSenderServer implements ICustomCommandSender {
    private DedicatedServer server;

    public CommandSenderServer(DedicatedServer server) {
        this.server = server;
    }

    @Override
    public void sendProperChat(String incomingMessage, Object... params) {
        IChatComponent message;
        String rawMessage = StatCollector.translateToLocal(incomingMessage);
        message = new ChatComponentText(String.format(rawMessage, params));
        server.addChatMessage(message);
    }

    @Override
    public boolean canRunCommands() {
        return true;
    }

    @Override
    public String localise(String input) {
        return StatCollector.translateToLocal(input);
    }
}
