package portablejim.veinminer.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.network.packet.PacketClientPresent;
import portablejim.veinminer.network.packet.PacketMinerActivate;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 19/02/14
 * Time: 2:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class ActivateMinerKeybindManager {
    public KeyBinding keyBinding = new KeyBinding("veinminer.enable", Keyboard.KEY_GRAVE, "veinminer.key.category");
    private static boolean statusEnabled = false;

    public ActivateMinerKeybindManager() {
        ClientRegistry.registerKeyBinding(keyBinding);
        FMLCommonHandler.instance().bus().register(this);
    }

    @SubscribeEvent
    public void KeyEvent(InputEvent.KeyInputEvent event) {
        boolean sendPacket = false;
        if (keyBinding.getIsKeyPressed() && !statusEnabled) {
            statusEnabled = true;
            sendPacket = true;
        } else if (!keyBinding.getIsKeyPressed() && statusEnabled) {
            statusEnabled = false;
            sendPacket = true;
        }
        if (sendPacket) {
            PacketMinerActivate packet = new PacketMinerActivate(statusEnabled);
            VeinMiner.instance.channelHandler.sendToServer(packet);
        }
    }
}
