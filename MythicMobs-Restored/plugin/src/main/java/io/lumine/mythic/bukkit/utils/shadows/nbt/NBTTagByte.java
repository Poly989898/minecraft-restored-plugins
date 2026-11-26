package io.lumine.mythic.bukkit.utils.shadows.nbt;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowFactory;
import me.lucko.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagByte")
public interface NBTTagByte extends Shadow, NBTBase, NBTNumber {
   static NBTTagByte create(byte data) {
      return ShadowFactory.global().constructShadow(NBTTagByte.class, data);
   }
}
