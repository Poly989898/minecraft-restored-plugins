package io.lumine.mythic.bukkit.utils.shadows.nbt;

import io.lumine.shadow.Shadow;
import io.lumine.shadow.ShadowFactory;
import io.lumine.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagEnd")
public interface NBTTagEnd extends Shadow, NBTBase {
   static NBTTagEnd create() {
      return ShadowFactory.global().constructShadow(NBTTagEnd.class);
   }
}
