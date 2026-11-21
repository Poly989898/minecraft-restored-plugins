package io.lumine.mythic.bukkit.utils.shadows.nbt;

import io.lumine.shadow.Shadow;
import io.lumine.shadow.ShadowFactory;
import io.lumine.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagByte")
public interface NBTTagByte extends Shadow, NBTBase, NBTNumber {
   static NBTTagByte create(byte data) {
      return ShadowFactory.global().constructShadow(NBTTagByte.class, data);
   }
}
