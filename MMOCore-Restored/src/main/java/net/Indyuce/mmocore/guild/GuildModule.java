package net.Indyuce.mmocore.guild;

import io.lumine.mythic.lib.comp.interaction.relation.Relationship;
import net.Indyuce.mmocore.api.player.PlayerData;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface GuildModule {
   @Nullable
   AbstractGuild getGuild(PlayerData playerData);

   @NotNull
   Relationship getRelationship(Player player, Player player2);
}
