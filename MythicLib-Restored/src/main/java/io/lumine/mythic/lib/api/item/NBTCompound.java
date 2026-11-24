package io.lumine.mythic.lib.api.item;

import java.util.Set;

public abstract class NBTCompound {
   public abstract boolean hasTag(String var1);

   public abstract Object get(String var1);

   public abstract String getString(String var1);

   public abstract boolean getBoolean(String var1);

   public abstract double getDouble(String var1);

   public abstract int getInteger(String var1);

   public abstract NBTCompound getNBTCompound(String var1);

   public abstract Set<String> getTags();

   public abstract int getTypeId(String var1);
}
