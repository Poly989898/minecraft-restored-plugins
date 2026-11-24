package me.TechsCode.UltraPermissions.internal.lookup.checks;

import me.TechsCode.UltraPermissions.internal.lookup.LookupCheck;
import me.TechsCode.UltraPermissions.internal.lookup.LookupOutcome;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.permissions.Permission;

public class DefaultPermissionCheck implements LookupCheck {
   private final OfflinePlayer offlinePlayer;

   public DefaultPermissionCheck(OfflinePlayer offlinePlayer) {
      this.offlinePlayer = offlinePlayer;
   }

   @Override
   public LookupOutcome perform(String text) {
      Permission permission = Bukkit.getPluginManager().getPermission(text);
      return permission != null && permission.getDefault().getValue(this.offlinePlayer.isOp()) ? new LookupOutcome(true, "Default Permission") : null;
   }
}
