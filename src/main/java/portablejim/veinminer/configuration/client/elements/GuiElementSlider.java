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

package portablejim.veinminer.configuration.client.elements;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

/**
 * Slider which saves it's value via callback.
 * (instead of a Option)
 * Uses logic from vanilla slider
 */

public class GuiElementSlider extends GuiButton {
    IGuiElementValuePersist callback;
    float storedValue;
    float min, max;

    boolean mouseDragging;

    public GuiElementSlider(int id, int xPos, int yPos, IGuiElementValuePersist callback, float min, float max) {
        this(id, xPos, yPos, 150, 20, callback, min, max);
    }

    public GuiElementSlider(int id, int xPos, int yPos, IGuiElementValuePersist callback) {
        this(id, xPos, yPos, 150, 20, callback, 0F, 1F);
    }

    public GuiElementSlider(int id, int xPos, int yPos, int width, int height, IGuiElementValuePersist callback, float min, float max) {
        super(id, xPos, yPos, width, height, "");
        this.callback = callback;
        this.min = min;
        this.max = max;
        this.storedValue = normalise((Float)callback.getGuiElementValue(this));
        this.displayString = callback.getGuiElementDisplayString(this, deNormalise(storedValue));
    }

    @Override
    protected int getHoverState(boolean val) {
        return 0;
    }

    @Override
    public void mouseDragged(Minecraft minecraft, int par1, int par2) {
        if(this.visible) {
            if(mouseDragging) {
                float rawValue = (float)(par1 - (this.xPosition + 4)) / (float)(this.width - 8);

                if(rawValue < 0F) {
                    rawValue = 0F;
                }
                else if(rawValue > 1F) {
                    rawValue = 1F;
                }
                this.storedValue = rawValue;

                float scaledValue = deNormalise(rawValue);
                callback.saveGuiElementValue(this, scaledValue);
                displayString = callback.getGuiElementDisplayString(this, scaledValue);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexturedModalRect(this.xPosition + (int)(this.storedValue * (float)(this.width - 8)), this.yPosition, 0, 66, 4, 20);
            this.drawTexturedModalRect(this.xPosition + (int)(this.storedValue * (float)(this.width - 8)) + 4, this.yPosition, 196, 66, 4, 20);
        }
    }

    @Override
    public boolean mousePressed(Minecraft minecraft, int par1, int par2) {
        if(super.mousePressed(minecraft, par1, par2)) {
            float rawValue = (float)(par1 - (this.xPosition + 4)) / (float)(this.width - 8);

            if(rawValue < 0F) {
                rawValue = 0F;
            }
            else if(rawValue > 1F) {
                rawValue = 1F;
            }
            this.storedValue = rawValue;

            float scaledValue = deNormalise(rawValue);
            callback.saveGuiElementValue(this, scaledValue);
            displayString = callback.getGuiElementDisplayString(this, scaledValue);

            mouseDragging = true;

            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public void mouseReleased(int par1, int par2) {
        mouseDragging = false;
    }

    protected float deNormalise(float input) {
        float range = max - min;
        float scaled = input * range;
        return min + scaled;
    }

    protected float normalise(float input) {
        float range = max - min;
        float reduced = input - min;
        return reduced / range;
    }
}
