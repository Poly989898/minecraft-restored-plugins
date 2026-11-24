package me.TechsCode.UltraPermissions.commands.arguments;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.command.Argument;
import me.TechsCode.UltraPermissions.base.command.ArgumentValue;
import me.TechsCode.UltraPermissions.base.command.EmptyReason;
import me.TechsCode.UltraPermissions.storage.objects.User;

public class UserArgument extends Argument<User> {
   private UltraPermissions plugin;
   private EmptyReason emptyReason;

   public UserArgument(UltraPermissions ultraPermissions, EmptyReason emptyReason) {
      this.plugin = ultraPermissions;
      this.emptyReason = emptyReason;
   }

   @Override
   public ArgumentValue<User> get(String text) {
      Optional<User> name = this.plugin.getUsers().name(text);
      return name.<ArgumentValue<User>>map(user -> ArgumentValue.of(text, user)).orElseGet(() -> ArgumentValue.empty(text, this.emptyReason));
   }

   @Override
   public String getUsage() {
      return "<User>";
   }

   @Override
   public List<String> getTabCompletions() {
      return this.plugin.getUsers().stream().map(User::getName).collect(Collectors.toList());
   }
}
