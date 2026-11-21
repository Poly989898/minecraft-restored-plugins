package io.lumine.mythic.bukkit.utils.shadows.nbt;

import io.lumine.shadow.Shadow;
import io.lumine.shadow.ShadowFactory;
import io.lumine.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagDouble")
public interface NBTTagDouble extends Shadow, NBTBase, NBTNumber {
   static NBTTagDouble create(double data) {
      return ShadowFactory.global().constructShadow(NBTTagDouble.class, data);
   }
}
