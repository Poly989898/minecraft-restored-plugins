package net.Indyuce.mmocore.guild.compat;

import io.lumine.mythic.lib.comp.interaction.relation.Relationship;
import java.util.Objects;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.guild.AbstractGuild;
import net.Indyuce.mmocore.guild.GuildModule;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.kingdoms.constants.group.Kingdom;
import org.kingdoms.constants.group.model.relationships.KingdomRelation;
import org.kingdoms.constants.player.KingdomPlayer;

public class KingdomsXGuildModule implements GuildModule {
   @Override
   public AbstractGuild getGuild(PlayerData playerData) {
      KingdomPlayer kPlayer = KingdomPlayer.getKingdomPlayer(playerData.getPlayer());
      if (kPlayer == null) {
         return null;
      }

      Kingdom kingdom = kPlayer.getKingdom();
      return kingdom == null ? null : new KingdomsXGuildModule.CustomGuild(kingdom);
   }

   @Override
   public Relationship getRelationship(Player player, Player target) {
      KingdomPlayer kingdomPlayer = KingdomPlayer.getKingdomPlayer(player);
      if (kingdomPlayer == null) {
         return Relationship.GUILD_NEUTRAL;
      }

      Kingdom playerKingdom = kingdomPlayer.getKingdom();
      if (playerKingdom == null) {
         return Relationship.GUILD_NEUTRAL;
      }

      KingdomPlayer targetKingdomPlayer = KingdomPlayer.getKingdomPlayer(target.getPlayer());
      if (targetKingdomPlayer == null) {
         return Relationship.GUILD_NEUTRAL;
      }

      Kingdom targetKingdom = targetKingdomPlayer.getKingdom();
      return targetKingdom == null ? Relationship.GUILD_NEUTRAL : this.adapt(playerKingdom.getRelationWith(targetKingdom));
   }

   private Relationship adapt(KingdomRelation rel) {
      switch (rel) {
         case ALLY:
         case SELF:
            return Relationship.GUILD_ALLY;
         case ENEMY:
            return Relationship.GUILD_ENEMY;
         case NEUTRAL:
         case TRUCE:
         case NATION:
         default:
            return Relationship.GUILD_NEUTRAL;
      }
   }

   class CustomGuild implements AbstractGuild {
      @NotNull
      private final Kingdom kingdom;

      CustomGuild(Kingdom kingdom) {
         this.kingdom = Objects.requireNonNull(kingdom);
      }

      @Override
      public boolean hasMember(Player player) {
         return this.kingdom.isMember(player);
      }
   }
}
