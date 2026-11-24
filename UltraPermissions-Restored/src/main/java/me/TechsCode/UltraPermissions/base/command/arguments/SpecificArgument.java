package me.TechsCode.UltraPermissions.base.command.arguments;

import java.util.Collections;
import java.util.List;
import me.TechsCode.UltraPermissions.base.command.Argument;
import me.TechsCode.UltraPermissions.base.command.ArgumentValue;
import me.TechsCode.UltraPermissions.base.command.EmptyReason;

public class SpecificArgument extends Argument<String> {
   private final String argument;

   public SpecificArgument(String text) {
      this.argument = text;
   }

   @Override
   public String getUsage() {
      return this.argument;
   }

   @Override
   public ArgumentValue<String> get(String text) {
      return this.argument.equalsIgnoreCase(text) ? ArgumentValue.of(text, this.argument) : ArgumentValue.empty(text, EmptyReason.NO_MATCH);
   }

   @Override
   public List<String> getTabCompletions() {
      return Collections.singletonList(this.argument);
   }
}
