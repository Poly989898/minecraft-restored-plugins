package com.nexomc.libs.actions.placeholders;

import com.nexomc.libs.actions.actions.Context;
import java.util.HashSet;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public class PlaceholderManager {
   private final Set<PlaceholderProvider<?>> providers = new HashSet();

   public void register(@NotNull PlaceholderProvider<?> provider) {
      this.providers.add(provider);
   }

   public <T> @NotNull String replace(@NotNull T t, @NotNull String string, @NotNull Context<T> context) {
      return this.replace(t, context.replaceData(string));
   }

   public <T> @NotNull String replace(@NotNull T t, @NotNull String string) {
      for(PlaceholderProvider<?> provider : this.providers) {
         if (provider.getType().isAssignableFrom(t.getClass())) {
            string = provider.replace(t, string);
         }
      }

      return string;
   }
}
