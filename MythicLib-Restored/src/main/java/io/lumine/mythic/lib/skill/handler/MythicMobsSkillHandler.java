package io.lumine.mythic.lib.skill.handler;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.skills.MetaSkill;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.comp.anticheat.CheatType;
import io.lumine.mythic.lib.skill.SkillMetadata;
import io.lumine.mythic.lib.skill.result.MythicMobsSkillResult;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

public class MythicMobsSkillHandler extends SkillHandler<MythicMobsSkillResult> {
   private final Skill skill;
   private final Map<CheatType, Integer> antiCheat = new HashMap<>();

   public MythicMobsSkillHandler(ConfigurationSection config) {
      super(config, config.getName().isEmpty() ? config.getString("mythicmobs-skill-id") : config.getName());
      SkillExecutor skillManager = MythicBukkit.inst().getSkillManager();
      if (config.contains("extra-skills")) {
         for (String key : config.getConfigurationSection("extra-skills").getKeys(false)) {
            try {
               MythicConfig mythicConfig = new MythicConfigImpl("extra-skills." + key, config);
               MetaSkill metaSkill = new MetaSkill(skillManager, null, null, key, mythicConfig);
               skillManager.registerSkill(key, metaSkill);
            } catch (RuntimeException exception) {
               MythicLib.plugin
                  .getLogger()
                  .log(
                     Level.WARNING,
                     "Could not register MythicMob extra skill '" + key + "' for custom skill handler '" + this.getId() + "': " + exception.getMessage()
                  );
            }
         }
      }

      String skillName = config.getString("mythicmobs-skill-id");
      Optional<Skill> opt = skillManager.getSkill(skillName);
      Validate.isTrue(opt.isPresent(), "Could not find MM skill with name '" + skillName + "'");
      this.skill = opt.get();
      if (config.isConfigurationSection("disable-anti-cheat")) {
         for (String key : config.getConfigurationSection("disable-anti-cheat").getKeys(false)) {
            CheatType cheatType = CheatType.valueOf(key.toUpperCase().replace(" ", "_").replace("-", "_"));
            this.antiCheat.put(cheatType, config.getInt("disable-anti-cheat." + key));
         }
      }
   }

   public String getInternalName() {
      return this.skill.getInternalName();
   }

   public Skill getSkill() {
      return this.skill;
   }

   public Map<CheatType, Integer> getAntiCheat() {
      return this.antiCheat;
   }

   public MythicMobsSkillResult getResult(SkillMetadata meta) {
      return new MythicMobsSkillResult(meta, this);
   }

   public void whenCast(MythicMobsSkillResult result, SkillMetadata skillMeta) {
      if (MythicLib.plugin.hasAntiCheat()) {
         MythicLib.plugin.getAntiCheat().disableAntiCheat(skillMeta.getCaster().getPlayer(), this.antiCheat);
      }

      this.skill.execute(result.getMythicMobsSkillMetadata());
   }
}
