package com.nexomc.nexo.compatibilities.mythicmobs;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.utils.logs.Logs;
import io.lumine.mythic.api.adapters.AbstractItemStack;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.drops.DropMetadata;
import io.lumine.mythic.api.drops.IItemDrop;
import io.lumine.mythic.api.skills.placeholders.PlaceholderDouble;
import io.lumine.mythic.bukkit.adapters.item.ItemComponentBukkitItemStack;
import io.lumine.mythic.bukkit.utils.numbers.Numbers;
import io.lumine.mythic.bukkit.utils.serialize.Chroma;
import io.lumine.mythic.core.drops.Drop;
import io.lumine.mythic.core.drops.droppables.ItemDrop;
import io.lumine.mythic.core.glow.GlowColor;
import io.lumine.mythic.core.logging.MythicLogger;
import java.util.Optional;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0004\u0018\u00002\u00020\u00012\u00020\u0002B\u001f\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\u001a\u0010\u0012\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0016J3\u0010\u0018\u001a\u0004\u0018\u00010\u0004*\u00020\b2\u0012\u0010\u0019\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\u001a\"\u00020\u00042\n\b\u0002\u0010\u001b\u001a\u0004\u0018\u00010\u0004H\u0002¢\u0006\u0002\u0010\u001cJ'\u0010\u001d\u001a\u0004\u0018\u00010\u001e*\u00020\b2\u0012\u0010\u0019\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\u001a\"\u00020\u0004H\u0002¢\u0006\u0002\u0010\u001fJ%\u0010 \u001a\u00020!*\u00020\b2\u0012\u0010\u0019\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\u001a\"\u00020\u0004H\u0002¢\u0006\u0002\u0010\"J'\u0010#\u001a\u0004\u0018\u00010\u000f*\u00020\b2\u0012\u0010\u0019\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00040\u001a\"\u00020\u0004H\u0002¢\u0006\u0002\u0010$R\u0011\u0010\u0005\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006%"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/mythicmobs/NexoDrop;", "Lio/lumine/mythic/core/drops/droppables/ItemDrop;", "Lio/lumine/mythic/api/drops/IItemDrop;", "line", "", "item", "Lorg/bukkit/inventory/ItemStack;", "config", "Lio/lumine/mythic/api/config/MythicLineConfig;", "<init>", "(Ljava/lang/String;Lorg/bukkit/inventory/ItemStack;Lio/lumine/mythic/api/config/MythicLineConfig;)V", "Lio/lumine/mythic/bukkit/adapters/item/ItemComponentBukkitItemStack;", "getItem", "()Lio/lumine/mythic/bukkit/adapters/item/ItemComponentBukkitItemStack;", "level", "Lio/lumine/mythic/api/skills/placeholders/PlaceholderDouble;", "getLevel", "()Lio/lumine/mythic/api/skills/placeholders/PlaceholderDouble;", "getDrop", "Lio/lumine/mythic/api/adapters/AbstractItemStack;", "meta", "Lio/lumine/mythic/api/drops/DropMetadata;", "amount", "", "string", "keys", "", "p1", "(Lio/lumine/mythic/api/config/MythicLineConfig;[Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;", "boolean", "", "(Lio/lumine/mythic/api/config/MythicLineConfig;[Ljava/lang/String;)Ljava/lang/Boolean;", "int", "", "(Lio/lumine/mythic/api/config/MythicLineConfig;[Ljava/lang/String;)I", "placeholderDouble", "(Lio/lumine/mythic/api/config/MythicLineConfig;[Ljava/lang/String;)Lio/lumine/mythic/api/skills/placeholders/PlaceholderDouble;", "com.nexomc:core"}
)
public final class NexoDrop extends ItemDrop implements IItemDrop {
   @NotNull
   private final ItemComponentBukkitItemStack item;
   @Nullable
   private final PlaceholderDouble level;

   public NexoDrop(@NotNull String line, @NotNull ItemStack item, @NotNull MythicLineConfig config) {
      Intrinsics.checkNotNullParameter(line, "line");
      Intrinsics.checkNotNullParameter(item, "item");
      Intrinsics.checkNotNullParameter(config, "config");
      super(line, config);
      this.item = new ItemComponentBukkitItemStack(item);
      String[] var4 = new String[]{"level", "lvl", "l"};
      this.level = this.placeholderDouble(config, var4);
      CharSequence var10001 = (CharSequence)line;
      String[] var5 = new String[]{" "};
      String var53 = (String)CollectionsKt.getOrNull(StringsKt.split(var10001, var5, false, 0), 2);
      if (var53 == null) {
         var53 = this.dropVar;
      }

      this.dropVar = var53;
      var5 = new String[]{"lootsplosion", "lootsplosionenabled", "ls"};
      Boolean var10000 = this.boolean_(config, var5);
      if (var10000 != null) {
         boolean it = var10000;
         int var7 = 0;
         this.lootsplosionEnabled = Optional.of(it);
      }

      var5 = new String[]{"itemvfx", "itemvfxenabled", "iv"};
      var10000 = this.boolean_(config, var5);
      if (var10000 != null) {
         boolean it = var10000;
         int var30 = 0;
         this.itemVFXEnabled = Optional.of(it);
      }

      var4 = new String[]{"itemvfxmaterial", "itemvfxmaterial", "ivm"};
      this.itemVfxMaterial = this.string(config, var4, (String)null);
      var4 = new String[]{"vfxdata", "vfxd"};
      this.itemVfxData = this.int_(config, var4);
      var4 = new String[]{"vfxcolor", "vfxc", "color"};
      this.vfxColor = this.string(config, var4, (String)null);
      var5 = new String[]{"hologramname", "hologramnameenabled", "hn"};
      var10000 = this.boolean_(config, var5);
      if (var10000 != null) {
         boolean it = var10000;
         int var31 = 0;
         this.hologramNameEnabled = Optional.of(it);
      }

      var5 = new String[]{"clientsidedrops", "clientsidedropsenabled", "csd"};
      var10000 = this.boolean_(config, var5);
      if (var10000 != null) {
         boolean it = var10000;
         int var32 = 0;
         this.clientSideDropsEnabled = Optional.of(it);
      }

      var5 = new String[]{"itemglowcolor", "glowcolor", "gc"};
      String var46 = this.string(config, var5, (String)null);
      if (var46 != null) {
         String color = var46;
         int var33 = 0;
         NexoDrop var8 = this;

         Object $this$lambda_u244_u240;
         try {
            NexoDrop $this$lambda_u244_u240_2 = var8;
            int var10 = 0;
            $this$lambda_u244_u240_2.itemGlow = GlowColor.valueOf(color);
            $this$lambda_u244_u240_2.itemGlowEnabled = Optional.of(true);
            $this$lambda_u244_u240 = ResultShim.ok(Unit.INSTANCE);
         } catch (Throwable var13) {
            $this$lambda_u244_u240 = ResultShim.ok(ResultKt.createFailure(var13));
         }

         Object var35 = $this$lambda_u244_u240;
         Throwable var49 = ResultShim.exceptionOrNull($this$lambda_u244_u240);
         if (var49 != null) {
            Throwable it = var49;
            int var11 = 0;
            MythicLogger.errorDropConfig((Drop)this, config, "Invalid ItemGlow Color specified");
            Logs.logWarn(ExceptionsKt.stackTraceToString(it), false);
         }

         ResultShim.ok(var35);
      }

      var5 = new String[]{"itembeamcolor", "beamColor", "bc"};
      Chroma var50 = config.getColor(var5, (Chroma)null);
      if (var50 != null) {
         Chroma beamColor = var50;
         int var34 = 0;
         NexoDrop var36 = this;

         Object it;
         try {
            NexoDrop $this$lambda_u245_u240 = var36;
            int it_1 = 0;
            $this$lambda_u245_u240.itemBeam = beamColor;
            $this$lambda_u245_u240.itemBeamEnabled = Optional.of(true);
            it = ResultShim.ok(Unit.INSTANCE);
         } catch (Throwable var12) {
            it = ResultShim.ok(ResultKt.createFailure(var12));
         }

         if (ResultShim.exceptionOrNull(it) != null) {
            int var42 = 0;
            MythicLogger.errorDropConfig((Drop)this, config, "Invalid BeamColor Color specified");
         }

         ResultShim.ok(it);
      }

      var4 = new String[]{"billboarding", "billboard", "bill"};
      this.displayBillboarding = this.string(config, var4, "VERTICAL");
      var4 = new String[]{"brightness", "bright", "b"};
      this.displayBrightness = this.int_(config, var4);
   }

   @NotNull
   public final ItemComponentBukkitItemStack getItem() {
      return this.item;
   }

   @Nullable
   public final PlaceholderDouble getLevel() {
      return this.level;
   }

   @Nullable
   public AbstractItemStack getDrop(@NotNull DropMetadata meta, double amount) {
      Intrinsics.checkNotNullParameter(meta, "meta");
      int finalAmount = this.rollBonuses(meta, Numbers.floor((double)((int)((double)this.item.getAmount() * amount))));
      return (AbstractItemStack)this.item.copy().amount(finalAmount);
   }

   private final String string(MythicLineConfig $this$string, String[] keys, String p1) {
      return $this$string.getString(keys, p1, new String[0]);
   }

   private final Boolean boolean_(MythicLineConfig $this$boolean, String... keys) {
      String var10000 = $this$boolean.getString(keys, (String)null, new String[0]);
      return var10000 != null ? StringsKt.toBooleanStrictOrNull(var10000) : null;
   }

   private final int int_(MythicLineConfig $this$int, String... keys) {
      return $this$int.getInteger(keys, 0);
   }

   private final PlaceholderDouble placeholderDouble(MythicLineConfig $this$placeholderDouble, String... keys) {
      return $this$placeholderDouble.getPlaceholderDouble(keys, "null", new String[0]);
   }
}
