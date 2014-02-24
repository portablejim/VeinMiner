package portablejim.veinminer.configuration.client;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.api.ToolType;
import portablejim.veinminer.configuration.client.elements.GuiElementSlotItemlist;
import portablejim.veinminer.util.BlockID;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 23/02/14
 * Time: 5:47 PM
 * To change this template use File | Settings | File Templates.
 */
public class ItemlistConfigGuiScreen extends GuiScreen {
    ToolType toolType;
    int mode;
    public RenderItem renderItem = new RenderItem();
    GuiButton addButton;
    GuiButton clearButton;
    GuiTextField textFieldAdd;
    String textFieldText = "";
    boolean renderIcon = false;

    String titleString;

    GuiElementSlotItemlist itemList;
    private Object parent;

    public ItemlistConfigGuiScreen(GuiScreen parent, ToolType toolType, int mode) {
        this.toolType = toolType;
        this.mode = mode;
        this.parent = parent;

        String[] toolNames = { "axe", "hoe", "pickaxe", "shears", "shovel"};
        String toolName = toolNames[toolType.ordinal()];
        String[] modeNames = { "blocklist", "toollist" };
        String modeName = modeNames[mode];

        this.titleString = String.format("gui.veinminer.title.%s.%s", modeName, toolName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void initGui() {
        itemList = new GuiElementSlotItemlist(this, toolType, false);
        this.buttonList.add(new GuiButton(1, this.width / 2 + 2, this.height - 34, 150, 20, I18n.format("gui.veinminer.back")));
        this.buttonList.add(new GuiButton(2, this.width / 2 - 152, this.height - 34, 150, 20, I18n.format("gui.veinminer.item.delete")));
        addButton = new GuiButton(3, this.width / 2 + 52, 34, 48, 20, I18n.format("gui.veinminer.item.add"));
        addButton.enabled = false;
        this.buttonList.add(addButton);
        clearButton = new GuiButton(4, this.width / 2 + 104, 34, 48, 20, I18n.format("gui.veinminer.item.clear"));
        clearButton.enabled = false;
        this.buttonList.add(clearButton);

        textFieldAdd = new GuiTextField(this.getFontRenderer(), this.width / 2 - 128, 34, 176, 20);
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
                    break;
                }
                case 4:
                    this.textFieldAdd.setText("");
                    this.textFieldText = "";

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
        super.mouseClicked(par1, par2, par3);
        this.textFieldAdd.mouseClicked(par1, par2, par3);
    }

    private void renderItemStackIcon(int renderX, int renderY, ItemStack itemStack)
    {
        this.setupRender(renderX + 1, renderY + 1, 0, 0);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        if (itemStack != null)
        {
            RenderHelper.enableGUIStandardItemLighting();
            this.renderItem.renderItemIntoGUI(this.getFontRenderer(), this.mc.getTextureManager(), itemStack, renderX + 2, renderY + 2);
            RenderHelper.disableStandardItemLighting();
        }
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    private void setupRender(int xBase, int yBase, int uBase, int vBase)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(Gui.statIcons);
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(xBase), (double)(yBase + 18), (double)this.getZLevel(), (double)((float)(uBase) * 0.0078125F), (double)((float)(vBase + 18) * 0.0078125F));
        tessellator.addVertexWithUV((double) (xBase + 18), (double) (yBase + 18), (double) this.getZLevel(), (double) ((float) (uBase + 18) * 0.0078125F), (double) ((float) (vBase + 18) * 0.0078125F));
        tessellator.addVertexWithUV((double)(xBase + 18), (double)(yBase), (double)this.getZLevel(), (double)((float)(uBase + 18) * 0.0078125F), (double)((float)(vBase) * 0.0078125F));
        tessellator.addVertexWithUV((double)(xBase), (double)(yBase), (double)this.getZLevel(), (double)((float)(uBase) * 0.0078125F), (double)((float)(vBase) * 0.0078125F));
        tessellator.draw();
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
            ItemStack itemStack = GameRegistry.findItemStack(testItemName[0], testItemName[1], 1);
            renderItemStackIcon(this.width / 2 - 152, 34, itemStack);
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
