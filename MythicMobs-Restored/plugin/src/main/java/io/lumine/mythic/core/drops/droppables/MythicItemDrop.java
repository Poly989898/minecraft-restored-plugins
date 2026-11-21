package io.lumine.mythic.core.drops.droppables;

import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.core.glow.GlowColor;
import io.lumine.mythic.core.items.MythicItem;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.Optional;
import lombok.Getter;

@Getter
public class MythicItemDrop extends ItemDrop implements IItemDrop {
   private final MythicItem item;
   protected PlaceholderDouble level;

   public MythicItemDrop(String line, String item, MythicLineConfig config) {
      super(line, config);
      this.item = MythicBukkit.inst().getItemManager().getItem(item).get();
      this.level = config.getPlaceholderDouble(new String[]{"level", "lvl", "l"}, (PlaceholderDouble)null);
      String lootsplosion = config.getString(new String[]{"lootsplosion", "lootsplosionenabled", "ls"}, null);
      if (lootsplosion != null) {
         this.lootsplosionEnabled = Optional.of(Boolean.parseBoolean(lootsplosion));
      } else if (this.item.isDropLootsplosion()) {
         this.lootsplosionEnabled = Optional.of(true);
      }

      String itemVFXEnabled = config.getString(new String[]{"itemvfx", "itemvfxenabled", "iv"}, null);
      if (itemVFXEnabled != null) {
         this.itemVFXEnabled = Optional.of(Boolean.parseBoolean(itemVFXEnabled));
      } else if (this.item.isDropVFX()) {
         this.itemVFXEnabled = Optional.of(true);
      }

      this.itemVfxMaterial = config.getString(new String[]{"itemvfxmaterial", "itemvfxmaterial", "ivm"}, this.item.getDropVFXMaterial());
      this.itemVfxData = config.getInteger(new String[]{"vfxdata", "vfxd"}, this.item.getDropVFXData());
      this.vfxColor = config.getString(new String[]{"vfxcolor", "vfxc", "color"}, this.item.getDropVFXColor());
      String hologramNameEnabled = config.getString(new String[]{"hologramname", "hologramnameenabled", "hn"}, null);
      if (hologramNameEnabled != null) {
         this.hologramNameEnabled = Optional.of(Boolean.parseBoolean(hologramNameEnabled));
      } else if (this.item.isDropHologram()) {
         this.hologramNameEnabled = Optional.of(true);
      }

      String clientSideEnabled = config.getString(new String[]{"clientsidedrops", "clientsidedropsenabled", "csd"}, null);
      if (clientSideEnabled != null) {
         this.clientSideDropsEnabled = Optional.of(Boolean.parseBoolean(clientSideEnabled));
      } else if (this.item.isDropClientSide()) {
         this.clientSideDropsEnabled = Optional.of(true);
      }

      String glowColor = config.getString(new String[]{"itemglowcolor", "glowcolor", "gc"}, this.item.getDropGlowColor());
      if (glowColor != null) {
         try {
            this.itemGlow = GlowColor.valueOf(glowColor);
            this.itemGlowEnabled = Optional.of(true);
         } catch (Throwable e) {
            MythicLogger.errorDropConfig(this, config, "Invalid ItemGlow Color specified");
            e.printStackTrace();
         }
      } else if (this.item.isDropGlow()) {
         this.itemGlowEnabled = Optional.of(true);
         if (this.item.getDropGlowColor() != null) {
            try {
               this.itemGlow = GlowColor.valueOf(this.item.getDropGlowColor());
            } catch (Throwable ex) {
               MythicLogger.errorDropConfig(this, config, "Invalid ItemGlow Color specified on item");
               ex.printStackTrace();
            }
         }
      }

      Chroma beamColor = config.getColor(new String[]{"itembeamcolor", "beamColor", "bc"}, this.item.getDropBeamColor());
      if (beamColor != null) {
         try {
            this.itemBeam = beamColor;
            this.itemBeamEnabled = Optional.of(true);
         } catch (Throwable ex2) {
            MythicLogger.errorDropConfig(this, config, "Invalid BeamColor Color specified");
         }
      } else if (this.item.isDropBeam()) {
         this.itemBeamEnabled = Optional.of(true);
         this.itemBeam = this.item.getDropBeamColor();
      }

      this.displayBillboarding = config.getString(new String[]{"billboarding", "billboard", "bill"}, this.item.getDropBillboarding());
      this.displayBrightness = config.getInteger(new String[]{"brightness", "bright", "b"}, this.item.getDropBrightness());
   }

   @Override
   public AbstractItemStack getDrop(DropMetadata meta, double amount) {
      if (this.level != null) {
         meta = meta.clone();
         meta.setLevel(this.level.get(meta));
      }

      int finalAmount = this.rollBonuses(meta, Numbers.floor(amount));
      return this.item.generateItemStack(meta, finalAmount);
   }
}
