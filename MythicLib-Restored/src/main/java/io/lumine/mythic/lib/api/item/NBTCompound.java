package io.lumine.mythic.lib.api.item;

import java.util.Set;

public abstract class NBTCompound {
   public abstract boolean hasTag(String text);

   public abstract Object get(String text);

   public abstract String getString(String text);

   public abstract boolean getBoolean(String text);

   public abstract double getDouble(String text);

   public abstract int getInteger(String text);

   public abstract NBTCompound getNBTCompound(String text);

   public abstract Set<String> getTags();

   public abstract int getTypeId(String text);
}
