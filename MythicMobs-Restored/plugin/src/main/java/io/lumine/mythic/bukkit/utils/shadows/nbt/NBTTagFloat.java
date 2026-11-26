package io.lumine.mythic.bukkit.utils.shadows.nbt;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowFactory;
import me.lucko.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagFloat")
public interface NBTTagFloat extends Shadow, NBTBase, NBTNumber {
   static NBTTagFloat create(float data) {
      return ShadowFactory.global().constructShadow(NBTTagFloat.class, data);
   }
}
