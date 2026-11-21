package io.lumine.mythic.core.skills.targeters;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.skills.SkillExecutor;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.util.Collection;
import java.util.HashSet;
import org.bukkit.entity.Interaction;
import org.bukkit.entity.Player;
import org.bukkit.entity.Interaction.PreviousInteraction;

@MythicTargeter(
   name = "interactionLastAttacker",
   aliases = {"lastAttacker"}
)
public class InteractionLastAttackerTargeter extends IEntitySelector {
   public InteractionLastAttackerTargeter(SkillExecutor manager, MythicLineConfig mlc) {
      super(manager, mlc);
   }

   @Override
   public Collection<AbstractEntity> getEntities(SkillMetadata data) {
      HashSet<AbstractEntity> targets = new HashSet<>();
      if (BukkitAdapter.adapt(data.getCaster().getEntity()) instanceof Interaction interaction) {
         PreviousInteraction lastAttack = interaction.getLastAttack();
         if (lastAttack != null && lastAttack.getPlayer().getPlayer() != null) {
            Player player = lastAttack.getPlayer().getPlayer();
            targets.add(BukkitAdapter.adapt(player));
         }
      }

      return targets;
   }
}
