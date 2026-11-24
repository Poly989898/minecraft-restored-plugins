package io.lumine.mythic.lib.manager;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.comp.interaction.relation.EmptyPvPInteractionRules;
import io.lumine.mythic.lib.comp.interaction.relation.InteractionRules;
import io.lumine.mythic.lib.skill.SimpleSkill;
import io.lumine.mythic.lib.skill.Skill;
import io.lumine.mythic.lib.skill.handler.MythicLibSkillHandler;
import io.lumine.mythic.lib.skill.trigger.TriggerType;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import javax.annotation.Nullable;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public class ConfigManager {
   public final DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();
   public DecimalFormat decimal;
   public DecimalFormat decimals;
   public boolean playerAbilityDamage;
   public boolean castingDelayCancelOnMove;
   public boolean enableCastingDelayBossbar;
   public boolean fixTooLargePackets;
   public boolean debugMode;
   public boolean ignoreShiftTriggers;
   public boolean ignoreOffhandClickTriggers;
   public boolean skipElementalDamageApplication;
   public String naturalDefenseFormula;
   public String elementalDefenseFormula;
   public String castingDelayBossbarFormat;
   public BarColor castingDelayBarColor;
   public BarStyle castingDelayBarStyle;
   public double castingDelaySlowness;
   public int maxSyncTries;
   @NotNull
   public InteractionRules interactionRules;
   @Nullable
   public Skill skillCastScript;
   @Nullable
   public Skill skillCancelScript;

   public void reload() {
      ConfigurationSection config = MythicLib.plugin.getConfig();
      this.formatSymbols.setDecimalSeparator(this.getFirstChar(config.getString("number-format.decimal-separator")));
      this.decimal = this.newDecimalFormat("0.#");
      this.decimals = this.newDecimalFormat("0.##");
      this.interactionRules = config.getBoolean("interaction_rules.enabled")
         ? new InteractionRules(config.getConfigurationSection("interaction_rules"))
         : new EmptyPvPInteractionRules();
      this.playerAbilityDamage = config.getBoolean("player-ability-damage");
      this.naturalDefenseFormula = config.getString("defense-application.natural");
      this.elementalDefenseFormula = config.getString("defense-application.elemental");
      this.fixTooLargePackets = config.getBoolean("fix-too-large-packets");
      this.debugMode = config.getBoolean("debug");
      this.maxSyncTries = config.getInt("max-sync-tries");
      this.ignoreShiftTriggers = config.getBoolean("ignore_shift_triggers");
      this.ignoreOffhandClickTriggers = config.getBoolean("ignore_offhand_click_triggers");
      this.skipElementalDamageApplication = config.getBoolean("skip_elemental_damage_application");
      this.castingDelaySlowness = config.getDouble("casting-delay.slowness");
      this.castingDelayCancelOnMove = config.getBoolean("casting-delay.cancel-on-move");
      this.enableCastingDelayBossbar = config.getBoolean("casting-delay.bossbar.enabled");
      this.castingDelayBossbarFormat = config.getString("casting-delay.bossbar.format");
      this.castingDelayBarColor = UtilityMethods.resolveEnumField(
         BarColor::valueOf, () -> BarColor.PURPLE, config.getString("casting-delay.bossbar.color", "PURPLE")
      );
      this.castingDelayBarStyle = UtilityMethods.resolveEnumField(
         BarStyle::valueOf, () -> BarStyle.SEGMENTED_20, config.getString("casting-delay.bossbar.style", "SEGMENTED_20")
      );

      try {
         this.skillCastScript = config.getBoolean("casting-delay.cast-script.enabled")
            ? new SimpleSkill(
               TriggerType.CAST, new MythicLibSkillHandler(MythicLib.plugin.getSkills().loadScript(config.get("casting-delay.cast-script.script")))
            )
            : null;
      } catch (IllegalArgumentException exception) {
         this.skillCastScript = null;
      }

      try {
         this.skillCancelScript = config.getBoolean("casting-delay.cancel-script.enabled")
            ? new SimpleSkill(
               TriggerType.CAST, new MythicLibSkillHandler(MythicLib.plugin.getSkills().loadScript(config.get("casting-delay.cancel-script.script")))
            )
            : null;
      } catch (IllegalArgumentException exception) {
         this.skillCancelScript = null;
      }
   }

   public DecimalFormat newDecimalFormat(String pattern) {
      return new DecimalFormat(pattern, this.formatSymbols);
   }

   private char getFirstChar(String str) {
      return str != null && !str.isEmpty() ? str.charAt(0) : '.';
   }
}
