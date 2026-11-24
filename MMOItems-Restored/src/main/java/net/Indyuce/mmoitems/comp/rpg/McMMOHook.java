package net.Indyuce.mmoitems.comp.rpg;

import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.api.exceptions.McMMOPlayerNotFoundException;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelDownEvent;
import com.gmail.nossr50.events.experience.McMMOPlayerLevelUpEvent;
import net.Indyuce.mmoitems.api.player.PlayerData;
import net.Indyuce.mmoitems.api.player.RPGPlayer;
import net.Indyuce.mmoitems.stat.type.DisableStat;
import net.Indyuce.mmoitems.stat.type.ItemStat;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class McMMOHook implements RPGHandler, Listener {
   public static final ItemStat disableMcMMORepair = new DisableStat(
      "MCMMO_REPAIR", Material.IRON_BLOCK, "Disable McMMO Repair", "Players can't repair this with McMMO."
   );

   @EventHandler(ignoreCancelled = true)
   public void a(McMMOPlayerLevelUpEvent var1) {
      PlayerData.get(var1.getPlayer()).getInventory().scheduleUpdate();
   }

   @EventHandler(ignoreCancelled = true)
   public void b(McMMOPlayerLevelDownEvent var1) {
      PlayerData.get(var1.getPlayer()).getInventory().scheduleUpdate();
   }

   @Override
   public RPGPlayer getInfo(PlayerData var1) {
      return new McMMOHook.McMMOPlayer(var1);
   }

   @Override
   public void refreshStats(PlayerData var1) {
   }

   public static class McMMOPlayer extends RPGPlayer {
      public McMMOPlayer(PlayerData var1) {
         super(var1);
      }

      @Override
      public int getLevel() {
         try {
            return ExperienceAPI.getPowerLevel(this.getPlayer());
         } catch (McMMOPlayerNotFoundException var2) {
            return 0;
         }
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
