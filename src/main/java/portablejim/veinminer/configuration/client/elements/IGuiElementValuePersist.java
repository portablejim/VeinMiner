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

import net.minecraft.client.gui.GuiButton;

/**
 * Interface to be used as a callback for elements.
 * Used in Slider classes to save value.
 */

public interface IGuiElementValuePersist {
    public void saveGuiElementValue(GuiButton element, Object value);
    public Object getGuiElementValue(GuiButton element);
    public String getGuiElementDisplayString(GuiButton element, Object value);
}
