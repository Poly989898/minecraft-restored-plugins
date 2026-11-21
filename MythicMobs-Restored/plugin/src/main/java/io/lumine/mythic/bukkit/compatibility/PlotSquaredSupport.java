package io.lumine.mythic.bukkit.compatibility;

import com.plotsquared.bukkit.util.BukkitUtil;
import com.plotsquared.core.plot.Plot;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Location;

public class PlotSquaredSupport implements ClaimSupport {
   public PlotSquaredSupport(MythicBukkit mm) {
   }

   @Override
   public boolean isLocationClaimed(AbstractLocation aLocation) {
      Location location = BukkitAdapter.adapt(aLocation);
      Plot plot = Plot.getPlot(BukkitUtil.adapt(location));
      return plot != null && plot.hasOwner();
   }
}
