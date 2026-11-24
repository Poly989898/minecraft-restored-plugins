package net.Indyuce.mmocore.api.quest.trigger;

import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.api.MMOLineConfig;
import io.lumine.mythic.lib.player.modifier.ModifierType;
import io.lumine.mythic.lib.player.skillmod.SkillModifier;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import java.util.List;
import java.util.stream.Collectors;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.quest.trigger.api.Removable;
import net.Indyuce.mmocore.api.quest.trigger.api.Temporary;
import net.Indyuce.mmocore.skill.RegisteredSkill;
import org.apache.commons.lang.Validate;
import org.jetbrains.annotations.NotNull;

public class SkillModifierTrigger extends Trigger implements Removable, Temporary {
   private SkillModifier mod;
   private boolean mutable = true;

   public SkillModifierTrigger(MMOLineConfig config) {
      super(config);
      config.validateKeys(new String[]{"modifier"});
      config.validateKeys(new String[]{"amount"});
      double amount = config.getDouble("amount");
      String parameter = config.getString("modifier");
      String formula = config.getString("formula", "true");
      ModifierType type = config.contains("type") ? ModifierType.valueOf(UtilityMethods.enumName(config.getString("type"))) : ModifierType.FLAT;
      List<SkillHandler<?>> targetSkills = MMOCore.plugin
         .skillManager
         .getAll()
         .stream()
         .filter(skill -> skill.matchesFormula(formula))
         .map(RegisteredSkill::getHandler)
         .collect(Collectors.toList());
      this.mod = new SkillModifier(Trigger.STAT_MODIFIER_KEY, parameter, targetSkills, amount, type);
   }

   public void updateKey(@NotNull String key) {
      Validate.isTrue(this.mutable, "No longer mutable");
      this.mod = new SkillModifier(key, this.mod.getParameter(), this.mod.getSkills(), this.mod.getValue(), this.mod.getType());
   }

   public List<SkillHandler<?>> getTargetSkills() {
      return this.mod.getSkills();
   }

   @Override
   public void apply(PlayerData player) {
      this.mod.register(player.getMMOPlayerData());
   }

   @Override
   public void remove(PlayerData playerData) {
      this.mod.unregister(playerData.getMMOPlayerData());
   }

   public void apply(PlayerData playerData, SkillHandler<?> skill) {
      this.mutable = false;
      this.mod.register(playerData.getMMOPlayerData(), skill);
   }

   public void remove(PlayerData playerData, SkillHandler<?> skill) {
      this.mod.unregister(playerData.getMMOPlayerData(), skill);
   }
}
