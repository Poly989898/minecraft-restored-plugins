package net.Indyuce.mmoitems.comp.rpg;

import de.tobiyas.racesandclasses.eventprocessing.events.leveling.LevelDownEvent;
import de.tobiyas.racesandclasses.eventprocessing.events.leveling.LevelUpEvent;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayer;
import de.tobiyas.racesandclasses.playermanagement.player.RaCPlayerManager;
import net.Indyuce.mmoitems.ItemStats;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class RacesAndClassesHook implements RPGHandler, Listener {
   @Override
   public void refreshStats(PlayerData var1) {
      RaCPlayer var2 = RaCPlayerManager.get().getPlayer(var1.getPlayer());
      var2.getManaManager().removeMaxManaBonus("MMOItems");
      var2.getManaManager().addMaxManaBonus("MMOItems", var1.getStats().getStat(ItemStats.MAX_MANA));
   }

   @Override
   public RPGPlayer getInfo(PlayerData var1) {
      return new RacesAndClassesHook.RacePlayer(var1);
   }

   @EventHandler
   public void a(LevelUpEvent var1) {
      PlayerData.get(var1.getPlayer()).getInventory().scheduleUpdate();
   }

   @EventHandler
   public void b(LevelDownEvent var1) {
      PlayerData.get(var1.getPlayer()).getInventory().scheduleUpdate();
   }

   public static class RacePlayer extends RPGPlayer {
      public RacePlayer(PlayerData var1) {
         super(var1);
      }

      @Override
      public int getLevel() {
         RaCPlayer var1 = RaCPlayerManager.get().getPlayer(this.getPlayer().getUniqueId());
         return var1.getCurrentLevel();
      }

      @Override
      public String getClassName() {
         RaCPlayer var1 = RaCPlayerManager.get().getPlayer(this.getPlayer().getUniqueId());
         return var1.getclass().getDisplayName();
      }

      @Override
      public double getMana() {
         RaCPlayer var1 = RaCPlayerManager.get().getPlayer(this.getPlayer().getUniqueId());
         return var1.getCurrentMana();
      }

      @Override
      public double getStamina() {
         RaCPlayer var1 = RaCPlayerManager.get().getPlayer(this.getPlayer().getUniqueId());
         return var1.getPlayer().getFoodLevel();
      }

      @Override
      public void setMana(double var1) {
         RaCPlayer var3 = RaCPlayerManager.get().getPlayer(this.getPlayer().getUniqueId());
         var3.getManaManager().fillMana(var1 - var3.getManaManager().getCurrentMana());
      }

      @Override
      public void setStamina(double var1) {
         RaCPlayer var3 = RaCPlayerManager.get().getPlayer(this.getPlayer().getUniqueId());
         var3.getPlayer().setFoodLevel((int)var1);
      }
   }
}
