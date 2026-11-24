package net.Indyuce.mmocore.skilltree.tree;

import java.util.function.Function;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

public enum SkillTreeType {
   CUSTOM(CustomSkillTree::new),
   PROXIMITY(ProximitySkillTree::new);

   private final Function<ConfigurationSection, SkillTree> constructor;

   SkillTreeType(Function<ConfigurationSection, SkillTree> constructor) {
      this.constructor = constructor;
   }

   @NotNull
   public SkillTree construct(@NotNull ConfigurationSection config) {
      return this.constructor.apply(config);
   }
}
