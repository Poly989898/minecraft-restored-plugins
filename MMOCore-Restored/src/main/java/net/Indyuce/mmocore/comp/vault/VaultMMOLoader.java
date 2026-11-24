package net.Indyuce.mmocore.comp.vault;

import io.lumine.mythic.lib.api.MMOLineConfig;
import net.Indyuce.mmocore.api.load.MMOLoader;
import net.Indyuce.mmocore.api.quest.trigger.Trigger;

public class VaultMMOLoader extends MMOLoader {
   @Override
   public Trigger loadTrigger(MMOLineConfig config) {
      return config.getKey().equalsIgnoreCase("money") ? new MoneyTrigger(config) : null;
   }
}
