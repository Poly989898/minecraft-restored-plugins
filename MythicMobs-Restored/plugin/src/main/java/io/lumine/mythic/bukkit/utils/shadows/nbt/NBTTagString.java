package io.lumine.mythic.bukkit.utils.shadows.nbt;

import io.lumine.shadow.Shadow;
import io.lumine.shadow.ShadowFactory;
import io.lumine.shadow.bukkit.Mapping;
import io.lumine.shadow.bukkit.NmsClassTarget;
import io.lumine.shadow.bukkit.ObfuscatedTarget;
import io.lumine.shadow.bukkit.PackageVersion;

@NmsClassTarget("NBTTagString")
public interface NBTTagString extends Shadow, NBTBase {
   static NBTTagString create(String data) {
      return ShadowFactory.global().constructShadow(NBTTagString.class, data);
   }

   @ObfuscatedTarget({@Mapping(
         value = "getString",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "a_",
         version = PackageVersion.v1_8_R3
      )})
   String getString();
}
