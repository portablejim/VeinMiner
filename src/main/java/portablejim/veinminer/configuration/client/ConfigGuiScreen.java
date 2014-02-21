package portablejim.veinminer.configuration.client;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 21/02/14
 * Time: 10:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigGuiScreen extends GuiScreen {
    private GuiScreen parent;

    public ConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        this.buttonList.add(new GuiButton(1, this.width / 2 - 75, this.height - 38, I18n.format("gui.done")));
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled && par1GuiButton.id == 1)
        {
            FMLClientHandler.instance().showGuiScreen(parent);
        }
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        //this.drawCenteredString(this.fontRendererObj, "Veinminer test config screen", this.width / 2, 40, 0xFFFFFF);
        super.drawScreen(par1, par2, par3);
    }
}
