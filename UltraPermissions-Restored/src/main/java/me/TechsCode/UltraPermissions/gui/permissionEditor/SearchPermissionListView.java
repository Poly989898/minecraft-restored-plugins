package me.TechsCode.UltraPermissions.gui.permissionEditor;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.base.gui.pageableview.SearchFeature;
import me.TechsCode.UltraPermissions.permissionDatabase.PermissionInfo;
import me.TechsCode.UltraPermissions.storage.objects.PermissionHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public abstract class SearchPermissionListView extends PermissionListView {
   private final String searchTerm;
   private final PermissionWithInfo[] permissions;

   public SearchPermissionListView(Player player, UltraPermissions ultraPermissions, PermissionHolder permissionHolder, String text) {
      super(player, ultraPermissions, permissionHolder);
      this.searchTerm = text;
      List list = Arrays.stream(Bukkit.getPluginManager().getPlugins()).<String>map(Plugin::getName).map(String::toLowerCase).collect(Collectors.toList());
      this.permissions = ultraPermissions.getPermissionDatabase()
         .getAllPermissionInfos()
         .stream()
         .filter(permissionInfo -> list.contains(permissionInfo.getPlugin().toLowerCase()) || permissionInfo.getPlugin().toLowerCase().equals("bukkit"))
         .filter(permissionInfo -> permissionInfo.getPermission().toLowerCase().contains(text.toLowerCase()))
         .sorted(Comparator.comparing(PermissionInfo::getPermission))
         .map(permissionInfo -> new PermissionWithInfo(permissionInfo.getPermission(), permissionInfo))
         .toArray(PermissionWithInfo[]::new);
   }

   @Override
   public String getTitle() {
      return "Permissions with '" + this.searchTerm + "'";
   }

   public PermissionWithInfo[] getObjects() {
      return this.permissions;
   }

   @Override
   public SearchFeature<PermissionWithInfo> getSearch() {
      return null;
   }
}
