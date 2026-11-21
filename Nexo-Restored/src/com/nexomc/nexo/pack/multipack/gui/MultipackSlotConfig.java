package com.nexomc.nexo.pack.multipack.gui;

import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.logs.Logs;
import java.util.Collection;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2i;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\b\u0018\u0000 &2\u00020\u0001:\u0001&B9\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\u0010\t\u001a\u0004\u0018\u00010\n\u0012\u0006\u0010\u000b\u001a\u00020\f¢\u0006\u0004\b\r\u0010\u000eJ\t\u0010\u001a\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001b\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001c\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001d\u001a\u00020\bHÆ\u0003J\u000b\u0010\u001e\u001a\u0004\u0018\u00010\nHÆ\u0003J\t\u0010\u001f\u001a\u00020\fHÆ\u0003JG\u0010 \u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\n2\b\b\u0002\u0010\u000b\u001a\u00020\fHÆ\u0001J\u0014\u0010!\u001a\u00020\b2\b\u0010\"\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010#\u001a\u00020$HÖ\u0081\u0004J\n\u0010%\u001a\u00020\u0003HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0012R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0013\u0010\t\u001a\u0004\u0018\u00010\n¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019¨\u0006'"},
   d2 = {"Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotConfig;", "", "id", "", "offset", "Lorg/joml/Vector2i;", "size", "allSlotsExceptFirstEmpty", "", "baseItem", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem;", "action", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotAction;", "<init>", "(Ljava/lang/String;Lorg/joml/Vector2i;Lorg/joml/Vector2i;ZLcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem;Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotAction;)V", "getId", "()Ljava/lang/String;", "getOffset", "()Lorg/joml/Vector2i;", "getSize", "getAllSlotsExceptFirstEmpty", "()Z", "getBaseItem", "()Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem;", "getAction", "()Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotAction;", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "equals", "other", "hashCode", "", "toString", "Companion", "com.nexomc:core"}
)
public final class MultipackSlotConfig {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final String id;
   @NotNull
   private final Vector2i offset;
   @NotNull
   private final Vector2i size;
   private final boolean allSlotsExceptFirstEmpty;
   @Nullable
   private final MultipackSlotItem baseItem;
   @NotNull
   private final MultipackSlotAction action;

   public MultipackSlotConfig(@NotNull String id, @NotNull Vector2i offset, @NotNull Vector2i size, boolean allSlotsExceptFirstEmpty, @Nullable MultipackSlotItem baseItem, @NotNull MultipackSlotAction action) {
      super();
      this.id = id;
      this.offset = offset;
      this.size = size;
      this.allSlotsExceptFirstEmpty = allSlotsExceptFirstEmpty;
      this.baseItem = baseItem;
      this.action = action;
   }

   @NotNull
   public final String getId() {
      return this.id;
   }

   @NotNull
   public final Vector2i getOffset() {
      return this.offset;
   }

   @NotNull
   public final Vector2i getSize() {
      return this.size;
   }

   public final boolean getAllSlotsExceptFirstEmpty() {
      return this.allSlotsExceptFirstEmpty;
   }

   @Nullable
   public final MultipackSlotItem getBaseItem() {
      return this.baseItem;
   }

   @NotNull
   public final MultipackSlotAction getAction() {
      return this.action;
   }

   @NotNull
   public final String component1() {
      return this.id;
   }

   @NotNull
   public final Vector2i component2() {
      return this.offset;
   }

   @NotNull
   public final Vector2i component3() {
      return this.size;
   }

   public final boolean component4() {
      return this.allSlotsExceptFirstEmpty;
   }

   @Nullable
   public final MultipackSlotItem component5() {
      return this.baseItem;
   }

   @NotNull
   public final MultipackSlotAction component6() {
      return this.action;
   }

   @NotNull
   public final MultipackSlotConfig copy(@NotNull String id, @NotNull Vector2i offset, @NotNull Vector2i size, boolean allSlotsExceptFirstEmpty, @Nullable MultipackSlotItem baseItem, @NotNull MultipackSlotAction action) {
      return new MultipackSlotConfig(id, offset, size, allSlotsExceptFirstEmpty, baseItem, action);
   }

   @NotNull
   public String toString() {
      return "MultipackSlotConfig(id=" + this.id + ", offset=" + this.offset + ", size=" + this.size + ", allSlotsExceptFirstEmpty=" + this.allSlotsExceptFirstEmpty + ", baseItem=" + this.baseItem + ", action=" + this.action + ")";
   }

   public int hashCode() {
      int result = this.id.hashCode();
      result = result * 31 + this.offset.hashCode();
      result = result * 31 + this.size.hashCode();
      result = result * 31 + Boolean.hashCode(this.allSlotsExceptFirstEmpty);
      result = result * 31 + (this.baseItem == null ? 0 : this.baseItem.hashCode());
      result = result * 31 + this.action.hashCode();
      return result;
   }

   public boolean equals(@Nullable Object other) {
      if (this == other) {
         return true;
      } else if (!(other instanceof MultipackSlotConfig)) {
         return false;
      } else {
         MultipackSlotConfig multipackSlotConfig = (MultipackSlotConfig)other;
         if (!Intrinsics.areEqual(this.id, multipackSlotConfig.id)) {
            return false;
         } else if (!Intrinsics.areEqual(this.offset, multipackSlotConfig.offset)) {
            return false;
         } else if (!Intrinsics.areEqual(this.size, multipackSlotConfig.size)) {
            return false;
         } else if (this.allSlotsExceptFirstEmpty != multipackSlotConfig.allSlotsExceptFirstEmpty) {
            return false;
         } else if (!Intrinsics.areEqual(this.baseItem, multipackSlotConfig.baseItem)) {
            return false;
         } else {
            return Intrinsics.areEqual(this.action, multipackSlotConfig.action);
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\"\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0002¨\u0006\u0010"},
      d2 = {"Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotConfig$Companion;", "", "<init>", "()V", "fromSection", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotConfig;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "validateItems", "", "id", "", "action", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotAction;", "fallback", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem;", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @Nullable
      public final MultipackSlotConfig fromSection(@NotNull ConfigurationSection section) {
         String name = section.getName();
         String id = name;
         ConfigurationSection configurationSection = NexoYamlKt.section(section, "action");
         if (configurationSection != null) {
            ConfigurationSection it = configurationSection;
            int value = 0;
            MultipackSlotAction multipackSlotAction = MultipackSlotAction.Companion.fromSection(it, id);
            if (multipackSlotAction != null) {
               MultipackSlotAction action = multipackSlotAction;
               MultipackSlotItem baseItem = MultipackSlotItem.Companion.fromSection(NexoYamlKt.section(section, "base_item"), id + "/fallback");
               if (!this.validateItems(id, action, baseItem)) {
                  return null;
               }

               return new MultipackSlotConfig(id, NexoYamlKt.vector2i(section, "offset", 0), NexoYamlKt.vector2i(section, "size", 1), section.getBoolean("all_slots_except_first_empty", false), baseItem, action);
            }
         }

         Companion companion = this;
         int value2 = 0;
         Logs.logWarn("Multipack GUI slot <aqua>" + id + "</aqua> has no valid action definition, skipping.", false);
         return null;
      }

      private final boolean validateItems(String id, MultipackSlotAction action, MultipackSlotItem fallback) {
         boolean flag;
         if (action instanceof MultipackSlotAction.Toggle) {
            flag = fallback == null && (((MultipackSlotAction.Toggle)action).getEnabledItem() == null || ((MultipackSlotAction.Toggle)action).getDisabledItem() == null);
         } else {
            block: {
               if (!(action instanceof MultipackSlotAction.Cycling)) {
                  throw new NoWhenBranchMatchedException();
               }

               if (fallback == null) {
                  Iterable entries = (Iterable)((MultipackSlotAction.Cycling)action).getEntries();
                  if (entries instanceof Collection && ((Collection)entries).isEmpty()) {
                     flag = false;
                  } else {
                     block2: {
                        for(Object element : entries) {
                           MultipackSlotAction.CyclingEntry it = (MultipackSlotAction.CyclingEntry)element;
                           int value = 0;
                           if (it.getItem() == null) {
                              flag = true;
                              break block2;
                           }
                        }

                        flag = false;
                     }
                  }

                  if (flag) {
                     flag = true;
                     break block;
                  }
               }

               flag = false;
            }
         }

         boolean missing = flag;
         if (missing) {
            Logs.logWarn("Multipack GUI slot <aqua>" + id + "</aqua> has no fallback <aqua>item:</aqua> and at least one state is missing its own item; skipping.", false);
         }

         return !missing;
      }
   }
}
