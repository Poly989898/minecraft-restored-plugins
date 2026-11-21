package io.lumine.mythic.bukkit.utils.nbt;

import io.lumine.mythic.bukkit.utils.shadows.nbt.MojangsonParser;
import io.lumine.mythic.bukkit.utils.shadows.nbt.NBTBase;
import io.lumine.mythic.bukkit.utils.shadows.nbt.NBTTagCompound;
import io.lumine.shadow.ShadowFactory;

public final class NBT {
   private static MojangsonParser parser = null;

   private static MojangsonParser parser() {
      return parser == null ? (parser = ShadowFactory.global().staticShadow(MojangsonParser.class)) : parser;
   }

   public static NBTBase shadow(Object nbtObject) {
      NBTBase shadow = ShadowFactory.global().shadow(NBTBase.class, nbtObject);
      NBTTagType type = shadow.getType();
      Class<? extends NBTBase> realClass = type.shadowClass();
      return ShadowFactory.global().shadow(realClass, nbtObject);
   }

   public static NBTTagCompound parse(String s) {
      return parser().parse(s);
   }

   private NBT() {
   }
}
