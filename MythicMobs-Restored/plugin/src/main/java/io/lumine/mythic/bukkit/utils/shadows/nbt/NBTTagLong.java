package io.lumine.mythic.bukkit.utils.shadows.nbt;

import io.lumine.shadow.Shadow;
import io.lumine.shadow.ShadowFactory;
import io.lumine.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagLong")
public interface NBTTagLong extends Shadow, NBTBase, NBTNumber {
   static NBTTagLong create(long data) {
      return ShadowFactory.global().constructShadow(NBTTagLong.class, data);
   }
}
