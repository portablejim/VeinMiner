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

/**
 * Slider that changes value using a logarithmic scale, instead of a linear one.
 */

public class GuiElementSliderLog extends GuiElementSlider {
    int exponent;

    public GuiElementSliderLog(int id, int xPos, int yPos, IGuiElementValuePersist callback, float min, float max, int exponent) {
        super(id, xPos, yPos, callback, min, max);
        this.exponent = exponent;
        this.storedValue = normalise((Float)callback.getGuiElementValue(this));
        this.displayString = callback.getGuiElementDisplayString(this, deNormalise(storedValue));
    }


    @Override
    protected float deNormalise(float input) {
        float range = max - min;
        float scaled = (float) (Math.pow(input, exponent) * range);
        return min + scaled;
    }

    @Override
    protected float normalise(float input) {
        float range = max - min;
        float reduced = input - min;
        return (float) Math.pow(reduced / range, 1D/((double)exponent));
    }
}
