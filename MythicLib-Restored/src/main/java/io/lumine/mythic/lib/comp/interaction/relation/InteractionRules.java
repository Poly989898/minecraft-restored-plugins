package io.lumine.mythic.lib.comp.interaction.relation;

import io.lumine.mythic.lib.comp.interaction.InteractionType;
import io.lumine.mythic.lib.util.Triplet;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InteractionRules {
   private final Map<Triplet<Boolean, Boolean, Relationship>, Boolean> rules = new HashMap<>();
   public final boolean supportSkillsOnMobs;

   public InteractionRules(@Nullable ConfigurationSection config) {
      if (config == null) {
         this.supportSkillsOnMobs = true;
      } else {
         for (Relationship rel : Relationship.values()) {
            this.rules.put(Triplet.of(true, true, rel), config.getBoolean("pvp_on.offense." + rel.name().toLowerCase(), true));
            this.rules.put(Triplet.of(true, false, rel), config.getBoolean("pvp_on.support." + rel.name().toLowerCase(), true));
            this.rules.put(Triplet.of(false, true, rel), config.getBoolean("pvp_off.offense." + rel.name().toLowerCase(), true));
            this.rules.put(Triplet.of(false, false, rel), config.getBoolean("pvp_off.support." + rel.name().toLowerCase(), true));
         }

         for (Relationship rel : Relationship.values()) {
            if (rel != Relationship.SELF) {
               this.rules.put(Triplet.of(false, true, rel), false);
            }
         }

         this.rules.put(Triplet.of(true, true, Relationship.PARTY_OTHER), true);
         this.rules.put(Triplet.of(true, true, Relationship.GUILD_ENEMY), true);
         this.supportSkillsOnMobs = config.getBoolean("support_skills_on_mobs");
      }
   }

   public boolean isEnabled(boolean pvpEnabled, @NotNull InteractionType interaction, @NotNull Relationship relationship) {
      Boolean setting = this.rules.get(Triplet.of(pvpEnabled, interaction.isOffense(), relationship));
      return setting == null || setting;
   }
}
