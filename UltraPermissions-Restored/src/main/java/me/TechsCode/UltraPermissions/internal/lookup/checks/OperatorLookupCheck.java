package me.TechsCode.UltraPermissions.internal.lookup.checks;

import java.util.Optional;
import me.TechsCode.UltraPermissions.UltraPermissions;
import me.TechsCode.UltraPermissions.internal.lookup.LookupCheck;
import me.TechsCode.UltraPermissions.internal.lookup.LookupOutcome;
import me.TechsCode.UltraPermissions.storage.objects.User;
import org.bukkit.OfflinePlayer;

public class OperatorLookupCheck implements LookupCheck {
   private final OfflinePlayer offlinePlayer;
   private final UltraPermissions plugin;

   public OperatorLookupCheck(UltraPermissions ultraPermissions, OfflinePlayer offlinePlayer) {
      this.offlinePlayer = offlinePlayer;
      this.plugin = ultraPermissions;
   }

   @Override
   public LookupOutcome perform(String text) {
      if (this.plugin.isNegativePermsOverrideEnabled()) {
         if (this.offlinePlayer.isOp()) {
            Optional optional = this.plugin.getUsers().uuid(this.offlinePlayer.getUniqueId());
            if (optional.isPresent()) {
               boolean flag = ((User)optional.get()).getPermissions().name(text).stream().anyMatch(permission -> !permission.isPositive());
               return new LookupOutcome(!flag, "Operator Rights");
            }
         }

         return null;
      } else {
         return this.offlinePlayer.isOp() ? new LookupOutcome(true, "Operator Rights") : null;
      }
   }
}
