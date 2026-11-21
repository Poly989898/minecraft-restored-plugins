package io.lumine.mythic.bukkit.utils.shadows.nbt;

import io.lumine.shadow.Shadow;
import io.lumine.shadow.ShadowFactory;
import io.lumine.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagShort")
public interface NBTTagShort extends Shadow, NBTBase, NBTNumber {
   static NBTTagShort create(short data) {
      return ShadowFactory.global().constructShadow(NBTTagShort.class, data);
   }
}
