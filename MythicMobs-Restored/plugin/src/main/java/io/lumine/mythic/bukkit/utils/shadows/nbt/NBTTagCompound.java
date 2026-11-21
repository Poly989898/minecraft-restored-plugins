package io.lumine.mythic.bukkit.utils.shadows.nbt;

import io.lumine.shadow.Shadow;
import io.lumine.shadow.ShadowFactory;
import io.lumine.shadow.bukkit.Mapping;
import io.lumine.shadow.bukkit.NmsClassTarget;
import io.lumine.shadow.bukkit.ObfuscatedTarget;
import io.lumine.shadow.bukkit.PackageVersion;
import java.util.Set;

@NmsClassTarget("NBTTagCompound")
public interface NBTTagCompound extends Shadow, NBTBase {
   static NBTTagCompound create() {
      return ShadowFactory.global().constructShadow(NBTTagCompound.class);
   }

   @ObfuscatedTarget({@Mapping(
         value = "c",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "c",
         version = PackageVersion.v1_8_R3
      )})
   Set<String> keySet();

   @ObfuscatedTarget({@Mapping(
         value = "d",
         version = PackageVersion.v1_12_R1
      )})
   int size();

   @ObfuscatedTarget({@Mapping(
         value = "set",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "set",
         version = PackageVersion.v1_8_R3
      )})
   void setTag(String str, NBTBase nbtBase);

   @ObfuscatedTarget({@Mapping(
         value = "remove",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "remove",
         version = PackageVersion.v1_8_R3
      )})
   void removeTag(String str);

   @ObfuscatedTarget({@Mapping(
         value = "setByte",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "setByte",
         version = PackageVersion.v1_8_R3
      )})
   void setByte(String str, byte b);

   @ObfuscatedTarget({@Mapping(
         value = "setShort",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "setShort",
         version = PackageVersion.v1_8_R3
      )})
   void setShort(String str, short value2);

   @ObfuscatedTarget({@Mapping(
         value = "setInt",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "setInt",
         version = PackageVersion.v1_8_R3
      )})
   void setInteger(String str, int value2);

   @ObfuscatedTarget({@Mapping(
         value = "setLong",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "setLong",
         version = PackageVersion.v1_8_R3
      )})
   void setLong(String str, long value2);

   @ObfuscatedTarget({@Mapping(
         value = "setFloat",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "setFloat",
         version = PackageVersion.v1_8_R3
      )})
   void setFloat(String str, float value2);

   @ObfuscatedTarget({@Mapping(
         value = "setDouble",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "setDouble",
         version = PackageVersion.v1_8_R3
      )})
   void setDouble(String str, double value2);

   @ObfuscatedTarget({@Mapping(
         value = "setString",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "setString",
         version = PackageVersion.v1_8_R3
      )})
   void setString(String str, String str2);

   @ObfuscatedTarget({@Mapping(
         value = "setByteArray",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "setByteArray",
         version = PackageVersion.v1_8_R3
      )})
   void setByteArray(String str, byte[] bs);

   @ObfuscatedTarget({@Mapping(
         value = "setIntArray",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "setIntArray",
         version = PackageVersion.v1_8_R3
      )})
   void setIntArray(String str, int[] ints);

   @ObfuscatedTarget({@Mapping(
         value = "setBoolean",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "setBoolean",
         version = PackageVersion.v1_8_R3
      )})
   void setBoolean(String str, boolean flag);

   @ObfuscatedTarget({@Mapping(
         value = "get",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "get",
         version = PackageVersion.v1_8_R3
      )})
   NBTBase getTag(String str);

   @ObfuscatedTarget({@Mapping(
         value = "d",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "b",
         version = PackageVersion.v1_8_R3
      )})
   byte getTagId(String str);

   @ObfuscatedTarget({@Mapping(
         value = "hasKey",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "hasKey",
         version = PackageVersion.v1_8_R3
      )})
   boolean hasKey(String str);

   @ObfuscatedTarget({@Mapping(
         value = "hasKeyOfType",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "hasKeyOfType",
         version = PackageVersion.v1_8_R3
      )})
   boolean hasKey(String str, int value2);

   @ObfuscatedTarget({@Mapping(
         value = "getByte",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "getByte",
         version = PackageVersion.v1_8_R3
      )})
   byte getByte(String str);

   @ObfuscatedTarget({@Mapping(
         value = "getShort",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "getShort",
         version = PackageVersion.v1_8_R3
      )})
   short getShort(String str);

   @ObfuscatedTarget({@Mapping(
         value = "getInt",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "getInt",
         version = PackageVersion.v1_8_R3
      )})
   int getInteger(String str);

   @ObfuscatedTarget({@Mapping(
         value = "getLong",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "getLong",
         version = PackageVersion.v1_8_R3
      )})
   long getLong(String str);

   @ObfuscatedTarget({@Mapping(
         value = "getFloat",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "getFloat",
         version = PackageVersion.v1_8_R3
      )})
   float getFloat(String str);

   @ObfuscatedTarget({@Mapping(
         value = "getDouble",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "getDouble",
         version = PackageVersion.v1_8_R3
      )})
   double getDouble(String str);

   @ObfuscatedTarget({@Mapping(
         value = "getString",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "getString",
         version = PackageVersion.v1_8_R3
      )})
   String getString(String str);

   @ObfuscatedTarget({@Mapping(
         value = "getByteArray",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "getByteArray",
         version = PackageVersion.v1_8_R3
      )})
   byte[] getByteArray(String str);

   @ObfuscatedTarget({@Mapping(
         value = "getIntArray",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "getIntArray",
         version = PackageVersion.v1_8_R3
      )})
   int[] getIntArray(String str);

   @ObfuscatedTarget({@Mapping(
         value = "getCompound",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "getCompound",
         version = PackageVersion.v1_8_R3
      )})
   NBTTagCompound getCompoundTag(String str);

   @ObfuscatedTarget({@Mapping(
         value = "getList",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "getList",
         version = PackageVersion.v1_8_R3
      )})
   NBTTagList getTagList(String str, int value2);

   @ObfuscatedTarget({@Mapping(
         value = "getBoolean",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "getBoolean",
         version = PackageVersion.v1_8_R3
      )})
   boolean getBoolean(String str);

   @ObfuscatedTarget({@Mapping(
         value = "a",
         version = PackageVersion.v1_12_R1
      ), @Mapping(
         value = "a",
         version = PackageVersion.v1_8_R3
      )})
   void merge(NBTTagCompound nbtTagCompound);
}
