package io.lumine.mythic.bukkit.compatibility;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.classes.HeroClass.ExperienceType;
import com.herocraftonline.heroes.characters.party.HeroParty;
import com.herocraftonline.heroes.util.Properties;
import com.herocraftonline.heroes.util.Util;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.core.config.ConfigExecutor;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class HeroesSupport {
   private Heroes heroes = (Heroes)Bukkit.getPluginManager().getPlugin("Heroes");

   public Heroes getHeroes() {
      return this.heroes;
   }

   public void giveHeroesExp(SkillCaster am, Player player, int amount) {
      if (ConfigExecutor.compatHeroesXPEnable) {
         if (player != null) {
            Hero h = this.heroes.getCharacterManager().getHero(player);
            if (h.hasParty()) {
               HeroParty hp = h.getParty();
               Set<Hero> inRangeMembers = new HashSet<>();

               for (Hero partyMember : hp.getMembers()) {
                  if (player.getLocation().getWorld().equals(partyMember.getPlayer().getLocation().getWorld())
                     && !(player.getLocation().distanceSquared(partyMember.getPlayer().getLocation()) > 2500.0)
                     && partyMember.canGain(ExperienceType.KILLING)) {
                     inRangeMembers.add(partyMember);
                  }
               }

               int partySize = inRangeMembers.size();
               double sharedExp = amount / partySize;
               double bonusExp = partySize > 1 ? sharedExp : 0.0;
               if (partySize > 1) {
                  bonusExp *= Properties.partyMults[partySize - 1];
               }

               bonusExp *= Heroes.properties.partyBonus;
               bonusExp = Util.formatDouble(bonusExp);

               for (Hero partyMemberx : inRangeMembers) {
                  partyMemberx.gainExp(sharedExp + bonusExp, ExperienceType.KILLING, BukkitAdapter.adapt(am.getLocation()));
                  this.sendXPMessage(partyMemberx.getPlayer(), am, sharedExp + bonusExp);
               }
            } else {
               h.gainExp(amount, ExperienceType.KILLING, BukkitAdapter.adapt(am.getLocation()));
               this.sendXPMessage(h.getPlayer(), am, amount);
            }
         }
      }
   }

   private void sendXPMessage(Player p, SkillCaster am, double amount) {
      if (ConfigExecutor.compatHeroesShowXPMessage) {
         String message = ConfigExecutor.compatHeroesXPMessageFormat;
         message = PlaceholderString.of(message).get(BukkitAdapter.adapt(p));
         message = message.replace("<drops.xp>", String.valueOf(amount));
         p.sendMessage(message);
      }
   }

   public void setMobDamage(LivingEntity l, double damage) {
      this.heroes.getCharacterManager().getMonster(l).setDamage(damage);
   }
}
