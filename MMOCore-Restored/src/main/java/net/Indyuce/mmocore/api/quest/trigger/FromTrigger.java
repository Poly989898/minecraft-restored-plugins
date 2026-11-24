package net.Indyuce.mmocore.api.quest.trigger;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigFile;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.apache.commons.lang.Validate;

public class FromTrigger extends Trigger {
   private final List<Trigger> triggers = new ArrayList<>();

   public FromTrigger(MMOLineConfig config) {
      super(config);
      List<String> list = new ConfigFile("triggers").getConfig().getStringList(config.getString("source"));
      Validate.isTrue(list.size() != 0, "There is no source matching " + config.getString("key"));
      list.stream().<MMOLineConfig>map(MMOLineConfig::new).forEach(mmoLineConfig -> this.triggers.add(MMOCore.plugin.loadManager.loadTrigger(mmoLineConfig)));
   }

   @Override
   public void apply(PlayerData player) {
      this.triggers.forEach(trigger -> trigger.apply(player));
   }
}
