package com.nexomc.nexo.compatibilities.mmoitems;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.utils.PluginUtils;
import com.nexomc.nexo.utils.logs.Logs;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import net.Indyuce.mmoitems.MMOItems;
import net.Indyuce.mmoitems.api.ItemTier;
import net.Indyuce.mmoitems.api.Type;
import net.Indyuce.mmoitems.api.item.build.ItemStackBuilder;
import net.Indyuce.mmoitems.api.item.build.MMOItemBuilder;
import net.Indyuce.mmoitems.api.item.mmoitem.MMOItem;
import net.Indyuce.mmoitems.api.item.template.MMOItemTemplate;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B?\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\f\u0010\rB\u0011\b\u0016\u0012\u0006\u0010\u000e\u001a\u00020\u000f¢\u0006\u0004\b\f\u0010\u0010B\u0019\b\u0016\u0012\u0006\u0010\u000e\u001a\u00020\u000f\u0012\u0006\u0010\u0011\u001a\u00020\u000b¢\u0006\u0004\b\f\u0010\u0012J\n\u0010\u0017\u001a\u0004\u0018\u00010\u0018H\u0002J\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fR\u0010\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0014\"\u0004\b\u0015\u0010\u0016R\u0018\u0010\u0019\u001a\t\u0018\u00010\u001a¢\u0006\u0002\b\u001b¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001d¨\u0006 "},
   d2 = {"Lcom/nexomc/nexo/compatibilities/mmoitems/WrappedMMOItem;", "", "type", "Lnet/Indyuce/mmoitems/api/Type;", "id", "", "level", "", "tier", "Lnet/Indyuce/mmoitems/api/ItemTier;", "cache", "", "<init>", "(Lnet/Indyuce/mmoitems/api/Type;Ljava/lang/String;ILnet/Indyuce/mmoitems/api/ItemTier;Z)V", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "silent", "(Lorg/bukkit/configuration/ConfigurationSection;Z)V", "getCache", "()Z", "setCache", "(Z)V", "template", "Lnet/Indyuce/mmoitems/api/item/template/MMOItemTemplate;", "material", "Lorg/bukkit/Material;", "Lorg/jetbrains/annotations/NotNull;", "getMaterial", "()Lorg/bukkit/Material;", "build", "Lorg/bukkit/inventory/ItemStack;", "com.nexomc:core"}
)
public final class WrappedMMOItem {
   @Nullable
   private Type type;
   @Nullable
   private String id;
   private int level;
   @Nullable
   private ItemTier tier;
   private boolean cache;
   @Nullable
   private final Material material;

   public WrappedMMOItem(@Nullable Type type, @Nullable String id, int level, @Nullable ItemTier tier, boolean cache) {
      this.type = type;
      this.id = id;
      this.level = level;
      this.tier = tier;
      this.cache = cache;
      WrappedMMOItem var6 = this;

      Object $this$material_u24lambda_u240;
      try {
         Material var18;
         block: {
            WrappedMMOItem $this$material_u24lambda_u240_1 = var6;
            int var8 = 0;
            MMOItemTemplate var13 = MMOItems.plugin.getTemplates().getTemplate($this$material_u24lambda_u240_1.type, $this$material_u24lambda_u240_1.id);
            if (var13 != null) {
               MMOItemBuilder var14 = var13.newBuilder();
               if (var14 != null) {
                  MMOItem var15 = var14.build();
                  if (var15 != null) {
                     ItemStackBuilder var16 = var15.newBuilder();
                     if (var16 != null) {
                        ItemStack var17 = var16.buildSilently();
                        if (var17 != null) {
                           var18 = var17.getType();
                           break block;
                        }
                     }
                  }
               }
            }

            var18 = null;
         }

         $this$material_u24lambda_u240 = ResultShim.ok(var18);
      } catch (Throwable var10) {
         $this$material_u24lambda_u240 = ResultShim.ok(ResultKt.createFailure(var10));
      }

      this.material = (Material)(ResultShim.isFailure($this$material_u24lambda_u240) ? null : $this$material_u24lambda_u240);
   }

   public final boolean getCache() {
      return this.cache;
   }

   public final void setCache(boolean var1) {
      this.cache = var1;
   }

   public WrappedMMOItem(@NotNull ConfigurationSection section) {
      Intrinsics.checkNotNullParameter(section, "section");
      this(section, false);
   }

   public WrappedMMOItem(@NotNull ConfigurationSection section, boolean silent) {
      Intrinsics.checkNotNullParameter(section, "section");
      this((Type)null, (String)null, 0, (ItemTier)null, true);
      if (PluginUtils.INSTANCE.isMMOItemsEnabled()) {
         this.type = MMOItems.plugin.getTypes().get(section.getString("type"));
         this.id = section.getString("id");
         if (!silent && !MMOItems.plugin.getTemplates().hasTemplate(this.type, this.id)) {
            Logs.logError("Failed to load MMOItem " + this.id, false);
            Logs.logError("Template does not exist", false);
         }

         this.level = section.getInt("level", 1);
         this.tier = MMOItems.plugin.getTiers().get(section.getString("tier"));
         this.cache = section.getBoolean("cache", true);
      }
   }

   private final MMOItemTemplate template() {
      MMOItemTemplate var10000 = MMOItems.plugin.getTemplates().getTemplate(this.type, this.id);
      if (var10000 == null) {
         WrappedMMOItem $this$template_u24lambda_u240 = this;
         int var2 = 0;
         Logs.logError("Failed to load MMOItem " + $this$template_u24lambda_u240.id + ", Template does not exist", false);
         var10000 = null;
      }

      return var10000;
   }

   @Nullable
   public final Material getMaterial() {
      return this.material;
   }

   @Nullable
   public final ItemStack build() {
      if (!PluginUtils.INSTANCE.isMMOItemsEnabled()) {
         Logs.logError("Failed to load MMOItem " + this.id + ", MMOItems is not installed", false);
         return null;
      } else {
         ItemStack var7;
         block: {
            MMOItemTemplate var10000 = this.template();
            if (var10000 != null) {
               MMOItemBuilder var4 = var10000.newBuilder();
               if (var4 != null) {
                  MMOItem var5 = var4.build();
                  if (var5 != null) {
                     ItemStackBuilder var6 = var5.newBuilder();
                     if (var6 != null) {
                        var7 = var6.buildSilently();
                        break block;
                     }
                  }
               }
            }

            var7 = null;
         }

         ItemStack it = var7;
         int var3 = 0;
         if (it == null) {
            Logs.logError("Failed to load MMOItem " + this.id + ", Item does not exist", false);
         }

         return it;
      }
   }

   public WrappedMMOItem() {
      this((Type)null, (String)null, 0, (ItemTier)null, true);
   }
}
