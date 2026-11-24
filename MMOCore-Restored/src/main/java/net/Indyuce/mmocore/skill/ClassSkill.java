package net.Indyuce.mmocore.skill;

import io.lumine.mythic.lib.api.player.EquipmentSlot;
import io.lumine.mythic.lib.gui.editable.placeholder.Placeholders;
import io.lumine.mythic.lib.player.cooldown.CooldownObject;
import io.lumine.mythic.lib.player.modifier.ModifierSource;
import io.lumine.mythic.lib.player.skill.PassiveSkill;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.api.util.math.formula.IntegerLinearValue;
import net.Indyuce.mmocore.api.util.math.formula.LinearValue;
import net.Indyuce.mmocore.player.Unlockable;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class ClassSkill implements CooldownObject, Unlockable {
   private final RegisteredSkill skill;
   private final int unlockLevel;
   private final int maxSkillLevel;
   private final boolean unlockedByDefault;
   private final boolean permanent;
   private final boolean upgradable;
   private final Map<String, LinearValue> parameters = new HashMap<>();

   public ClassSkill(RegisteredSkill skill, int unlockLevel, int maxSkillLevel) {
      this(skill, unlockLevel, maxSkillLevel, true);
   }

   public ClassSkill(RegisteredSkill skill, int unlockLevel, int maxSkillLevel, boolean unlockedByDefault) {
      this(skill, unlockLevel, maxSkillLevel, unlockedByDefault, MMOCore.plugin.configManager.passiveSkillsNeedBinding);
   }

   public ClassSkill(RegisteredSkill skill, int unlockLevel, int maxSkillLevel, boolean unlockedByDefault, boolean needsBinding) {
      this(skill, unlockLevel, maxSkillLevel, unlockedByDefault, needsBinding, true);
   }

   public ClassSkill(RegisteredSkill skill, int unlockLevel, int maxSkillLevel, boolean unlockedByDefault, boolean needsBinding, boolean upgradable) {
      this.skill = skill;
      this.unlockLevel = unlockLevel;
      this.maxSkillLevel = maxSkillLevel;
      this.unlockedByDefault = unlockedByDefault;
      this.permanent = !needsBinding && skill.getTrigger().isPassive();
      this.upgradable = upgradable;

      for (String param : skill.getHandler().getParameters()) {
         this.parameters.put(param, skill.getParameterInfo(param));
      }
   }

   public ClassSkill(RegisteredSkill skill, ConfigurationSection config) {
      this.skill = skill;
      this.unlockLevel = config.getInt("level");
      this.maxSkillLevel = config.getInt("max-level");
      this.unlockedByDefault = config.getBoolean("unlocked-by-default", true);
      this.permanent = !config.getBoolean("needs-bound", MMOCore.plugin.configManager.passiveSkillsNeedBinding) && skill.getTrigger().isPassive();
      this.upgradable = config.getBoolean("upgradable", true);

      for (String param : skill.getHandler().getParameters()) {
         LinearValue defaultValue = skill.getParameterInfo(param);
         this.parameters
            .put(param, config.isConfigurationSection(param) ? this.readLinearValue(defaultValue, config.getConfigurationSection(param)) : defaultValue);
      }
   }

   @NotNull
   public RegisteredSkill getSkill() {
      return this.skill;
   }

   public int getUnlockLevel() {
      return this.unlockLevel;
   }

   public boolean hasMaxLevel() {
      return this.maxSkillLevel > 0;
   }

   public int getMaxLevel() {
      return this.maxSkillLevel;
   }

   public boolean isUpgradable() {
      return this.upgradable;
   }

   @Override
   public boolean isUnlockedByDefault() {
      return this.unlockedByDefault;
   }

   @Deprecated
   public boolean needsBound() {
      return this.getSkill().getTrigger().isPassive() && !this.isPermanent();
   }

   public boolean isPermanent() {
      return this.permanent;
   }

   @Override
   public String getUnlockNamespacedKey() {
      return "skill:" + this.skill.getHandler().getId().toLowerCase();
   }

   @Override
   public void whenLocked(PlayerData playerData) {
      new HashMap<>(playerData.getBoundSkills()).forEach((slot, bound) -> {
         if (this.equals(bound.getClassSkill())) {
            playerData.unbindSkill(slot);
         }
      });
      if (this.isPermanent()) {
         playerData.getStats().updateStats();
      }
   }

   @Override
   public void whenUnlocked(PlayerData playerData) {
      if (this.isPermanent()) {
         playerData.getStats().updateStats();
      }
   }

   @Deprecated
   public void addModifier(String modifier, LinearValue linear) {
      this.addParameter(modifier, linear);
   }

   public void addParameter(String parameter, LinearValue linear) {
      Validate.isTrue(this.parameters.containsKey(parameter), "Could not find parameter '" + parameter + "'");
      this.parameters.put(parameter, linear);
   }

   @Deprecated
   public double getModifier(String modifier, int level) {
      return this.getParameter(modifier, level);
   }

   public double getParameter(String parameter, int level) {
      return Objects.requireNonNull(this.parameters.get(parameter), "Could not find parameter '" + parameter + "'").calculate(level);
   }

   public List<String> calculateLore(PlayerData data) {
      return this.calculateLore(data, data.getSkillLevel(this.skill));
   }

   public List<String> calculateLore(PlayerData data, int x) {
      Placeholders placeholders = new Placeholders();
      this.parameters
         .keySet()
         .forEach(
            param -> placeholders.register(
               param,
               this.skill
                  .getDecimalFormat(param)
                  .format(data.getMMOPlayerData().getSkillModifierMap().calculateValue(this.skill.getHandler(), this.parameters.get(param).calculate(x), param))
            )
         );
      placeholders.register("mana_name", data.getProfess().getManaDisplay().getName());
      placeholders.register("mana_color", data.getProfess().getManaDisplay().getFull().toString());
      List<String> list = new ArrayList<>();
      this.skill.getLore().forEach(str -> list.add(placeholders.apply(data.getPlayer(), str)));
      return list;
   }

   private LinearValue readLinearValue(LinearValue current, ConfigurationSection config) {
      return current instanceof IntegerLinearValue ? new IntegerLinearValue(config) : new LinearValue(config);
   }

   public CastableSkill toCastable(PlayerData caster) {
      return new CastableSkill(this, caster);
   }

   @NotNull
   public PassiveSkill toPassive(PlayerData caster) {
      Validate.isTrue(this.skill.getTrigger().isPassive(), "Skill is active");
      return new PassiveSkill(
         "MMOCore" + (this.permanent ? "Permanent" : "Passive") + "Skill", this.toCastable(caster), EquipmentSlot.OTHER, ModifierSource.OTHER
      );
   }

   public String getCooldownPath() {
      return "skill_" + this.skill.getHandler().getId();
   }
}
