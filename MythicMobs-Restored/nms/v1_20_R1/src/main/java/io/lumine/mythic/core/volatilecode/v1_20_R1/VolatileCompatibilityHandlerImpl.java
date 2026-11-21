package io.lumine.mythic.core.volatilecode.v1_20_R1;

import io.lumine.mythic.api.volatilecode.VolatileCodeHandler;
import io.lumine.mythic.api.volatilecode.handlers.VolatileCompatibilityHandler;
import org.bukkit.DyeColor;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Wolf;
import org.bukkit.entity.Cat.Type;
import org.bukkit.inventory.meta.BannerMeta;

public class VolatileCompatibilityHandlerImpl implements VolatileCompatibilityHandler {
   public VolatileCompatibilityHandlerImpl(VolatileCodeHandler handler) {
   }

   @Override
   public void setLegacyBannerPattern(BannerMeta bannerMeta, DyeColor color, String pattern) {
      PatternType patternType = PatternType.valueOf(pattern.toUpperCase());
      Pattern p = new Pattern(color, patternType);
      bannerMeta.addPattern(p);
   }

   @Override
   public void setLegacyBannerPattern(Banner bannerMeta, DyeColor color, String pattern) {
      PatternType patternType = PatternType.valueOf(pattern.toUpperCase());
      Pattern p = new Pattern(color, patternType);
      bannerMeta.addPattern(p);
   }

   @Override
   public void setCatVariant(Cat cat, String key) {
      cat.setCatType(Type.valueOf(key.toUpperCase()));
   }

   @Override
   public void setWolfVariant(Wolf wolf, String key) {
   }
}
