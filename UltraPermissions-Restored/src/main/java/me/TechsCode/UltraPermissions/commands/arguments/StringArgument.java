package me.TechsCode.UltraPermissions.commands.arguments;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.command.Argument;
import me.TechsCode.UltraPermissions.base.command.ArgumentValue;
import me.TechsCode.UltraPermissions.permissionDatabase.PermissionInfo;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class StringArgument extends Argument<String> {
   private UltraPermissions plugin;
   private String usage;

   public StringArgument(UltraPermissions ultraPermissions, String text) {
      this.plugin = ultraPermissions;
   }

   @Override
   public ArgumentValue<String> get(String text) {
      return ArgumentValue.of(text, text);
   }

   @Override
   public String getUsage() {
      return "<" + this.usage + ">";
   }

   @Override
   public List<String> getTabCompletions() {
      List list = Arrays.stream(Bukkit.getPluginManager().getPlugins()).map(Plugin::getName).collect(Collectors.toList());
      list.add("Bukkit");
      return this.plugin.getPermissionDatabase().getAllPermissionInfos().plugins(list).stream().map(PermissionInfo::getPermission).collect(Collectors.toList());
   }
}
