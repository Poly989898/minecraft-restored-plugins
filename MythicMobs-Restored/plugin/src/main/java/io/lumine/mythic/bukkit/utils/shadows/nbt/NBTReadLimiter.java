package io.lumine.mythic.bukkit.utils.shadows.nbt;

import io.lumine.shadow.Field;
import io.lumine.shadow.Shadow;
import io.lumine.shadow.ShadowFactory;
import io.lumine.shadow.Static;
import io.lumine.shadow.bukkit.Mapping;
import io.lumine.shadow.bukkit.NmsClassTarget;
import io.lumine.shadow.bukkit.ObfuscatedTarget;
import io.lumine.shadow.bukkit.PackageVersion;

@NmsClassTarget("NBTReadLimiter")
public interface NBTReadLimiter extends Shadow {
   static NBTReadLimiter create(long max) {
      return ShadowFactory.global().constructShadow(NBTReadLimiter.class, max);
   }

   @Static
   @Field
   @ObfuscatedTarget({@Mapping(
         value = "a",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "a",
         version = PackageVersion.v1_8_R3
      )})
   NBTReadLimiter infinite();
}
