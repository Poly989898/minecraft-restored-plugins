package com.nexomc.nexo.compatibilities.axiom;

import com.moulberry.axiom.AxiomPaper;
import com.moulberry.axiom.event.AxiomAfterManipulateEntityEvent;
import com.moulberry.axiom.event.AxiomSpawnEntityEvent;
import com.moulberry.axiom.paperapi.AxiomCustomBlocksAPI;
import com.moulberry.axiom.paperapi.AxiomCustomDisplayAPI;
import com.moulberry.axiom.paperapi.display.AxiomCustomDisplayBuilder;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.api.events.NexoItemsLoadedEvent;
import com.nexomc.nexo.compatibilities.CompatibilityProvider;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockRegistry;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockType;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanic;
import com.nexomc.nexo.mechanics.custom_block.noteblock.directional.DirectionalBlock;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import com.nexomc.nexo.mechanics.furniture.bed.FurnitureBed;
import com.nexomc.nexo.mechanics.furniture.seats.FurnitureSeat;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.logs.Logs;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u000fB\u0007¢\u0006\u0004\b\u0003\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u0006*\u00020\u0007H\u0007b\u0002\b\bJ\u0010\u0010\t\u001a\u00020\u0006*\u00020\nH\u0007b\u0002\b\bJ\u0010\u0010\u000b\u001a\u00020\u0006*\u00020\fH\u0007b\u0002\b\bJ\u0006\u0010\r\u001a\u00020\u0006J\u0006\u0010\u000e\u001a\u00020\u0006¨\u0006\u0010"},
   d2 = {"Lcom/nexomc/nexo/compatibilities/axiom/AxiomCompatibility;", "Lcom/nexomc/nexo/compatibilities/CompatibilityProvider;", "Lcom/moulberry/axiom/AxiomPaper;", "<init>", "()V", "onLoaded", "", "Lcom/nexomc/nexo/api/events/NexoItemsLoadedEvent;", "Lorg/bukkit/event/EventHandler;", "onAxiomSpawn", "Lcom/moulberry/axiom/event/AxiomSpawnEntityEvent;", "onAxiomManio", "Lcom/moulberry/axiom/event/AxiomAfterManipulateEntityEvent;", "registerFurniture", "registerCustomBlocks", "DirectionalBlocks", "com.nexomc:core"}
)
public final class AxiomCompatibility extends CompatibilityProvider<AxiomPaper> {
   @EventHandler
   public final void onLoaded(@NotNull NexoItemsLoadedEvent $this$onLoaded) {
      Intrinsics.checkNotNullParameter($this$onLoaded, "<this>");
      this.registerCustomBlocks();
      this.registerFurniture();
   }

   @EventHandler
   public final void onAxiomSpawn(@NotNull AxiomSpawnEntityEvent $this$onAxiomSpawn) {
      Intrinsics.checkNotNullParameter($this$onAxiomSpawn, "<this>");
      Entity var4 = $this$onAxiomSpawn.getEntity();
      ItemDisplay var10000 = var4 instanceof ItemDisplay ? (ItemDisplay)var4 : null;
      if ((var4 instanceof ItemDisplay ? (ItemDisplay)var4 : null) != null) {
         ItemDisplay baseEntity = var10000;
         FurnitureMechanic var5 = NexoFurniture.furnitureMechanic(baseEntity.getItemStack());
         if (var5 != null) {
            FurnitureMechanic mechanic = var5;
            baseEntity.getPersistentDataContainer().set(FurnitureMechanic.Companion.getFURNITURE_KEY(), PersistentDataType.STRING, mechanic.getItemID());
         }
      }
   }

   @EventHandler
   public final void onAxiomManio(@NotNull AxiomAfterManipulateEntityEvent $this$onAxiomManio) {
      Intrinsics.checkNotNullParameter($this$onAxiomManio, "<this>");
      Entity var4 = $this$onAxiomManio.getEntity();
      ItemDisplay var10000 = var4 instanceof ItemDisplay ? (ItemDisplay)var4 : null;
      if ((var4 instanceof ItemDisplay ? (ItemDisplay)var4 : null) != null) {
         ItemDisplay baseEntity = var10000;
         FurnitureMechanic var5 = NexoFurniture.furnitureMechanic((Entity)baseEntity);
         if (var5 != null) {
            FurnitureMechanic mechanic = var5;
            mechanic.getHitbox().refreshHitboxes(baseEntity, mechanic);
            mechanic.getLight().refreshLights(baseEntity, mechanic);
            mechanic.getTextEntities().refreshTextEntities(baseEntity, mechanic);
            FurnitureSeat.Companion.updateSeats(baseEntity, mechanic);
            FurnitureBed.Companion.updateBeds(baseEntity, mechanic);
         }
      }
   }

   public final void registerFurniture() {
      AxiomCustomDisplayAPI.getAPI().unregisterAll((Plugin)NexoPlugin.Companion.instance());
      String[] $this$mapNotNull$iv = NexoFurniture.furnitureIDs();
      NexoFurniture var10000 = NexoFurniture.INSTANCE;
      Collection destination$iv$iv = (Collection)(new ArrayList());
      Object[] $this$forEach$iv$iv$iv = $this$mapNotNull$iv;
      int var9 = 0;

      for(int var10 = $this$mapNotNull$iv.length; var9 < var10; ++var9) {
         Object element$iv$iv$iv = $this$forEach$iv$iv$iv[var9];
         int var13 = 0;
         int var15 = 0;
         FurnitureMechanic var27 = NexoFurniture.furnitureMechanic((String)element$iv$iv$iv);
         if (var27 != null) {
            Object it$iv$iv = var27;
            int var17 = 0;
            destination$iv$iv.add(it$iv$iv);
         }
      }

      Iterable $this$forEach$iv = (Iterable)((List)destination$iv$iv);

      for (Object element$iv : $this$forEach$iv) {
         FurnitureMechanic mechanic = (FurnitureMechanic)element$iv;
         com.nexomc.nexo.items.ItemBuilder nexoItem = NexoItems.itemFromId(mechanic.getItemID());
         Component displayName = null;
         if (nexoItem != null) {
            displayName = nexoItem.getItemName();
            if (displayName == null) {
               displayName = nexoItem.getCustomName();
            }
         }

         String searchKey = null;
         if (displayName != null) {
            searchKey = (String)AdventureUtils.INSTANCE.getMINI_MESSAGE().serialize(displayName);
         }

         if (searchKey == null) {
            searchKey = mechanic.getItemID();
         }

         AxiomCustomDisplayBuilder builder = AxiomCustomDisplayAPI.getAPI().create(Key.key("nexo:" + mechanic.getItemID()), searchKey, nexoItem != null ? nexoItem.build() : null);
         AxiomCustomDisplayAPI.getAPI().register((Plugin)NexoPlugin.Companion.instance(), builder);
      }
   }

   public final void registerCustomBlocks() {
      AxiomCustomBlocksAPI.getAPI().unregisterAll((Plugin)NexoPlugin.Companion.instance());
      String[] $this$mapNotNull$iv = NexoBlocks.blockIDs();
      NexoBlocks var10000 = NexoBlocks.INSTANCE;
      Collection destination$iv$iv = (Collection)(new ArrayList());
      Object[] $this$forEach$iv$iv$iv = $this$mapNotNull$iv;
      int var9 = 0;

      for(int var10 = $this$mapNotNull$iv.length; var9 < var10; ++var9) {
         Object element$iv$iv$iv = $this$forEach$iv$iv$iv[var9];
         int var13 = 0;
         int var15 = 0;
         CustomBlockMechanic var22 = NexoBlocks.customBlockMechanic((String)element$iv$iv$iv);
         if (var22 != null) {
            Object it$iv$iv = var22;
            int var17 = 0;
            destination$iv$iv.add(it$iv$iv);
         }
      }

      Iterable $this$forEach$iv = (Iterable)((List)destination$iv$iv);

      for(Object element$iv : $this$forEach$iv) {
         CustomBlockMechanic mechanic = (CustomBlockMechanic)element$iv;
         CustomBlockType var23 = CustomBlockRegistry.INSTANCE.getByClass(mechanic.getClass());
         if (var23 != null) {
            var23.registerAxiom(mechanic);
         }
      }

      Logs.logSuccess("Finished registering Nexo's Custom Blocks with Axiom!", false);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\u0004\b\u0007\u0010\bB\u0011\b\u0016\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0004\b\u0007\u0010\u000bJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0003J#\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0001J\u0014\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010\u0016\u001a\u00020\u0017HÖ\u0081\u0004J\n\u0010\u0018\u001a\u00020\u0019HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001a"},
      d2 = {"Lcom/nexomc/nexo/compatibilities/axiom/AxiomCompatibility$DirectionalBlocks;", "", "type", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/directional/DirectionalBlock$DirectionalType;", "blocks", "", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/NoteBlockMechanic;", "<init>", "(Lcom/nexomc/nexo/mechanics/custom_block/noteblock/directional/DirectionalBlock$DirectionalType;Ljava/util/List;)V", "parent", "Lcom/nexomc/nexo/mechanics/custom_block/noteblock/directional/DirectionalBlock;", "(Lcom/nexomc/nexo/mechanics/custom_block/noteblock/directional/DirectionalBlock;)V", "getType", "()Lcom/nexomc/nexo/mechanics/custom_block/noteblock/directional/DirectionalBlock$DirectionalType;", "getBlocks", "()Ljava/util/List;", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "com.nexomc:core"}
   )
   public static final class DirectionalBlocks {
      @NotNull
      private final DirectionalBlock.DirectionalType type;
      @NotNull
      private final List<NoteBlockMechanic> blocks;

      public DirectionalBlocks(@NotNull DirectionalBlock.DirectionalType type, @NotNull List<NoteBlockMechanic> blocks) {
         Intrinsics.checkNotNullParameter(type, "type");
         Intrinsics.checkNotNullParameter(blocks, "blocks");
         super();
         this.type = type;
         this.blocks = blocks;
      }

      @NotNull
      public final DirectionalBlock.DirectionalType getType() {
         return this.type;
      }

      @NotNull
      public final List<NoteBlockMechanic> getBlocks() {
         return this.blocks;
      }

      public DirectionalBlocks(@NotNull DirectionalBlock parent) {
         Intrinsics.checkNotNullParameter(parent, "parent");
         DirectionalBlock.DirectionalType var10001 = parent.getDirectionalType();
         Iterable $this$mapNotNull$iv = (Iterable)parent.getBlocks();
         NexoBlocks var10002 = NexoBlocks.INSTANCE;
         DirectionalBlock.DirectionalType var18 = var10001;
         Collection destination$iv$iv = (Collection)(new ArrayList());

         for(Object element$iv$iv$iv : $this$mapNotNull$iv) {
            int var12 = 0;
            String p0 = (String)element$iv$iv$iv;
            int var14 = 0;
            NoteBlockMechanic var10000 = NexoBlocks.noteBlockMechanic(p0);
            if (var10000 != null) {
               Object it$iv$iv = var10000;
               int var16 = 0;
               destination$iv$iv.add(it$iv$iv);
            }
         }

         List var19 = (List)destination$iv$iv;
         this(var18, var19);
      }

      @NotNull
      public final DirectionalBlock.DirectionalType component1() {
         return this.type;
      }

      @NotNull
      public final List<NoteBlockMechanic> component2() {
         return this.blocks;
      }

      @NotNull
      public final DirectionalBlocks copy(@NotNull DirectionalBlock.DirectionalType type, @NotNull List<NoteBlockMechanic> blocks) {
         Intrinsics.checkNotNullParameter(type, "type");
         Intrinsics.checkNotNullParameter(blocks, "blocks");
         return new DirectionalBlocks(type, blocks);
      }

      @NotNull
      public String toString() {
         return "DirectionalBlocks(type=" + this.type + ", blocks=" + this.blocks + ")";
      }

      public int hashCode() {
         int result = this.type.hashCode();
         result = result * 31 + this.blocks.hashCode();
         return result;
      }

      public boolean equals(@Nullable Object other) {
         if (this == other) {
            return true;
         } else if (!(other instanceof DirectionalBlocks)) {
            return false;
         } else {
            DirectionalBlocks var2 = (DirectionalBlocks)other;
            if (this.type != var2.type) {
               return false;
            } else {
               return Intrinsics.areEqual(this.blocks, var2.blocks);
            }
         }
      }
   }
}
