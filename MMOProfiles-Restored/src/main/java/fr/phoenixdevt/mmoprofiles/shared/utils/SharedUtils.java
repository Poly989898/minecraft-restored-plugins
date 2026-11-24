package fr.phoenixdevt.mmoprofiles.shared.utils;

import com.google.gson.JsonArray;
import java.util.ArrayList;
import java.util.List;

public class SharedUtils {
   public static final String PROPERTY_KEY = "MMOProfilesData";
   public static final String PROPERTY_SIGNATURE = "ApprovedByTaner";
   public static final String TEMP_SERVER_KEY = "UsingProfileSelectionTempServer";
   public static final String MMOPROFILES_PROXIED_DATA_KEY = "MMOProfilesProxiedData";

   public static JsonArray toArray(List<String> stringList) {
      JsonArray array = new JsonArray();
      stringList.forEach(entry -> array.add(entry));
      return array;
   }

   public static List<String> toStringList(JsonArray array) {
      List<String> list = new ArrayList<>();
      array.forEach(el -> list.add(el.getAsString()));
      return list;
   }
}
