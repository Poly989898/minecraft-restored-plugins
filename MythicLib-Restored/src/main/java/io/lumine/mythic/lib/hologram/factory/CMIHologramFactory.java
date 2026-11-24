package io.lumine.mythic.lib.hologram.factory;

import com.Zrips.CMI.CMI;
import com.Zrips.CMI.Modules.Holograms.CMIHologram;
import io.lumine.mythic.lib.hologram.Hologram;
import io.lumine.mythic.lib.hologram.HologramFactory;
import java.util.List;
import java.util.UUID;
import net.Zrips.CMILib.Container.CMILocation;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class CMIHologramFactory implements HologramFactory {
   @NotNull
   @Override
   public Hologram newHologram(@NotNull Location loc, @NotNull List<String> lines) {
      return new CMIHologramFactory.HologramImpl(loc, lines);
   }

   private static final class HologramImpl extends Hologram {
      private final CMIHologram holo;
      private boolean spawned = true;

      public HologramImpl(Location loc, List<String> list) {
         this.holo = new CMIHologram("MythicLib-" + UUID.randomUUID().toString(), new CMILocation(loc));
         this.holo.setLines(list);
         CMI.getInstance().getHologramManager().addHologram(this.holo);
         this.holo.update();
      }

      @Override
      public void updateLines(List<String> list) {
         this.holo.setLines(list);
         this.holo.update();
      }

      @Override
      public Location getLocation() {
         return this.holo.getLocation();
      }

      @Override
      public void despawn() {
         Validate.isTrue(this.spawned, "Hologram is already despawned");
         CMI.getInstance().getHologramManager().removeHolo(this.holo);
         this.spawned = false;
      }

      @Override
      public boolean isSpawned() {
         return this.spawned;
      }

      @Override
      public void updateLocation(@NotNull Location loc) {
         this.holo.setLoc(loc);
         this.holo.update();
      }

      @Override
      public List<String> getLines() {
         return this.holo.getLines();
      }
   }
}
