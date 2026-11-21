package io.lumine.mythic.bukkit.utils.shadows.nbt;

import io.lumine.shadow.Shadow;
import io.lumine.shadow.ShadowFactory;
import io.lumine.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagFloat")
public interface NBTTagFloat extends Shadow, NBTBase, NBTNumber {
   static NBTTagFloat create(float data) {
      return ShadowFactory.global().constructShadow(NBTTagFloat.class, data);
   }
}
