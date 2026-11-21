package com.nexomc.nexo.tags;

import com.nexomc.nexo.decomp.ResultShim;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.tag.TagKey;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.jvm.JvmStatic;
import net.kyori.adventure.key.Key;
import org.bukkit.block.BlockType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eH\u0007b\u0002\b\u000fR\u0019\u0010\u0004\u001a\r\u0012\t\u0012\u00070\u0006¢\u0006\u0002\b\u00070\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0019\u0010\b\u001a\r\u0012\t\u0012\u00070\u0006¢\u0006\u0002\b\u00070\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0019\u0010\t\u001a\r\u0012\t\u0012\u00070\u0006¢\u0006\u0002\b\u00070\nX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0010"},
   d2 = {"Lcom/nexomc/nexo/tags/NexoTags;", "", "<init>", "()V", "MINEABLE_AXE", "Lio/papermc/paper/registry/tag/TagKey;", "Lorg/bukkit/block/BlockType;", "Lkotlin/jvm/internal/EnhancedNullability;", "BEACON_BASE", "NOTEBLOCK", "Lio/papermc/paper/registry/TypedKey;", "registerTags", "", "context", "Lio/papermc/paper/plugin/bootstrap/BootstrapContext;", "Lkotlin/jvm/JvmStatic;", "com.nexomc:core"}
)
public final class NexoTags {
   @NotNull
   public static final NexoTags INSTANCE = new NexoTags();
   @NotNull
   private static final TagKey<BlockType> MINEABLE_AXE;
   @NotNull
   private static final TagKey<BlockType> BEACON_BASE;
   @NotNull
   private static final TypedKey<BlockType> NOTEBLOCK;

   private NexoTags() {
   }

   @JvmStatic
   public static final void registerTags(@NotNull BootstrapContext context) {
      NexoTags nexoTags = INSTANCE;

      Object obj;
      try {
         int value = 0;
         obj = ResultShim.ok(YamlConfiguration.loadConfiguration(context.getDataDirectory().resolve("mechanics.yml").toFile()));
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      YamlConfiguration yamlConfiguration = (YamlConfiguration)(ResultShim.isFailure(obj) ? null : obj);
      if (yamlConfiguration != null) {
         YamlConfiguration mechanics = yamlConfiguration;
         boolean isNoteBlocksEnabled = mechanics.getBoolean("custom_blocks.enabled", mechanics.getBoolean("custom_blocks.noteblock.enabled"));
         if (isNoteBlocksEnabled) {
            context.getLifecycleManager().registerEventHandler((LifecycleEventType)LifecycleEvents.TAGS.postFlatten(RegistryKey.BLOCK), NexoTags$registerTags$1.INSTANCE);
         }

      }
   }

   static {
      TagKey tagKey = TagKey.create(RegistryKey.BLOCK, Key.key("mineable/axe"));
      MINEABLE_AXE = tagKey;
      tagKey = TagKey.create(RegistryKey.BLOCK, Key.key("beacon_base_blocks"));
      BEACON_BASE = tagKey;
      TypedKey typedKey = TypedKey.create(RegistryKey.BLOCK, Key.key("note_block"));
      NOTEBLOCK = typedKey;
   }

   /** Synthetic accessors kept for binary compatibility with classes that were not recompiled. */
   public static final TagKey<BlockType> access$getMINEABLE_AXE$p() { return MINEABLE_AXE; }
   public static final TagKey<BlockType> access$getBEACON_BASE$p() { return BEACON_BASE; }
   public static final TypedKey<BlockType> access$getNOTEBLOCK$p() { return NOTEBLOCK; }
}
