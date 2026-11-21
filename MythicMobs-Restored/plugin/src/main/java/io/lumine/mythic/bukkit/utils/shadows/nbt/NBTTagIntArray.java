package io.lumine.mythic.bukkit.utils.shadows.nbt;

import io.lumine.shadow.Shadow;
import io.lumine.shadow.ShadowFactory;
import io.lumine.shadow.bukkit.Mapping;
import io.lumine.shadow.bukkit.NmsClassTarget;
import io.lumine.shadow.bukkit.ObfuscatedTarget;
import io.lumine.shadow.bukkit.PackageVersion;
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
