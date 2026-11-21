package io.lumine.mythic.core.skills.conditions.all;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.conditions.IEntityCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.mobs.ActiveMob;
import io.lumine.mythic.core.skills.SkillCondition;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicField;

@MythicCondition(
   author = "Ashijin",
   name = "faction",
   aliases = {},
   version = "4.9",
   description = "Tests the target's faction"
)
public class FactionCondition extends SkillCondition implements IEntityCondition {
   @MythicField(
      name = "faction",
      aliases = {"f"},
      description = "The faction to check"
   )
   private final PlaceholderString faction;

   public FactionCondition(String line, MythicLineConfig mlc) {
      super(line);
      this.faction = mlc.getPlaceholderString(new String[]{"faction", "f"}, this.conditionVar);
   }

   @Override
   public boolean check(AbstractEntity target) {
      String faction = this.faction.get(target);
      if (target.isPlayer()) {
         return getPlugin().getPlayerManager().getFactionProvider().isInFaction(target.asPlayer(), faction);
      } else {
         ActiveMob am = MythicBukkit.inst().getMobManager().getMythicMobInstance(target);
         if (am == null) {
            return false;
         } else {
            return !am.hasFaction() ? false : am.getFaction().equals(faction);
         }
      }
   }
}
