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
   public void refreshStats(PlayerData playerData) {
      RaCPlayer player = RaCPlayerManager.get().getPlayer(playerData.getPlayer());
      player.getManaManager().removeMaxManaBonus("MMOItems");
      player.getManaManager().addMaxManaBonus("MMOItems", playerData.getStats().getStat(ItemStats.MAX_MANA));
   }

   @Override
   public RPGPlayer getInfo(PlayerData playerData) {
      return new RacesAndClassesHook.RacePlayer(playerData);
   }

   @EventHandler
   public void a(LevelUpEvent event) {
      PlayerData.get(event.getPlayer()).getInventory().scheduleUpdate();
   }

   @EventHandler
   public void b(LevelDownEvent event) {
      PlayerData.get(event.getPlayer()).getInventory().scheduleUpdate();
   }

   public static class RacePlayer extends RPGPlayer {
      public RacePlayer(PlayerData playerData) {
         super(playerData);
      }

      @Override
      public int getLevel() {
         RaCPlayer player = RaCPlayerManager.get().getPlayer(this.getPlayer().getUniqueId());
         return player.getCurrentLevel();
      }

      @Override
      public String getClassName() {
         RaCPlayer player = RaCPlayerManager.get().getPlayer(this.getPlayer().getUniqueId());
         return player.getclass().getDisplayName();
      }

      @Override
      public double getMana() {
         RaCPlayer player = RaCPlayerManager.get().getPlayer(this.getPlayer().getUniqueId());
         return player.getCurrentMana();
      }

      @Override
      public double getStamina() {
         RaCPlayer player = RaCPlayerManager.get().getPlayer(this.getPlayer().getUniqueId());
         return player.getPlayer().getFoodLevel();
      }

      @Override
      public void setMana(double value) {
         RaCPlayer player = RaCPlayerManager.get().getPlayer(this.getPlayer().getUniqueId());
         player.getManaManager().fillMana(value - player.getManaManager().getCurrentMana());
      }

      @Override
      public void setStamina(double value) {
         RaCPlayer player = RaCPlayerManager.get().getPlayer(this.getPlayer().getUniqueId());
         player.getPlayer().setFoodLevel((int)value);
      }
   }
}
