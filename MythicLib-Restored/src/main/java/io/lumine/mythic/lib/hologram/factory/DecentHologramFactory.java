package io.lumine.mythic.lib.hologram.factory;

import eu.decentsoftware.holograms.api.holograms.HologramLine;
import eu.decentsoftware.holograms.api.holograms.HologramPage;
import io.lumine.mythic.lib.hologram.Hologram;
import io.lumine.mythic.lib.hologram.HologramFactory;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class DecentHologramFactory implements HologramFactory {
   @NotNull
   @Override
   public Hologram newHologram(@NotNull Location loc, @NotNull List<String> lines) {
      return new DecentHologramFactory.HologramImpl(loc, lines);
   }

   private static final class HologramImpl extends Hologram {
      private final eu.decentsoftware.holograms.api.holograms.Hologram holo;
      private final HologramPage page;
      private boolean spawned = true;

      public HologramImpl(Location loc, List<String> lines) {
         this.holo = new eu.decentsoftware.holograms.api.holograms.Hologram(UUID.randomUUID().toString(), loc, false);
         this.page = this.holo.getPage(0);

         for (String line : lines) {
            this.page.addLine(new HologramLine(this.page, this.page.getNextLineLocation(), line));
         }

         this.holo.showAll();
      }

      @Override
      public void updateLines(List<String> list) {
         while (!this.page.getLines().isEmpty()) {
            this.page.removeLine(0);
         }

         for (String line : list) {
            this.page.addLine(new HologramLine(this.page, this.page.getNextLineLocation(), line));
         }

         this.holo.updateAll();
      }

      @Override
      public Location getLocation() {
         return this.holo.getLocation();
      }

      @Override
      public void despawn() {
         Validate.isTrue(this.spawned, "Hologram is already despawned");
         this.holo.destroy();
         this.spawned = false;
      }

      @Override
      public boolean isSpawned() {
         return this.spawned;
      }

      @Override
      public void updateLocation(@NotNull Location loc) {
         this.holo.setLocation(loc);
         this.holo.realignLines();
      }

      @Override
      public List<String> getLines() {
         return this.holo.getPage(0).getLines().stream().map(line -> line.getContent()).collect(Collectors.toList());
      }
   }
}
