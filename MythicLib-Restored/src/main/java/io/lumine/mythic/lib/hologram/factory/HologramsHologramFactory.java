package io.lumine.mythic.lib.hologram.factory;

import com.sainttx.holograms.HologramPlugin;
import com.sainttx.holograms.api.HologramManager;
import com.sainttx.holograms.api.line.TextLine;
import io.lumine.mythic.lib.hologram.Hologram;
import io.lumine.mythic.lib.hologram.HologramFactory;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class HologramsHologramFactory implements HologramFactory {
   private final HologramManager hologramManager = ((HologramPlugin)JavaPlugin.getPlugin(HologramPlugin.class)).getHologramManager();

   @NotNull
   @Override
   public Hologram newHologram(@NotNull Location loc, @NotNull List<String> lines) {
      return new HologramsHologramFactory.HologramImpl(loc, lines);
   }

   private final class HologramImpl extends Hologram {
      private final com.sainttx.holograms.api.Hologram holo;
      private boolean spawned = true;

      public HologramImpl(Location loc, List<String> list) {
         this.holo = new com.sainttx.holograms.api.Hologram("MythicLib-" + UUID.randomUUID(), loc);

         for (String str : list) {
            this.holo.addLine(new TextLine(this.holo, str));
         }
      }

      @Override
      public List<String> getLines() {
         return this.holo.getLines().stream().map(line -> line.getRaw()).collect(Collectors.toList());
      }

      @Override
      public Location getLocation() {
         return this.holo.getLocation();
      }

      @Override
      public void updateLines(@NotNull List<String> list) {
         throw new RuntimeException("Adding lines is not supported");
      }

      @Override
      public void despawn() {
         Validate.isTrue(this.spawned, "Hologram is already despawned");
         HologramsHologramFactory.this.hologramManager.deleteHologram(this.holo);
         this.spawned = false;
      }

      @Override
      public boolean isSpawned() {
         return this.spawned;
      }

      @Override
      public void updateLocation(@NotNull Location loc) {
         this.holo.teleport(loc);
      }
   }
}
