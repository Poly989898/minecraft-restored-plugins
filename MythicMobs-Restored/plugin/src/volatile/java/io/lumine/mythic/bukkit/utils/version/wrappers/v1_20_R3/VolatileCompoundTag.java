package io.lumine.mythic.bukkit.utils.version.wrappers.v1_20_R3;

import io.lumine.mythic.bukkit.utils.items.nbt.jnbt.ByteArrayTag;
import io.lumine.mythic.bukkit.utils.items.nbt.jnbt.ByteTag;
import io.lumine.mythic.bukkit.utils.items.nbt.jnbt.CompoundTag;
import io.lumine.mythic.bukkit.utils.items.nbt.jnbt.DoubleTag;
import io.lumine.mythic.bukkit.utils.items.nbt.jnbt.EndTag;
import io.lumine.mythic.bukkit.utils.items.nbt.jnbt.FloatTag;
import io.lumine.mythic.bukkit.utils.items.nbt.jnbt.IntArrayTag;
import io.lumine.mythic.bukkit.utils.items.nbt.jnbt.IntTag;
import io.lumine.mythic.bukkit.utils.items.nbt.jnbt.ListTag;
import io.lumine.mythic.bukkit.utils.items.nbt.jnbt.ListTagBuilder;
import io.lumine.mythic.bukkit.utils.items.nbt.jnbt.LongTag;
import io.lumine.mythic.bukkit.utils.items.nbt.jnbt.ShortTag;
import io.lumine.mythic.bukkit.utils.items.nbt.jnbt.StringTag;
import io.lumine.mythic.bukkit.utils.items.nbt.jnbt.Tag;
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
            }
         } else {
            NBTTagList list = new NBTTagList();

            for (Tag btag : ((ListTag)entry.getValue()).getValue()) {
               HashMap<String, Tag> btags = new HashMap<>();
               btags.put("test", btag);
               VolatileCompoundTag comp = new VolatileCompoundTag(btags);
               list.add(comp.toNMSTag().c("test"));
            }

            tag.a(entry.getKey(), list);
         }
      }

      return tag;
   }

   public static CompoundTag fromNMSTag(NBTTagCompound tag) {
      HashMap<String, Tag> tags = new HashMap<>();

      for (String key : tag.e()) {
         NBTBase base = tag.c(key);
         if (base instanceof NBTTagInt) {
            tags.put(key, new IntTag(((NBTTagInt)base).g()));
         } else if (base instanceof NBTTagByte) {
            tags.put(key, new ByteTag(((NBTTagByte)base).i()));
         } else if (base instanceof NBTTagFloat) {
            tags.put(key, new FloatTag(((NBTTagFloat)base).k()));
         } else if (base instanceof NBTTagDouble) {
            tags.put(key, new DoubleTag(((NBTTagDouble)base).j()));
         } else if (base instanceof NBTTagByteArray) {
            tags.put(key, new ByteArrayTag(((NBTTagByteArray)base).e()));
         } else if (base instanceof NBTTagIntArray) {
            tags.put(key, new IntArrayTag(((NBTTagIntArray)base).g()));
         } else if (base instanceof NBTTagCompound) {
            tags.put(key, fromNMSTag((NBTTagCompound)base));
         } else if (base instanceof NBTTagEnd) {
            tags.put(key, new EndTag());
         } else if (base instanceof NBTTagLong) {
            tags.put(key, new LongTag(((NBTTagLong)base).f()));
         } else if (base instanceof NBTTagShort) {
            tags.put(key, new ShortTag(((NBTTagShort)base).h()));
         } else if (base instanceof NBTTagString) {
            tags.put(key, new StringTag(((NBTTagString)base).t_()));
         } else if (base instanceof NBTTagList) {
            NBTTagList list = (NBTTagList)base;
            if (list.size() > 0) {
               NBTBase nbase = list.k(0);
               NBTTagCompound comp = new NBTTagCompound();
               comp.a("test", nbase);
               ListTagBuilder ltb = new ListTagBuilder((Class<? extends Tag>)fromNMSTag(comp).getValue().get("test").getClass());

               for (int i = 0; i < list.size(); i++) {
                  NBTBase nbase2 = list.k(i);
                  NBTTagCompound comp2 = new NBTTagCompound();
                  comp2.a("test", nbase2);
                  ltb.add(fromNMSTag(comp2).getValue().get("test"));
               }

               tags.put(key, ltb.build());
            }
         }
      }

      return new VolatileCompoundTag(tags);
   }
}
