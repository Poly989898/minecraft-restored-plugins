package me.TechsCode.UltraPermissions.commands.arguments;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.command.Argument;
import me.TechsCode.UltraPermissions.base.command.ArgumentValue;
import me.TechsCode.UltraPermissions.base.command.EmptyReason;
import me.TechsCode.UltraPermissions.storage.objects.Group;

public class GroupArgument extends Argument<Group> {
   private UltraPermissions plugin;
   private EmptyReason emptyReason;

   public GroupArgument(UltraPermissions ultraPermissions, EmptyReason emptyReason) {
      this.plugin = ultraPermissions;
      this.emptyReason = emptyReason;
   }

   @Override
   public ArgumentValue<Group> get(String text) {
      Optional<Group> name = this.plugin.getGroups().name(text);
      return name.<ArgumentValue<Group>>map(group -> ArgumentValue.of(text, group)).orElseGet(() -> ArgumentValue.empty(text, this.emptyReason));
   }

   @Override
   public String getUsage() {
      return "<Group>";
   }

   @Override
   public List<String> getTabCompletions() {
      return this.plugin.getGroups().stream().map(Group::getName).collect(Collectors.toList());
   }
}
