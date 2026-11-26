package io.lumine.mythic.bukkit.utils.shadows.nbt;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowFactory;
import me.lucko.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagDouble")
public interface NBTTagDouble extends Shadow, NBTBase, NBTNumber {
   static NBTTagDouble create(double data) {
      return ShadowFactory.global().constructShadow(NBTTagDouble.class, data);
   }
}
