package portablejim.veinminer.configuration.client;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiOptionSlider;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraftforge.client.EnumHelperClient;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.configuration.client.elements.GuiElementSlider;
import portablejim.veinminer.configuration.client.elements.GuiElementSliderLog;
import portablejim.veinminer.configuration.client.elements.IGuiElementValuePersist;
import portablejim.veinminer.util.PreferredMode;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 21/02/14
 * Time: 10:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConfigGuiScreen extends GuiScreen implements IGuiElementValuePersist {
    private GuiScreen parent;

    private ConfigurationSettings settings = VeinMiner.instance.configurationSettings;
    private String[] currentModeStrings = { "disabled", "pressed", "released" };
    private GuiButton currentModeButton;

    //private Options optionRange = EnumHelperClient.addOptions("veinminer.blocklimit", "gui.veinminer.config.blocklimit", false, false);

    private boolean needsUpdate = true;

    public ConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        this.buttonList.clear();

        this.buttonList.add(new GuiButton(1, this.width / 2 - 75, this.height - 38, I18n.format("gui.done")));

        //labelList.add(new GuiLabel());

        int topOffset = 32;

        this.buttonList.add(new GuiButton(2, this.width / 2 + 2, topOffset, 150, 20, I18n.format(String.format("gui.veinminer.config.%s", currentModeStrings[settings.getPreferredMode()]))));
        //optionRange.setValueMax(100F);
        this.buttonList.add(new GuiElementSliderLog(3, this.width / 2 - 152, topOffset + 1 * 24, this, 1F, 1000F, 4));
        this.buttonList.add(new GuiElementSliderLog(4, this.width / 2 + 2, topOffset + 1 * 24, this, 1F, (2 << 16) + 1, 3));

        this.buttonList.add(new GuiButton(5, this.width / 2 - 152 + 0 * 60, this.height - 38 - 1 * 22 - 6, 56, 20, I18n.format("gui.veinminer.config.list.axe")));
        this.buttonList.add(new GuiButton(6, this.width / 2 - 152 + 0 * 60, this.height - 38 - 3 * 22 - 6, 56, 20, I18n.format("gui.veinminer.config.list.axe")));
        this.buttonList.add(new GuiButton(7, this.width / 2 - 152 + 1 * 60, this.height - 38 - 1 * 22 - 6, 56, 20, I18n.format("gui.veinminer.config.list.hoe")));
        this.buttonList.add(new GuiButton(8, this.width / 2 - 152 + 1 * 60, this.height - 38 - 3 * 22 - 6, 56, 20, I18n.format("gui.veinminer.config.list.hoe")));
        this.buttonList.add(new GuiButton(9, this.width / 2 - 152 + 2 * 60, this.height - 38 - 1 * 22 - 6, 56, 20, I18n.format("gui.veinminer.config.list.pickaxe")));
        this.buttonList.add(new GuiButton(10, this.width / 2 - 152 + 2 * 60, this.height - 38 - 3 * 22 - 6, 56, 20, I18n.format("gui.veinminer.config.list.pickaxe")));
        this.buttonList.add(new GuiButton(11, this.width / 2 - 152 + 3 * 60, this.height - 38 - 1 * 22 - 6, 56, 20, I18n.format("gui.veinminer.config.list.shears")));
        this.buttonList.add(new GuiButton(12, this.width / 2 - 152 + 3 * 60, this.height - 38 - 3 * 22 - 6, 56, 20, I18n.format("gui.veinminer.config.list.shears")));
        this.buttonList.add(new GuiButton(13, this.width / 2 - 152 + 4 * 60, this.height - 38 - 1 * 22 - 6, 56, 20, I18n.format("gui.veinminer.config.list.shovel")));
        this.buttonList.add(new GuiButton(14, this.width / 2 - 152 + 4 * 60, this.height - 38 - 3 * 22 - 6, 56, 20, I18n.format("gui.veinminer.config.list.shovel")));
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            switch (par1GuiButton.id) {
                case 1:
                    FMLClientHandler.instance().showGuiScreen(parent);
                    break;
                case 2:
                    settings.setPreferredMode((settings.getPreferredMode() + 1) % PreferredMode.length());
                    settings.saveConfigs();
                    par1GuiButton.displayString = I18n.format(String.format("gui.veinminer.config.%s", currentModeStrings[settings.getPreferredMode()]));
                    break;
                case 3:


            }
        }
    }

    public void updateScreen() {
        needsUpdate = true;
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("gui.veinminer.config"), this.width / 2, 15, 0xFFFFFF);
        this.drawString(this.fontRendererObj, I18n.format("gui.veinminer.config.enable.text"), this.width/2 - 95, 38, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, I18n.format("gui.veinminer.config.blocklist"), this.width / 2, this.height - 126, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, I18n.format("gui.veinminer.config.toollist"), this.width / 2, this.height - 82, 0xFFFFFF);
        super.drawScreen(par1, par2, par3);
    }

    @Override
    public void saveGuiElementValue(GuiButton element, Object value) {
        float floatValue;
        int intValue;
        switch (element.id) {
            case 3:
                floatValue = (Float)value;
                settings.setRadiusLimit((int)floatValue);
                settings.saveConfigs();
            case 4:
                floatValue = (Float)value;
                intValue = Math.round(floatValue);
                if(intValue == (2 << 16) + 1) {
                    settings.setBlockLimit(-1);
                }
                else {
                    settings.setBlockLimit((int)floatValue);
                }
                settings.saveConfigs();
        }
    }

    @Override
    public Object getGuiElementValue(GuiButton element) {
        switch (element.id) {
            case 3:
                return (float)settings.getRadiusLimit();
            case 4:
                int blockLimit = settings.getBlockLimit();
                return blockLimit == -1 ? (float)(2 << 16) + 1 : (float)settings.getBlockLimit();
            default:
                return null;
        }
    }

    @Override
    public String getGuiElementDisplayString(GuiButton element, Object value) {
        float floatValue;
        switch (element.id) {
            case 3:
                floatValue = (Float)value;
                return I18n.format("gui.veinminer.config.range", (int)floatValue);
            case 4:
                floatValue = (Float)value;
                if(floatValue == (2 << 16) + 1) {
                    return I18n.format("gui.veinminer.config.blocklimit.infinite");
                }
                else {
                    return I18n.format("gui.veinminer.config.blocklimit", (int)floatValue);
                }
            default:
                return "";
        }
    }
}
