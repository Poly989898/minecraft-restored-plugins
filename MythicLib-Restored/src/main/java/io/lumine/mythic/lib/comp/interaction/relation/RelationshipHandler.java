package io.lumine.mythic.lib.comp.interaction.relation;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface RelationshipHandler {
   @NotNull
   Relationship getRelationship(Player var1, Player var2);
}
