package me.TechsCode.UltraPermissions.commands.arguments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.command.Argument;
import me.TechsCode.UltraPermissions.base.command.ArgumentValue;
import me.TechsCode.UltraPermissions.base.command.EmptyReason;

public class BooleanArgument extends Argument<Boolean> {
   private UltraPermissions plugin;
   private EmptyReason emptyReason;

   public BooleanArgument(UltraPermissions ultraPermissions, EmptyReason emptyReason) {
      this.plugin = ultraPermissions;
      this.emptyReason = emptyReason;
   }

   @Override
   public ArgumentValue<Boolean> get(String text) {
      return ArgumentValue.of(text, Boolean.getBoolean(text));
   }

   @Override
   public String getUsage() {
      return "<AssignDefaultGroup>";
   }

   @Override
   public List<String> getTabCompletions() {
      return new ArrayList<>(Arrays.asList("True", "False"));
   }
}
