package net.Indyuce.mmocore.guild.compat;

import io.lumine.mythic.lib.comp.interaction.relation.Relationship;
import java.util.Objects;
import me.glaremasters.guilds.Guilds;
import me.glaremasters.guilds.guild.Guild;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.guild.AbstractGuild;
import net.Indyuce.mmocore.guild.GuildModule;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GuildsGuildModule implements GuildModule {
   @Override
   public AbstractGuild getGuild(PlayerData playerData) {
      Guild guild = Guilds.getApi().getGuild(playerData.getUniqueId());
      return guild == null ? null : new GuildsGuildModule.CustomGuild(guild);
   }

   @Override
   public Relationship getRelationship(Player player, Player target) {
      Guild playerGuild = Guilds.getApi().getGuild(player);
      if (playerGuild == null) {
         return Relationship.GUILD_NEUTRAL;
      } else {
         Guild targetGuild = Guilds.getApi().getGuild(target);
         if (targetGuild == null) {
            return Relationship.GUILD_NEUTRAL;
         } else {
            return !playerGuild.getId().equals(targetGuild.getId()) && !playerGuild.getAllies().contains(targetGuild.getId()) ? Relationship.GUILD_ENEMY : Relationship.GUILD_ALLY;
         }
      }
   }

   class CustomGuild implements AbstractGuild {
      @NotNull
      private final Guild guild;

      CustomGuild(Guild guild) {
         this.guild = Objects.requireNonNull(guild);
      }

      @Override
      public boolean hasMember(Player player) {
         return this.guild.getMember(player.getUniqueId()) != null;
      }
   }
}
