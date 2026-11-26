package io.lumine.mythic.bukkit.utils.shadows.nbt;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowFactory;
import me.lucko.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagShort")
public interface NBTTagShort extends Shadow, NBTBase, NBTNumber {
   static NBTTagShort create(short data) {
      return ShadowFactory.global().constructShadow(NBTTagShort.class, data);
   }
}
