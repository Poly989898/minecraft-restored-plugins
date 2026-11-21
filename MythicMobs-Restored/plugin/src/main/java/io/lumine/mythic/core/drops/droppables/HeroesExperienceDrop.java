package io.lumine.mythic.core.drops.droppables;

import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IIntangibleDrop;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.compatibility.CompatibilityManager;
import io.lumine.mythic.core.drops.Drop;
import io.lumine.mythic.core.logging.MythicLogger;

public class HeroesExperienceDrop extends Drop implements IIntangibleDrop {
   public HeroesExperienceDrop(String line, MythicLineConfig config) {
      super(line, config);
   }

   public HeroesExperienceDrop(String line, MythicLineConfig config, double amount) {
      super(line, config, amount);
   }

   @Override
   public void giveDrop(AbstractPlayer target, DropMetadata metadata, double amount) {
      MythicLogger.debug(MythicLogger.DebugLevel.SKILL, "Granting " + this.getAmount() + " Heroes EXP to " + target.getName());
      if (CompatibilityManager.Heroes != null) {
         CompatibilityManager.Heroes.giveHeroesExp(metadata.getDropper().get(), BukkitAdapter.adapt(target.asPlayer()), (int)amount);
      }
   }
}
