package io.lumine.mythic.core.volatilecode.v26_3;

import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.utils.jnbt.ByteArrayTag;
import io.lumine.mythic.core.utils.jnbt.ByteTag;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.DoubleTag;
import io.lumine.mythic.core.utils.jnbt.FloatTag;
import io.lumine.mythic.core.utils.jnbt.IntArrayTag;
import io.lumine.mythic.core.utils.jnbt.IntTag;
import io.lumine.mythic.core.utils.jnbt.ListTag;
import io.lumine.mythic.core.utils.jnbt.ListTagBuilder;
import io.lumine.mythic.core.utils.jnbt.LongTag;
import io.lumine.mythic.core.utils.jnbt.PlaceholderDoubleTag;
import io.lumine.mythic.core.utils.jnbt.PlaceholderFloatTag;
import io.lumine.mythic.core.utils.jnbt.PlaceholderIntTag;
import io.lumine.mythic.core.utils.jnbt.PlaceholderStringTag;
import io.lumine.mythic.core.utils.jnbt.ShortTag;
import io.lumine.mythic.core.utils.jnbt.StringTag;
import io.lumine.mythic.core.utils.jnbt.Tag;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.nbt.EndTag;

public class VolatileCompoundTag extends CompoundTag {
   public VolatileCompoundTag(Map<String, Tag> value) {
      super(value);
   }

   public net.minecraft.nbt.CompoundTag toNMSTag() {
      net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();

      for (Entry<String, Tag> entry : this.value.entrySet()) {
         if (entry.getValue() instanceof IntTag) {
            tag.putInt(entry.getKey(), ((IntTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof ByteTag) {
            tag.putByte(entry.getKey(), ((ByteTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof ByteArrayTag) {
            tag.putByteArray(entry.getKey(), ((ByteArrayTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof CompoundTag) {
            tag.put(entry.getKey(), ((VolatileCompoundTag)entry.getValue()).toNMSTag());
         } else if (entry.getValue() instanceof DoubleTag) {
            tag.putDouble(entry.getKey(), ((DoubleTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof FloatTag) {
            tag.putFloat(entry.getKey(), ((FloatTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof IntArrayTag) {
            tag.putIntArray(entry.getKey(), ((IntArrayTag)entry.getValue()).getValue());
         } else if (!(entry.getValue() instanceof ListTag)) {
            if (entry.getValue() instanceof LongTag) {
               tag.putLong(entry.getKey(), ((LongTag)entry.getValue()).getValue());
            } else if (entry.getValue() instanceof ShortTag) {
               tag.putShort(entry.getKey(), ((ShortTag)entry.getValue()).getValue());
            } else if (entry.getValue() instanceof StringTag) {
               tag.putString(entry.getKey(), ((StringTag)entry.getValue()).getValue());
            } else if (entry.getValue() instanceof PlaceholderDoubleTag) {
               tag.putDouble(entry.getKey(), ((PlaceholderDoubleTag)entry.getValue()).getValue().get());
            } else if (entry.getValue() instanceof PlaceholderFloatTag) {
               tag.putFloat(entry.getKey(), ((PlaceholderFloatTag)entry.getValue()).getValue().get());
            } else if (entry.getValue() instanceof PlaceholderIntTag) {
               tag.putInt(entry.getKey(), ((PlaceholderIntTag)entry.getValue()).getValue().get());
            } else if (entry.getValue() instanceof PlaceholderStringTag) {
               tag.putString(entry.getKey(), ((PlaceholderStringTag)entry.getValue()).getValue().get());
            }
         } else {
            net.minecraft.nbt.ListTag list = new net.minecraft.nbt.ListTag();

            for (Tag btag : ((ListTag)entry.getValue()).getValue()) {
               HashMap<String, Tag> btags = new HashMap<>();
               btags.put("test", btag);
               VolatileCompoundTag comp = new VolatileCompoundTag(btags);
               list.add(comp.toNMSTag().get("test"));
            }

            tag.put(entry.getKey(), list);
         }
      }

      return tag;
   }

   public net.minecraft.nbt.CompoundTag toNMSTag(PlaceholderMeta meta) {
      net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();

      for (Entry<String, Tag> entry : this.value.entrySet()) {
         if (entry.getValue() instanceof IntTag) {
            tag.putInt(entry.getKey(), ((IntTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof ByteTag) {
            tag.putByte(entry.getKey(), ((ByteTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof ByteArrayTag) {
            tag.putByteArray(entry.getKey(), ((ByteArrayTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof CompoundTag) {
            tag.put(entry.getKey(), ((VolatileCompoundTag)entry.getValue()).toNMSTag(meta));
         } else if (entry.getValue() instanceof DoubleTag) {
            tag.putDouble(entry.getKey(), ((DoubleTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof FloatTag) {
            tag.putFloat(entry.getKey(), ((FloatTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof IntArrayTag) {
            tag.putIntArray(entry.getKey(), ((IntArrayTag)entry.getValue()).getValue());
         } else if (!(entry.getValue() instanceof ListTag)) {
            if (entry.getValue() instanceof LongTag) {
               tag.putLong(entry.getKey(), ((LongTag)entry.getValue()).getValue());
            } else if (entry.getValue() instanceof ShortTag) {
               tag.putShort(entry.getKey(), ((ShortTag)entry.getValue()).getValue());
            } else if (entry.getValue() instanceof StringTag) {
               tag.putString(entry.getKey(), ((StringTag)entry.getValue()).getValue());
            } else if (entry.getValue() instanceof PlaceholderDoubleTag) {
               tag.putDouble(entry.getKey(), ((PlaceholderDoubleTag)entry.getValue()).getValue().get(meta));
            } else if (entry.getValue() instanceof PlaceholderFloatTag) {
               tag.putFloat(entry.getKey(), ((PlaceholderFloatTag)entry.getValue()).getValue().get(meta));
            } else if (entry.getValue() instanceof PlaceholderIntTag) {
               tag.putInt(entry.getKey(), ((PlaceholderIntTag)entry.getValue()).getValue().get(meta));
            } else if (entry.getValue() instanceof PlaceholderStringTag) {
               tag.putString(entry.getKey(), ((PlaceholderStringTag)entry.getValue()).getValue().get(meta));
            }
         } else {
            net.minecraft.nbt.ListTag list = new net.minecraft.nbt.ListTag();

            for (Tag btag : ((ListTag)entry.getValue()).getValue()) {
               HashMap<String, Tag> btags = new HashMap<>();
               btags.put("test", btag);
               VolatileCompoundTag comp = new VolatileCompoundTag(btags);
               list.add(comp.toNMSTag(meta).get("test"));
            }

            tag.put(entry.getKey(), list);
         }
      }

      return tag;
   }

   public static CompoundTag fromNMSTag(net.minecraft.nbt.CompoundTag tag) {
      HashMap<String, Tag> tags = new HashMap<>();

      for (String key : tag.keySet()) {
         net.minecraft.nbt.Tag base = tag.get(key);
         if (base instanceof net.minecraft.nbt.IntTag intTag) {
            tags.put(key, new IntTag(intTag.intValue()));
         } else if (base instanceof net.minecraft.nbt.ByteTag byteTag) {
            tags.put(key, new ByteTag(byteTag.byteValue()));
         } else if (base instanceof net.minecraft.nbt.FloatTag floatTag) {
            tags.put(key, new FloatTag(floatTag.floatValue()));
         } else if (base instanceof net.minecraft.nbt.DoubleTag doubleTag) {
            tags.put(key, new DoubleTag(doubleTag.doubleValue()));
         } else if (base instanceof net.minecraft.nbt.ByteArrayTag byteArrayTag) {
            tags.put(key, new ByteArrayTag(byteArrayTag.getAsByteArray()));
         } else if (base instanceof net.minecraft.nbt.IntArrayTag intArrayTag) {
            tags.put(key, new IntArrayTag(intArrayTag.getAsIntArray()));
         } else if (base instanceof net.minecraft.nbt.CompoundTag) {
            tags.put(key, fromNMSTag((net.minecraft.nbt.CompoundTag)base));
         } else if (base instanceof EndTag) {
            tags.put(key, new io.lumine.mythic.core.utils.jnbt.EndTag());
         } else if (base instanceof net.minecraft.nbt.LongTag longTag) {
            tags.put(key, new LongTag(longTag.longValue()));
         } else if (base instanceof net.minecraft.nbt.ShortTag shortTag) {
            tags.put(key, new ShortTag(shortTag.shortValue()));
         } else if (base instanceof net.minecraft.nbt.StringTag stringTag) {
            tags.put(key, new StringTag(stringTag.value()));
         } else if (base instanceof net.minecraft.nbt.ListTag list && !list.isEmpty()) {
            net.minecraft.nbt.Tag nbase = list.get(0);
            net.minecraft.nbt.CompoundTag comp = new net.minecraft.nbt.CompoundTag();
            comp.put("test", nbase);
            ListTagBuilder ltb = new ListTagBuilder((Class<? extends Tag>)fromNMSTag(comp).getValue().get("test").getClass());

            for (int i = 0; i < list.size(); i++) {
               net.minecraft.nbt.Tag nbase2 = list.get(i);
               net.minecraft.nbt.CompoundTag comp2 = new net.minecraft.nbt.CompoundTag();
               comp2.put("test", nbase2);
               ltb.add(fromNMSTag(comp2).getValue().get("test"));
            }

            tags.put(key, ltb.build());
         }
      }

      return new VolatileCompoundTag(tags);
   }
}
