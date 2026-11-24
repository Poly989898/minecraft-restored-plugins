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

   public boolean hasValue(@Nullable Type var1, @Nullable String var2) {
      return var1 != null && var2 != null ? this.typeMap.containsKey(var1) && this.typeMap.get(var1).idMap.containsKey(var2) : false;
   }

   @Nullable
   public C getValue(@Nullable Type var1, @Nullable String var2) {
      if (var1 != null && var2 != null) {
         TemplateMap<C>.Submap var3 = this.typeMap.get(var1);
         return var3 == null ? null : var3.idMap.get(var2);
      } else {
         return null;
      }
   }

   public void removeValue(@Nullable Type var1, @Nullable String var2) {
      if (var1 != null && var2 != null) {
         if (this.typeMap.containsKey(var1)) {
            this.typeMap.get(var1).idMap.remove(var2);
         }
      }
   }

   public void setValue(@NotNull Type var1, @NotNull String var2, @NotNull C var3) {
      Validate.notNull(var3, "Value cannot be null");
      if (!this.typeMap.containsKey(var1)) {
         this.typeMap.put(var1, new TemplateMap.Submap());
      }

      this.typeMap.get(var1).idMap.put(var2, (C)var3);
   }

   public void forEach(@NotNull Consumer<C> var1) {
      this.typeMap.values().forEach(var1x -> var1x.idMap.values().forEach(var1));
   }

   @NotNull
   public Collection<C> collectValues() {
      HashSet var1 = new HashSet();
      this.typeMap.values().forEach(var1x -> var1.addAll(var1x.idMap.values()));
      return var1;
   }

   @NotNull
   public Collection<C> collectValues(@NotNull Type var1) {
      return this.typeMap.containsKey(var1) ? this.typeMap.get(var1).idMap.values() : new HashSet<>();
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
