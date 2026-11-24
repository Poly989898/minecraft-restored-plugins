package net.Indyuce.mmocore.guild.provided;

import io.lumine.mythic.lib.comp.interaction.relation.Relationship;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.guild.AbstractGuild;
import net.Indyuce.mmocore.guild.GuildModule;
import org.bukkit.entity.Player;

public class MMOCoreGuildModule implements GuildModule {
   @Override
   public AbstractGuild getGuild(PlayerData playerData) {
      return playerData.getGuild();
   }

   @Override
   public Relationship getRelationship(Player player, Player target) {
      Guild playerGuild = PlayerData.get(player).getGuild();
      if (playerGuild == null) {
         return Relationship.GUILD_NEUTRAL;
      } else {
         Guild targetGuild = PlayerData.get(target).getGuild();
         if (targetGuild == null) {
            return Relationship.GUILD_NEUTRAL;
         } else {
            return playerGuild.equals(targetGuild) ? Relationship.GUILD_ALLY : Relationship.GUILD_ENEMY;
         }
      }
   }
}
