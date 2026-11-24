package me.TechsCode.UltraPermissions.base.command.arguments;

import java.util.Collections;
import java.util.List;
import me.TechsCode.UltraPermissions.base.command.Argument;
import me.TechsCode.UltraPermissions.base.command.ArgumentValue;
import me.TechsCode.UltraPermissions.base.command.EmptyReason;

public class AnyIntegerArgument extends Argument<Integer> {
   private final String usage;
   private final EmptyReason emptyReason;

   public AnyIntegerArgument(String text, EmptyReason emptyReason) {
      this.usage = text;
      this.emptyReason = emptyReason;
   }

   @Override
   public ArgumentValue<Integer> get(String text) {
      try {
         return ArgumentValue.of(text, Integer.parseInt(text));
      } catch (NumberFormatException exception) {
         return ArgumentValue.empty(text, this.emptyReason);
      }
   }

   @Override
   public String getUsage() {
      return this.usage;
   }

   @Override
   public List<String> getTabCompletions() {
      return Collections.emptyList();
   }
}
