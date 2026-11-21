package io.lumine.mythic.bukkit.utils.shadows.nbt;

import io.lumine.shadow.Shadow;
import io.lumine.shadow.ShadowFactory;
import io.lumine.shadow.bukkit.NmsClassTarget;

@NmsClassTarget("NBTTagInt")
public interface NBTTagInt extends Shadow, NBTBase, NBTNumber {
   static NBTTagInt create(int data) {
      return ShadowFactory.global().constructShadow(NBTTagInt.class, data);
   }
}
