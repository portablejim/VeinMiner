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

package portablejim.veinminer.proxy;

import portablejim.veinminer.event.server.JoinServerEvent;

/**
 * Server implementation of the proxy interface.
 */

@SuppressWarnings("UnusedDeclaration")
public class ServerProxy implements CommonProxy {
    private JoinServerEvent joinServerEvent;

    @Override
    public void registerKeybind() { }

    @Override
    public void addOtherEvents() {
        joinServerEvent = new JoinServerEvent();
    }
}
