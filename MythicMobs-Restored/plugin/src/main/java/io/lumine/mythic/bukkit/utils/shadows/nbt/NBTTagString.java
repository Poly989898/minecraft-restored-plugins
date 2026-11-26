package io.lumine.mythic.bukkit.utils.shadows.nbt;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;

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
