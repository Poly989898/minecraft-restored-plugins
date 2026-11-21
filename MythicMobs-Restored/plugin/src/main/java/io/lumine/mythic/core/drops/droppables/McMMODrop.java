package io.lumine.mythic.core.drops.droppables;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IIntangibleDrop;
import io.lumine.mythic.api.drops.IMessagingDrop;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.compatibility.CompatibilityManager;
import io.lumine.mythic.core.config.ConfigExecutor;
import io.lumine.mythic.core.drops.Drop;

public class McMMODrop extends Drop implements IIntangibleDrop, IMessagingDrop {
   private PlaceholderString rewardMessage = !ConfigExecutor.compatMcMMOShowXPMessage ? null : PlaceholderString.of(ConfigExecutor.compatMcMMOXPMessageFormat);

   public McMMODrop(String line, MythicLineConfig config) {
      super(line, config);
   }

   public McMMODrop(String line, MythicLineConfig config, double amount) {
      super(line, config, amount);
   }

   @Override
   public void giveDrop(AbstractPlayer target, DropMetadata metadata, double amount) {
      if (CompatibilityManager.mcMMO != null) {
         CompatibilityManager.mcMMO.giveExp(BukkitAdapter.adapt(target), (float)amount, "swords");
      }
   }

   @Override
   public String getRewardMessage(DropMetadata meta, double amount) {
      if (this.rewardMessage != null && ConfigExecutor.compatMcMMOShowXPMessage) {
         String message = this.rewardMessage.get(meta);
         String amountStr = String.valueOf(amount);
         message = message.replace("<drops.mcmmo>", amountStr);
         message = message.replace("<drops.xp>", amountStr);
         return message.replace("<drop.amount>", amountStr);
      } else {
         return null;
      }
   }
}
