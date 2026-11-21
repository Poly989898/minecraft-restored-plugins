package io.lumine.mythic.core.utils.jnbt;

import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.api.skills.placeholders.PlaceholderFloat;
import io.lumine.mythic.api.skills.placeholders.PlaceholderInt;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.uuid.DashedUUIDs;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.HashMap;
import java.util.List;
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

   public CompoundTagBuilder putPlaceholderDouble(String key, PlaceholderDouble value) {
      return this.put(key, new PlaceholderDoubleTag(value));
   }

   public CompoundTagBuilder putPlaceholderFloat(String key, PlaceholderFloat value) {
      return this.put(key, new PlaceholderFloatTag(value));
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

   public CompoundTagBuilder putPlaceholderInt(String key, PlaceholderInt value) {
      return this.put(key, new PlaceholderIntTag(value));
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

   public CompoundTagBuilder putPlaceholderString(String key, PlaceholderString value) {
      return this.put(key, new PlaceholderStringTag(value));
   }

   public CompoundTagBuilder putStringList(String key, List<String> value) {
      ListTagBuilder builder = ListTagBuilder.create(StringTag.class);
      value.forEach(e -> builder.add(new StringTag(e)));
      return this.put(key, builder.build());
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
         return MythicBukkit.inst().getVolatileCodeHandler().createCompoundTag(new HashMap<>(this.entries));
      } catch (Error | Exception e) {
         MythicLogger.error("This version of Minecraft is not supported for NBT/Data");
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
