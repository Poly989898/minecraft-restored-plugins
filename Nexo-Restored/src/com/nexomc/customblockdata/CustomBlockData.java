package com.nexomc.customblockdata;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Deprecated;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.annotation.AnnotationRetention;
import kotlin.annotation.AnnotationTarget;
import kotlin.annotation.Retention;
import kotlin.annotation.Target;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.IntRange;
import kotlin.text.StringsKt;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 2, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0080\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010#\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0007\u0018\u0000 E2\u00020\u0001:\u0003CDEB\u0019\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007B1\b\u0016\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\f\u001a\u00020\u000b\u0012\u0006\u0010\r\u001a\u00020\u000b\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u000eB\u0019\b\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u000f\u001a\u00020\u0010¢\u0006\u0004\b\u0006\u0010\u0011J\u0006\u0010%\u001a\u00020&J\b\u0010'\u001a\u00020&H\u0002J\u0016\u0010(\u001a\u00020&2\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005JB\u0010)\u001a\u00020&\"\u0004\b\u0000\u0010*\"\b\b\u0001\u0010+*\u00020,2\u0006\u0010-\u001a\u00020\u00162\u0012\u0010.\u001a\u000e\u0012\u0004\u0012\u0002H*\u0012\u0004\u0012\u0002H+0/2\u0006\u0010\r\u001a\u0002H+H\u0096\u0002¢\u0006\u0002\u00100J8\u00101\u001a\u00020!\"\b\b\u0000\u0010**\u00020,\"\b\b\u0001\u0010+*\u00020,2\u0006\u0010-\u001a\u00020\u00162\u0012\u0010.\u001a\u000e\u0012\u0004\u0012\u0002H*\u0012\u0004\u0012\u0002H+0/H\u0016J\u0010\u00101\u001a\u00020!2\u0006\u0010-\u001a\u00020\u0016H\u0016J@\u00102\u001a\u0004\u0018\u0001H+\"\b\b\u0000\u0010**\u00020,\"\b\b\u0001\u0010+*\u00020,2\u0006\u0010-\u001a\u00020\u00162\u0012\u0010.\u001a\u000e\u0012\u0004\u0012\u0002H*\u0012\u0004\u0012\u0002H+0/H\u0096\u0002¢\u0006\u0002\u00103JE\u00104\u001a\u0002H+\"\b\b\u0000\u0010**\u00020,\"\b\b\u0001\u0010+*\u00020,2\u0006\u0010-\u001a\u00020\u00162\u0012\u0010.\u001a\u000e\u0012\u0004\u0012\u0002H*\u0012\u0004\u0012\u0002H+0/2\u0006\u0010\r\u001a\u0002H+H\u0016¢\u0006\u0002\u00105J\u000e\u00106\u001a\b\u0012\u0004\u0012\u00020\u001607H\u0016J\u0010\u00108\u001a\u00020&2\u0006\u0010-\u001a\u00020\u0016H\u0016J\b\u00109\u001a\u00020!H\u0016J\u0018\u0010(\u001a\u00020&2\u0006\u0010:\u001a\u00020\u00012\u0006\u0010;\u001a\u00020!H\u0016J\b\u0010<\u001a\u00020=H\u0016J\b\u0010>\u001a\u00020?H\u0017J\u0018\u0010@\u001a\u00020&2\u0006\u0010A\u001a\u00020?2\u0006\u0010%\u001a\u00020!H\u0017J\u0010\u0010@\u001a\u00020&2\u0006\u0010A\u001a\u00020?H\u0017J0\u0010B\u001a\u0010\u0012\u0004\u0012\u0002H*\u0012\u0004\u0012\u0002H+\u0018\u00010/\"\b\b\u0000\u0010**\u00020,\"\b\b\u0001\u0010+*\u00020,2\u0006\u0010\u0015\u001a\u00020\u0016R\u000e\u0010\u0012\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0017\u001a\u000e\u0012\u0004\u0012\u00020\u0019\u0012\u0004\u0012\u00020\u001a0\u0018X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u00038F¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u001cR\u0014\u0010\u001d\u001a\u00020\u00018BX\u0082\u0004¢\u0006\u0006\u001a\u0004\b\u001e\u0010\u001fR$\u0010 \u001a\u00020!2\u0006\u0010 \u001a\u00020!8F@FX\u0086\u000e¢\u0006\f\u001a\u0004\b \u0010\"\"\u0004\b#\u0010$¨\u0006F"},
   d2 = {"Lcom/nexomc/customblockdata/CustomBlockData;", "Lorg/bukkit/persistence/PersistentDataContainer;", "block", "Lorg/bukkit/block/Block;", "plugin", "Lorg/bukkit/plugin/Plugin;", "<init>", "(Lorg/bukkit/block/Block;Lorg/bukkit/plugin/Plugin;)V", "world", "Lorg/bukkit/World;", "x", "", "y", "z", "(Lorg/bukkit/World;IIILorg/bukkit/plugin/Plugin;)V", "namespace", "", "(Lorg/bukkit/block/Block;Ljava/lang/String;)V", "pdc", "chunk", "Lorg/bukkit/Chunk;", "key", "Lorg/bukkit/NamespacedKey;", "blockEntry", "Lkotlin/Pair;", "Ljava/util/UUID;", "Lorg/bukkit/util/BlockVector;", "getBlock", "()Lorg/bukkit/block/Block;", "persistentDataContainer", "getPersistentDataContainer", "()Lorg/bukkit/persistence/PersistentDataContainer;", "isProtected", "", "()Z", "setProtected", "(Z)V", "clear", "", "save", "copyTo", "set", "T", "Z", "", "namespacedKey", "persistentDataType", "Lorg/bukkit/persistence/PersistentDataType;", "(Lorg/bukkit/NamespacedKey;Lorg/bukkit/persistence/PersistentDataType;Ljava/lang/Object;)V", "has", "get", "(Lorg/bukkit/NamespacedKey;Lorg/bukkit/persistence/PersistentDataType;)Ljava/lang/Object;", "getOrDefault", "(Lorg/bukkit/NamespacedKey;Lorg/bukkit/persistence/PersistentDataType;Ljava/lang/Object;)Ljava/lang/Object;", "getKeys", "", "remove", "isEmpty", "other", "replace", "getAdapterContext", "Lorg/bukkit/persistence/PersistentDataAdapterContext;", "serializeToBytes", "", "readFromBytes", "bytes", "getDataType", "PaperOnly", "DataType", "Companion", "custom-block-data"}
)
public final class CustomBlockData implements PersistentDataContainer {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final PersistentDataContainer pdc;
   @NotNull
   private final Chunk chunk;
   @NotNull
   private final NamespacedKey key;
   @NotNull
   private final Pair<UUID, BlockVector> blockEntry;
   @NotNull
   private final Plugin plugin;
   @NotNull
   private static final char[] DEFAULT_PACKAGE;
   @NotNull
   private static final Set<Pair<UUID, BlockVector>> DIRTY_BLOCKS;
   @NotNull
   private static final PersistentDataType<?, ?>[] PRIMITIVE_DATA_TYPES;
   @NotNull
   private static final NamespacedKey PERSISTENCE_KEY;
   private static final int CHUNK_MIN_XZ = 0;
   private static final int CHUNK_MAX_XZ = 15;
    private static final boolean ON_FOLIA;
   @NotNull
   private static final IntRange CHUNK_RANGE_XZ;

   public CustomBlockData(@NotNull Block block, @NotNull Plugin plugin) {
      Intrinsics.checkNotNullParameter(block, "block");
      Intrinsics.checkNotNullParameter(plugin, "plugin");
      super();
      Chunk var10001 = block.getChunk();
      Intrinsics.checkNotNullExpressionValue(var10001, "getChunk(...)");
      this.chunk = var10001;
      this.key = Companion.getKey(plugin, block);
      this.pdc = this.getPersistentDataContainer();
      this.blockEntry = Companion.getBlockEntry(block);
      this.plugin = plugin;
   }

   public CustomBlockData(@NotNull World world, int x, int y, int z, @NotNull Plugin plugin) {
      Intrinsics.checkNotNullParameter(world, "world");
      Intrinsics.checkNotNullParameter(plugin, "plugin");
      super();
      Chunk var10001 = world.getChunkAt(x, z);
      Intrinsics.checkNotNullExpressionValue(var10001, "getChunkAt(...)");
      this.chunk = var10001;
      this.key = new NamespacedKey(plugin, Companion.getKey(x, y, z));
      this.pdc = this.getPersistentDataContainer();
      this.blockEntry = Companion.getBlockEntry(world, x, y, z);
      this.plugin = plugin;
   }

   /** @deprecated */
   @Deprecated(
      message = "Use {@link #CustomBlockData(Block, Plugin)} instead."
   )
   public CustomBlockData(@NotNull Block block, @NotNull String namespace) {
      Intrinsics.checkNotNullParameter(block, "block");
      Intrinsics.checkNotNullParameter(namespace, "namespace");
      super();
      Chunk var10001 = block.getChunk();
      Intrinsics.checkNotNullExpressionValue(var10001, "getChunk(...)");
      this.chunk = var10001;
      this.key = new NamespacedKey(namespace, Companion.getKey(block));
      this.pdc = this.getPersistentDataContainer();
      JavaPlugin var3 = JavaPlugin.getProvidingPlugin(CustomBlockData.class);
      Intrinsics.checkNotNullExpressionValue(var3, "getProvidingPlugin(...)");
      this.plugin = (Plugin)var3;
      this.blockEntry = Companion.getBlockEntry(block);
   }

   @Nullable
   public final Block getBlock() {
      Pair var1 = this.blockEntry;
      UUID uuid = (UUID)var1.component1();
      BlockVector vector = (BlockVector)var1.component2();
      World var10000 = Bukkit.getWorld(uuid);
      if (var10000 == null) {
         return null;
      } else {
         World world = var10000;
         return world.getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
      }
   }

   private final PersistentDataContainer getPersistentDataContainer() {
      PersistentDataContainer var10000 = this.chunk.getPersistentDataContainer();
      Intrinsics.checkNotNullExpressionValue(var10000, "getPersistentDataContainer(...)");
      PersistentDataContainer chunkPDC = var10000;
      PersistentDataContainer blockPDC = (PersistentDataContainer)chunkPDC.get(this.key, PersistentDataType.TAG_CONTAINER);
      if (blockPDC != null) {
         return blockPDC;
      } else {
         var10000 = chunkPDC.getAdapterContext().newPersistentDataContainer();
         Intrinsics.checkNotNullExpressionValue(var10000, "newPersistentDataContainer(...)");
         return var10000;
      }
   }

   public final boolean isProtected() {
      return this.has(PERSISTENCE_KEY, CustomBlockData.DataType.INSTANCE.getBOOLEAN());
   }

   public final void setProtected(boolean isProtected) {
      if (isProtected) {
         this.set(PERSISTENCE_KEY, CustomBlockData.DataType.INSTANCE.getBOOLEAN(), true);
      } else {
         this.remove(PERSISTENCE_KEY);
      }

   }

   public final void clear() {
      Set var10000 = this.pdc.getKeys();
      Intrinsics.checkNotNullExpressionValue(var10000, "getKeys(...)");
      Iterable $this$forEach$iv = (Iterable)var10000;
      PersistentDataContainer var2 = this.pdc;

      for(Object element$iv : $this$forEach$iv) {
         NamespacedKey p0 = (NamespacedKey)element$iv;
         int var7 = 0;
         var2.remove(p0);
      }

      this.save();
   }

   private final void save() {
      Companion.setDirty(this.plugin, this.blockEntry);
      if (this.pdc.isEmpty()) {
         this.chunk.getPersistentDataContainer().remove(this.key);
      } else {
         this.chunk.getPersistentDataContainer().set(this.key, PersistentDataType.TAG_CONTAINER, this.pdc);
      }

   }

   public final void copyTo(@NotNull Block block, @NotNull Plugin plugin) {
      Intrinsics.checkNotNullParameter(block, "block");
      Intrinsics.checkNotNullParameter(plugin, "plugin");
      CustomBlockData newCbd = new CustomBlockData(block, plugin);
      Iterable $this$forEach$iv = (Iterable)this.getKeys();

      for(Object element$iv : $this$forEach$iv) {
         NamespacedKey key = (NamespacedKey)element$iv;
         int var9 = 0;
         PersistentDataType var10000 = Companion.getDataType(this, key);
         if (var10000 != null) {
            PersistentDataType dataType = var10000;
            Object var12 = this.get(key, dataType);
            if (var12 != null) {
               Object value = var12;
               newCbd.set(key, dataType, value);
            }
         }
      }

   }

   public <T, Z> void set(@NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<T, Z> persistentDataType, @NotNull Z z) {
      Intrinsics.checkNotNullParameter(namespacedKey, "namespacedKey");
      Intrinsics.checkNotNullParameter(persistentDataType, "persistentDataType");
      Intrinsics.checkNotNullParameter(z, "z");
      this.pdc.set(namespacedKey, persistentDataType, z);
      this.save();
   }

   public <T, Z> boolean has(@NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<T, Z> persistentDataType) {
      Intrinsics.checkNotNullParameter(namespacedKey, "namespacedKey");
      Intrinsics.checkNotNullParameter(persistentDataType, "persistentDataType");
      return this.pdc.has(namespacedKey, persistentDataType);
   }

   public boolean has(@NotNull NamespacedKey namespacedKey) {
      Intrinsics.checkNotNullParameter(namespacedKey, "namespacedKey");
      PersistentDataType[] var2 = PRIMITIVE_DATA_TYPES;
      int var3 = 0;

      for(int var4 = var2.length; var3 < var4; ++var3) {
         PersistentDataType type = var2[var3];
         if (this.pdc.has(namespacedKey, type)) {
            return true;
         }
      }

      return false;
   }

   @Nullable
   public <T, Z> Z get(@NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<T, Z> persistentDataType) {
      Intrinsics.checkNotNullParameter(namespacedKey, "namespacedKey");
      Intrinsics.checkNotNullParameter(persistentDataType, "persistentDataType");
      return (Z)this.pdc.get(namespacedKey, persistentDataType);
   }

   @NotNull
   public <T, Z> Z getOrDefault(@NotNull NamespacedKey namespacedKey, @NotNull PersistentDataType<T, Z> persistentDataType, @NotNull Z z) {
      Intrinsics.checkNotNullParameter(namespacedKey, "namespacedKey");
      Intrinsics.checkNotNullParameter(persistentDataType, "persistentDataType");
      Intrinsics.checkNotNullParameter(z, "z");
      Object var10000 = this.pdc.getOrDefault(namespacedKey, persistentDataType, z);
      Intrinsics.checkNotNullExpressionValue(var10000, "getOrDefault(...)");
      return (Z)var10000;
   }

   @NotNull
   public Set<NamespacedKey> getKeys() {
      Set var10000 = this.pdc.getKeys();
      Intrinsics.checkNotNullExpressionValue(var10000, "getKeys(...)");
      return var10000;
   }

   public void remove(@NotNull NamespacedKey namespacedKey) {
      Intrinsics.checkNotNullParameter(namespacedKey, "namespacedKey");
      this.pdc.remove(namespacedKey);
      this.save();
   }

   public boolean isEmpty() {
      return this.pdc.isEmpty();
   }

    public @NotNull PersistentDataType<?, ?> getPrimitiveStorageType(@NotNull net.kyori.adventure.key.Key key) {
       return this.pdc.getPrimitiveStorageType(key);
    }

    public int getSize() {
       return this.pdc.getKeys().size();
    }

    public void copyTo(@NotNull PersistentDataContainer other, boolean replace) {
      Intrinsics.checkNotNullParameter(other, "other");
      this.pdc.copyTo(other, replace);
   }

   @NotNull
   public PersistentDataAdapterContext getAdapterContext() {
      PersistentDataAdapterContext var10000 = this.pdc.getAdapterContext();
      Intrinsics.checkNotNullExpressionValue(var10000, "getAdapterContext(...)");
      return var10000;
   }

   /** @deprecated */
   @Deprecated(
      message = "Paper-only"
   )
   @CustomBlockData.PaperOnly
   @NotNull
   public byte[] serializeToBytes() throws IOException {
      byte[] var10000 = this.pdc.serializeToBytes();
      Intrinsics.checkNotNullExpressionValue(var10000, "serializeToBytes(...)");
      return var10000;
   }

   /** @deprecated */
   @Deprecated(
      message = "Paper-only"
   )
   @CustomBlockData.PaperOnly
   public void readFromBytes(@NotNull byte[] bytes, boolean clear) throws IOException {
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      this.pdc.readFromBytes(bytes, clear);
   }

   /** @deprecated */
   @Deprecated(
      message = "Paper-only"
   )
   @CustomBlockData.PaperOnly
   public void readFromBytes(@NotNull byte[] bytes) throws IOException {
      Intrinsics.checkNotNullParameter(bytes, "bytes");
      this.pdc.readFromBytes(bytes);
   }

   @Nullable
   public final <T, Z> PersistentDataType<T, Z> getDataType(@NotNull NamespacedKey key) {
      Intrinsics.checkNotNullParameter(key, "key");
      return Companion.<T, Z>getDataType(this, key);
   }

   private static boolean checkFolia() {
      try {
         Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
         return true;
      } catch (Throwable var3) {
         return false;
      }
   }

   static {
      char[] var0 = new char[]{'c', 'o', 'm', '.', 'n', 'e', 'x', 'o', 'm', 'c', '.', 'c', 'u', 's', 't', 'o', 'm', 'b', 'l', 'o', 'c', 'k', 'd', 'a', 't', 'a'};
      DEFAULT_PACKAGE = var0;
      ConcurrentHashMap.KeySetView var10000 = ConcurrentHashMap.newKeySet();
      Intrinsics.checkNotNullExpressionValue(var10000, "newKeySet(...)");
      DIRTY_BLOCKS = (Set)var10000;
      PersistentDataType[] var1 = new PersistentDataType[12];
      PersistentDataType var10002 = PersistentDataType.BYTE;
      Intrinsics.checkNotNullExpressionValue(var10002, "BYTE");
      var1[0] = var10002;
      var10002 = PersistentDataType.SHORT;
      Intrinsics.checkNotNullExpressionValue(var10002, "SHORT");
      var1[1] = var10002;
      var10002 = PersistentDataType.INTEGER;
      Intrinsics.checkNotNullExpressionValue(var10002, "INTEGER");
      var1[2] = var10002;
      var10002 = PersistentDataType.LONG;
      Intrinsics.checkNotNullExpressionValue(var10002, "LONG");
      var1[3] = var10002;
      var10002 = PersistentDataType.FLOAT;
      Intrinsics.checkNotNullExpressionValue(var10002, "FLOAT");
      var1[4] = var10002;
      var10002 = PersistentDataType.DOUBLE;
      Intrinsics.checkNotNullExpressionValue(var10002, "DOUBLE");
      var1[5] = var10002;
      var10002 = PersistentDataType.STRING;
      Intrinsics.checkNotNullExpressionValue(var10002, "STRING");
      var1[6] = var10002;
      var10002 = PersistentDataType.BYTE_ARRAY;
      Intrinsics.checkNotNullExpressionValue(var10002, "BYTE_ARRAY");
      var1[7] = var10002;
      var10002 = PersistentDataType.INTEGER_ARRAY;
      Intrinsics.checkNotNullExpressionValue(var10002, "INTEGER_ARRAY");
      var1[8] = var10002;
      var10002 = PersistentDataType.LONG_ARRAY;
      Intrinsics.checkNotNullExpressionValue(var10002, "LONG_ARRAY");
      var1[9] = var10002;
      var10002 = PersistentDataType.TAG_CONTAINER_ARRAY;
      Intrinsics.checkNotNullExpressionValue(var10002, "TAG_CONTAINER_ARRAY");
      var1[10] = var10002;
      var10002 = PersistentDataType.TAG_CONTAINER;
      Intrinsics.checkNotNullExpressionValue(var10002, "TAG_CONTAINER");
      var1[11] = var10002;
      PRIMITIVE_DATA_TYPES = var1;
      NamespacedKey var2 = NamespacedKey.fromString("customblockdata:protected");
      Intrinsics.checkNotNull(var2);
      PERSISTENCE_KEY = var2;
ON_FOLIA = checkFolia();
      CHUNK_RANGE_XZ = new IntRange(0, 15);
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0005\n\u0002\u0010\u000b\n\u0002\b\u0003\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u001d\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\n"},
      d2 = {"Lcom/nexomc/customblockdata/CustomBlockData$DataType;", "", "<init>", "()V", "BOOLEAN", "Lorg/bukkit/persistence/PersistentDataType;", "", "", "getBOOLEAN", "()Lorg/bukkit/persistence/PersistentDataType;", "custom-block-data"}
   )
   public static final class DataType {
      @NotNull
      public static final DataType INSTANCE = new DataType();
      @NotNull
      private static final PersistentDataType<Byte, Boolean> BOOLEAN = new PersistentDataType<Byte, Boolean>() {
         public Class<Byte> getPrimitiveType() {
            return Byte.TYPE;
         }

         public Class<Boolean> getComplexType() {
            return Boolean.TYPE;
         }

public Byte toPrimitive(Boolean complex, PersistentDataAdapterContext context) {
            Intrinsics.checkNotNullParameter(context, "context");
            return (byte)(complex ? 1 : 0);
         }

public Boolean fromPrimitive(Byte primitive, PersistentDataAdapterContext context) {
            Intrinsics.checkNotNullParameter(context, "context");
            return primitive == 1;
         }
      };

      private DataType() {
      }

      @NotNull
      public final PersistentDataType<Byte, Boolean> getBOOLEAN() {
         return BOOLEAN;
      }
   }

   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0080\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0019\n\u0000\n\u0002\u0010#\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\n0\b2\u0006\u0010\u001b\u001a\u00020\u001cH\u0002J4\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\n0\b2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u00122\u0006\u0010 \u001a\u00020\u00122\u0006\u0010!\u001a\u00020\u0012H\u0002J\u000e\u0010\"\u001a\u00020\u00152\u0006\u0010\u001b\u001a\u00020\u001cJ\"\u0010#\u001a\u00020$2\u0006\u0010%\u001a\u00020&2\u0012\u0010'\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\n0\bJ\u0018\u0010(\u001a\u00020\u00102\u0006\u0010%\u001a\u00020&2\u0006\u0010\u001b\u001a\u00020\u001cH\u0002J\u000e\u0010(\u001a\u00020)2\u0006\u0010\u001b\u001a\u00020\u001cJ\u001e\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020\u00122\u0006\u0010+\u001a\u00020\u00122\u0006\u0010,\u001a\u00020\u0012J\u0018\u0010/\u001a\u0004\u0018\u00010\u001c2\u0006\u00100\u001a\u00020\u00102\u0006\u00101\u001a\u000202J\u0016\u00103\u001a\u00020\u00152\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010%\u001a\u00020&J\u0016\u00104\u001a\u00020\u00152\u0006\u0010\u001b\u001a\u00020\u001c2\u0006\u0010%\u001a\u00020&J\u000e\u00105\u001a\u00020$2\u0006\u0010%\u001a\u00020&J\u001c\u00106\u001a\b\u0012\u0004\u0012\u00020\u001c0\u00072\u0006\u0010%\u001a\u00020&2\u0006\u00101\u001a\u000202J\u001e\u00106\u001a\b\u0012\u0004\u0012\u00020\u001c0\u00072\u0006\u00101\u001a\u0002022\u0006\u00107\u001a\u00020\u0010H\u0002J\u001c\u00106\u001a\b\u0012\u0004\u0012\u00020\u001c0\u00072\u0006\u00107\u001a\u00020)2\u0006\u00101\u001a\u000202J8\u00108\u001a\u0010\u0012\u0004\u0012\u0002H9\u0012\u0004\u0012\u0002H:\u0018\u00010\r\"\b\b\u0000\u00109*\u00020\u0001\"\b\b\u0001\u0010:*\u00020\u00012\u0006\u0010;\u001a\u00020<2\u0006\u00100\u001a\u00020\u0010R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R \u0010\u0006\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\n0\b0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\u000b\u001a\u0010\u0012\f\u0012\n\u0012\u0002\b\u0003\u0012\u0002\b\u00030\r0\fX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u000eR\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0012X\u0082T¢\u0006\u0002\n\u0000R\u001b\u0010\u0014\u001a\u00020\u00158BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0018\u0010\u0019\u001a\u0004\b\u0016\u0010\u0017R\u000e\u0010-\u001a\u00020.X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006="},
      d2 = {"Lcom/nexomc/customblockdata/CustomBlockData$Companion;", "", "<init>", "()V", "DEFAULT_PACKAGE", "", "DIRTY_BLOCKS", "", "Lkotlin/Pair;", "Ljava/util/UUID;", "Lorg/bukkit/util/BlockVector;", "PRIMITIVE_DATA_TYPES", "", "Lorg/bukkit/persistence/PersistentDataType;", "[Lorg/bukkit/persistence/PersistentDataType;", "PERSISTENCE_KEY", "Lorg/bukkit/NamespacedKey;", "CHUNK_MIN_XZ", "", "CHUNK_MAX_XZ", "onFolia", "", "getOnFolia", "()Z", "onFolia$delegate", "Lkotlin/Lazy;", "getBlockEntry", "block", "Lorg/bukkit/block/Block;", "world", "Lorg/bukkit/World;", "x", "y", "z", "isDirty", "setDirty", "", "plugin", "Lorg/bukkit/plugin/Plugin;", "blockEntry", "getKey", "", "blockX", "blockY", "blockZ", "CHUNK_RANGE_XZ", "Lkotlin/ranges/IntRange;", "getBlockFromKey", "key", "chunk", "Lorg/bukkit/Chunk;", "hasCustomBlockData", "isProtected", "registerListener", "getBlocksWithCustomData", "namespace", "getDataType", "T", "Z", "pdc", "Lorg/bukkit/persistence/PersistentDataContainer;", "custom-block-data"}
   )
   public static final class Companion {
      private Companion() {
      }

      private final boolean getOnFolia() {
return ON_FOLIA;
      }

      private final Pair<UUID, BlockVector> getBlockEntry(Block block) {
         UUID var10000 = block.getWorld().getUID();
         Intrinsics.checkNotNullExpressionValue(var10000, "getUID(...)");
         return TuplesKt.to(var10000, new BlockVector(block.getX(), block.getY(), block.getZ()));
      }

      private final Pair<UUID, BlockVector> getBlockEntry(World world, int x, int y, int z) {
         UUID var10000 = world.getUID();
         Intrinsics.checkNotNullExpressionValue(var10000, "getUID(...)");
         return TuplesKt.to(var10000, new BlockVector(x, y, z));
      }

      public final boolean isDirty(@NotNull Block block) {
         Intrinsics.checkNotNullParameter(block, "block");
         return CustomBlockData.DIRTY_BLOCKS.contains(this.getBlockEntry(block));
      }

public final void setDirty(@NotNull Plugin plugin, @NotNull Pair<UUID, BlockVector> blockEntry) {
         Intrinsics.checkNotNullParameter(plugin, "plugin");
         Intrinsics.checkNotNullParameter(blockEntry, "blockEntry");
         if (plugin.isEnabled()) {
            CustomBlockData.DIRTY_BLOCKS.add(blockEntry);
            if (this.getOnFolia()) {
   Bukkit.getServer().getGlobalRegionScheduler().runDelayed(plugin, (ScheduledTask task) -> CustomBlockData.DIRTY_BLOCKS.remove(blockEntry), 1L);
   } else {
   Bukkit.getScheduler().runTask(plugin, () -> CustomBlockData.DIRTY_BLOCKS.remove(blockEntry));
   }

         }
      }

      private final NamespacedKey getKey(Plugin plugin, Block block) {
         return new NamespacedKey(plugin, this.getKey(block));
      }

      @NotNull
      public final String getKey(@NotNull Block block) {
         Intrinsics.checkNotNullParameter(block, "block");
         int x = block.getX() & 15;
         int y = block.getY();
         int z = block.getZ() & 15;
         return "x" + x + "y" + y + "z" + z;
      }

      @NotNull
      public final String getKey(int blockX, int blockY, int blockZ) {
         int x = blockX & 15;
         int z = blockZ & 15;
         return "x" + x + "y" + blockY + "z" + z;
      }

      @Nullable
      public final Block getBlockFromKey(@NotNull NamespacedKey key, @NotNull Chunk chunk) {
         Intrinsics.checkNotNullParameter(key, "key");
         Intrinsics.checkNotNullParameter(chunk, "chunk");
   try {
   World world = chunk.getWorld();
   String value = key.value();
   List<Integer> parts = new ArrayList<>();
   for (String s : value.split("[xyz]", -1)) {
   parts.add(Integer.parseInt(s));
   }
   int x = parts.get(0);
   int y = parts.get(1);
   int z = parts.get(2);
   if (x >= 0 && x <= 15 && z >= 0 && z <= 15) {
   if (y <= world.getMaxHeight() && world.getMinHeight() <= y) {
   return chunk.getBlock(x, y, z);
   }
   }
   return null;
   } catch (Throwable var18) {
   return null;
   }
      }

      public final boolean hasCustomBlockData(@NotNull Block block, @NotNull Plugin plugin) {
         Intrinsics.checkNotNullParameter(block, "block");
         Intrinsics.checkNotNullParameter(plugin, "plugin");
         return block.getChunk().getPersistentDataContainer().has(this.getKey(plugin, block), PersistentDataType.TAG_CONTAINER);
      }

      public final boolean isProtected(@NotNull Block block, @NotNull Plugin plugin) {
         Intrinsics.checkNotNullParameter(block, "block");
         Intrinsics.checkNotNullParameter(plugin, "plugin");
         return (new CustomBlockData(block, plugin)).isProtected();
      }

      public final void registerListener(@NotNull Plugin plugin) {
         Intrinsics.checkNotNullParameter(plugin, "plugin");
         Bukkit.getPluginManager().registerEvents(new BlockDataListener(plugin), plugin);
      }

      @NotNull
      public final Set<Block> getBlocksWithCustomData(@NotNull Plugin plugin, @NotNull Chunk chunk) {
         Intrinsics.checkNotNullParameter(plugin, "plugin");
         Intrinsics.checkNotNullParameter(chunk, "chunk");
         NamespacedKey dummy = new NamespacedKey(plugin, "dummy");
         return this.getBlocksWithCustomData(chunk, dummy);
      }

      private final Set<Block> getBlocksWithCustomData(Chunk chunk, NamespacedKey namespace) {
         PersistentDataContainer var10000 = chunk.getPersistentDataContainer();
         Intrinsics.checkNotNullExpressionValue(var10000, "getPersistentDataContainer(...)");
         PersistentDataContainer chunkPDC = var10000;
         Set blocks = (Set)(new HashSet());

         for(Object var9 : chunkPDC.getKeys()) {
            Intrinsics.checkNotNullExpressionValue(var9, "next(...)");
            NamespacedKey key = (NamespacedKey)var9;
            if (Intrinsics.areEqual(key.getNamespace(), namespace.getNamespace())) {
               Block var10 = this.getBlockFromKey(key, chunk);
               if (var10 != null) {
                  Block p0 = var10;
                  int var8 = 0;
                  blocks.add(p0);
               }
            }
         }

         return blocks;
      }

      @NotNull
      public final Set<Block> getBlocksWithCustomData(@NotNull String namespace, @NotNull Chunk chunk) {
         Intrinsics.checkNotNullParameter(namespace, "namespace");
         Intrinsics.checkNotNullParameter(chunk, "chunk");
         NamespacedKey dummy = new NamespacedKey(namespace, "dummy");
         return this.getBlocksWithCustomData(chunk, dummy);
      }

      @Nullable
      public final <T, Z> PersistentDataType<T, Z> getDataType(@NotNull PersistentDataContainer pdc, @NotNull NamespacedKey key) {
         Intrinsics.checkNotNullParameter(pdc, "pdc");
         Intrinsics.checkNotNullParameter(key, "key");
         PersistentDataType[] var3 = CustomBlockData.PRIMITIVE_DATA_TYPES;
         int var4 = 0;

         for(int var5 = var3.length; var4 < var5; ++var4) {
            PersistentDataType dataType = var3[var4];
            if (pdc.has(key, dataType)) {
               Intrinsics.checkNotNull(dataType, "null cannot be cast to non-null type org.bukkit.persistence.PersistentDataType<T of com.nexomc.customblockdata.CustomBlockData.Companion.getDataType, Z of com.nexomc.customblockdata.CustomBlockData.Companion.getDataType>");
               return dataType;
            }
         }

         return null;
      }


   }

   @Retention(AnnotationRetention.BINARY)
   @Target(
      allowedTargets = {AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER}
   )
   @java.lang.annotation.Retention(RetentionPolicy.CLASS)
   @java.lang.annotation.Target({ElementType.METHOD})
   @Metadata(
      mv = {2, 2, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\n\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0000\b\u0083\u0002\u0018\u00002\u00020\u0001B\u0000¨\u0006\u0002"},
      d2 = {"Lcom/nexomc/customblockdata/CustomBlockData$PaperOnly;", "", "custom-block-data"}
   )
   private @interface PaperOnly {
   }
}
