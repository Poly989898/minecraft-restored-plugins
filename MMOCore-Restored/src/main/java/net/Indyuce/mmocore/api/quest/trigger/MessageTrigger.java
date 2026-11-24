package net.Indyuce.mmocore.api.quest.trigger;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.entity.Player;

public class MessageTrigger extends Trigger {
   private final String message;

   public MessageTrigger(MMOLineConfig config) {
      super(config);
      config.validate(new String[]{"format"});
      this.message = config.getString("format");
   }

   @Override
   public void apply(PlayerData player) {
      if (player.isOnline()) {
         player.getPlayer().sendMessage(this.format(player.getPlayer()));
      }
   }

   private String format(Player player) {
      return MMOCore.plugin.placeholderParser.parse(player, this.message.replace("%player%", player.getName()));
   }
}
