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

package portablejim.veinminer.configuration.client;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.configuration.ConfigurationSettings;
import portablejim.veinminer.configuration.client.elements.GuiElementSliderLog;
import portablejim.veinminer.configuration.client.elements.GuiElementSlotToolTypeList;
import portablejim.veinminer.configuration.client.elements.IGuiElementValuePersist;
import portablejim.veinminer.util.PreferredMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Screen to configure non-whitelist settings and provide links to edit
 * tool/item whitelists
 */

public class ConfigGuiScreen extends GuiScreen implements IGuiElementValuePersist {
    private GuiScreen parent;

    private ConfigurationSettings settings = VeinMiner.instance.configurationSettings;
    private String[] currentModeStrings = { "disabled", "pressed", "released", "sneak", "nosneak" };

    GuiElementSlotToolTypeList toolTypeList;

    private ArrayList<ToolDisplay> toolIds;

    public ConfigGuiScreen(GuiScreen parent) {
        this.parent = parent;

        toolIds = new ArrayList<ToolDisplay>();
        for(String toolType : settings.getToolTypeNames()) {
            toolIds.add(new ToolDisplay(toolType, settings.getToolTypeIcon(toolType), settings.getToolTypeName(toolType)));
        }
        Collections.sort(toolIds, new Comparator<ToolDisplay>() {
            @Override
            public int compare(ToolDisplay toolDisplay, ToolDisplay toolDisplay2) {
                if(toolDisplay.name == null) return -1;
                if(toolDisplay2.name == null) return 1;

                String s1 = toolDisplay.name.toLowerCase();
                String s2 = toolDisplay2.name.toLowerCase();
                return s1.compareTo(s2);
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui()
    {
        this.buttonList.clear();

        this.buttonList.add(new GuiButton(1, this.width / 2 - 75, this.height - 38, I18n.format("gui.done")));

        //labelList.add(new GuiLabel());

        toolTypeList = new GuiElementSlotToolTypeList(this);

        int topOffset = 32;

        this.buttonList.add(new GuiButton(2, this.width / 2 + 2, topOffset, 150, 20, I18n.format(String.format("gui.veinminer.config.%s", currentModeStrings[settings.getPreferredMode()]))));
        //optionRange.setValueMax(100F);
        this.buttonList.add(new GuiElementSliderLog(3, this.width / 2 - 152, topOffset + 24, this, 1F, 1000F, 4));
        this.buttonList.add(new GuiElementSliderLog(4, this.width / 2 + 2, topOffset + 24, this, 1F, (2 << 16) + 1, 3));

        this.buttonList.add(new GuiButton(5, this.width / 2 + 2, 100, 150, 20, I18n.format("gui.veinminer.config.toollist")));
        this.buttonList.add(new GuiButton(6, this.width / 2 + 2, 122, 150, 20, I18n.format("gui.veinminer.config.blocklist")));
        //this.buttonList.add(new GuiButton(5, this.width / 2 - 152, this.height - 38 - 3 * 22 - 6, 57, 20, I18n.format("gui.veinminer.config.list.axe")));
        /*this.buttonList.add(new GuiButton(6, this.width / 2 - 152, this.height - 38 - 22 - 6, 57, 20, I18n.format("gui.veinminer.config.list.axe")));
        this.buttonList.add(new GuiButton(7, this.width / 2 - 152 + 62 - 1, this.height - 38 - 3 * 22 - 6, 57, 20, I18n.format("gui.veinminer.config.list.hoe")));
        this.buttonList.add(new GuiButton(8, this.width / 2 - 152 + 62 - 1, this.height - 38 - 22 - 6, 57, 20, I18n.format("gui.veinminer.config.list.hoe")));
        this.buttonList.add(new GuiButton(9, this.width / 2 - 152 + 2 * 62 - 1, this.height - 38 - 3 * 22 - 6, 57, 20, I18n.format("gui.veinminer.config.list.pickaxe")));
        this.buttonList.add(new GuiButton(10, this.width / 2 - 152 + 2 * 62 - 1, this.height - 38 - 22 - 6, 57, 20, I18n.format("gui.veinminer.config.list.pickaxe")));
        this.buttonList.add(new GuiButton(11, this.width / 2 - 152 + 3 * 62 - 1, this.height - 38 - 3 * 22 - 6, 57, 20, I18n.format("gui.veinminer.config.list.shears")));
        this.buttonList.add(new GuiButton(12, this.width / 2 - 152 + 3 * 62 - 1, this.height - 38 - 22 - 6, 57, 20, I18n.format("gui.veinminer.config.list.shears")));
        this.buttonList.add(new GuiButton(13, this.width / 2 - 152 + 4 * 62 - 1, this.height - 38 - 3 * 22 - 6, 57, 20, I18n.format("gui.veinminer.config.list.shovel")));
        this.buttonList.add(new GuiButton(14, this.width / 2 - 152 + 4 * 62 - 1, this.height - 38 - 22 - 6, 57, 20, I18n.format("gui.veinminer.config.list.shovel")));*/
    }

    @Override
    protected void actionPerformed(GuiButton par1GuiButton)
    {
        ItemlistConfigGuiScreen newScreen;
        ToolDisplay toolDisplay;
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
                case 5:
                    toolDisplay = toolIds.get(toolTypeList.selectedItem);
                    newScreen = new ItemlistConfigGuiScreen(this, toolDisplay.id, 1);
                    mc.displayGuiScreen(newScreen);
                    break;
                case 6:
                    toolDisplay = toolIds.get(toolTypeList.selectedItem);
                    newScreen = new ItemlistConfigGuiScreen(this, toolDisplay.id, 0);
                    mc.displayGuiScreen(newScreen);
                    break;
            }
        }
    }

    public void updateScreen() {
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();
        toolTypeList.drawScreen(par1, par2, par3);
        this.drawCenteredString(this.fontRendererObj, I18n.format("gui.veinminer.config"), this.width / 2, 15, 0xFFFFFF);
        this.drawString(this.fontRendererObj, I18n.format("gui.veinminer.config.enable.text"), this.width / 2 - 95, 38, 0xFFFFFF);
        this.drawCenteredString(this.fontRendererObj, I18n.format("gui.veinminer.config.toolTypes"), this.width / 2, 86, 0xFFFFFF);
        //this.drawCenteredString(this.fontRendererObj, I18n.format("gui.veinminer.config.toollist"), this.width / 2, this.height - 82, 0xFFFFFF);
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
                break;
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

    public double getZLevel() {
        return zLevel;
    }

    public FontRenderer getFontRenderer() {
        return fontRendererObj;
    }

    public ArrayList<ToolDisplay> getList() {
        return toolIds;
    }
}
