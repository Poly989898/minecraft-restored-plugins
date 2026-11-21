package io.lumine.mythic.bukkit.utils.gson.adapters;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class HashBasedTableAdapter implements JsonDeserializer<Table<?, ?, ?>>, JsonSerializer<Table> {
   public static final HashBasedTableAdapter INSTANCE = new HashBasedTableAdapter();

   public JsonElement serialize(Table table, Type type, JsonSerializationContext context) {
      return context.serialize(table.rowMap());
   }

   public Table deserialize(JsonElement json, Type type, JsonDeserializationContext context) throws JsonParseException {
      Type[] typeArguments = ((ParameterizedType)type).getActualTypeArguments();
      Type parameterizedType = hashMapOf(typeArguments[0], hashMapOf(typeArguments[1], typeArguments[2]).getType()).getType();
      Map<?, Map<?, ?>> map = (Map<?, Map<?, ?>>)context.deserialize(json, parameterizedType);
      Table<Object, Object, Object> table = HashBasedTable.create();

      for (Object rowKey : map.keySet()) {
         Map<?, ?> rowMap = map.get(rowKey);

         for (Object columnKey : rowMap.keySet()) {
            Object value = rowMap.get(columnKey);
            table.put(rowKey, columnKey, value);
         }
      }

      return table;
   }

   static <K, V> TypeToken<HashMap<K, V>> hashMapOf(Type key, Type value) {
      TypeParameter<K> newKeyTypeParameter = new TypeParameter<K>() {};
      TypeParameter<V> newValueTypeParameter = new TypeParameter<V>() {};
      return (new TypeToken<HashMap<K, V>>() {}).where(newKeyTypeParameter, typeTokenOf(key)).where(newValueTypeParameter, typeTokenOf(value));
   }

   private static <E> TypeToken<E> typeTokenOf(Type type) {
      return (TypeToken<E>)TypeToken.of(type);
   }
}
