package net.Indyuce.mmoitems.api.crafting.trigger;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmoitems.api.player.PlayerData;

public class MessageTrigger extends Trigger {
   private final String message;

   public MessageTrigger(MMOLineConfig mmoLineConfig) {
      super("message");
      mmoLineConfig.validate(new String[]{"format"});
      this.message = mmoLineConfig.getString("format");
   }

   @Override
   public void whenCrafting(PlayerData playerData) {
      if (playerData.isOnline()) {
         playerData.getPlayer().sendMessage(MythicLib.plugin.getPlaceholderParser().parse(playerData.getPlayer(), this.message));
      }
   }
}
