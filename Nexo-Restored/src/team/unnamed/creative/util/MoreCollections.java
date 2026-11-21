package team.unnamed.creative.util;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class MoreCollections {
   private MoreCollections() {
   }

   public static <T> List<T> immutableListOf(List<T> list) {
      return list.isEmpty() ? Collections.emptyList() : List.copyOf(list);
   }

   public static <K, V> Map<K, V> immutableMapOf(Map<K, V> map) {
      return map.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(new LinkedHashMap(map));
   }
}
