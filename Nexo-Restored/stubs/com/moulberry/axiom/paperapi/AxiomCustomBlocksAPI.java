package com.moulberry.axiom.paperapi;

import com.moulberry.axiom.paperapi.block.AxiomCustomBlockBuilder;
import net.kyori.adventure.key.Key;
import org.bukkit.block.data.BlockData;
import org.bukkit.plugin.Plugin;

/** Compile-only stub of the Axiom paper API (not packaged). */
public abstract class AxiomCustomBlocksAPI {
   public static AxiomCustomBlocksAPI getAPI() { return null; }
   public abstract AxiomCustomBlockBuilder createSingle(Key key, String translationKey, BlockData data);
   public abstract AxiomCustomBlockBuilder createAxis(Key key, String translationKey, BlockData x, BlockData y, BlockData z);
   public abstract AxiomCustomBlockBuilder createHorizontalFacing(Key key, String translationKey, BlockData n, BlockData e, BlockData s, BlockData w);
   public abstract AxiomCustomBlockBuilder createFacing(Key key, String translationKey, BlockData n, BlockData e, BlockData s, BlockData w, BlockData u, BlockData d);
   public abstract void register(Plugin plugin, AxiomCustomBlockBuilder builder);
   public abstract void unregisterAll(Plugin plugin);
}
