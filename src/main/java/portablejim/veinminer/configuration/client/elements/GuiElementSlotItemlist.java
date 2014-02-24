package portablejim.veinminer.configuration.client.elements;

import cpw.mods.fml.client.GuiScrollingList;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import portablejim.veinminer.VeinMiner;
import portablejim.veinminer.api.ToolType;
import portablejim.veinminer.configuration.client.ItemlistConfigGuiScreen;
import portablejim.veinminer.util.BlockID;
import portablejim.veinminer.util.ItemStackID;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 23/02/14
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuiElementSlotItemlist extends GuiScrollingList {
    ItemlistConfigGuiScreen parent;
    RenderBlocks renderBlocks = new RenderBlocks();
    public ArrayList<String> items;
    public int selectedItem;
    ToolType toolType;
    boolean forceBlocks;

    public GuiElementSlotItemlist(ItemlistConfigGuiScreen parent, ToolType toolType, boolean forceBlocks) {
        super(Minecraft.getMinecraft(), parent.width - 10, parent.height, 60, parent.height - 40, 5, 22);
        this.parent = parent;
        this.toolType = toolType;
        this.forceBlocks = forceBlocks;
        updateItemIds();
    }

    public void updateItemIds() {
        items = parent.getList();
    }

    @Override
    protected int getSize() {
        return items.size();
    }

    @Override
    protected void elementClicked(int index, boolean doubleClick) {
        selectedItem = index;
    }

    @Override
    protected boolean isSelected(int index) {
        return selectedItem == index;
    }

    @Override
    protected void drawBackground() {
        parent.drawDefaultBackground();
    }

    private void func_148225_a(int p_148225_1_, int p_148225_2_, ItemStack p_148225_3_)
    {
        this.func_148226_e(p_148225_1_ + 1, p_148225_2_ + 1);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);

        if (p_148225_3_ != null)
        {
            RenderHelper.enableGUIStandardItemLighting();
            parent.renderItem.renderItemIntoGUI(parent.getFontRenderer(), parent.mc.getTextureManager(), p_148225_3_, p_148225_1_ + 2, p_148225_2_ + 2);
            RenderHelper.disableStandardItemLighting();
        }

        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    private void func_148226_e(int p_148226_1_, int p_148226_2_)
    {
        this.func_148224_c(p_148226_1_, p_148226_2_, 0, 0);
    }

    private void func_148224_c(int p_148224_1_, int p_148224_2_, int p_148224_3_, int p_148224_4_)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        parent.mc.getTextureManager().bindTexture(Gui.statIcons);
        float f = 0.0078125F;
        float f1 = 0.0078125F;
        boolean flag = true;
        boolean flag1 = true;
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.addVertexWithUV((double)(p_148224_1_ + 0), (double)(p_148224_2_ + 18), (double)parent.getZLevel(), (double)((float)(p_148224_3_ + 0) * 0.0078125F), (double)((float)(p_148224_4_ + 18) * 0.0078125F));
        tessellator.addVertexWithUV((double)(p_148224_1_ + 18), (double)(p_148224_2_ + 18), (double)parent.getZLevel(), (double)((float)(p_148224_3_ + 18) * 0.0078125F), (double)((float)(p_148224_4_ + 18) * 0.0078125F));
        tessellator.addVertexWithUV((double)(p_148224_1_ + 18), (double)(p_148224_2_ + 0), (double)parent.getZLevel(), (double)((float)(p_148224_3_ + 18) * 0.0078125F), (double)((float)(p_148224_4_ + 0) * 0.0078125F));
        tessellator.addVertexWithUV((double)(p_148224_1_ + 0), (double)(p_148224_2_ + 0), (double)parent.getZLevel(), (double)((float)(p_148224_3_ + 0) * 0.0078125F), (double)((float)(p_148224_4_ + 0) * 0.0078125F));
        tessellator.draw();
    }

    @Override
    protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5) {
        String entry = items.get(listIndex);
        ItemStack item;

        BlockID itemParts = new BlockID(entry);
        String[] nameParts = itemParts.name.split(":", 2);

        String rawName = "";
        String displayName = "";
        boolean renderItem = false;

        Block tryBlock = Block.getBlockFromName(entry);
        Item tryItem;
        if(tryBlock != null) {
            displayName = tryBlock.getUnlocalizedName();

            tryItem = Item.getItemFromBlock(tryBlock);
            renderItem = tryItem != null;
        }
        else {
            tryItem = GameRegistry.findItem(nameParts[0], nameParts[1]);
            displayName = tryItem.getUnlocalizedName();

            renderItem = true;
        }

        if(renderItem) {
            ItemStack itemStack = new ItemStack(tryItem);
            itemStack.setItemDamage(itemParts.metadata == -1 ? 0 : itemParts.metadata);

            // Get new name based on damage value.
            displayName = itemStack.getUnlocalizedName();
            func_148225_a(this.listWidth / 2 - 148, var3 - 1 , itemStack);
        }

        this.parent.getFontRenderer().drawString(I18n.format(displayName + ".name"), this.listWidth / 2 + 60, var3 + 5, 0xFFFFFF);
        this.parent.getFontRenderer().drawString(entry, this.listWidth / 2 - 120, var3 + 5, 0xFFFFFF);
    }
}
