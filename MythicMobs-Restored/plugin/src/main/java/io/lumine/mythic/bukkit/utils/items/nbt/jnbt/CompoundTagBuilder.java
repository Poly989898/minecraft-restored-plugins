package io.lumine.mythic.bukkit.utils.items.nbt.jnbt;

import io.lumine.mythic.bukkit.utils.uuid.DashedUUIDs;
import io.lumine.mythic.bukkit.utils.version.wrappers.VersionWrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

public class CompoundTagBuilder {
   private final Map<String, Tag> entries;

   CompoundTagBuilder() {
      this.entries = new HashMap<>();
   }

   CompoundTagBuilder(Map<String, Tag> value) {
      checkNotNull(value);
      this.entries = value;
   }

   public CompoundTagBuilder put(String key, Tag value) {
      checkNotNull(key);
      checkNotNull(value);
      this.entries.put(key, value);
      return this;
   }

   public CompoundTagBuilder putTag(String key, Tag tag) {
      return this.put(key, tag);
   }

   public CompoundTagBuilder putByteArray(String key, byte[] value) {
      return this.put(key, new ByteArrayTag(value));
   }

   public CompoundTagBuilder putByte(String key, byte value) {
      return this.put(key, new ByteTag(value));
   }

   public CompoundTagBuilder putBoolean(String key, boolean value) {
      return this.putByte(key, (byte)(value ? 1 : 0));
   }

   public CompoundTagBuilder putDouble(String key, double value) {
      return this.put(key, new DoubleTag(value));
   }

   public CompoundTagBuilder putFloat(String key, float value) {
      return this.put(key, new FloatTag(value));
   }

   public CompoundTagBuilder putIntArray(String key, int[] value) {
      return this.put(key, new IntArrayTag(value));
   }

   public CompoundTagBuilder putInt(String key, int value) {
      return this.put(key, new IntTag(value));
   }

   public CompoundTagBuilder putLong(String key, long value) {
      return this.put(key, new LongTag(value));
   }

   public CompoundTagBuilder putShort(String key, short value) {
      return this.put(key, new ShortTag(value));
   }

   public CompoundTagBuilder putString(String key, String value) {
      return this.put(key, new StringTag(value));
   }

   public CompoundTagBuilder putUUID(String key, UUID uuid) {
      return this.putIntArray(key, DashedUUIDs.uuidToIntArray(uuid));
   }

   public CompoundTagBuilder putAll(Map<String, ? extends Tag> value) {
      checkNotNull(value);

      for (Entry<String, ? extends Tag> entry : value.entrySet()) {
         this.put(entry.getKey(), entry.getValue());
      }

      return this;
   }

   public CompoundTagBuilder remove(String key) {
      this.entries.remove(key);
      return this;
   }

   public CompoundTag build() {
      try {
         return VersionWrapper.get().getItemHandler().createCompoundTag(new HashMap<>(this.entries));
      } catch (Exception e) {
         return null;
      } catch (Error ex) {
         return null;
      }
   }

   public static CompoundTagBuilder create() {
      return new CompoundTagBuilder();
   }

   private static void checkNotNull(Object object) {
      if (object == null) {
         throw new NullPointerException();
      }
   }
}
