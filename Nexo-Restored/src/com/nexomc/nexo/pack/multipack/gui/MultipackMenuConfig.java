package com.nexomc.nexo.pack.multipack.gui;

import com.nexomc.nexo.utils.AdventureUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\b\u0018\u0000 \u001b2\u00020\u0001:\u0001\u001bB%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0005HÆ\u0003J\u000f\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\b0\u0007HÆ\u0003J-\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\u000e\b\u0002\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007HÆ\u0001J\u0014\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0018\u001a\u00020\u0005HÖ\u0081\u0004J\n\u0010\u0019\u001a\u00020\u001aHÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u001c"},
   d2 = {"Lcom/nexomc/nexo/pack/multipack/gui/MultipackMenuConfig;", "", "title", "Lnet/kyori/adventure/text/Component;", "rows", "", "slots", "", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotConfig;", "<init>", "(Lnet/kyori/adventure/text/Component;ILjava/util/List;)V", "getTitle", "()Lnet/kyori/adventure/text/Component;", "getRows", "()I", "getSlots", "()Ljava/util/List;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "", "Companion", "com.nexomc:core"}
)
public final class MultipackMenuConfig {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final Component title;
   private final int rows;
   @NotNull
   private final List<MultipackSlotConfig> slots;

   public MultipackMenuConfig(@NotNull Component title, int rows, @NotNull List<MultipackSlotConfig> slots) {
      super();
      this.title = title;
      this.rows = rows;
      this.slots = slots;
   }

   @NotNull
   public final Component getTitle() {
      return this.title;
   }

   public final int getRows() {
      return this.rows;
   }

   @NotNull
   public final List<MultipackSlotConfig> getSlots() {
      return this.slots;
   }

   @NotNull
   public final Component component1() {
      return this.title;
   }

   public final int component2() {
      return this.rows;
   }

   @NotNull
   public final List<MultipackSlotConfig> component3() {
      return this.slots;
   }

   @NotNull
   public final MultipackMenuConfig copy(@NotNull Component title, int rows, @NotNull List<MultipackSlotConfig> slots) {
      return new MultipackMenuConfig(title, rows, slots);
   }

   @NotNull
   public String toString() {
      return "MultipackMenuConfig(title=" + this.title + ", rows=" + this.rows + ", slots=" + this.slots + ")";
   }

   public int hashCode() {
      int result = this.title.hashCode();
      result = result * 31 + Integer.hashCode(this.rows);
      result = result * 31 + this.slots.hashCode();
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof MultipackMenuConfig)) {
         return false;
      } else {
         MultipackMenuConfig multipackMenuConfig = (MultipackMenuConfig)other;
         if (!Intrinsics.areEqual(this.title, multipackMenuConfig.title)) {
            return false;
         } else if (this.rows != multipackMenuConfig.rows) {
            return false;
         } else {
            return Intrinsics.areEqual(this.slots, multipackMenuConfig.slots);
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¨\u0006\b"},
      d2 = {"Lcom/nexomc/nexo/pack/multipack/gui/MultipackMenuConfig$Companion;", "", "<init>", "()V", "fromSection", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackMenuConfig;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @Nullable
      public final MultipackMenuConfig fromSection(@Nullable ConfigurationSection section) {
         java.util.List list;
         if (section == null) {
            return null;
         } else {
            int rows = RangesKt.coerceIn(section.getInt("rows", 6), 1, 6);
            String str = NexoYamlKt.stringOrNull(section, "title");
            if (str == null) {
               str = "<gold>Multipack";
            }

            Component title;
            block: {
               title = AdventureUtilsKt.deserialize(str);
               ConfigurationSection configurationSection = NexoYamlKt.section(section, "slots");
               if (configurationSection != null) {
                  Map map = NexoYamlKt.childSections(configurationSection);
                  if (map != null) {
                     Collection collection = map.values();
                     if (collection != null) {
                        Iterable iterable = (Iterable)collection;
                        MultipackSlotConfig.Companion companion = MultipackSlotConfig.Companion;
                        Collection destination = (Collection)(new ArrayList());

                        for(Object element : iterable) {
                           int value = 0;
                           ConfigurationSection p0 = (ConfigurationSection)element;
                           int value2 = 0;
                           MultipackSlotConfig multipackSlotConfig = companion.fromSection(p0);
                           if (multipackSlotConfig != null) {
                              Object it = multipackSlotConfig;
                              int value3 = 0;
                              destination.add(it);
                           }
                        }

                        list = (List)destination;
                        break block;
                     }
                  }
               }

               list = null;
            }

            if (list == null) {
               list = CollectionsKt.emptyList();
            }

            List slots = list;
            return new MultipackMenuConfig(title, rows, slots);
         }
      }
   }
}
