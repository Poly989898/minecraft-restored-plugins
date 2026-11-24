package net.Indyuce.mmoitems.api.crafting.trigger;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmoitems.api.player.PlayerData;

public class MessageTrigger extends Trigger {
   private final String message;

   public MessageTrigger(MMOLineConfig var1) {
      super("message");
      var1.validate(new String[]{"format"});
      this.message = var1.getString("format");
   }

   @Override
   public void whenCrafting(PlayerData var1) {
      if (var1.isOnline()) {
         var1.getPlayer().sendMessage(MythicLib.plugin.getPlaceholderParser().parse(var1.getPlayer(), this.message));
      }
   }
}
