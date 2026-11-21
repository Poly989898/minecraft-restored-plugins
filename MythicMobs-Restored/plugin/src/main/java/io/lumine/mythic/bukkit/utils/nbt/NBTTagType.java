package io.lumine.mythic.bukkit.utils.nbt;

import io.lumine.mythic.bukkit.utils.shadows.nbt.NBTBase;
import io.lumine.mythic.bukkit.utils.shadows.nbt.NBTTagByte;
import io.lumine.mythic.bukkit.utils.shadows.nbt.NBTTagByteArray;
import io.lumine.mythic.bukkit.utils.shadows.nbt.NBTTagCompound;
import io.lumine.mythic.bukkit.utils.shadows.nbt.NBTTagDouble;
import io.lumine.mythic.bukkit.utils.shadows.nbt.NBTTagEnd;
import io.lumine.mythic.bukkit.utils.shadows.nbt.NBTTagFloat;
import io.lumine.mythic.bukkit.utils.shadows.nbt.NBTTagInt;
import io.lumine.mythic.bukkit.utils.shadows.nbt.NBTTagIntArray;
import io.lumine.mythic.bukkit.utils.shadows.nbt.NBTTagList;
import io.lumine.mythic.bukkit.utils.shadows.nbt.NBTTagLong;
import io.lumine.mythic.bukkit.utils.shadows.nbt.NBTTagLongArray;
import io.lumine.mythic.bukkit.utils.shadows.nbt.NBTTagShort;
import io.lumine.mythic.bukkit.utils.shadows.nbt.NBTTagString;

public enum NBTTagType {
   END(NBTTagEnd.class, (byte)0),
   BYTE(NBTTagByte.class, (byte)1, true),
   SHORT(NBTTagShort.class, (byte)2, true),
   INT(NBTTagInt.class, (byte)3, true),
   LONG(NBTTagLong.class, (byte)4, true),
   FLOAT(NBTTagFloat.class, (byte)5, true),
   DOUBLE(NBTTagDouble.class, (byte)6, true),
   BYTE_ARRAY(NBTTagByteArray.class, (byte)7),
   STRING(NBTTagString.class, (byte)8),
   LIST(NBTTagList.class, (byte)9),
   COMPOUND(NBTTagCompound.class, (byte)10),
   INT_ARRAY(NBTTagIntArray.class, (byte)11),
   LONG_ARRAY(NBTTagLongArray.class, (byte)12);

   private static final NBTTagType[] TYPES = values();
   private final Class<? extends NBTBase> shadowClass;
   private final byte id;
   private final boolean number;

   public static NBTTagType of(byte id) {
      return TYPES[id];
   }

   private NBTTagType(Class<? extends NBTBase> shadowClass, byte id) {
      this(shadowClass, id, false);
   }

   private NBTTagType(Class<? extends NBTBase> shadowClass, byte id, boolean number) {
      this.shadowClass = shadowClass;
      this.id = id;
      this.number = number;
   }

   public Class<? extends NBTBase> shadowClass() {
      return this.shadowClass;
   }

   public byte id() {
      return this.id;
   }

   public boolean number() {
      return this.number;
   }
}
