package me.TechsCode.UltraPermissions.commands.arguments;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.command.Argument;
import me.TechsCode.UltraPermissions.base.command.ArgumentValue;
import me.TechsCode.UltraPermissions.base.command.EmptyReason;
import me.TechsCode.UltraPermissions.storage.objects.GroupPath;

public class GroupPathArgument extends Argument<GroupPath> {
   private UltraPermissions plugin;
   private EmptyReason emptyReason;

   public GroupPathArgument(UltraPermissions ultraPermissions, EmptyReason emptyReason) {
      this.plugin = ultraPermissions;
      this.emptyReason = emptyReason;
   }

   @Override
   public ArgumentValue<GroupPath> get(String text) {
      Optional<GroupPath> name = this.plugin.getGroupPaths().name(text);
      return name.<ArgumentValue<GroupPath>>map(groupPath -> ArgumentValue.of(text, groupPath)).orElseGet(() -> ArgumentValue.empty(text, this.emptyReason));
   }

   @Override
   public String getUsage() {
      return "<GroupPath>";
   }

   @Override
   public List<String> getTabCompletions() {
      return this.plugin.getGroupPaths().stream().map(GroupPath::getName).collect(Collectors.toList());
   }
}
