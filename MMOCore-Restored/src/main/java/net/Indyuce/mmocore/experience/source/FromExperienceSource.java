package net.Indyuce.mmocore.experience.source;

import io.lumine.mythic.lib.api.MMOLineConfig;
import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.ConfigFile;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.experience.dispenser.ExperienceDispenser;
import net.Indyuce.mmocore.experience.source.type.ExperienceSource;
import net.Indyuce.mmocore.manager.profession.ExperienceSourceManager;
import org.apache.commons.lang.Validate;

public class FromExperienceSource extends ExperienceSource {
   private final List<ExperienceSource> experienceSources = new ArrayList<>();

   public FromExperienceSource(ExperienceDispenser dispenser, MMOLineConfig config) {
      super(dispenser);
      config.validateKeys(new String[]{"source"});
      List<String> list = new ConfigFile("exp-sources").getConfig().getStringList(config.getString("source"));
      Validate.isTrue(list != null && !list.isEmpty(), "There is no source matching " + config.getString("source"));
      list.stream()
         .<MMOLineConfig>map(MMOLineConfig::new)
         .forEach(mmoLineConfig -> this.experienceSources.add(MMOCore.plugin.loadManager.loadExperienceSource(mmoLineConfig, dispenser)));
   }

   @Override
   public ExperienceSourceManager<FromExperienceSource> newManager() {
      return new FromExperienceSource.Manager();
   }

   @Override
   public boolean matchesParameter(PlayerData player, Object obj) {
      return false;
   }

   private static class Manager extends ExperienceSourceManager<FromExperienceSource> {
      public void registerSource(FromExperienceSource source) {
         source.experienceSources.forEach(expSource -> MMOCore.plugin.experience.registerSource(expSource));
      }
   }
}
