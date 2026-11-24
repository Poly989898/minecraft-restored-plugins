package net.Indyuce.mmoitems.api.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;
import net.Indyuce.mmoitems.api.Type;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TemplateMap<C> {
   private final Map<Type, TemplateMap<C>.Submap> typeMap = new HashMap<>();

   public boolean hasValue(@Nullable Type type, @Nullable String text) {
      return type != null && text != null ? this.typeMap.containsKey(type) && this.typeMap.get(type).idMap.containsKey(text) : false;
   }

   @Nullable
   public C getValue(@Nullable Type type, @Nullable String text) {
      if (type != null && text != null) {
         TemplateMap<C>.Submap submap = this.typeMap.get(type);
         return submap == null ? null : submap.idMap.get(text);
      } else {
         return null;
      }
   }

   public void removeValue(@Nullable Type type, @Nullable String text) {
      if (type != null && text != null) {
         if (this.typeMap.containsKey(type)) {
            this.typeMap.get(type).idMap.remove(text);
         }
      }
   }

   public void setValue(@NotNull Type type, @NotNull String text, @NotNull C value) {
      Validate.notNull(value, "Value cannot be null");
      if (!this.typeMap.containsKey(type)) {
         this.typeMap.put(type, new TemplateMap.Submap());
      }

      this.typeMap.get(type).idMap.put(text, (C)value);
   }

   public void forEach(@NotNull Consumer<C> consumer) {
      this.typeMap.values().forEach(var1x -> var1x.idMap.values().forEach(consumer));
   }

   @NotNull
   public Collection<C> collectValues() {
      HashSet set = new HashSet();
      this.typeMap.values().forEach(var1x -> set.addAll(var1x.idMap.values()));
      return set;
   }

   @NotNull
   public Collection<C> collectValues(@NotNull Type type) {
      return this.typeMap.containsKey(type) ? this.typeMap.get(type).idMap.values() : new HashSet<>();
   }

   public void clear() {
      this.typeMap.clear();
   }

   private class Submap {
      private final Map<String, C> idMap = new LinkedHashMap<>();

      private Submap() {
      }
   }
}
