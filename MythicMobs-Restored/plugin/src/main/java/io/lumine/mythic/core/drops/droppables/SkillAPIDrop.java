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

public class SkillAPIDrop extends Drop implements IIntangibleDrop, IMessagingDrop {
   private PlaceholderString rewardMessage = !ConfigExecutor.compatSkillAPIShowXPMessage
      ? null
      : PlaceholderString.of(ConfigExecutor.compatSkillAPIXPMessageFormat);

   public SkillAPIDrop(String line, MythicLineConfig config) {
      super(line, config);
   }

   public SkillAPIDrop(String line, MythicLineConfig config, double amount) {
      super(line, config, amount);
   }

   @Override
   public void giveDrop(AbstractPlayer target, DropMetadata metadata, double amount) {
      if (CompatibilityManager.SkillAPI != null) {
         CompatibilityManager.SkillAPI.giveExp(BukkitAdapter.adapt(target), (int)amount);
      }
   }

   @Override
   public String getRewardMessage(DropMetadata meta, double amount) {
      if (this.rewardMessage != null && ConfigExecutor.compatSkillAPIShowXPMessage) {
         String message = this.rewardMessage.get(meta);
         String formatted = String.format("%.2f", amount);
         message = message.replace("<drops.skillapi>", formatted);
         message = message.replace("<drops.xp>", formatted);
         return message.replace("<drop.amount>", formatted);
      } else {
         return null;
      }
   }
}
