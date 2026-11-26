package io.lumine.mythic.bukkit.utils.shadows.nbt;

import me.lucko.shadow.Shadow;
import me.lucko.shadow.ShadowFactory;
import me.lucko.shadow.bukkit.Mapping;
import me.lucko.shadow.bukkit.NmsClassTarget;
import me.lucko.shadow.bukkit.ObfuscatedTarget;
import me.lucko.shadow.bukkit.PackageVersion;

@NmsClassTarget("NBTTagList")
public interface NBTTagList extends Shadow, NBTBase {
   static NBTTagList create() {
      return ShadowFactory.global().constructShadow(NBTTagList.class);
   }

   @ObfuscatedTarget({@Mapping(
         value = "add",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "add",
         version = PackageVersion.v1_8_R3
      )})
   void appendTag(NBTBase nbtBase);

   @ObfuscatedTarget({@Mapping(
         value = "a",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "a",
         version = PackageVersion.v1_8_R3
      )})
   void setTag(int value2, NBTBase nbtBase);

   @ObfuscatedTarget({@Mapping(
         value = "remove",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "a",
         version = PackageVersion.v1_8_R3
      )})
   void removeTag(int value2);

   @ObfuscatedTarget({@Mapping(
         value = "i",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "g",
         version = PackageVersion.v1_8_R3
      )})
   NBTBase getTag(int value2);

   @ObfuscatedTarget({@Mapping(
         value = "get",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "get",
         version = PackageVersion.v1_8_R3
      )})
   NBTTagCompound getCompoundTag(int value2);

   @ObfuscatedTarget({@Mapping(
         value = "size",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "size",
         version = PackageVersion.v1_8_R3
      )})
   int size();
}
