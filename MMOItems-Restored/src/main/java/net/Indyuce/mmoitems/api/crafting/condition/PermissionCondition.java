package net.Indyuce.mmoitems.api.crafting.condition;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.Arrays;
import java.util.List;
import net.Indyuce.mmoitems.api.player.PlayerData;

public class PermissionCondition extends GenericCondition {
   private final List<String> permissions;

   public PermissionCondition(MMOLineConfig mmoLineConfig) {
      super("permission", mmoLineConfig);
      mmoLineConfig.validate(new String[]{"list"});
      this.permissions = Arrays.asList(mmoLineConfig.getString("list").split(","));
   }

   @Override
   public boolean isMet(PlayerData playerData) {
      for (String permission : this.permissions) {
         if (!playerData.getPlayer().hasPermission(permission)) {
            return false;
         }
      }

      return true;
   }

   @Override
   public void whenCrafting(PlayerData playerData) {
   }
}
