package me.TechsCode.UltraPermissions.base.command;

import java.util.List;

public abstract class Argument<T> {
   public Argument() {
   }

   public boolean hasMatch(String text) {
      return this.get(text).getValidity() != Validity.NO_MATCH;
   }

   public abstract ArgumentValue<T> get(String text);

   public abstract String getUsage();

   public abstract List<String> getTabCompletions();
}
