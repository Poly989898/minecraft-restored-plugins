package io.lumine.mythic.lib.manager;

import io.lumine.mythic.lib.MythicLib;
import com.google.gson.JsonElement;

@Deprecated
public class JsonManager {
   public <T> T parse(String s, Class<T> c) {
      return MythicLib.plugin.getGson().fromJson(s, c);
   }

   public String toString(JsonElement json) {
      return MythicLib.plugin.getGson().toJson(json);
   }
}
