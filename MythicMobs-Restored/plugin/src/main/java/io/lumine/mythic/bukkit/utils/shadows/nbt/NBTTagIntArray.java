package io.lumine.mythic.bukkit.utils.shadows.nbt;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;
import java.util.List;

@NmsClassTarget("NBTTagIntArray")
public interface NBTTagIntArray extends Shadow, NBTBase {
   static NBTTagIntArray create(int[] data) {
      return ShadowFactory.global().constructShadow(NBTTagIntArray.class, data);
   }

   static NBTTagIntArray create(List<Integer> data) {
      return ShadowFactory.global().constructShadow(NBTTagIntArray.class, data);
   }

   @ObfuscatedTarget({@Mapping(
         value = "d",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "c",
         version = PackageVersion.v1_8_R3
      )})
   int[] getIntArray();
}
