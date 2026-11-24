package me.TechsCode.UltraPermissions.base.fileconf;

import java.util.Set;

public interface Configuration {
   Object get(String text);

   int getInt(String text);

   String getString(String text);

   boolean getBoolean(String text);

   Set<String> getKeys(boolean flag);

   boolean contains(String text);

   void set(String text, Object obj);

   void save();
}
