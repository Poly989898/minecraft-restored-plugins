package io.lumine.mythic.lib.hologram;

import io.lumine.mythic.lib.hologram.factory.BukkitHologramFactory;
import io.lumine.mythic.lib.hologram.factory.CMIHologramFactory;
import io.lumine.mythic.lib.hologram.factory.DecentHologramFactory;
import io.lumine.mythic.lib.hologram.factory.HDHologramFactory;
import io.lumine.mythic.lib.hologram.factory.HologramsHologramFactory;
import io.lumine.mythic.lib.hologram.factory.LegacyBukkitHologramFactory;
import io.lumine.mythic.lib.hologram.factory.TrHologramFactory;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public enum HologramFactoryList {
   TEXT_DISPLAYS("TextDisplays", BukkitHologramFactory::new),
   CMI("CMI", CMIHologramFactory::new),
   DECENT_HOLOGRAMS("DecentHolograms", DecentHologramFactory::new),
   HOLOGRAPHIC_DISPLAYS("HolographicDisplays", HDHologramFactory::new),
   HOLOGRAMS("Holograms", HologramsHologramFactory::new),
   LEGACY_ARMOR_STANDS("LegacyArmorStands", LegacyBukkitHologramFactory::new),
   TR_HOLOGRAM("TrHologram", TrHologramFactory::new);

   private final String name;
   private final Supplier<HologramFactory> factoryProvider;

   HologramFactoryList(@NotNull String name, @NotNull Supplier<HologramFactory> factoryProvider) {
      this.name = name;
      this.factoryProvider = factoryProvider;
   }

   public String getName() {
      return this.name;
   }

   public HologramFactory provide() {
      return this.factoryProvider.get();
   }
}
