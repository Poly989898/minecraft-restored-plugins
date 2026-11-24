package io.lumine.mythic.lib.hologram.factory;

import io.lumine.mythic.lib.hologram.Hologram;
import io.lumine.mythic.lib.hologram.HologramFactory;
import io.lumine.mythic.lib.listener.option.GameIndicators;
import java.util.List;
import me.arasple.mc.trhologram.api.TrHologramAPI;
import me.arasple.mc.trhologram.api.hologram.HologramBuilder;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

public class TrHologramFactory implements HologramFactory {
   @NotNull
   @Override
   public Hologram newHologram(@NotNull Location loc, @NotNull List<String> lines) {
      return new TrHologramFactory.HologramImpl(loc, lines);
   }

   static final class HologramImpl extends Hologram {
      private final me.arasple.mc.trhologram.module.display.Hologram holo;
      private final List<String> lines;
      private boolean spawned = true;

      public HologramImpl(Location loc, List<String> lines) {
         HologramBuilder builder = TrHologramAPI.builder(loc);

         for (String line : lines) {
            builder.append(line);
         }

         this.holo = builder.build();
         this.lines = lines;
      }

      @Override
      public boolean isSpawned() {
         return this.spawned;
      }

      @Override
      public List<String> getLines() {
         return this.lines;
      }

      @Override
      public void updateLines(@NotNull List<String> list) {
         throw new RuntimeException("Updating lines is not supported");
      }

      @Override
      public Location getLocation() {
         return this.holo.getPosition().toLocation();
      }

      @Override
      public void updateLocation(Location loc) {
      }

      @Override
      public void flyOut(@NotNull GameIndicators settings, @NotNull Vector dir) {
      }

      @Override
      public void despawn() {
         Validate.isTrue(this.spawned, "Hologram is already despawned");
         this.holo.destroy();
         this.spawned = false;
      }
   }
}
