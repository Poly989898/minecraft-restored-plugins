package io.lumine.mythic.lib.hologram.factory;

import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.hologram.Hologram;
import io.lumine.mythic.lib.hologram.HologramFactory;
import java.util.List;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class HDHologramFactory implements HologramFactory {
   @NotNull
   @Override
   public Hologram newHologram(@NotNull Location loc, @NotNull List<String> lines) {
      return new HDHologramFactory.HologramImpl(loc, lines);
   }

   private static final class HologramImpl extends Hologram {
      private final com.gmail.filoghost.holographicdisplays.api.Hologram holo;
      private final List<String> lines;
      private boolean spawned = true;

      public HologramImpl(Location loc, List<String> list) {
         this.holo = HologramsAPI.createHologram(MythicLib.plugin, loc);
         this.lines = list;

         for (String line : this.lines) {
            this.holo.appendTextLine(line);
         }
      }

      @Override
      public List<String> getLines() {
         return this.lines;
      }

      @Override
      public void updateLines(@NotNull List<String> list) {
         throw new NotImplementedException("Adding lines is not supported");
      }

      @Override
      public Location getLocation() {
         return this.holo.getLocation();
      }

      @Override
      public void updateLocation(@NotNull Location loc) {
         this.holo.teleport(loc);
      }

      @Override
      public void despawn() {
         Validate.isTrue(this.spawned, "Hologram is already despawned");
         this.holo.delete();
         this.spawned = false;
      }

      @Override
      public boolean isSpawned() {
         return this.spawned;
      }
   }
}
