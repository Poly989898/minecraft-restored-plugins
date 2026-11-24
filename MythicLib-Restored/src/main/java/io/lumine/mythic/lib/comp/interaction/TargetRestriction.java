package io.lumine.mythic.lib.comp.interaction;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

@Deprecated
public interface TargetRestriction {
   @Deprecated
   boolean canTarget(Player player, LivingEntity entity, InteractionType interactionType);
}
