package io.lumine.mythic.core.skills.mechanics;

import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.IMetaSkill;
import io.lumine.mythic.api.skills.Skill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.utils.random.RandomSelector;
import io.lumine.mythic.bukkit.utils.random.Weighted;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.skills.SkillMechanic;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@MythicMechanic(
   author = "Ashijin",
   name = "randomskill",
   aliases = {"randommeta"},
   description = "Executes a random metaskill"
)
public class RandomSkillMechanic extends SkillMechanic implements IMetaSkill {
   protected Optional<Skill> fallbackSkill = Optional.empty();
   protected Collection<RandomSkillMechanic.RandomSkill> skills = new ArrayList<>();

   public RandomSkillMechanic(SkillExecutor manager, File file, String skill, MythicLineConfig mlc) {
      super(manager, file, skill, mlc);
      this.target_creative = true;
      String fallbackSkillString = mlc.getString(new String[]{"fallbackskill", "fbskill", "fbs"});
      String s = mlc.getString(new String[]{"skills", "s", "metas", "meta", "m"});
      String[] ss = s.split(",(?=(?:[^\\]]*(?:\\[|$)))", -1);
      this.getPlugin().getSkillManager().queueSecondPass(() -> {
         MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Loading RandomSkillMechanic mechanics -> {0}", s);
         if (fallbackSkillString != null) {
            this.fallbackSkill = this.getPlugin().getSkillManager().getSkill(file, this, fallbackSkillString);
         }

         for (String sx : ss) {
            if (sx != null) {
               double weight = 1.0;
               String mySkill;
               if (sx.contains("[")) {
                  int first = sx.indexOf("[");
                  String skillString = sx.substring(first);
                  int sep = skillString.lastIndexOf("]");
                  String newSkill = skillString.substring(0, sep + 1);
                  if (skillString.length() == newSkill.length()) {
                     mySkill = newSkill;
                  } else {
                     mySkill = newSkill;
                     weight = Double.parseDouble(skillString.substring(sep + 2));
                  }
               } else {
                  String[] xd = sx.trim().split(" ");
                  if (xd.length > 1) {
                     mySkill = xd[0];
                     weight = Double.parseDouble(xd[1]);
                  } else {
                     mySkill = xd[0];
                  }
               }

               MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "Loading RandomSkill skill {0} with a weight of {1}", mySkill, weight);
               Optional<Skill> metaskill = this.getPlugin().getSkillManager().getSkill(file, this, mySkill);
               if (metaskill.isEmpty()) {
                  MythicLogger.errorMechanicConfig(this, mlc, "Could not find MetaSkill " + mySkill);
               } else {
                  MythicLogger.debug(MythicLogger.DebugLevel.MECHANIC, "+ Loaded MetaSkillMechanic successfully");
                  RandomSkillMechanic.RandomSkill random = new RandomSkillMechanic.RandomSkill(metaskill.get(), weight);
                  this.skills.add(random);
               }
            }
         }
      });
   }

   @Override
   public SkillResult cast(SkillMetadata data) {
      ArrayList<RandomSkillMechanic.RandomSkill> skills = new ArrayList<>(this.skills);

      while (!skills.isEmpty()) {
         RandomSelector<RandomSkillMechanic.RandomSkill> random = RandomSelector.weighted(skills);
         RandomSkillMechanic.RandomSkill rs = random.pick();
         if (rs.skill.isUsable(data)) {
            rs.skill.execute(data);
            return SkillResult.SUCCESS;
         }

         skills.remove(rs);
      }

      if (this.fallbackSkill.isPresent()) {
         Skill skill = this.fallbackSkill.get();
         if (skill.isUsable(data)) {
            skill.execute(data);
            return SkillResult.SUCCESS;
         }
      }

      return SkillResult.CONDITION_FAILED;
   }

   private record RandomSkill(Skill skill, double weight) implements Weighted {
      @Override
      public double getWeight() {
         return this.weight;
      }
   }
}
