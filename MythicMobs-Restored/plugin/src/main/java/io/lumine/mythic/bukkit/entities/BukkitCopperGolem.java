package io.lumine.mythic.bukkit.entities;

import io.lumine.mythic.api.config.MythicConfig;
import io.lumine.mythic.bukkit.adapters.BukkitEntityType;
import io.lumine.mythic.core.logging.MythicLogger;
import io.papermc.paper.world.WeatheringCopperState;
import org.bukkit.entity.CopperGolem;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.CopperGolem.Oxidizing;

public class BukkitCopperGolem extends BukkitEntityType {
   private Object oxidizingState;
   private WeatheringCopperState weatheringState;

   @Override
   public EntityType getBukkitType() {
      return EntityType.COPPER_GOLEM;
   }

   @Override
   public int getHeight() {
      return 1;
   }

   @Override
   public void instantiate(MythicConfig mc) {
      boolean waxed = mc.getBoolean("Options.Waxed", true);
      if (waxed) {
         try {
            this.oxidizingState = Oxidizing.waxed();
         } catch (Throwable e) {
            MythicLogger.errorEntityConfig(this, mc, "COPPER_GOLEM is not supported on this server version (requires Minecraft 1.21.9+)");
         }
      }

      String weatheringState = mc.getString("Options.WeatheringState", null);
      if (weatheringState != null) {
         try {
            this.weatheringState = WeatheringCopperState.valueOf(weatheringState);
         } catch (Throwable ex) {
            MythicLogger.errorEntityConfig(this, mc, "Invalid Weathering State");
         }
      }
   }

   @Override
   public Entity applyOptions(Entity entity) {
      CopperGolem copperGolem = (CopperGolem)entity;
      if (this.oxidizingState != null) {
         try {
            copperGolem.setOxidizing((Oxidizing)this.oxidizingState);
         } catch (Throwable e) {
         }
      }

      if (this.weatheringState != null) {
         copperGolem.setWeatheringState(this.weatheringState);
      }

      return copperGolem;
   }
}
