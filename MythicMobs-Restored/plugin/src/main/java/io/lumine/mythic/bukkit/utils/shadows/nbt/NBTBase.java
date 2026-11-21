package io.lumine.mythic.bukkit.utils.shadows.nbt;

import io.lumine.mythic.bukkit.utils.nbt.NBTTagType;
import io.lumine.shadow.Field;
import io.lumine.shadow.Shadow;
import io.lumine.shadow.Static;
import io.lumine.shadow.bukkit.Mapping;
import io.lumine.shadow.bukkit.NmsClassTarget;
import io.lumine.shadow.bukkit.ObfuscatedTarget;
import io.lumine.shadow.bukkit.PackageVersion;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

@NmsClassTarget("NBTBase")
public interface NBTBase extends Shadow {
   @Static
   @Field
   @ObfuscatedTarget({@Mapping(
         value = "a",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "a",
         version = PackageVersion.v1_8_R3
      )})
   String[] getTypes();

   @ObfuscatedTarget({@Mapping(
         value = "write",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "write",
         version = PackageVersion.v1_8_R3
      )})
   void write(DataOutput dataOutput) throws IOException;

   @ObfuscatedTarget({@Mapping(
         value = "load",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "load",
         version = PackageVersion.v1_8_R3
      )})
   void load(DataInput dataInput, int value2, NBTReadLimiter nbtReadLimiter) throws IOException;

   @ObfuscatedTarget({@Mapping(
         value = "getTypeId",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "getTypeId",
         version = PackageVersion.v1_8_R3
      )})
   byte getTypeId();

   default NBTTagType getType() {
      return NBTTagType.of(this.getTypeId());
   }

   @ObfuscatedTarget({@Mapping(
         value = "clone",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "clone",
         version = PackageVersion.v1_8_R3
      )})
   NBTBase copy();

   @ObfuscatedTarget({@Mapping(
         value = "isEmpty",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "isEmpty",
         version = PackageVersion.v1_8_R3
      )})
   boolean hasNoTags();
}
