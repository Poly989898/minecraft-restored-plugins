package com.ticxo.modelengine.api.utils;

import java.util.ArrayList;
import java.util.List;

public class RunnableGroup {
   private final List<Runnable> runnables = new ArrayList<>();

   public void add(Runnable runnable) {
      this.runnables.add(runnable);
   }

   public void execute() {
      this.runnables.forEach(Runnable::run);
   }
}
