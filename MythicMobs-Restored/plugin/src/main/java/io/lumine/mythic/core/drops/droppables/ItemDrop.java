package io.lumine.mythic.core.drops.droppables;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.skills.placeholders.PlaceholderString;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.numbers.RandomDouble;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.core.drops.Drop;
import io.lumine.mythic.core.glow.GlowColor;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;

public abstract class ItemDrop extends Drop {
   @Getter
   protected Optional<Boolean> lootsplosionEnabled;
   @Getter
   protected Optional<Boolean> clientSideDropsEnabled;
   @Getter
   protected Optional<Boolean> hologramNameEnabled;
   @Getter
   protected Optional<Boolean> itemGlowEnabled;
   @Getter
   protected Optional<Boolean> itemBeamEnabled;
   @Getter
   protected Optional<Boolean> itemVFXEnabled;
   @Getter
   protected GlowColor itemGlow = null;
   @Getter
   protected Chroma itemBeam = null;
   @Getter
   protected String itemVfxMaterial;
   @Getter
   protected int itemVfxData;
   @Getter
   protected String itemVfxModel;
   @Getter
   protected long pityModifier;
   @Getter
   protected boolean resetPity;
   @Getter
   protected String pityCategory;
   @Getter
   protected double minDamage;
   @Getter
   protected int requiredPlacement;
   @Getter
   protected String displayBillboarding;
   @Getter
   protected int displayBrightness;
   @Getter
   protected String vfxColor;
   protected double fortuneMod = 0.0;
   protected double lootingMod = 0.0;

   public ItemDrop(String line, MythicLineConfig config) {
      super(line, config);
      this.lootsplosionEnabled = Optional.ofNullable(config.getBooleanNullable(new String[]{"lootsplosion", "lootsplosionenabled", "ls"}));
      this.clientSideDropsEnabled = Optional.ofNullable(config.getBooleanNullable(new String[]{"clientsidedrops", "clientsidedropsenabled", "csd"}));
      this.hologramNameEnabled = Optional.ofNullable(config.getBooleanNullable(new String[]{"hologramname", "hologramnameenabled", "hn"}));
      this.itemGlowEnabled = Optional.ofNullable(config.getBooleanNullable(new String[]{"itemglow", "itemglowenabled", "ig"}));
      String glowColor = config.getString(new String[]{"itemglowcolor", "glowcolor", "gc"}, null);
      if (glowColor != null) {
         try {
            this.itemGlow = GlowColor.valueOf(glowColor);
         } catch (Throwable e) {
            MythicLogger.errorDropConfig(this, config, "Invalid ItemGlow Color specified");
            e.printStackTrace();
         }
      }

      this.itemBeamEnabled = Optional.ofNullable(config.getBooleanNullable(new String[]{"itembeam", "itembeamenabled", "ib"}));
      Chroma beamColor = config.getColor(new String[]{"itembeamcolor", "beamColor", "bc"}, null);
      if (beamColor != null) {
         try {
            this.itemBeam = beamColor;
         } catch (Throwable ex) {
            MythicLogger.errorDropConfig(this, config, "Invalid BeamColor Color specified");
         }
      }

      this.itemVFXEnabled = Optional.ofNullable(config.getBooleanNullable(new String[]{"itemvfx", "ivfx", "vfx"}));
      this.itemVfxMaterial = config.getString(new String[]{"vfxmaterial", "vfxmat", "vfxm"}, null);
      this.itemVfxData = config.getInteger(new String[]{"vfxdata", "vfxd"}, 0);
      PlaceholderString modelString = config.getPlaceholderString(new String[]{"vfxmodel", "vfxitemmodel"}, null);
      if (modelString != null) {
         this.itemVfxModel = modelString.get();
      }

      this.vfxColor = config.getString(new String[]{"vfxcolor", "vfxc", "color"}, null);
      this.pityModifier = config.getLong(new String[]{"pitymodifier", "pitymod", "pmod"}, 0L);
      this.resetPity = config.getBoolean(new String[]{"resetpity", "resetp", "rp"}, false);
      this.pityCategory = config.getString(new String[]{"pcategory", "pitycategory", "category"}, "DEFAULT");
      this.minDamage = config.getDouble(new String[]{"damage", "mindamage", "min"}, 0.0);
      this.requiredPlacement = config.getInteger(new String[]{"top", "placement", "required"}, Integer.MAX_VALUE);
      this.displayBillboarding = config.getString(new String[]{"billboarding", "billboard", "bill"}, "VERTICAL");
      this.displayBrightness = config.getInteger(new String[]{"brightness", "bright", "b"}, 0);
      boolean fortune = config.getBoolean("fortune", false);
      this.fortuneMod = config.getDouble(new String[]{"fortunemod"}, fortune ? 1.0 : 0.0);
      boolean looting = config.getBoolean("looting", false);
      this.lootingMod = config.getDouble(new String[]{"lootingmod"}, fortune ? 1.0 : 0.0);
   }

   public ItemDrop(String line, MythicLineConfig config, double amount) {
      super(line, config, amount);
   }

   public ItemDrop(String line, MythicLineConfig config, RandomDouble amount) {
      super(line, config, amount);
   }

   protected int rollBonuses(DropMetadata meta, int amount) {
      if (this.fortuneMod > 0.0 && meta.getCause().isPresent()) {
         AbstractEntity cause = meta.getCause().get();
         if (cause.isPlayer()) {
            AbstractPlayer player = cause.asPlayer();
            int fortuneLevel = player.getFortuneLevel();
            amount = this.getFortuneMultiplier(fortuneLevel) * amount;
         }
      }

      return amount;
   }

   protected int getFortuneMultiplier(int fortuneLevel) {
      if (fortuneLevel <= 0) {
         return 1;
      } else {
         int baseWeight = 2;
         int totalWeight = baseWeight + fortuneLevel;
         int randomWeight = Numbers.floor(ThreadLocalRandom.current().nextDouble() * totalWeight * this.fortuneMod);
         return randomWeight < baseWeight ? 1 : 2 + (randomWeight - baseWeight);
      }
   }
}
