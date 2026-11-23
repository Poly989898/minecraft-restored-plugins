package com.nexomc.nexo.compatibilities.worldedit;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.utils.PluginUtils;
import java.io.File;
import java.util.List;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0011\u001a\u00020\u0012J\u0006\u0010\u0013\u001a\u00020\u0012J0\u0010\u0014\u001a\u00020\u00122\u0006\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u00182\u0006\u0010\u0019\u001a\u00020\u00052\u0006\u0010\u001a\u001a\u00020\u00052\u0006\u0010\u001b\u001a\u00020\u0005J&\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001e0\u001d2\u0006\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u00182\u0006\u0010\u001f\u001a\u00020\u0005R\u001b\u0010\u0004\u001a\u00020\u00058FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R\u001b\u0010\n\u001a\u00020\u00058FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u000b\u0010\t\u001a\u0004\b\n\u0010\u0007R\u001b\u0010\f\u001a\u00020\r8FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\u0010\u0010\t\u001a\u0004\b\u000e\u0010\u000f¨\u0006 "},
   d2 = {"Lcom/nexomc/nexo/compatibilities/worldedit/WrappedWorldEdit;", "", "<init>", "()V", "loaded", "", "getLoaded", "()Z", "loaded$delegate", "Lkotlin/Lazy;", "isFaweEnabled", "isFaweEnabled$delegate", "handlers", "Lcom/nexomc/nexo/compatibilities/worldedit/WorldEditHandlers;", "getHandlers", "()Lcom/nexomc/nexo/compatibilities/worldedit/WorldEditHandlers;", "handlers$delegate", "registerParser", "", "unregister", "pasteSchematic", "loc", "Lorg/bukkit/Location;", "schematic", "Ljava/io/File;", "replaceBlocks", "shouldCopyBiomes", "shouldCopyEntities", "blocksInSchematic", "", "Lorg/bukkit/block/Block;", "ignoreSelf", "com.nexomc:core"}
)
public final class WrappedWorldEdit {
   @NotNull
   public static final WrappedWorldEdit INSTANCE = new WrappedWorldEdit();
   @NotNull
   private static final Lazy loaded$delegate = LazyKt.lazy(WrappedWorldEdit::loaded_delegate$lambda$0);
   @NotNull
   private static final Lazy isFaweEnabled$delegate = LazyKt.lazy(WrappedWorldEdit::isFaweEnabled_delegate$lambda$0);
   @NotNull
   private static final Lazy handlers$delegate = LazyKt.lazy(WrappedWorldEdit::handlers_delegate$lambda$0);

   private WrappedWorldEdit() {
   }

   public final boolean getLoaded() {
      Lazy lazy = loaded$delegate;
      return (Boolean)lazy.getValue();
   }

   public final boolean isFaweEnabled() {
      Lazy lazy = isFaweEnabled$delegate;
      return (Boolean)lazy.getValue();
   }

   @NotNull
   public final WorldEditHandlers getHandlers() {
      Lazy lazy = handlers$delegate;
      return (WorldEditHandlers)lazy.getValue();
   }

   public final void registerParser() {
      if (this.getLoaded()) {
         // WorldEdit-only classes live in WorldEditParserRegistrar so this class links without WorldEdit on the classpath
         WorldEditParserRegistrar.register(this.getHandlers());
      }
   }

   public final void unregister() {
      if (this.getLoaded()) {
         WorldEditParserRegistrar.unregister(this.getHandlers());
      }

   }

   public final void pasteSchematic(@NotNull Location loc, @Nullable File schematic, boolean replaceBlocks, boolean shouldCopyBiomes, boolean shouldCopyEntities) {
      if (this.getLoaded()) {
         WorldEditUtils worldEditUtils = WorldEditUtils.INSTANCE;
         worldEditUtils.pasteSchematic(loc, schematic, replaceBlocks, shouldCopyBiomes, shouldCopyEntities);
      }

   }

   @NotNull
   public final List<Block> blocksInSchematic(@NotNull Location loc, @Nullable File schematic, boolean ignoreSelf) {
      List list;
      if (this.getLoaded()) {
         WorldEditUtils worldEditUtils = WorldEditUtils.INSTANCE;
         list = worldEditUtils.blocksInSchematic(loc, schematic, ignoreSelf);
      } else {
         list = CollectionsKt.emptyList();
      }

      return list;
   }

   private static final boolean loaded_delegate$lambda$0() {
      return PluginUtils.INSTANCE.isEnabled("WorldEdit") || PluginUtils.INSTANCE.isEnabled("FastAsyncWorldEdit");
   }

   private static final boolean isFaweEnabled_delegate$lambda$0() {
      return PluginUtils.INSTANCE.isEnabled("FastAsyncWorldEdit");
   }

   private static final WorldEditHandlers handlers_delegate$lambda$0() {
      return new WorldEditHandlers();
   }
}
