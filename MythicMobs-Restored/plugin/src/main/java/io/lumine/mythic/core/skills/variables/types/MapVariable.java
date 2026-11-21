package io.lumine.mythic.core.skills.variables.types;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import io.lumine.mythic.api.skills.placeholders.PolymorphicPlaceholder;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.variables.Variable;
import io.lumine.mythic.core.skills.variables.VariableInfo;
import io.lumine.mythic.core.skills.variables.VariableType;
import io.lumine.mythic.core.skills.variables.interfaces.ContainingVariable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import lombok.Getter;

@Getter
public class MapVariable extends Variable implements ContainingVariable {
   final Map<String, String> value = new ConcurrentHashMap<>();
   public static final VariableInfo<Map> variableInfo = VariableInfo.<Map>builder()
      .clazz(MapVariable.class)
      .type(VariableType.MAP)
      .typeName("Map")
      .valueClass(Map.class)
      .defaultValue(PolymorphicPlaceholder.of(""))
      .objectConstructor(MapVariable::ofObject)
      .polymorphicConstructor(MapVariable::of)
      .build();

   @Override
   public VariableInfo<Map> getVariableInfo() {
      return variableInfo;
   }

   public static MapVariable of(PolymorphicPlaceholder data, PlaceholderContext context, long expirationTime) {
      return new MapVariable(data.getString(context), expirationTime);
   }

   public static String toString(Map<String, String> data) {
      if (data.isEmpty()) {
         return "";
      } else {
         StringBuilder sb = new StringBuilder();
         boolean first = true;

         for (Entry<String, String> entry : data.entrySet()) {
            if (!first) {
               sb.append(';');
            }

            sb.append(entry.getKey()).append('=').append(entry.getValue());
            first = false;
         }

         return sb.toString();
      }
   }

   public static Map<String, String> fromString(String data) {
      return parse(data);
   }

   public static MapVariable ofObject(Object o, Long expirationTime) {
      return o instanceof Map map ? new MapVariable(map, expirationTime) : new MapVariable(String.valueOf(o), expirationTime);
   }

   public MapVariable(String value) {
      this.build(value);
   }

   public MapVariable(String value, long expirationTime) {
      super(expirationTime);
      this.build(value);
   }

   public MapVariable(Map<String, String> value, long expirationTime) {
      super(expirationTime);
      Preconditions.checkNotNull(value);
      this.value.putAll(value);
   }

   private void build(String value) {
      Preconditions.checkNotNull(value);
      this.value.putAll(fromString(value));
   }

   public void put(String key, String value) {
      this.value.put(key.trim(), value);
   }

   public void put(String rawData) {
      this.value.putAll(parse(rawData));
   }

   public void remove(String key) {
      String[] split = key.split(",");

      for (String s : split) {
         this.value.remove(s.trim());
      }
   }

   public String get(String key) {
      return this.value.getOrDefault(key, "UNDEFINED");
   }

   public String get(String key, String defaultValue) {
      return this.value.getOrDefault(key, defaultValue);
   }

   public static String getWithPipeDefault(Map<String, String> map, String key) {
      if (key.contains("|")) {
         String[] split = key.split("\\|");
         if (split.length > 1) {
            key = split[0];
            String defValue = split[1];
            return map.getOrDefault(key, defValue);
         }
      }

      return map.get(key);
   }

   public int size() {
      return this.value.size();
   }

   public boolean containsKey(String key) {
      return this.value.containsKey(key);
   }

   public String getKeys() {
      return ListVariable.toString(this.value.keySet());
   }

   public String getValues() {
      return ListVariable.toString(this.value.values());
   }

   public static Map<String, String> parse(String value) {
      Map<String, String> ret = new HashMap<>();
      String[] segments = value.split(";");

      for (String segment : segments) {
         String[] split = segment.split("=");
         if (split.length == 2) {
            ret.put(split[0].trim(), split[1]);
         }
      }

      return ret;
   }

   @Override
   public String toString() {
      return toString(this.value);
   }

   public Map<String, String> get() {
      return this.value;
   }

   public Map<String, String> getValueClone() {
      return Maps.newHashMap(this.value);
   }

   @Override
   public boolean executeAddition(PolymorphicPlaceholder data, PlaceholderContext context) {
      this.put(data.getString(context));
      return true;
   }

   @Override
   public boolean executeSubtraction(PolymorphicPlaceholder data, PlaceholderContext context) {
      this.remove(data.getString(context));
      return true;
   }

   @Override
   public boolean checkEquality(PolymorphicPlaceholder data, PlaceholderContext context) {
      Map<String, String> newMap = parse(data.getString(context));
      return this.value.equals(newMap);
   }

   @Override
   public boolean checkContainsAll(PolymorphicPlaceholder data, PlaceholderContext context) {
      Map<String, String> newMap = parse(data.getString(context));

      for (String newKey : newMap.keySet()) {
         if (!this.value.containsKey(newKey)) {
            return false;
         }
      }

      return true;
   }

   @Generated
   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof MapVariable other)) {
         return false;
      } else if (!other.canEqual(this)) {
         return false;
      } else {
         Object this$value = this.getValue();
         Object other$value = other.getValue();
         return this$value == null ? other$value == null : this$value.equals(other$value);
      }
   }

   @Generated
   protected boolean canEqual(Object other) {
      return other instanceof MapVariable;
   }

   @Generated
   @Override
   public int hashCode() {
      int PRIME = 59;
      int result = 1;
      Object $value = this.getValue();
      return result * 59 + ($value == null ? 43 : $value.hashCode());
   }
}
