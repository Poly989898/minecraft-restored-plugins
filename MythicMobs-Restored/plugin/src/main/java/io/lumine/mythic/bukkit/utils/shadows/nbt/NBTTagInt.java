package io.lumine.mythic.bukkit.utils.shadows.nbt;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowFactory;
import me.lucko.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagInt")
public interface NBTTagInt extends Shadow, NBTBase, NBTNumber {
   static NBTTagInt create(int data) {
      return ShadowFactory.global().constructShadow(NBTTagInt.class, data);
   }
}
