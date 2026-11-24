package net.Indyuce.mmoitems.api.crafting.condition;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.Arrays;
import java.util.List;
import net.Indyuce.mmoitems.api.player.PlayerData;

public class PermissionCondition extends GenericCondition {
   private final List<String> permissions;

   public PermissionCondition(MMOLineConfig var1) {
      super("permission", var1);
      var1.validate(new String[]{"list"});
      this.permissions = Arrays.asList(var1.getString("list").split(","));
   }

   @Override
   public boolean isMet(PlayerData var1) {
      for (String var3 : this.permissions) {
         if (!var1.getPlayer().hasPermission(var3)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void whenCrafting(PlayerData var1) {
   }
}
