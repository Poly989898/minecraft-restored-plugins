package net.Indyuce.mmocore.party.compat;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.party.HeroParty;
import java.util.ArrayList;
import java.util.List;
import net.Indyuce.mmocore.api.player.PlayerData;
import net.Indyuce.mmocore.party.AbstractParty;
import net.Indyuce.mmocore.party.PartyModule;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HeroesPartyModule implements PartyModule, Listener {
   @Nullable
   @Override
   public AbstractParty getParty(PlayerData playerData) {
      Hero hero = Heroes.getInstance().getCharacterManager().getHero(playerData.getPlayer());
      return hero.getParty() == null ? null : new HeroesPartyModule.CustomParty(hero.getParty());
   }

   private static class CustomParty implements AbstractParty {
      private final HeroParty party;

      public CustomParty(HeroParty party) {
         this.party = party;
      }

      @Override
      public boolean hasMember(@NotNull Player player) {
         return this.party.isPartyMember(player);
      }

      @Override
      public List<PlayerData> getOnlineMembers() {
         List<PlayerData> list = new ArrayList<>();

         for (Hero hero : this.party.getMembers()) {
            try {
               list.add(PlayerData.get(hero.getPlayer()));
            } catch (Exception exception) {
            }
         }

         return list;
      }

      @Override
      public int countMembers() {
         return this.party.getMembers().size();
      }
   }
}
