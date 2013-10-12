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

package portablejim.veinminer.asm;

import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

/**
 * Common class that includes shared methods to handle deobfuscation of class
 * methods.
 */

import java.util.HashMap;

public abstract class GenericTransformer {
    public static HashMap<String, String> srgMappings;
    public boolean obfuscated = true;

    protected HashMap<String, String> typemap;

    public GenericTransformer() {
        typemap = new HashMap<String, String>();
    }

    protected String getCorrectName(String name) {
        if(obfuscated && srgMappings.containsKey(name)) {
            return srgMappings.get(name);
        }
        return name;
    }

    public boolean isMethodWithName(AbstractInsnNode instruction, String name) {
        if(instruction.getType() == AbstractInsnNode.METHOD_INSN) {
            MethodInsnNode methodNode = (MethodInsnNode)instruction;
            String srgName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(methodNode.owner, methodNode.name, methodNode.desc);
            return srgName.equals(getCorrectName(name));
        }
        return false;
    }
}
