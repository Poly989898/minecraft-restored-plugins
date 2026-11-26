package io.lumine.mythic.bukkit.utils.shadows.nbt;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowFactory;
import me.lucko.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagEnd")
public interface NBTTagEnd extends Shadow, NBTBase {
   static NBTTagEnd create() {
      return ShadowFactory.global().constructShadow(NBTTagEnd.class);
   }
}
