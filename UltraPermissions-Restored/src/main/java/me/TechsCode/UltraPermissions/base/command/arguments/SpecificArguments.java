package me.TechsCode.UltraPermissions.base.command.arguments;

import java.util.Arrays;
import java.util.List;
import me.TechsCode.UltraPermissions.base.command.Argument;
import me.TechsCode.UltraPermissions.base.command.ArgumentValue;
import me.TechsCode.UltraPermissions.base.command.EmptyReason;

public class SpecificArguments extends Argument<String> {
   private final String[] arguments;

   public SpecificArguments(String[] texts) {
      this.arguments = texts;
   }

   @Override
   public ArgumentValue<String> get(String text) {
      return Arrays.stream(this.arguments)
         .filter(candidate -> candidate.equalsIgnoreCase(text))
         .findFirst()
         .map(candidate -> ArgumentValue.of(text, candidate))
         .orElse(ArgumentValue.empty(text, EmptyReason.NO_MATCH));
   }

   @Override
   public String getUsage() {
      return "<" + String.join(" / ", this.arguments) + ">";
   }

   @Override
   public List<String> getTabCompletions() {
      return Arrays.asList(this.arguments);
   }
}
