package com.nexomc.libs.actions.actions.implementations;

import com.nexomc.libs.actions.actions.Action;
import com.nexomc.libs.actions.actions.ActionMeta;
import com.nexomc.libs.actions.actions.Context;
import org.jetbrains.annotations.NotNull;

public class DataAction<T> extends Action<T> {
   public static final String ID = "data";

   public DataAction(@NotNull ActionMeta<T> meta) {
      super(meta);
   }

   public void run(@NotNull T t, @NotNull Context<T> context) {
      String[] parts = this.getMeta().getParsedData(t, context).split(" ", 2);
      if (parts.length == 2) {
         context.storeData(parts[0], parts[1]);
      }
   }
}
