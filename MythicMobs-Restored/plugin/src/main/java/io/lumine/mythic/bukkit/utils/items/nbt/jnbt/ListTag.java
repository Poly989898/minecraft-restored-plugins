package io.lumine.mythic.bukkit.utils.items.nbt.jnbt;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

public final class ListTag extends Tag {
   private final Class<? extends Tag> type;
   private final List<Tag> value;

   public ListTag(Class<? extends Tag> type, List<? extends Tag> value) {
      checkNotNull(value);
      this.type = type;
      this.value = Collections.unmodifiableList(value);
   }

   public Class<? extends Tag> getType() {
      return this.type;
   }

   public List<Tag> getValue() {
      return this.value;
   }

   public ListTag setValue(List<Tag> list) {
      return new ListTag(this.getType(), list);
   }

   public Tag getIfExists(int index) {
      try {
         return this.value.get(index);
      } catch (NoSuchElementException e) {
         return null;
      }
   }

   public byte[] getByteArray(int index) {
      Tag tag = this.getIfExists(index);
      return tag instanceof ByteArrayTag ? ((ByteArrayTag)tag).getValue() : new byte[0];
   }

   public byte getByte(int index) {
      Tag tag = this.getIfExists(index);
      return tag instanceof ByteTag ? ((ByteTag)tag).getValue() : 0;
   }

   public double getDouble(int index) {
      Tag tag = this.getIfExists(index);
      return tag instanceof DoubleTag ? ((DoubleTag)tag).getValue() : 0.0;
   }

   public double asDouble(int index) {
      Tag tag = this.getIfExists(index);
      if (tag instanceof ByteTag) {
         return ((ByteTag)tag).getValue().byteValue();
      } else if (tag instanceof ShortTag) {
         return ((ShortTag)tag).getValue().shortValue();
      } else if (tag instanceof IntTag) {
         return ((IntTag)tag).getValue().intValue();
      } else if (tag instanceof LongTag) {
         return ((LongTag)tag).getValue().longValue();
      } else if (tag instanceof FloatTag) {
         return ((FloatTag)tag).getValue().floatValue();
      } else {
         return tag instanceof DoubleTag ? ((DoubleTag)tag).getValue() : 0.0;
      }
   }

   public float getFloat(int index) {
      Tag tag = this.getIfExists(index);
      return tag instanceof FloatTag ? ((FloatTag)tag).getValue() : 0.0F;
   }

   public int[] getIntArray(int index) {
      Tag tag = this.getIfExists(index);
      return tag instanceof IntArrayTag ? ((IntArrayTag)tag).getValue() : new int[0];
   }

   public int getInt(int index) {
      Tag tag = this.getIfExists(index);
      return tag instanceof IntTag ? ((IntTag)tag).getValue() : 0;
   }

   public int asInt(int index) {
      Tag tag = this.getIfExists(index);
      if (tag instanceof ByteTag) {
         return ((ByteTag)tag).getValue();
      } else if (tag instanceof ShortTag) {
         return ((ShortTag)tag).getValue();
      } else if (tag instanceof IntTag) {
         return ((IntTag)tag).getValue();
      } else if (tag instanceof LongTag) {
         return ((LongTag)tag).getValue().intValue();
      } else if (tag instanceof FloatTag) {
         return ((FloatTag)tag).getValue().intValue();
      } else {
         return tag instanceof DoubleTag ? ((DoubleTag)tag).getValue().intValue() : 0;
      }
   }

   public List<Tag> getList(int index) {
      Tag tag = this.getIfExists(index);
      return tag instanceof ListTag ? ((ListTag)tag).getValue() : Collections.emptyList();
   }

   public ListTag getListTag(int index) {
      Tag tag = this.getIfExists(index);
      return tag instanceof ListTag ? (ListTag)tag : new ListTag(StringTag.class, Collections.emptyList());
   }

   public <T extends Tag> List<T> getList(int index, Class<T> listType) {
      if (this.getIfExists(index) instanceof ListTag listTag) {
         return (List<T>)(listTag.getType().equals(listType) ? listTag.getValue() : Collections.emptyList());
      } else {
         return Collections.emptyList();
      }
   }

   public long getLong(int index) {
      Tag tag = this.getIfExists(index);
      return tag instanceof LongTag ? ((LongTag)tag).getValue() : 0L;
   }

   public long asLong(int index) {
      Tag tag = this.getIfExists(index);
      if (tag instanceof ByteTag) {
         return ((ByteTag)tag).getValue().byteValue();
      } else if (tag instanceof ShortTag) {
         return ((ShortTag)tag).getValue().shortValue();
      } else if (tag instanceof IntTag) {
         return ((IntTag)tag).getValue().intValue();
      } else if (tag instanceof LongTag) {
         return ((LongTag)tag).getValue();
      } else if (tag instanceof FloatTag) {
         return ((FloatTag)tag).getValue().longValue();
      } else {
         return tag instanceof DoubleTag ? ((DoubleTag)tag).getValue().longValue() : 0L;
      }
   }

   public short getShort(int index) {
      Tag tag = this.getIfExists(index);
      return tag instanceof ShortTag ? ((ShortTag)tag).getValue() : 0;
   }

   public String getString(int index) {
      Tag tag = this.getIfExists(index);
      return tag instanceof StringTag ? ((StringTag)tag).getValue() : "";
   }

   @Override
   public String toString() {
      StringBuilder bldr = new StringBuilder();
      bldr.append("TAG_List").append(": ").append(this.value.size()).append(" entries of type ").append(NBTUtils.getTypeName(this.type)).append("\r\n{\r\n");

      for (Tag t : this.value) {
         bldr.append("   ").append(t.toString().replaceAll("\r\n", "\r\n   ")).append("\r\n");
      }

      bldr.append("}");
      return bldr.toString();
   }

   private static void checkNotNull(Object object) {
      if (object == null) {
         throw new NullPointerException();
      }
   }
}
