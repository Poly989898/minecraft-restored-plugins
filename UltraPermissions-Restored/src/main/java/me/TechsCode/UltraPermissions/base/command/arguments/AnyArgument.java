package me.TechsCode.UltraPermissions.base.command.arguments;

import java.util.Collections;
import java.util.List;
import me.TechsCode.UltraPermissions.base.command.Argument;
import me.TechsCode.UltraPermissions.base.command.ArgumentValue;

public class AnyArgument extends Argument<String> {
   private final String usage;

   public AnyArgument(String text) {
      this.usage = text;
   }

   @Override
   public String getUsage() {
      return this.usage;
   }

   @Override
   public ArgumentValue<String> get(String text) {
      return ArgumentValue.of(text, text);
   }

   @Override
   public List<String> getTabCompletions() {
      return Collections.emptyList();
   }
}
