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

import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.configuration.client.elements.GuiElementSlotItemlist;
import portablejim.veinminer.lib.IconRenderer;
import portablejim.veinminer.util.BlockID;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Screen to help add and remove items from the item/tool whitelists.
 */

public class ItemlistConfigGuiScreen extends GuiScreen {
    String toolType;
    int mode;
    IconRenderer iconRenderer;
    GuiButton addButton;
    GuiButton clearButton;
    GuiTextField textFieldAdd;
    String textFieldText = "";
    boolean renderIcon = false;

    String titleString;

    GuiElementSlotItemlist itemList;
    private Object parent;

    public ItemlistConfigGuiScreen(GuiScreen parent, String toolType, int mode) {
        this.toolType = toolType;
        this.mode = mode;
        this.parent = parent;

        String[] modeNames = { "blocklist", "toollist" };
        String modeName = modeNames[mode];

        this.titleString = String.format("gui.veinminer.title.%s.%s", modeName, toolType);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        itemList = new GuiElementSlotItemlist(this, toolType);
        this.buttonList.add(new GuiButton(1, this.width / 2 + 2, this.height - 34, 150, 20, I18n.format("gui.veinminer.back")));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 152, this.height - 34, 150, 20, I18n.format("gui.veinminer.item.delete")));
        addButton = new GuiButton(3, this.width / 2 + 52, 34, 48, 20, I18n.format("gui.veinminer.item.add"));
        addButton.enabled = false;
        this.buttonList.add(addButton);
        clearButton = new GuiButton(4, this.width / 2 + 104, 34, 48, 20, I18n.format("gui.veinminer.item.clear"));
        clearButton.enabled = false;
        this.buttonList.add(clearButton);

        iconRenderer = new IconRenderer(mc, zLevel, fontRendererObj, mc.getTextureManager());

        textFieldAdd = new GuiTextField(5, this.getFontRenderer(), this.width / 2 - 128, 34, 176, 20);
        textFieldAdd.setMaxStringLength(128);
        textFieldAdd.setEnabled(true);
        textFieldAdd.setFocused(true);
    }

    public FontRenderer getFontRenderer() {
        return fontRendererObj;
    }

    public float getZLevel() {
        return zLevel;
    }

    public void addButtonCheck() {
        BlockID testBlockId = new BlockID(textFieldText);

        String[] testItemName = testBlockId.name.split(":", 2);
        if(testItemName.length == 2) {
            Block testBlock = Block.getBlockFromName(testBlockId.name);
            Item testItem = GameRegistry.findItem(testItemName[0], testItemName[1]);

            switch(mode) {
                case 0:
                    addButton.enabled = testBlock != null;
                    break;
                case 1:
                    addButton.enabled = testBlock != null || testItem != null;
            }

            renderIcon = testItem != null;
        }
        else {
            addButton.enabled = false;
        }
    }

    protected void actionPerformed(GuiButton par1GuiButton)
    {
        if (par1GuiButton.enabled)
        {
            switch (par1GuiButton.id) {
                case 1:
                    FMLClientHandler.instance().showGuiScreen(this.parent);
                    break;
                case 2:
                {
                    String selectedItem = this.itemList.items.get(this.itemList.selectedItem);
                    switch (mode) {
                        case 0:
                            BlockID targetItem = new BlockID(selectedItem);
                            VeinMiner.instance.configurationSettings.removeBlockFromWhitelist(toolType, targetItem);
                            break;
                        case 1:
                            VeinMiner.instance.configurationSettings.removeTool(toolType, selectedItem);
                    }
                    VeinMiner.instance.configurationSettings.saveConfigs();
                    break;
                }
                case 3:
                {
                    switch (mode) {
                        case 0:
                            BlockID targetItem = new BlockID(this.textFieldText);
                            VeinMiner.instance.configurationSettings.addBlockToWhitelist(toolType, targetItem);
                            break;
                        case 1:
                            VeinMiner.instance.configurationSettings.addTool(toolType, this.textFieldText);
                    }
                    VeinMiner.instance.configurationSettings.saveConfigs();
                    break;
                }
                case 4:
                    this.textFieldAdd.setText("");
                    this.textFieldText = "";
                    addButtonCheck();

            }
            this.itemList.updateItemIds();
        }
    }

    @Override
    protected void keyTyped(char par1, int par2) {
        if(textFieldAdd.isFocused()) {
            textFieldAdd.textboxKeyTyped(par1, par2);
            textFieldText = textFieldAdd.getText();

            addButtonCheck();
        }
    }

    @Override
    protected void mouseClicked(int par1, int par2, int par3) {
        try {
            super.mouseClicked(par1, par2, par3);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.textFieldAdd.mouseClicked(par1, par2, par3);
    }

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        drawDefaultBackground();
        itemList.drawScreen(par1, par2, par3);
        drawCenteredString(getFontRenderer(), I18n.format(titleString), this.width / 2, 14, 0xFFFFFF);
        clearButton.enabled = !textFieldText.isEmpty();
        textFieldAdd.drawTextBox();
        super.drawScreen(par1, par2, par3);
        if(renderIcon) {
            BlockID testBlockId = new BlockID(textFieldText);

            String[] testItemName = testBlockId.name.split(":", 2);
            if(testItemName.length == 2) {
                ItemStack itemStack = GameRegistry.findItemStack(testItemName[0], testItemName[1], 1);
                itemStack.setItemDamage(testBlockId.metadata == -1 ? 0 : testBlockId.metadata);
                iconRenderer.renderItemStackIcon(this.width / 2 - 152, 34, itemStack);
            }
        }
    }

    public ArrayList<String> getList() {
        if(mode == 0) {
            return VeinMiner.instance.configurationSettings.getBlockIDArray(toolType);
        }
        else if(mode == 1) {
            return VeinMiner.instance.configurationSettings.getToolIdArray(toolType);
        }
        return null;
    }
}
