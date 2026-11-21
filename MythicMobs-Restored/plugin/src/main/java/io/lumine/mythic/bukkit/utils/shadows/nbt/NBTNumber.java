package io.lumine.mythic.bukkit.utils.shadows.nbt;

import io.lumine.shadow.bukkit.Mapping;
import io.lumine.shadow.bukkit.ObfuscatedTarget;
import io.lumine.shadow.bukkit.PackageVersion;

public interface NBTNumber {
   @ObfuscatedTarget({@Mapping(
         value = "d",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "c",
         version = PackageVersion.v1_8_R3
      )})
   long asLong();

   @ObfuscatedTarget({@Mapping(
         value = "e",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "d",
         version = PackageVersion.v1_8_R3
      )})
   int asInt();

   @ObfuscatedTarget({@Mapping(
         value = "f",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "e",
         version = PackageVersion.v1_8_R3
      )})
   short asShort();

   @ObfuscatedTarget({@Mapping(
         value = "g",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "f",
         version = PackageVersion.v1_8_R3
      )})
   byte asByte();

   @ObfuscatedTarget({@Mapping(
         value = "asDouble",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "g",
         version = PackageVersion.v1_8_R3
      )})
   double asDouble();

   @ObfuscatedTarget({@Mapping(
         value = "i",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "h",
         version = PackageVersion.v1_8_R3
      )})
   float asFloat();
}
