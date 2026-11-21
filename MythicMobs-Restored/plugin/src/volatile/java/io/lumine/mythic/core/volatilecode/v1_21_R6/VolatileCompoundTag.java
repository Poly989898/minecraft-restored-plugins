package io.lumine.mythic.core.volatilecode.v1_21_R6;

import io.lumine.mythic.core.skills.placeholders.PlaceholderMeta;
import io.lumine.mythic.core.utils.jnbt.ByteArrayTag;
import io.lumine.mythic.core.utils.jnbt.ByteTag;
import io.lumine.mythic.core.utils.jnbt.CompoundTag;
import io.lumine.mythic.core.utils.jnbt.DoubleTag;
import io.lumine.mythic.core.utils.jnbt.EndTag;
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
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

public class VolatileCompoundTag extends CompoundTag {
   public VolatileCompoundTag(Map<String, Tag> value) {
      super(value);
   }

   public NBTTagCompound toNMSTag() {
      NBTTagCompound tag = new NBTTagCompound();

      for (Entry<String, Tag> entry : this.value.entrySet()) {
         if (entry.getValue() instanceof IntTag) {
            tag.a(entry.getKey(), ((IntTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof ByteTag) {
            tag.a(entry.getKey(), ((ByteTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof ByteArrayTag) {
            tag.a(entry.getKey(), ((ByteArrayTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof CompoundTag) {
            tag.a(entry.getKey(), ((VolatileCompoundTag)entry.getValue()).toNMSTag());
         } else if (entry.getValue() instanceof DoubleTag) {
            tag.a(entry.getKey(), ((DoubleTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof FloatTag) {
            tag.a(entry.getKey(), ((FloatTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof IntArrayTag) {
            tag.a(entry.getKey(), ((IntArrayTag)entry.getValue()).getValue());
         } else if (!(entry.getValue() instanceof ListTag)) {
            if (entry.getValue() instanceof LongTag) {
               tag.a(entry.getKey(), ((LongTag)entry.getValue()).getValue());
            } else if (entry.getValue() instanceof ShortTag) {
               tag.a(entry.getKey(), ((ShortTag)entry.getValue()).getValue());
            } else if (entry.getValue() instanceof StringTag) {
               tag.a(entry.getKey(), ((StringTag)entry.getValue()).getValue());
            } else if (entry.getValue() instanceof PlaceholderDoubleTag) {
               tag.a(entry.getKey(), ((PlaceholderDoubleTag)entry.getValue()).getValue().get());
            } else if (entry.getValue() instanceof PlaceholderFloatTag) {
               tag.a(entry.getKey(), ((PlaceholderFloatTag)entry.getValue()).getValue().get());
            } else if (entry.getValue() instanceof PlaceholderIntTag) {
               tag.a(entry.getKey(), ((PlaceholderIntTag)entry.getValue()).getValue().get());
            } else if (entry.getValue() instanceof PlaceholderStringTag) {
               tag.a(entry.getKey(), ((PlaceholderStringTag)entry.getValue()).getValue().get());
            }
         } else {
            NBTTagList list = new NBTTagList();

            for (Tag btag : ((ListTag)entry.getValue()).getValue()) {
               HashMap<String, Tag> btags = new HashMap<>();
               btags.put("test", btag);
               VolatileCompoundTag comp = new VolatileCompoundTag(btags);
               list.add(comp.toNMSTag().a("test"));
            }

            tag.a(entry.getKey(), list);
         }
      }

      return tag;
   }

   public NBTTagCompound toNMSTag(PlaceholderMeta meta) {
      NBTTagCompound tag = new NBTTagCompound();

      for (Entry<String, Tag> entry : this.value.entrySet()) {
         if (entry.getValue() instanceof IntTag) {
            tag.a(entry.getKey(), ((IntTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof ByteTag) {
            tag.a(entry.getKey(), ((ByteTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof ByteArrayTag) {
            tag.a(entry.getKey(), ((ByteArrayTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof CompoundTag) {
            tag.a(entry.getKey(), ((VolatileCompoundTag)entry.getValue()).toNMSTag(meta));
         } else if (entry.getValue() instanceof DoubleTag) {
            tag.a(entry.getKey(), ((DoubleTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof FloatTag) {
            tag.a(entry.getKey(), ((FloatTag)entry.getValue()).getValue());
         } else if (entry.getValue() instanceof IntArrayTag) {
            tag.a(entry.getKey(), ((IntArrayTag)entry.getValue()).getValue());
         } else if (!(entry.getValue() instanceof ListTag)) {
            if (entry.getValue() instanceof LongTag) {
               tag.a(entry.getKey(), ((LongTag)entry.getValue()).getValue());
            } else if (entry.getValue() instanceof ShortTag) {
               tag.a(entry.getKey(), ((ShortTag)entry.getValue()).getValue());
            } else if (entry.getValue() instanceof StringTag) {
               tag.a(entry.getKey(), ((StringTag)entry.getValue()).getValue());
            } else if (entry.getValue() instanceof PlaceholderDoubleTag) {
               tag.a(entry.getKey(), ((PlaceholderDoubleTag)entry.getValue()).getValue().get(meta));
            } else if (entry.getValue() instanceof PlaceholderFloatTag) {
               tag.a(entry.getKey(), ((PlaceholderFloatTag)entry.getValue()).getValue().get(meta));
            } else if (entry.getValue() instanceof PlaceholderIntTag) {
               tag.a(entry.getKey(), ((PlaceholderIntTag)entry.getValue()).getValue().get(meta));
            } else if (entry.getValue() instanceof PlaceholderStringTag) {
               tag.a(entry.getKey(), ((PlaceholderStringTag)entry.getValue()).getValue().get(meta));
            }
         } else {
            NBTTagList list = new NBTTagList();

            for (Tag btag : ((ListTag)entry.getValue()).getValue()) {
               HashMap<String, Tag> btags = new HashMap<>();
               btags.put("test", btag);
               VolatileCompoundTag comp = new VolatileCompoundTag(btags);
               list.add(comp.toNMSTag(meta).a("test"));
            }

            tag.a(entry.getKey(), list);
         }
      }

      return tag;
   }

   public static CompoundTag fromNMSTag(NBTTagCompound tag) {
      HashMap<String, Tag> tags = new HashMap<>();

      for (String key : tag.e()) {
         NBTBase base = tag.a(key);
         if (base instanceof NBTTagInt intTag) {
            tags.put(key, new IntTag(intTag.h()));
         } else if (base instanceof NBTTagByte byteTag) {
            tags.put(key, new ByteTag(byteTag.j()));
         } else if (base instanceof NBTTagFloat floatTag) {
            tags.put(key, new FloatTag(floatTag.l()));
         } else if (base instanceof NBTTagDouble doubleTag) {
            tags.put(key, new DoubleTag(doubleTag.k()));
         } else if (base instanceof NBTTagByteArray byteArrayTag) {
            tags.put(key, new ByteArrayTag(byteArrayTag.e()));
         } else if (base instanceof NBTTagIntArray intArrayTag) {
            tags.put(key, new IntArrayTag(intArrayTag.g()));
         } else if (base instanceof NBTTagCompound) {
            tags.put(key, fromNMSTag((NBTTagCompound)base));
         } else if (base instanceof NBTTagEnd) {
            tags.put(key, new EndTag());
         } else if (base instanceof NBTTagLong longTag) {
            tags.put(key, new LongTag(longTag.g()));
         } else if (base instanceof NBTTagShort shortTag) {
            tags.put(key, new ShortTag(shortTag.i()));
         } else if (base instanceof NBTTagString stringTag) {
            tags.put(key, new StringTag(stringTag.k()));
         } else if (base instanceof NBTTagList list && !list.isEmpty()) {
            NBTBase nbase = list.c(0);
            NBTTagCompound comp = new NBTTagCompound();
            comp.a("test", nbase);
            ListTagBuilder ltb = new ListTagBuilder((Class<? extends Tag>)fromNMSTag(comp).getValue().get("test").getClass());

            for (int i = 0; i < list.size(); i++) {
               NBTBase nbase2 = list.c(i);
               NBTTagCompound comp2 = new NBTTagCompound();
               comp2.a("test", nbase2);
               ltb.add(fromNMSTag(comp2).getValue().get("test"));
            }

            tags.put(key, ltb.build());
         }
      }

      return new VolatileCompoundTag(tags);
   }
}
