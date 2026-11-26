package io.lumine.mythic.bukkit.utils.shadows.nbt;

import me.lucko.shadow.Field;
import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowFactory;
import me.lucko.shadow.Static;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;

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
