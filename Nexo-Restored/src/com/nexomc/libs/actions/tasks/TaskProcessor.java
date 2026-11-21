package com.nexomc.libs.actions.tasks;

import org.jetbrains.annotations.NotNull;

public interface TaskProcessor {
   void runAsync(@NotNull Runnable var1);

   void runAsync(@NotNull Runnable var1, long var2);

   void runSync(@NotNull Runnable var1);

   void runSync(@NotNull Runnable var1, long var2);
}
