package portablejim.veinminer.configuration.client.elements;

import cpw.mods.fml.client.GuiScrollingList;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import portablejim.veinminer.api.ToolType;
import portablejim.veinminer.configuration.client.ItemlistConfigGuiScreen;
import portablejim.veinminer.lib.IconRenderer;
import portablejim.veinminer.util.BlockID;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: james
 * Date: 23/02/14
 * Time: 4:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class GuiElementSlotItemlist extends GuiScrollingList {
    private final IconRenderer iconRenderer;
    ItemlistConfigGuiScreen parent;
    public ArrayList<String> items;
    public int selectedItem;
    ToolType toolType;
    boolean forceBlocks;

    public GuiElementSlotItemlist(ItemlistConfigGuiScreen parent, ToolType toolType, boolean forceBlocks) {
        super(Minecraft.getMinecraft(), parent.width - 10, parent.height, 60, parent.height - 40, 5, 22);
        iconRenderer = new IconRenderer(parent.mc, parent.getZLevel(), parent.getFontRenderer(), parent.mc.getTextureManager());
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

    @Override
    protected void drawSlot(int listIndex, int var2, int var3, int var4, Tessellator var5) {
        String entry = items.get(listIndex);
        BlockID itemParts = new BlockID(entry);
        String[] nameParts = itemParts.name.split(":", 2);

        String displayName;
        boolean renderItem;

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
            iconRenderer.renderItemStackIcon(this.listWidth / 2 - 148, var3 - 1 , itemStack);
        }

        this.parent.getFontRenderer().drawString(I18n.format(displayName + ".name"), this.listWidth / 2 + 60, var3 + 5, 0xFFFFFF);
        this.parent.getFontRenderer().drawString(entry, this.listWidth / 2 - 120, var3 + 5, 0xFFFFFF);
    }
}
