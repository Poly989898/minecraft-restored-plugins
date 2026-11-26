package io.lumine.mythic.bukkit.utils.shadows.nbt;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowFactory;
import me.lucko.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagLong")
public interface NBTTagLong extends Shadow, NBTBase, NBTNumber {
   static NBTTagLong create(long data) {
      return ShadowFactory.global().constructShadow(NBTTagLong.class, data);
   }
}
