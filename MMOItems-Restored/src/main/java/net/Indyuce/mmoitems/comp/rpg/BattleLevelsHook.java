package net.Indyuce.mmoitems.comp.rpg;

import me.robin.battlelevels.api.BattleLevelsAPI;
import me.robin.battlelevels.events.PlayerLevelUpEvent;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class BattleLevelsHook implements RPGHandler, Listener {
   @EventHandler
   public void a(PlayerLevelUpEvent var1) {
      PlayerData.get(var1.getPlayer()).getInventory().scheduleUpdate();
   }

   @Override
   public RPGPlayer getInfo(PlayerData var1) {
      return new BattleLevelsHook.BattleLevelsPlayer(var1);
   }

   @Override
   public void refreshStats(PlayerData var1) {
   }

   public static class BattleLevelsPlayer extends RPGPlayer {
      public BattleLevelsPlayer(PlayerData var1) {
         super(var1);
      }

      @Override
      public int getLevel() {
         return BattleLevelsAPI.getLevel(this.getPlayer().getUniqueId());
      }

      @Override
      public String getClassName() {
         return "";
      }

      @Override
      public double getMana() {
         return this.getPlayer().getFoodLevel();
      }

      @Override
      public double getStamina() {
         return 0.0;
      }

      @Override
      public void setMana(double var1) {
         this.getPlayer().setFoodLevel((int)var1);
      }

      @Override
      public void setStamina(double var1) {
      }
   }
}
