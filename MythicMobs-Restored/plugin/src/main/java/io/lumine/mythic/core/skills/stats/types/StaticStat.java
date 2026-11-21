package io.lumine.mythic.core.skills.stats.types;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.stats.StatExecutor;
import io.lumine.mythic.core.skills.stats.StatType;
import java.io.File;

public class StaticStat extends StatType {
   public StaticStat(String key) {
      super(key);
   }

   public StaticStat(File file, String key) {
      super(file, key);
   }

   @Override
   protected void load(StatExecutor manager) {
   }

   @Override
   public boolean isApplicable(AbstractEntity entity) {
      return entity.isPlayer() || MythicBukkit.inst().getMobManager().isMythicMob(entity);
   }
}
