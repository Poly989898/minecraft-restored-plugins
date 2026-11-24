package net.Indyuce.mmocore.skill.cast;

import java.util.Objects;
import java.util.function.Function;
import net.Indyuce.mmocore.MMOCore;
import net.Indyuce.mmocore.skill.cast.handler.KeyCombos;
import net.Indyuce.mmocore.skill.cast.handler.SkillBar;
import net.Indyuce.mmocore.skill.cast.handler.SkillCastingDisabled;
import net.Indyuce.mmocore.skill.cast.handler.SkillScroller;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public enum SkillCastingMode {
   SKILL_BAR(SkillBar::new),
   SKILL_SCROLLER(SkillScroller::new),
   KEY_COMBOS(KeyCombos::new),
   NONE(SkillCastingDisabled::new);

   private final Function<ConfigurationSection, SkillCastingHandler> listenerLoader;
   private static SkillCastingHandler current;

   SkillCastingMode(Function<ConfigurationSection, SkillCastingHandler> listenerLoader) {
      this.listenerLoader = listenerLoader;
   }

   public void setCurrent(@NotNull ConfigurationSection config) {
      Validate.isTrue(current == null, "Skill casting mode already initialized");
      current = this.listenerLoader.apply(config);
      Bukkit.getPluginManager().registerEvents(current, MMOCore.plugin);
   }

   @NotNull
   public static SkillCastingHandler getInstance() {
      return Objects.requireNonNull(current, "Skill casting mode hasn't been initialized yet");
   }

   @Deprecated
   public static SkillCastingHandler getCurrent() {
      return getInstance();
   }
}
