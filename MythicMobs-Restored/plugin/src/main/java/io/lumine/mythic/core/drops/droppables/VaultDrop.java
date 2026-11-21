package io.lumine.mythic.core.drops.droppables;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IIntangibleDrop;
import io.lumine.mythic.api.drops.IMessagingDrop;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.config.ConfigExecutor;
import io.lumine.mythic.core.drops.Drop;

public class VaultDrop extends Drop implements IIntangibleDrop, IMessagingDrop {
   private PlaceholderString rewardMessage;
   private boolean sendMessage = true;

   public VaultDrop(String line, MythicLineConfig config) {
      super(line, config);
      this.rewardMessage = !ConfigExecutor.compatVaultShowMoneyMessage ? null : PlaceholderString.of(ConfigExecutor.compatVaultMoneyMessageFormat);
      this.sendMessage = config.getBoolean(new String[]{"sendmessage", "sm"}, true);
   }

   public VaultDrop(String line, MythicLineConfig config, double amount) {
      super(line, config, amount);
      this.rewardMessage = !ConfigExecutor.compatVaultShowMoneyMessage ? null : PlaceholderString.of(ConfigExecutor.compatVaultMoneyMessageFormat);
   }

   @Override
   public void giveDrop(AbstractPlayer target, DropMetadata metadata, double amount) {
      if (MythicBukkit.inst().getCompatibility().getVault().isPresent() && amount > 0.0) {
         MythicBukkit.inst().getCompatibility().getVault().get().giveMoney(target, amount);
      }
   }

   @Override
   public String getRewardMessage(DropMetadata meta, double amount) {
      if (this.rewardMessage != null && this.sendMessage) {
         String message = this.rewardMessage.get(meta);
         String formatted = String.format("%.2f", amount);
         message = message.replace("<drops.money>", formatted);
         return message.replace("<drop.amount>", formatted);
      } else {
         return null;
      }
   }
}
