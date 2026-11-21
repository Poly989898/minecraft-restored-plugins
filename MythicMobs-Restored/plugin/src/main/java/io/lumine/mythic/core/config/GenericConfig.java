package io.lumine.mythic.core.config;

import io.lumine.mythic.core.skills.placeholders.parsers.PlaceholderColorImpl;

public interface GenericConfig {
   boolean getBoolean(String str);

   boolean getBoolean(String str, boolean flag);

   String getString(String str);

   String getString(String str, String str2);

   int getInteger(String str);

   int getInteger(String str, int value);

   PlaceholderColorImpl getPlaceholderColor(String str, String str2);
}
