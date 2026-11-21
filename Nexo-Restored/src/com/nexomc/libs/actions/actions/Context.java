package com.nexomc.libs.actions.actions;

import com.nexomc.libs.actions.utils.Strings;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Context<T> implements Iterable<Action<T>> {
   private final Map<String, Object> data;
   private final List<Action<T>> actions;
   private final Map<String, String> stringData = new HashMap();

   public Context(List<Action<T>> actions, Map<String, Object> data) {
      this.actions = actions;
      this.data = data;
      data.forEach((key, value) -> this.stringData.put(key, String.valueOf(value)));
   }

   public @NotNull Iterator<Action<T>> iterator() {
      return this.actions.iterator();
   }

   public void storeData(@NotNull String key, @NotNull Object value) {
      this.data.put(key, value);
      this.stringData.put(key, String.valueOf(value));
   }

   public @Nullable Object getData(@NotNull String key) {
      return this.data.get(key);
   }

   public @NotNull String replaceData(@NotNull String input) {
      return !this.data.isEmpty() && !input.trim().isEmpty() ? Strings.replaceEach(input, (String[])this.stringData.keySet().toArray(new String[0]), (String[])this.stringData.values().toArray(new String[0])) : input;
   }
}
