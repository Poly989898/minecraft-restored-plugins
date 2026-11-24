package net.Indyuce.mmocore.api.player.attribute;

import io.lumine.mythic.lib.api.stat.StatInstance;
import io.lumine.mythic.lib.api.stat.handler.StatHandler;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.configuration.ConfigurationSection;

public class MMOCoreAttributeStatHandler extends StatHandler {
   private final PlayerAttribute attr;

   public MMOCoreAttributeStatHandler(ConfigurationSection config, PlayerAttribute attr) {
      super(config, "ADDITIONAL_" + attr.getId().toUpperCase().replace("-", "_"));
      this.attr = attr;
   }

   public void runUpdate(StatInstance instance) {
      try {
         PlayerData playerData = PlayerData.get(instance.getMap().getPlayerData());
         playerData.getAttributes().getInstance(this.attr).updateStats();
      } catch (NullPointerException var3) {
      }
   }
}
