package net.Indyuce.mmocore.guild.compat;

import cc.javajobs.factionsbridge.FactionsBridge;
import cc.javajobs.factionsbridge.bridge.infrastructure.struct.FPlayer;
import cc.javajobs.factionsbridge.bridge.infrastructure.struct.Faction;
import cc.javajobs.factionsbridge.bridge.infrastructure.struct.FactionsAPI;
import io.lumine.mythic.lib.comp.interaction.relation.Relationship;
import java.util.Objects;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.guild.AbstractGuild;
import net.Indyuce.mmocore.guild.GuildModule;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class FactionsGuildModule implements GuildModule {
   @Override
   public AbstractGuild getGuild(PlayerData playerData) {
      FactionsAPI api = FactionsBridge.getFactionsAPI();
      Faction faction = api.getFaction(playerData.getPlayer());
      return faction == null ? null : new FactionsGuildModule.CustomGuild(faction);
   }

   @Override
   public Relationship getRelationship(Player player, Player target) {
      FactionsAPI api = FactionsBridge.getFactionsAPI();
      Faction faction = api.getFaction(player);
      if (faction != null) {
         return this.adapt(faction.getRelationshipTo(api.getFPlayer(target)));
      }

      faction = api.getFaction(target);
      return faction != null ? this.adapt(faction.getRelationshipTo(api.getFPlayer(player))) : Relationship.GUILD_NEUTRAL;
   }

   private Relationship adapt(cc.javajobs.factionsbridge.bridge.infrastructure.struct.Relationship rel) {
      switch (rel) {
         case ENEMY:
            return Relationship.GUILD_ENEMY;
         case ALLY:
         case TRUCE:
         case MEMBER:
            return Relationship.GUILD_ALLY;
         case NONE:
         default:
            return Relationship.GUILD_NEUTRAL;
      }
   }

   class CustomGuild implements AbstractGuild {
      @NotNull
      private final Faction faction;

      CustomGuild(Faction faction) {
         this.faction = Objects.requireNonNull(faction);
      }

      @Override
      public boolean hasMember(Player player) {
         for (FPlayer member : this.faction.getMembers()) {
            if (member.getUniqueId().equals(player.getUniqueId())) {
               return true;
            }
         }

         return false;
      }
   }
}
