package litebans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public final class ConfigSection {
   private static final char CHAR_VALUE = '.';
   final Map keys = new LinkedHashMap();
   private final ConfigSection section;

   public ConfigSection() {
      this(null);
   }

   public ConfigSection(ConfigSection configSection) {
      this(new LinkedHashMap(), configSection);
   }

   public ConfigSection(Map map, ConfigSection configSection) {
      this.section = configSection;

      for (Entry entry : (Iterable<Entry>) map.entrySet()) {
         String text = entry.getKey() == null ? "null" : entry.getKey().toString();
         if (entry.getValue() instanceof Map) {
            this.keys.put(text, new ConfigSection((Map)entry.getValue(), configSection == null ? null : configSection.getSection(text)));
         } else {
            this.keys.put(text, entry.getValue());
         }
      }
   }

   private final ConfigSection computeConfigSection(String text) {
      int indexOf2 = text.indexOf(46);
      if (indexOf2 == -1) {
         return this;
      }

      String substring2 = text.substring(0, indexOf2);
      Object obj = this.keys.get(substring2);
      if (obj == null) {
         obj = new ConfigSection(this.section == null ? null : this.section.getSection(substring2));
         this.keys.put(substring2, obj);
      }

      return (ConfigSection)obj;
   }

   private final String computeText(String text) {
      int indexOf2 = text.indexOf(46);
      return indexOf2 == -1 ? text : text.substring(indexOf2 + 1);
   }

   public Object computeObj(String text, Object obj) {
      ConfigSection configSection = this.computeConfigSection(text);
      Object obj2 = null;
      if (configSection == this) {
         obj2 = this.keys.get(text);
      } else {
         obj2 = configSection.computeObj(this.computeText(text), obj);
      }

      if (obj2 == null && obj instanceof ConfigSection) {
         this.keys.put(text, obj);
      }

      return obj2 != null ? obj2 : obj;
   }

   public Object computeObj(String text) {
      return this.computeObj(text, this.computeObj2(text));
   }

   public Object computeObj2(String text) {
      return this.section == null ? null : this.section.computeObj(text);
   }

   public void set(String text, Object obj) {
      if (obj instanceof Map) {
         obj = new ConfigSection((Map)obj, this.section == null ? null : this.section.getSection(text));
      }

      ConfigSection configSection = this.computeConfigSection(text);
      if (configSection == this) {
         if (obj == null) {
            this.keys.remove(text);
         } else {
            this.keys.put(text, obj);
         }
      } else {
         configSection.set(this.computeText(text), obj);
      }
   }

   public ConfigSection getSection(String text) {
      Object obj = this.computeObj2(text);
      return (ConfigSection)this.computeObj(text, obj instanceof ConfigSection ? obj : new ConfigSection(this.section == null ? null : this.section.getSection(text)));
   }

   public Collection getKeys() {
      return new LinkedHashSet(this.keys.keySet());
   }

   public int getInt(String text, int value) {
      Object obj = this.computeObj(text, value);
      return obj instanceof Number ? ((Number)obj).intValue() : value;
   }

   public long getLong(String text, long value) {
      Object obj = this.computeObj(text, value);
      return obj instanceof Number ? ((Number)obj).longValue() : value;
   }

   public double getDouble(String text) {
      Object obj = this.computeObj2(text);
      return this.getDouble(text, obj instanceof Number ? ((Number)obj).doubleValue() : 0.0);
   }

   public double getDouble(String text, double number) {
      Object obj = this.computeObj(text, number);
      return obj instanceof Number ? ((Number)obj).doubleValue() : number;
   }

   public boolean getBoolean(String text) {
      Object obj = this.computeObj2(text);
      return this.getBoolean(text, obj instanceof Boolean ? (Boolean)obj : false);
   }

   public boolean getBoolean(String text, boolean flag) {
      Object obj = this.computeObj(text, flag);
      return obj instanceof Boolean ? (Boolean)obj : flag;
   }

   public String getString(String text) {
      Object obj = this.computeObj2(text);
      return this.getString(text, obj instanceof String ? (String)obj : "");
   }

   public String getString(String text, String text2) {
      Object obj = this.computeObj(text, text2);
      return obj instanceof String ? (String)obj : text2;
   }

   public List computeList(String text) {
      List list = this.computeList2(text);
      ArrayList arrayList = new ArrayList();

      for (Object obj : list) {
         if (obj instanceof String) {
            arrayList.add((String)obj);
         }
      }

      return arrayList;
   }

   public List computeList2(String text) {
      Object obj = this.computeObj2(text);
      return this.computeList(text, obj instanceof List ? (List)obj : Collections.EMPTY_LIST);
   }

   public List computeList(String text, List list) {
      Object obj = this.computeObj(text, list);
      return obj instanceof List ? (List)obj : list;
   }
}
