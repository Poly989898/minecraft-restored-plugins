package com.nexomc.nexo.pack.multipack.gui;

import com.nexomc.nexo.utils.NexoYamlKt;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.apache.commons.lang3.EnumUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u0000 \u000b2\u00020\u0001:\u0004\b\t\n\u000bB\t\b\u0004¢\u0006\u0004\b\u0002\u0010\u0003R\u0012\u0010\u0004\u001a\u00020\u0005X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007\u0082\u0001\u0002\f\r¨\u0006\u000e"},
   d2 = {"Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotAction;", "", "<init>", "()V", "type", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackActionType;", "getType", "()Lcom/nexomc/nexo/pack/multipack/gui/MultipackActionType;", "CyclingEntry", "Cycling", "Toggle", "Companion", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotAction$Cycling;", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotAction$Toggle;", "com.nexomc:core"}
)
public abstract sealed class MultipackSlotAction {
   @NotNull
   public static final Companion Companion = new Companion();

   private MultipackSlotAction() {
   }

   @NotNull
   public abstract MultipackActionType getType();

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u001b\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\r\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u001f\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005HÆ\u0001J\u0014\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0012\u001a\u00020\u0013HÖ\u0081\u0004J\n\u0010\u0014\u001a\u00020\u0003HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0015"},
      d2 = {"Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotAction$CyclingEntry;", "", "templateId", "", "item", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem;", "<init>", "(Ljava/lang/String;Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem;)V", "getTemplateId", "()Ljava/lang/String;", "getItem", "()Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "com.nexomc:core"}
   )
   public static final class CyclingEntry {
      @NotNull
      private final String templateId;
      @Nullable
      private final MultipackSlotItem item;

      public CyclingEntry(@NotNull String templateId, @Nullable MultipackSlotItem item) {
         super();
         this.templateId = templateId;
         this.item = item;
      }

      @NotNull
      public final String getTemplateId() {
         return this.templateId;
      }

      @Nullable
      public final MultipackSlotItem getItem() {
         return this.item;
      }

      @NotNull
      public final String component1() {
         return this.templateId;
      }

      @Nullable
      public final MultipackSlotItem component2() {
         return this.item;
      }

      @NotNull
      public final CyclingEntry copy(@NotNull String templateId, @Nullable MultipackSlotItem item) {
         return new CyclingEntry(templateId, item);
      }

      @NotNull
      public String toString() {
         return "CyclingEntry(templateId=" + this.templateId + ", item=" + this.item + ")";
      }

      public int hashCode() {
         int result = this.templateId.hashCode();
         result = result * 31 + (this.item == null ? 0 : this.item.hashCode());
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof CyclingEntry)) {
            return false;
         } else {
            CyclingEntry cyclingEntry = (CyclingEntry)other;
            if (!Intrinsics.areEqual(this.templateId, cyclingEntry.templateId)) {
               return false;
            } else {
               return Intrinsics.areEqual(this.item, cyclingEntry.item);
            }
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0015\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u000f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u0019\u0010\u0011\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0001J\u0014\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015HÖ\u0083\u0004J\n\u0010\u0016\u001a\u00020\u0017HÖ\u0081\u0004J\n\u0010\u0018\u001a\u00020\u000eHÖ\u0081\u0004R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\t\u001a\u00020\nX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u00038F¢\u0006\u0006\u001a\u0004\b\u000f\u0010\b¨\u0006\u0019"},
      d2 = {"Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotAction$Cycling;", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotAction;", "entries", "", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotAction$CyclingEntry;", "<init>", "(Ljava/util/List;)V", "getEntries", "()Ljava/util/List;", "type", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackActionType;", "getType", "()Lcom/nexomc/nexo/pack/multipack/gui/MultipackActionType;", "targets", "", "getTargets", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "com.nexomc:core"}
   )
   public static final class Cycling extends MultipackSlotAction {
      @NotNull
      private final List<CyclingEntry> entries;
      @NotNull
      private final MultipackActionType type;

      public Cycling(@NotNull List<CyclingEntry> entries) {
         super();
         this.entries = entries;
         this.type = MultipackActionType.CYCLING;
      }

      @NotNull
      public final List<CyclingEntry> getEntries() {
         return this.entries;
      }

      @NotNull
      public MultipackActionType getType() {
         return this.type;
      }

      @NotNull
      public final List<String> getTargets() {
         Iterable iterable = (Iterable)this.entries;
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

         for(Object item : iterable) {
            CyclingEntry it = (CyclingEntry)item;
            int value = 0;
            destination.add(it.getTemplateId());
         }

         return (List)destination;
      }

      @NotNull
      public final List<CyclingEntry> component1() {
         return this.entries;
      }

      @NotNull
      public final Cycling copy(@NotNull List<CyclingEntry> entries) {
         return new Cycling(entries);
      }

      @NotNull
      public String toString() {
         return "Cycling(entries=" + this.entries + ")";
      }

      public int hashCode() {
         return this.entries.hashCode();
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof Cycling)) {
            return false;
         } else {
            Cycling cycling = (Cycling)other;
            return Intrinsics.areEqual(this.entries, cycling.entries);
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B#\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u0013\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u000b\u0010\u0014\u001a\u0004\u0018\u00010\u0005HÆ\u0003J+\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0005HÆ\u0001J\u0014\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0019HÖ\u0083\u0004J\n\u0010\u001a\u001a\u00020\u001bHÖ\u0081\u0004J\n\u0010\u001c\u001a\u00020\u0003HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0014\u0010\u000e\u001a\u00020\u000fX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u001d"},
      d2 = {"Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotAction$Toggle;", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotAction;", "target", "", "enabledItem", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem;", "disabledItem", "<init>", "(Ljava/lang/String;Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem;Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem;)V", "getTarget", "()Ljava/lang/String;", "getEnabledItem", "()Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotItem;", "getDisabledItem", "type", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackActionType;", "getType", "()Lcom/nexomc/nexo/pack/multipack/gui/MultipackActionType;", "component1", "component2", "component3", "copy", "equals", "", "other", "", "hashCode", "", "toString", "com.nexomc:core"}
   )
   public static final class Toggle extends MultipackSlotAction {
      @NotNull
      private final String target;
      @Nullable
      private final MultipackSlotItem enabledItem;
      @Nullable
      private final MultipackSlotItem disabledItem;
      @NotNull
      private final MultipackActionType type;

      public Toggle(@NotNull String target, @Nullable MultipackSlotItem enabledItem, @Nullable MultipackSlotItem disabledItem) {
         super();
         this.target = target;
         this.enabledItem = enabledItem;
         this.disabledItem = disabledItem;
         this.type = MultipackActionType.TOGGLE;
      }

      @NotNull
      public final String getTarget() {
         return this.target;
      }

      @Nullable
      public final MultipackSlotItem getEnabledItem() {
         return this.enabledItem;
      }

      @Nullable
      public final MultipackSlotItem getDisabledItem() {
         return this.disabledItem;
      }

      @NotNull
      public MultipackActionType getType() {
         return this.type;
      }

      @NotNull
      public final String component1() {
         return this.target;
      }

      @Nullable
      public final MultipackSlotItem component2() {
         return this.enabledItem;
      }

      @Nullable
      public final MultipackSlotItem component3() {
         return this.disabledItem;
      }

      @NotNull
      public final Toggle copy(@NotNull String target, @Nullable MultipackSlotItem enabledItem, @Nullable MultipackSlotItem disabledItem) {
         return new Toggle(target, enabledItem, disabledItem);
      }

      @NotNull
      public String toString() {
         return "Toggle(target=" + this.target + ", enabledItem=" + this.enabledItem + ", disabledItem=" + this.disabledItem + ")";
      }

      public int hashCode() {
         int result = this.target.hashCode();
         result = result * 31 + (this.enabledItem == null ? 0 : this.enabledItem.hashCode());
         result = result * 31 + (this.disabledItem == null ? 0 : this.disabledItem.hashCode());
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof Toggle)) {
            return false;
         } else {
            Toggle toggle = (Toggle)other;
            if (!Intrinsics.areEqual(this.target, toggle.target)) {
               return false;
            } else if (!Intrinsics.areEqual(this.enabledItem, toggle.enabledItem)) {
               return false;
            } else {
               return Intrinsics.areEqual(this.disabledItem, toggle.disabledItem);
            }
         }
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u001a\u0010\n\u001a\u0004\u0018\u00010\u000b2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0002J\u001e\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u000e0\r2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\tH\u0002¨\u0006\u0010"},
      d2 = {"Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotAction$Companion;", "", "<init>", "()V", "fromSection", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotAction;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "slotCtx", "", "parseToggleEntry", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotAction$Toggle;", "parseCyclingEntries", "", "Lcom/nexomc/nexo/pack/multipack/gui/MultipackSlotAction$CyclingEntry;", "slotKeyContext", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @Nullable
      public final MultipackSlotAction fromSection(@NotNull ConfigurationSection section, @NotNull String slotCtx) {
         String key = "type";
         MultipackActionType multipackActionType = EnumUtils.getEnum(MultipackActionType.class, NexoYamlKt.stringOrNull(section, key));
         if (multipackActionType == null) {
            return null;
         } else {
            MultipackSlotAction multipackSlotAction;
            switch (multipackActionType) {
               case TOGGLE -> multipackSlotAction = this.parseToggleEntry(section, slotCtx);
               case CYCLING -> multipackSlotAction = new Cycling(this.parseCyclingEntries(section, slotCtx));
               default -> throw new NoWhenBranchMatchedException();
            }

            return multipackSlotAction;
         }
      }

      private final Toggle parseToggleEntry(ConfigurationSection section, String slotCtx) {
         String str = NexoYamlKt.stringOrNull(section, "target");
         if (str == null) {
            return null;
         } else {
            String target = str;
            ConfigurationSection parent = section.getParent();
            ConfigurationSection baseItem = parent != null ? NexoYamlKt.section(parent, "base_item") : null;
            parent = NexoYamlKt.section(section, "enabled_item");
            if (parent != null) {
               block: {
                  ConfigurationSection it = parent;
                  int value = 0;
                  if (baseItem != null) {
                     parent = NexoYamlKt.clone(baseItem);
                     if (parent != null) {
                        parent = NexoYamlKt.copyFrom(parent, it);
                        if (parent != null) {
                           break block;
                        }
                     }
                  }

                  parent = it;
               }
            } else {
               parent = null;
            }

            ConfigurationSection enabledSection = parent;
            parent = NexoYamlKt.section(section, "disabled_item");
            if (parent != null) {
               block: {
                  ConfigurationSection it = parent;
                  int value2 = 0;
                  if (baseItem != null) {
                     parent = NexoYamlKt.clone(baseItem);
                     if (parent != null) {
                        parent = NexoYamlKt.copyFrom(parent, it);
                        if (parent != null) {
                           break block;
                        }
                     }
                  }

                  parent = it;
               }
            } else {
               parent = null;
            }

            ConfigurationSection disabledSection = parent;
            MultipackSlotItem enabled = MultipackSlotItem.Companion.fromSection(enabledSection, slotCtx + "/enabled");
            MultipackSlotItem disabled = MultipackSlotItem.Companion.fromSection(disabledSection, slotCtx + "/disabled");
            return new Toggle(target, enabled, disabled);
         }
      }

      private final List<CyclingEntry> parseCyclingEntries(ConfigurationSection section, String slotKeyContext) {
         org.bukkit.configuration.ConfigurationSection configurationSection;
         Iterable iterable = (Iterable)NexoYamlKt.stringListOrSingle(section, "targets", (String)null);
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

         for(Object obj2 : iterable) {
            String p0 = (String)obj2;
            int value = 0;
            destination.add(new CyclingEntry(p0, null));
         }

         Collection collection = (Collection)((List)destination);
         Object obj;
         if (collection.isEmpty()) {
            ConfigurationSection parent = section.getParent();
            ConfigurationSection baseItem = parent != null ? NexoYamlKt.section(parent, "base_item") : null;
            Iterable iterable2 = (Iterable)NexoYamlKt.sectionListOrSingle(section, "targets");
            Collection collection2 = (Collection)(new ArrayList());

            for(Object element : iterable2) {
               int value2 = 0;
               ConfigurationSection target = (ConfigurationSection)element;
               int value3 = 0;
               String str = NexoYamlKt.stringOrNull(target, "id");
               CyclingEntry cyclingEntry;
               if (str == null) {
                  cyclingEntry = null;
               } else {
                  String id;
                  block: {
                     id = str;
                     if (baseItem != null) {
                        ConfigurationSection configurationSection2 = NexoYamlKt.clone(baseItem);
                        if (configurationSection2 != null) {
                           configurationSection = NexoYamlKt.copyFrom(configurationSection2, target);
                           if (configurationSection != null) {
                              break block;
                           }
                        }
                     }

                     configurationSection = target;
                  }

                  ConfigurationSection section_1 = configurationSection;
                  MultipackSlotItem item = MultipackSlotItem.Companion.fromSection(section_1, slotKeyContext + "/" + id);
                  cyclingEntry = new CyclingEntry(id, item);
               }

               if (cyclingEntry != null) {
                  Object it = cyclingEntry;
                  int value4 = 0;
                  collection2.add(it);
               }
            }

            obj = (List)collection2;
         } else {
            obj = collection;
         }

         return (List)obj;
      }
   }
}
