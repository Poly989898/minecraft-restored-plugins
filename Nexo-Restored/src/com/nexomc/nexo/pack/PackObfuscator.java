package com.nexomc.nexo.pack;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoPack;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.pack.creative.NexoPackReader;
import com.nexomc.nexo.pack.creative.NexoPackWriter;
import com.nexomc.nexo.utils.CollectionUtilsKt;
import com.nexomc.nexo.utils.FileUtils;
import com.nexomc.nexo.utils.FileUtilsKt;
import com.nexomc.nexo.utils.KeyUtils;
import com.nexomc.nexo.utils.ResourcePackUtilsKt;
import com.nexomc.nexo.utils.logs.Logs;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.atlas.Atlas;
import team.unnamed.creative.atlas.AtlasSource;
import team.unnamed.creative.atlas.SingleAtlasSource;
import team.unnamed.creative.blockstate.BlockState;
import team.unnamed.creative.blockstate.Condition;
import team.unnamed.creative.blockstate.MultiVariant;
import team.unnamed.creative.blockstate.Selector;
import team.unnamed.creative.blockstate.Variant;
import team.unnamed.creative.font.BitMapFontProvider;
import team.unnamed.creative.font.Font;
import team.unnamed.creative.font.FontProvider;
import team.unnamed.creative.item.CompositeItemModel;
import team.unnamed.creative.item.ConditionItemModel;
import team.unnamed.creative.item.Item;
import team.unnamed.creative.item.ItemModel;
import team.unnamed.creative.item.RangeDispatchItemModel;
import team.unnamed.creative.item.ReferenceItemModel;
import team.unnamed.creative.item.SelectItemModel;
import team.unnamed.creative.item.SpecialItemModel;
import team.unnamed.creative.item.property.ItemBooleanProperty;
import team.unnamed.creative.item.property.ItemNumericProperty;
import team.unnamed.creative.item.property.ItemStringProperty;
import team.unnamed.creative.item.special.SpecialRender;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.model.ModelTexture;
import team.unnamed.creative.model.ModelTextures;
import team.unnamed.creative.overlay.Overlay;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.sound.Sound;
import team.unnamed.creative.sound.SoundEntry;
import team.unnamed.creative.sound.SoundEvent;
import team.unnamed.creative.sound.SoundRegistry;
import team.unnamed.creative.texture.Texture;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u0098\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0015\u0018\u00002\u00020\u0001:\u0004UVWXB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001c\u0010\u001d\u001a\u0004\u0018\u00010\u001e*\b\u0012\u0004\u0012\u00020\u00150\u00102\u0006\u0010\u001f\u001a\u00020\u0011H\u0002J\u001f\u0010 \u001a\u00070\u0011¢\u0006\u0002\b!*\b\u0012\u0004\u0012\u00020\u00150\u00102\u0006\u0010\u001f\u001a\u00020\u0011H\u0002J\u001c\u0010\u001d\u001a\u0004\u0018\u00010\"*\b\u0012\u0004\u0012\u00020\u00170\u00102\u0006\u0010\u001f\u001a\u00020\u0011H\u0002J\u001a\u0010 \u001a\u00020#*\b\u0012\u0004\u0012\u00020\u00170\u00102\u0006\u0010\u001f\u001a\u00020\u0011H\u0002J\u001c\u0010 \u001a\u0004\u0018\u00010$*\b\u0012\u0004\u0012\u00020\u00190\u00102\u0006\u0010\u001f\u001a\u00020\u0011H\u0002J\u0012\u0010%\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00110&J\u0012\u0010'\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00110&J\u0012\u0010(\u001a\u000e\u0012\u0004\u0012\u00020\u0011\u0012\u0004\u0012\u00020\u00110&J\u000e\u0010)\u001a\u00020*2\u0006\u0010+\u001a\u00020\u001cJ\b\u0010,\u001a\u00020*H\u0002J\b\u0010-\u001a\u00020*H\u0002J\b\u0010.\u001a\u00020*H\u0002J\b\u0010/\u001a\u00020*H\u0002J\b\u00100\u001a\u00020*H\u0002J\b\u00101\u001a\u00020*H\u0002J\b\u00102\u001a\u00020*H\u0002J\b\u00103\u001a\u00020*H\u0002J\u0010\u00104\u001a\u00020\u001e2\u0006\u00105\u001a\u00020\u001eH\u0002J\u0018\u00106\u001a\u00020*2\u0006\u00105\u001a\u00020\u001e2\u0006\u00107\u001a\u000208H\u0002J\u0010\u00109\u001a\u00020:2\u0006\u0010;\u001a\u00020:H\u0002J\u0018\u0010<\u001a\u00020*2\u0006\u00105\u001a\u00020\u001e2\u0006\u00107\u001a\u000208H\u0002J\u0018\u0010=\u001a\u00020*2\u0006\u00105\u001a\u00020\u001e2\u0006\u00107\u001a\u000208H\u0002J\u0011\u0010>\u001a\u00070\"¢\u0006\u0002\b!*\u00020\"H\u0002J\u0012\u0010?\u001a\u0004\u0018\u00010\"2\u0006\u0010@\u001a\u00020AH\u0002J\u0012\u0010B\u001a\u0004\u0018\u00010\"2\u0006\u0010C\u001a\u00020DH\u0002J\f\u0010E\u001a\u00020\u0011*\u00020\u0011H\u0002J\u0011\u0010F\u001a\u00070\u0011¢\u0006\u0002\b!*\u00020\u0011H\u0002J\u0011\u0010G\u001a\u00070\u0011¢\u0006\u0002\b!*\u00020\u0011H\u0002J\u0011\u0010H\u001a\u00070\u0011¢\u0006\u0002\b!*\u00020\u0011H\u0002J\u000e\u0010S\u001a\u00020\u00002\u0006\u0010T\u001a\u00020\u0003R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u000e\u0010\n\u001a\u00020\u000bX\u0082.¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u0017\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00110\u0010¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0014\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00150\u0010X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\u0010X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\u0010X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u001a\u001a\u000e\u0012\u0004\u0012\u00020\u001c\u0012\u0004\u0012\u00020\u001c0\u001bX\u0082\u0004¢\u0006\u0002\n\u0000R,\u0010I\u001a\u0013\u0012\t\u0012\u00070\u0011¢\u0006\u0002\b!\u0012\u0004\u0012\u00020\"0\u001b8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\bL\u0010M\u001a\u0004\bJ\u0010KR9\u0010N\u001a \u0012\u0016\u0012\u0014 O*\t\u0018\u00010\u0011¢\u0006\u0002\b!0\u0011¢\u0006\u0002\b!\u0012\u0004\u0012\u00020\"0&8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\bR\u0010M\u001a\u0004\bP\u0010Q¨\u0006Y"},
   d2 = {"Lcom/nexomc/nexo/pack/PackObfuscator;", "", "resourcePack", "Lteam/unnamed/creative/overlay/ResourceContainer;", "<init>", "(Lteam/unnamed/creative/overlay/ResourceContainer;)V", "obfuscationType", "Lcom/nexomc/nexo/pack/PackObfuscator$Type;", "getObfuscationType", "()Lcom/nexomc/nexo/pack/PackObfuscator$Type;", "obfCachedPack", "Ljava/io/File;", "cache", "", "isOverlay", "skippedKeys", "Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;", "Lnet/kyori/adventure/key/Key;", "getSkippedKeys", "()Lit/unimi/dsi/fastutil/objects/ObjectOpenHashSet;", "obfuscatedModels", "Lcom/nexomc/nexo/pack/PackObfuscator$ObfuscatedModel;", "obfuscatedTextures", "Lcom/nexomc/nexo/pack/PackObfuscator$ObfuscatedTexture;", "obfuscatedSounds", "Lcom/nexomc/nexo/pack/PackObfuscator$ObfuscatedSound;", "obfuscatedNamespaceCache", "Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "", "findObfOrNull", "Lteam/unnamed/creative/model/Model;", "key", "findObf", "Lorg/jetbrains/annotations/NotNull;", "Lteam/unnamed/creative/texture/Texture;", "Lnet/kyori/adventure/key/Keyed;", "Lteam/unnamed/creative/sound/Sound;", "obfuscatedTextureMap", "", "obfuscatedModelMap", "obfuscatedSoundMap", "obfuscatePack", "", "hash", "obfuscateOverlays", "obfuscateModels", "obfuscateItems", "obfuscateBlockStates", "obfuscateFonts", "obfuscateTextures", "obfuscateAtlases", "obfuscateSounds", "obfuscateModel", "model", "obfuscateModelTextures", "builder", "Lteam/unnamed/creative/model/Model$Builder;", "obfuscateVariant", "Lteam/unnamed/creative/blockstate/Variant;", "variant", "obfuscateParentModel", "obfuscateOverrides", "obfuscate", "obfuscateModelTexture", "modelTexture", "Lteam/unnamed/creative/model/ModelTexture;", "obfuscateFontTexture", "provider", "Lteam/unnamed/creative/font/BitMapFontProvider;", "obfuscateKey", "specularMapKey", "normalMapKey", "emissiveKey", "vanillaModelTextures", "getVanillaModelTextures", "()Lit/unimi/dsi/fastutil/objects/Object2ObjectOpenHashMap;", "vanillaModelTextures$delegate", "Lkotlin/Lazy;", "vanillaFontTextures", "kotlin.jvm.PlatformType", "getVanillaFontTextures", "()Ljava/util/Map;", "vanillaFontTextures$delegate", "clone", "resourceContainer", "ObfuscatedModel", "ObfuscatedTexture", "ObfuscatedSound", "Type", "com.nexomc:core"}
)
public final class PackObfuscator {
   @NotNull
   private final ResourceContainer resourcePack;
   @NotNull
   private final Type obfuscationType;
   private File obfCachedPack;
   private final boolean cache;
   private final boolean isOverlay;
   @NotNull
   private final ObjectOpenHashSet<Key> skippedKeys;
   @NotNull
   private final ObjectOpenHashSet<ObfuscatedModel> obfuscatedModels;
   @NotNull
   private final ObjectOpenHashSet<ObfuscatedTexture> obfuscatedTextures;
   @NotNull
   private final ObjectOpenHashSet<ObfuscatedSound> obfuscatedSounds;
   @NotNull
   private final Object2ObjectOpenHashMap<String, String> obfuscatedNamespaceCache;
   @NotNull
   private final Lazy vanillaModelTextures$delegate;
   @NotNull
   private final Lazy vanillaFontTextures$delegate;

   public PackObfuscator(@NotNull ResourceContainer resourcePack) {
      super();
      this.resourcePack = resourcePack;
      this.obfuscationType = Settings.PACK_OBFUSCATION_TYPE.toEnumOrGet(Type.class, PackObfuscator::obfuscationType$lambda$0);
      this.cache = Settings.PACK_CACHE_OBFUSCATION.toBool();
      this.isOverlay = this.resourcePack instanceof Overlay;
      this.skippedKeys = new ObjectOpenHashSet();
      this.obfuscatedModels = new ObjectOpenHashSet();
      this.obfuscatedTextures = new ObjectOpenHashSet();
      this.obfuscatedSounds = new ObjectOpenHashSet();
      this.obfuscatedNamespaceCache = new Object2ObjectOpenHashMap();
      this.vanillaModelTextures$delegate = LazyKt.lazy((() -> PackObfuscator.vanillaModelTextures_delegate$lambda$0(this)));
      this.vanillaFontTextures$delegate = LazyKt.lazy((() -> PackObfuscator.vanillaFontTextures_delegate$lambda$0(this)));
   }

   @NotNull
   public final Type getObfuscationType() {
      return this.obfuscationType;
   }

   @NotNull
   public final ObjectOpenHashSet<Key> getSkippedKeys() {
      return this.skippedKeys;
   }

   private final Model findObfOrNull(ObjectOpenHashSet<ObfuscatedModel> objectOpenHashSet, Key key) {
      Iterable iterable = (Iterable)objectOpenHashSet;
      Iterator iterator = iterable.iterator();

      Object obj;
      while(true) {
         if (iterator.hasNext()) {
            Object obj2 = iterator.next();
            ObfuscatedModel it = (ObfuscatedModel)obj2;
            int value = 0;
            if (it.find(key) == null) {
               continue;
            }

            obj = obj2;
            break;
         }

         obj = null;
         break;
      }

      return (ObfuscatedModel)obj != null ? ((ObfuscatedModel)obj).getObfuscatedModel() : null;
   }

   private final Key findObf(ObjectOpenHashSet<ObfuscatedModel> objectOpenHashSet, Key key) {
      Model model = this.findObfOrNull(objectOpenHashSet, key);
      Key key2;
      if (model != null) {
         key2 = model.key();
         if (key2 != null) {
            return key2;
         }
      }

      key2 = key;
      return key2;
   }

   private final Texture findObfTextureOrNull(ObjectOpenHashSet<ObfuscatedTexture> objectOpenHashSet, Key key) {
      Key key_24 = KeyUtils.INSTANCE.appendSuffix(key, ".png");
      Iterable iterable = (Iterable)objectOpenHashSet;
      Iterator iterator = iterable.iterator();

      Object obj;
      while(true) {
         if (iterator.hasNext()) {
            Object element = iterator.next();
            ObfuscatedTexture it = (ObfuscatedTexture)element;
            int value = 0;
            if (it.find(key_24) == null) {
               continue;
            }

            obj = element;
            break;
         }

         obj = null;
         break;
      }

      return (ObfuscatedTexture)obj != null ? ((ObfuscatedTexture)obj).getObfuscatedTexture() : null;
   }

   private final Keyed findObfTexture(ObjectOpenHashSet<ObfuscatedTexture> objectOpenHashSet, Key key) {
      Texture texture = this.findObfTextureOrNull(objectOpenHashSet, key);
      return texture != null ? (Keyed)texture : (Keyed)key;
   }

   private final Sound findObfSound(ObjectOpenHashSet<ObfuscatedSound> objectOpenHashSet, Key key) {
      Iterable iterable = (Iterable)objectOpenHashSet;
      Iterator iterator = iterable.iterator();

      Object obj;
      while(true) {
         if (iterator.hasNext()) {
            Object obj2 = iterator.next();
            ObfuscatedSound it = (ObfuscatedSound)obj2;
            int value = 0;
            if (it.find(key) == null) {
               continue;
            }

            obj = obj2;
            break;
         }

         obj = null;
         break;
      }

      return (ObfuscatedSound)obj != null ? ((ObfuscatedSound)obj).getObfuscatedSound() : null;
   }

   @NotNull
   public final Map<Key, Key> obfuscatedTextureMap() {
      Iterable iterable = (Iterable)this.obfuscatedTextures;
      int capacity = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16);
      Map destination = (Map)(new LinkedHashMap(capacity));

      for(Object element : iterable) {
         ObfuscatedTexture it = (ObfuscatedTexture)element;
         int value = 0;
         Pair pair = TuplesKt.to(it.getOriginalTexture().key(), it.getObfuscatedTexture().key());
         destination.put(pair.getFirst(), pair.getSecond());
      }

      return destination;
   }

   @NotNull
   public final Map<Key, Key> obfuscatedModelMap() {
      Iterable iterable = (Iterable)this.obfuscatedModels;
      int capacity = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16);
      Map destination = (Map)(new LinkedHashMap(capacity));

      for(Object element : iterable) {
         ObfuscatedModel it = (ObfuscatedModel)element;
         int value = 0;
         Pair pair = TuplesKt.to(it.getOriginalModel().key(), it.getObfuscatedModel().key());
         destination.put(pair.getFirst(), pair.getSecond());
      }

      return destination;
   }

   @NotNull
   public final Map<Key, Key> obfuscatedSoundMap() {
      Iterable iterable = (Iterable)this.obfuscatedSounds;
      int capacity = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16);
      Map destination = (Map)(new LinkedHashMap(capacity));

      for(Object element : iterable) {
         ObfuscatedSound it = (ObfuscatedSound)element;
         int value = 0;
         Pair pair = TuplesKt.to(it.getOriginalSound().key(), it.getObfuscatedSound().key());
         destination.put(pair.getFirst(), pair.getSecond());
      }

      return destination;
   }

   public final void obfuscatePack(@NotNull String hash) {
      if (this.resourcePack instanceof ResourcePack && !this.obfuscationType.isNone()) {
         File dataFolder = NexoPlugin.Companion.instance().getDataFolder();
         String[] strs = new String[]{"pack", ".deobfCachedPacks", hash + ".zip"};
         this.obfCachedPack = FileUtilsKt.resolve(dataFolder, strs);
         File file = this.obfCachedPack;
         if (file == null) {
            Intrinsics.throwUninitializedPropertyAccessException("obfCachedPack");
            file = null;
         }

         File p0 = file.getParentFile();
         int value = 0;
         p0.mkdirs();
         Path toPath = p0.toPath();
         FileUtils.setHidden(toPath);
         File file2 = this.obfCachedPack;
         if (file2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("obfCachedPack");
            file2 = null;
         }

         File[] listFiles = file2.getParentFile().listFiles();
         if (listFiles != null) {
            Sequence p0_23 = ArraysKt.asSequence(listFiles);
            if (p0_23 != null) {
               Sequence p0_22 = SequencesKt.filter(p0_23, (a0_ -> PackObfuscator.obfuscatePack$lambda$0(this, (File)a0_)));
               if (p0_22 != null) {

                  for(Object element : kotlin.sequences.SequencesKt.asIterable(p0_22)) {
                     File it = (File)element;
                     int value2 = 0;
                     PackObfuscator packObfuscator = this;

                     try {
                        PackObfuscator packObfuscator2 = packObfuscator;
                        int value3 = 0;
                        Object obj = ResultShim.ok(FilesKt.deleteRecursively(it));
                     } catch (Throwable e) {
                        Object obj2 = ResultShim.ok(ResultKt.createFailure(e));
                     }
                  }
               }
            }
         }

         block: {
            if (this.cache) {
               file2 = this.obfCachedPack;
               if (file2 == null) {
                  Intrinsics.throwUninitializedPropertyAccessException("obfCachedPack");
                  file2 = null;
               }

               if (file2.exists()) {
                  break block;
               }
            }

            Logs.logInfo("Obfuscating NexoPack...");
            this.obfuscatedModels.clear();
            this.obfuscatedTextures.clear();
            this.obfuscatedSounds.clear();
            this.obfuscatedNamespaceCache.clear();
            this.obfuscateModels();
            this.obfuscateFonts();
            this.obfuscateTextures();
            this.obfuscateSounds();
            this.obfuscateOverlays();
            if (this.cache) {
               NexoPackWriter nexoPackWriter = NexoPackWriter.INSTANCE;
               dataFolder = this.obfCachedPack;
               if (dataFolder == null) {
                  Intrinsics.throwUninitializedPropertyAccessException("obfCachedPack");
                  dataFolder = null;
               }

               nexoPackWriter.writeToZipFile(dataFolder, (ResourcePack)this.resourcePack);
               Logs.logInfo("Caching obfuscated ResourcePack...");
            }
         }

         if (this.cache) {
            ResourcePack resourcePack2 = (ResourcePack)this.resourcePack;
            NexoPackReader nexoPackReader = NexoPackReader.INSTANCE;
            File file3 = this.obfCachedPack;
            if (file3 == null) {
               Intrinsics.throwUninitializedPropertyAccessException("obfCachedPack");
               file3 = null;
            }

            ResourcePack resourcePack3 = nexoPackReader.readFromZipFile(file3);
            NexoPack.overwritePack(resourcePack2, resourcePack3);
         }

      }
   }

   private final void obfuscateOverlays() {
      ResourceContainer resourceContainer = this.resourcePack;
      ResourcePack resourcePack2 = resourceContainer instanceof ResourcePack ? (ResourcePack)resourceContainer : null;
      if ((resourceContainer instanceof ResourcePack ? (ResourcePack)resourceContainer : null) != null) {
         Collection overlays = resourcePack2.overlays();
         if (overlays != null) {
            Iterable iterable = (Iterable)overlays;

            for(Object element : iterable) {
               Overlay overlay = (Overlay)element;
               int value = 0;
               PackObfuscator overlayObfuscator = this.clone(overlay);
               overlayObfuscator.obfuscateModels();
               overlayObfuscator.obfuscateFonts();
               overlayObfuscator.obfuscateTextures();
               overlayObfuscator.obfuscateSounds();
            }
         }
      }

   }

   private final void obfuscateModels() {
      Collection models = this.resourcePack.models();
      Iterable iterable = (Iterable)models;

      for(Object element : iterable) {
         Model p0 = (Model)element;
         int value = 0;
         this.obfuscateModel(p0);
      }

      iterable = (Iterable)CollectionsKt.toList((Iterable)this.obfuscatedModels);

      for(Object obj : iterable) {
         ObfuscatedModel it = (ObfuscatedModel)obj;
         int value2 = 0;
         if (this.resourcePack.removeModel(it.getOriginalModel().key()) || !this.isOverlay) {
            it.getObfuscatedModel().addTo(this.resourcePack);
         }
      }

      this.obfuscateBlockStates();
      this.obfuscateItems();
   }

   private final void obfuscateItems() {
      Collection items = this.resourcePack.items();
      Iterable iterable = (Iterable)CollectionsKt.toList((Iterable)items);

      for(Object element : iterable) {
         Item item = (Item)element;
         int value = 0;
         Key key = item.key();
         ItemModel model = item.model();
         Item.item(key, obfuscateItems$obfuscateItemModel(this, model), item.handAnimationOnSwap(), item.oversizedInGui()).addTo(this.resourcePack);
      }

   }

   private final void obfuscateBlockStates() {
      Collection blockStates = this.resourcePack.blockStates();
      Iterable iterable = (Iterable)CollectionsKt.toList((Iterable)blockStates);

      for(Object element : iterable) {
         BlockState blockState = (BlockState)element;
         int value = 0;
         List multipart = blockState.multipart();
         Iterable iterable2 = (Iterable)multipart;
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

         for(Object item : iterable2) {
            Selector it = (Selector)item;
            int value2 = 0;
            Condition condition = it.condition();
            List variants = it.variant().variants();
            Iterable iterable3 = (Iterable)variants;
            Condition condition2 = condition;
            Collection collection = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable3, 10)));

            for(Object obj2 : iterable3) {
               Variant p0 = (Variant)obj2;
               int value3 = 0;
               collection.add(this.obfuscateVariant(p0));
            }

            collection.add(Selector.of(condition2, MultiVariant.of((List)collection)));
         }

         List multiparts = (List)destination;
         Iterable entrySet = (Iterable)blockState.variants().entrySet();
         int capacity = RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(entrySet, 10)), 16);
         Map map = (Map)(new LinkedHashMap(capacity));

         for(Object obj3 : entrySet) {
            Map.Entry it = (Map.Entry)obj3;
            int value4 = 0;
            Object key = it.getKey();
            List list = ((MultiVariant)it.getValue()).variants();
            Iterable iterable4 = (Iterable)list;
            Object obj = key;
            Collection collection2 = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable4, 10)));

            for(Object obj4 : iterable4) {
               Variant p0 = (Variant)obj4;
               int value5 = 0;
               collection2.add(this.obfuscateVariant(p0));
            }

            Pair pair = TuplesKt.to(obj, MultiVariant.of((List)collection2));
            map.put(pair.getFirst(), pair.getSecond());
         }

         BlockState.of(blockState.key(), map, multiparts).addTo(this.resourcePack);
      }

   }

   private final void obfuscateFonts() {
      team.unnamed.creative.font.BitMapFontProvider.Builder builder;
      net.kyori.adventure.key.Key key;
      Collection fonts = this.resourcePack.fonts();
      Iterable iterable = (Iterable)CollectionsKt.toList((Iterable)fonts);

      for(Object element : iterable) {
         Font font = (Font)element;
         int value = 0;
         List providers = font.providers();
         Iterable iterable2 = (Iterable)providers;
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

         for(Object item : iterable2) {
            FontProvider provider = (FontProvider)item;
            int value2 = 0;
            FontProvider fontProvider;
            if (provider instanceof BitMapFontProvider) {
               block: {
                  builder = ((BitMapFontProvider)provider).toBuilder();
                  Texture texture = this.obfuscateFontTexture((BitMapFontProvider)provider);
                  if (texture != null) {
                     key = texture.key();
                     if (key != null) {
                        break block;
                     }
                  }

                  key = ((BitMapFontProvider)provider).file();
               }

               fontProvider = builder.file(key).build();
            } else {
               fontProvider = provider;
            }

            destination.add(fontProvider);
         }

         font.providers((List)destination).addTo(this.resourcePack);
      }

   }

   private final void obfuscateTextures() {
      Iterable iterable = (Iterable)this.obfuscatedTextures;

      for(Object element : iterable) {
         ObfuscatedTexture obf = (ObfuscatedTexture)element;
         int value = 0;
         Key key = obf.getOriginalTexture().key();
         Key originalKey = key;
         if (this.resourcePack.removeTexture(originalKey) || !this.isOverlay) {
            this.resourcePack.texture(obf.getObfuscatedTexture());
         }

         Texture texture = this.resourcePack.texture(this.emissiveKey(originalKey));
         if (texture != null) {
            Texture emissive = texture;
            int value2 = 0;
            this.resourcePack.removeTexture(emissive.key());
            ResourceContainer resourceContainer = this.resourcePack;
            Texture.Builder toBuilder = emissive.toBuilder();
            Key key2 = obf.getObfuscatedTexture().key();
            resourceContainer.texture(toBuilder.key(this.emissiveKey(key2)).build());
         }

         texture = this.resourcePack.texture(this.normalMapKey(originalKey));
         if (texture != null) {
            Texture emissive = texture;
            int value3 = 0;
            this.resourcePack.removeTexture(emissive.key());
            ResourceContainer resourceContainer2 = this.resourcePack;
            Texture.Builder builder = emissive.toBuilder();
            Key key3 = obf.getObfuscatedTexture().key();
            resourceContainer2.texture(builder.key(this.normalMapKey(key3)).build());
         }

         texture = this.resourcePack.texture(this.specularMapKey(originalKey));
         if (texture != null) {
            Texture emissive = texture;
            int value4 = 0;
            this.resourcePack.removeTexture(emissive.key());
            ResourceContainer resourceContainer3 = this.resourcePack;
            Texture.Builder builder2 = emissive.toBuilder();
            Key key4 = obf.getObfuscatedTexture().key();
            resourceContainer3.texture(builder2.key(this.specularMapKey(key4)).build());
         }
      }

      this.obfuscateAtlases();
   }

   private final void obfuscateAtlases() {
      team.unnamed.creative.atlas.AtlasSource atlasSource;
      Collection atlases = this.resourcePack.atlases();
      Iterable iterable = (Iterable)CollectionsKt.toList((Iterable)atlases);

      for(Object element : iterable) {
         Atlas atlas = (Atlas)element;
         int value = 0;
         Iterable sources = (Iterable)atlas.sources();
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(sources, 10)));

         for(Object item : sources) {
            block: {
               AtlasSource source = (AtlasSource)item;
               int value2 = 0;
               SingleAtlasSource singleAtlasSource = source instanceof SingleAtlasSource ? (SingleAtlasSource)source : null;
               if ((source instanceof SingleAtlasSource ? (SingleAtlasSource)source : null) != null) {
                  Key resource = singleAtlasSource.resource();
                  if (resource != null) {
                     Key it = resource;
                     int value3 = 0;
                     Texture texture = this.findObfTextureOrNull(this.obfuscatedTextures, it);
                     if (texture != null) {
                        Texture obfSource = texture;
                        KeyUtils keyUtils = KeyUtils.INSTANCE;
                        Key key = obfSource.key();
                        atlasSource = AtlasSource.single(keyUtils.removeSuffix(key, ".png"));
                        break block;
                     }
                  }
               }

               atlasSource = source;
            }

            destination.add(atlasSource);
         }

         List obfSources = (List)destination;
         this.resourcePack.atlas(Atlas.atlas(atlas.key(), obfSources));
      }

   }

   private final void obfuscateSounds() {
      team.unnamed.creative.sound.SoundEntry soundEntry2;
      Collection sounds = this.resourcePack.sounds();
      Iterable iterable = (Iterable)CollectionsKt.toList((Iterable)sounds);

      for(Object element : iterable) {
         Sound sound = (Sound)element;
         int value = 0;
         if (!VanillaResourcePack.INSTANCE.getVanillaSounds().contains(sound.key())) {
            Key key = sound.key();
            Sound it = Sound.sound(this.obfuscateKey(key), sound.data());
            int value2 = 0;
            sounds = (Collection)this.obfuscatedSounds;
            sounds.add(new ObfuscatedSound(sound, it));
            it.addTo(this.resourcePack);
         }
      }

      sounds = this.resourcePack.soundRegistries();
      iterable = (Iterable)CollectionsKt.toList((Iterable)sounds);

      for(Object obj : iterable) {
         SoundRegistry soundRegistry = (SoundRegistry)obj;
         int value3 = 0;
         String namespace = soundRegistry.namespace();
         Iterable it = (Iterable)soundRegistry.sounds();
         Iterable it_12 = it;
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(it_12, 10)));

         for(Object item : it_12) {
            SoundEvent soundEvent = (SoundEvent)item;
            int value4 = 0;
            SoundEvent.Builder toBuilder = soundEvent.toBuilder();
            Iterable iterable2 = (Iterable)soundEvent.sounds();
            SoundEvent.Builder builder = toBuilder;
            Collection collection = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable2, 10)));

            for(Object obj2 : iterable2) {
               block: {
                  SoundEntry soundEntry = (SoundEntry)obj2;
                  int value5 = 0;
                  ObjectOpenHashSet objectOpenHashSet = this.obfuscatedSounds;
                  Key key2 = soundEntry.key();
                  Sound sound2 = this.findObfSound(objectOpenHashSet, key2);
                  if (sound2 != null) {
                     Sound it_7 = sound2;
                     int value6 = 0;
                     soundEntry2 = soundEntry.toBuilder().key(it_7.key()).build();
                     if (soundEntry2 != null) {
                        break block;
                     }
                  }

                  soundEntry2 = soundEntry;
               }

               collection.add(soundEntry2);
            }

            collection.add(builder.sounds((List)collection).build());
         }

         SoundRegistry.soundRegistry(namespace, (Collection)((List)destination)).addTo(this.resourcePack);
      }

      iterable = (Iterable)this.obfuscatedSounds;

      for(Object obj3 : iterable) {
         ObfuscatedSound obfSound = (ObfuscatedSound)obj3;
         int value7 = 0;
         if (this.resourcePack.removeSound(obfSound.getOriginalSound().key()) || !this.isOverlay) {
            obfSound.getObfuscatedSound().addTo(this.resourcePack);
         }
      }

   }

   private final Model obfuscateModel(Model model) {
      ObjectOpenHashSet objectOpenHashSet = this.obfuscatedModels;
      Key key = model.key();
      Model it = this.findObfOrNull(objectOpenHashSet, key);
      if (it != null) {
         int value = 0;
         return it;
      } else {
         Model.Builder toBuilder = model.toBuilder();
         Model.Builder builder = toBuilder;
         this.obfuscateModelTextures(model, builder);
         this.obfuscateOverrides(model, builder);
         Key it_6 = model.key();
         int value2 = 0;
         Key key2 = !this.skippedKeys.contains(model.key()) && VanillaResourcePack.INSTANCE.getResourcePack().model(it_6) == null ? it_6 : null;
         if (key2 != null) {
            key2 = this.obfuscateKey(key2);
            if (key2 != null) {
               Key p0 = key2;
               int value3 = 0;
               builder.key(p0);
            }
         }

         Model model2 = builder.build();
         ObfuscatedModel it_5 = new ObfuscatedModel(model, model2);
         ObjectOpenHashSet it_4 = this.obfuscatedModels;
         int value4 = 0;
         it_4.add(it_5);
         this.obfuscateParentModel(model, builder);
         Model model3 = builder.build();
         it_5.setObfuscatedModel(model3);
         return it_5.getObfuscatedModel();
      }
   }

   private final void obfuscateModelTextures(Model model, Model.Builder builder) {
      ObjectOpenHashSet objectOpenHashSet = this.obfuscatedModels;
      Key key = model.key();
      Model it = this.findObfOrNull(objectOpenHashSet, key);
      if (it != null) {
         builder.textures(it.textures());
      } else if (VanillaResourcePack.INSTANCE.getResourcePack().model(model.key()) == null) {
         team.unnamed.creative.model.ModelTexture modelTexture2;
         List list = model.textures().layers();
         Iterable iterable = (Iterable)list;
         Collection destination = (Collection)(new ObjectArrayList());

         for(Object element : iterable) {
            int value = 0;
            ModelTexture modelTexture = (ModelTexture)element;
            int value2 = 0;
            ModelTexture modelTexture3;
            if (modelTexture.key() == null) {
               modelTexture3 = null;
            } else {
               block: {
                  Texture texture = this.obfuscateModelTexture(modelTexture);
                  if (texture != null) {
                     Key key2 = texture.key();
                     if (key2 != null) {
                        key2 = KeyUtils.INSTANCE.removeSuffix(key2, ".png");
                        if (key2 != null) {
                           Key p0 = key2;
                           int value3 = 0;
                           modelTexture3 = ModelTexture.ofKey(p0);
                           if (modelTexture3 != null) {
                              break block;
                           }
                        }
                     }
                  }

                  modelTexture3 = modelTexture;
               }
            }

            if (modelTexture3 != null) {
               Object obj3 = modelTexture3;
               int value4 = 0;
               destination.add(obj3);
            }
         }

         ObjectArrayList layers = (ObjectArrayList)destination;
         Map map = model.textures().variables();
         Map map2 = map;
         Map map3 = (Map)(new LinkedHashMap(MapsKt.mapCapacity(map2.size())));
         Iterable entrySet = (Iterable)map2.entrySet();
         Map map4 = map3;

         for(Object obj4 : entrySet) {
            Object obj;
            block: {
               Map.Entry entry = (Map.Entry)obj4;
               int value5 = 0;
               Object obj2 = entry.getKey();
               Map.Entry variable = (Map.Entry)obj4;
               obj = obj2;
               int value6 = 0;
               obj2 = variable.getValue();
               Texture texture2 = this.obfuscateModelTexture((ModelTexture)obj2);
               if (texture2 != null) {
                  Key key3 = texture2.key();
                  if (key3 != null) {
                     key3 = KeyUtils.INSTANCE.removeSuffix(key3, ".png");
                     if (key3 != null) {
                        Key p0 = key3;
                        int value7 = 0;
                        modelTexture2 = ModelTexture.ofKey(p0);
                        if (modelTexture2 != null) {
                           break block;
                        }
                     }
                  }
               }

               modelTexture2 = (ModelTexture)variable.getValue();
            }

            ModelTexture modelTexture4 = modelTexture2;
            map4.put(obj, modelTexture4);
         }

         Map variables = map4;
         ModelTexture modelTexture5 = model.textures().particle();
         if (modelTexture5 != null) {
            block: {
               ModelTexture p = modelTexture5;
               Texture texture3 = this.obfuscateModelTexture(p);
               if (texture3 != null) {
                  Key key4 = texture3.key();
                  if (key4 != null) {
                     key4 = KeyUtils.INSTANCE.removeSuffix(key4, ".png");
                     if (key4 != null) {
                        Key p0 = key4;
                        int value8 = 0;
                        modelTexture5 = ModelTexture.ofKey(p0);
                        if (modelTexture5 != null) {
                           break block;
                        }
                     }
                  }
               }

               modelTexture5 = p;
            }
         } else {
            modelTexture5 = null;
         }

         ModelTexture particle = modelTexture5;
         ModelTextures modelTextures2 = ModelTextures.builder().layers((List)layers).variables(variables).particle(particle).build();
         ModelTextures modelTextures = modelTextures2;
         builder.textures(modelTextures);
      }
   }

   private final Variant obfuscateVariant(Variant variant) {
      Variant.Builder builder = Variant.builder();
      ObjectOpenHashSet objectOpenHashSet = this.obfuscatedModels;
      Key model = variant.model();
      Variant variant2 = builder.model(this.findObf(objectOpenHashSet, model).key()).uvLock(variant.uvLock()).weight(variant.weight()).x(variant.x()).y(variant.y()).build();
      return variant2;
   }

   private final void obfuscateParentModel(Model model, Model.Builder builder) {
      Key parent = model.parent();
      if (parent != null) {
         Key key;
         block: {
            Model model2 = this.findObfOrNull(this.obfuscatedModels, parent);
            if (model2 != null) {
               key = model2.key();
               if (key != null) {
                  break block;
               }
            }

            if (VanillaResourcePack.INSTANCE.getResourcePack().model(parent) != null) {
               int value = 0;
               key = parent;
            } else {
               block2: {
                  ResourceContainer resourceContainer = this.resourcePack;
                  int value2 = 0;
                  ResourceContainer resourceContainer2 = !Intrinsics.areEqual(parent, model.key()) ? resourceContainer : null;
                  if (resourceContainer2 != null) {
                     Model model3 = resourceContainer2.model(parent);
                     if (model3 != null) {
                        Model p0 = model3;
                        int value3 = 0;
                        model3 = this.obfuscateModel(p0);
                        if (model3 != null) {
                           key = model3.key();
                           break block2;
                        }
                     }
                  }

                  key = null;
               }

               if (key == null) {
                  key = parent;
               }
            }
         }

         Key obfuscatedParent = key;
         builder.parent(obfuscatedParent);
      }
   }

   private final void obfuscateOverrides(Model model, Model.Builder builder) {
      team.unnamed.creative.model.ItemOverride itemOverride;
      net.kyori.adventure.key.Key key;
      List list;
      block: {
         ObjectOpenHashSet objectOpenHashSet = this.obfuscatedModels;
         Key key2 = model.key();
         Model model2 = this.findObfOrNull(objectOpenHashSet, key2);
         if (model2 != null) {
            list = model2.overrides();
            if (list != null) {
               break block;
            }
         }

         List overrides = model.overrides();
         Iterable iterable = (Iterable)overrides;
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

         for(Object item : iterable) {
            block2: {
               ItemOverride override;
               block3: {
                  override = (ItemOverride)item;
                  int value = 0;
                  Key overrideKey = override.model();
                  objectOpenHashSet = this.obfuscatedModels;
                  Model model3 = this.findObfOrNull(objectOpenHashSet, overrideKey);
                  if (model3 != null) {
                     key = model3.key();
                     if (key != null) {
                        break block3;
                     }
                  }

                  if (VanillaResourcePack.INSTANCE.getResourcePack().model(overrideKey) != null) {
                     int value2 = 0;
                     key = overrideKey;
                  } else {
                     key = null;
                  }

                  if (key == null) {
                     block4: {
                        ResourceContainer it = this.resourcePack;
                        int value3 = 0;
                        ResourceContainer resourceContainer = !Intrinsics.areEqual(overrideKey, model.key()) ? it : null;
                        if (resourceContainer != null) {
                           Model model4 = resourceContainer.model(overrideKey);
                           if (model4 != null) {
                              Model p0 = model4;
                              int value4 = 0;
                              model4 = this.obfuscateModel(p0);
                              if (model4 != null) {
                                 key = model4.key();
                                 break block4;
                              }
                           }
                        }

                        key = null;
                     }

                     if (key == null) {
                        itemOverride = override;
                        break block2;
                     }
                  }
               }

               Key modelKey = key;
               itemOverride = ItemOverride.of(modelKey, override.predicate());
            }

            destination.add(itemOverride);
         }

         list = (List)destination;
      }

      List obfuscatedOverrides = list;
      builder.overrides(obfuscatedOverrides);
   }

   private final Texture obfuscate(Texture texture) {
      Texture.Builder toBuilder = texture.toBuilder();
      KeyUtils keyUtils = KeyUtils.INSTANCE;
      Key key = texture.key();
      Texture it = toBuilder.key(keyUtils.appendSuffix(this.obfuscateKey(key), ".png")).build();
      int value = 0;
      Collection collection = (Collection)this.obfuscatedTextures;
      collection.add(new ObfuscatedTexture(texture, it));
      return it;
   }

   private final Texture obfuscateModelTexture(ModelTexture modelTexture) {
      Key key2 = modelTexture.key();
      if (key2 != null) {
         key2 = KeyUtils.INSTANCE.appendSuffix(key2, ".png");
         if (key2 != null) {
            Key key = key2;
            Texture texture = this.findObfTextureOrNull(this.obfuscatedTextures, key);
            if (texture == null) {
               texture = this.getVanillaModelTextures().get(key);
               if (texture == null) {
                  texture = this.resourcePack.texture(key);
                  texture = texture != null ? this.obfuscate(texture) : null;
               }
            }

            return texture;
         }
      }

      return null;
   }

   private final Texture obfuscateFontTexture(BitMapFontProvider provider) {
      KeyUtils keyUtils = KeyUtils.INSTANCE;
      Key file = provider.file();
      Key key = keyUtils.appendSuffix(file, ".png");
      Texture texture = this.findObfTextureOrNull(this.obfuscatedTextures, key);
      if (texture == null) {
         texture = this.getVanillaFontTextures().get(key);
         if (texture == null) {
            texture = this.resourcePack.texture(key);
            texture = texture != null ? this.obfuscate(texture) : null;
         }
      }

      return texture;
   }

   private final Key obfuscateKey(Key key2) {
      Key key;
      switch (PackObfuscator.WhenMappings.$EnumSwitchMapping$0[this.obfuscationType.ordinal()]) {
         case 1:
            key = key2;
            break;
         case 2:
            Map map = (Map)this.obfuscatedNamespaceCache;
            Object namespace = key2.namespace();
            Object obj2 = map.get(namespace);
            Object obj;
            if (obj2 == null) {
               int value = 0;
               Object answer = UUID.randomUUID().toString();
               map.put(namespace, answer);
               obj = answer;
            } else {
               obj = obj2;
            }

            key = Key.key((String)obj, UUID.randomUUID().toString());
            break;
         case 3:
            key = Key.key(key2.namespace(), UUID.randomUUID().toString());
            break;
         default:
            throw new NoWhenBranchMatchedException();
      }

      return key;
   }

   private final Key specularMapKey(Key key) {
      return KeyUtils.INSTANCE.replace(key, ".png", "_s.png");
   }

   private final Key normalMapKey(Key key) {
      return KeyUtils.INSTANCE.replace(key, ".png", "_n.png");
   }

   private final Key emissiveKey(Key key) {
      return KeyUtils.INSTANCE.replace(key, ".png", "_e.png");
   }

   private final Object2ObjectOpenHashMap<Key, Texture> getVanillaModelTextures() {
      Lazy lazy = this.vanillaModelTextures$delegate;
      return (Object2ObjectOpenHashMap)lazy.getValue();
   }

   private final Map<Key, Texture> getVanillaFontTextures() {
      Lazy lazy = this.vanillaFontTextures$delegate;
      return (Map)lazy.getValue();
   }

   @NotNull
   public final PackObfuscator clone(@NotNull ResourceContainer resourceContainer) {
      PackObfuscator obfuscator = new PackObfuscator(resourceContainer);
      CollectionsKt.addAll((Collection)obfuscator.skippedKeys, (Iterable)this.skippedKeys);
      CollectionsKt.addAll((Collection)obfuscator.obfuscatedModels, (Iterable)this.obfuscatedModels);
      CollectionsKt.addAll((Collection)obfuscator.obfuscatedTextures, (Iterable)this.obfuscatedTextures);
      CollectionsKt.addAll((Collection)obfuscator.obfuscatedSounds, (Iterable)this.obfuscatedSounds);
      ((Map)obfuscator.obfuscatedNamespaceCache).putAll((Map)this.obfuscatedNamespaceCache);
      return obfuscator;
   }

   private static final ItemModel obfuscateItems$obfuscateItemModel(PackObfuscator packObfuscator, ItemModel itemModel) {
      ItemModel itemModel2;
      if (itemModel instanceof SpecialItemModel) {
         SpecialRender render = ((SpecialItemModel)itemModel).render();
         ObjectOpenHashSet objectOpenHashSet = packObfuscator.obfuscatedModels;
         Key base = ((SpecialItemModel)itemModel).base();
         SpecialItemModel specialItemModel = ItemModel.special(render, packObfuscator.findObf(objectOpenHashSet, base), itemModel.transformation());
         itemModel2 = specialItemModel;
      } else if (itemModel instanceof ReferenceItemModel) {
         ObjectOpenHashSet objectOpenHashSet2 = packObfuscator.obfuscatedModels;
         Key model = ((ReferenceItemModel)itemModel).model();
         itemModel2 = ItemModel.reference(packObfuscator.findObf(objectOpenHashSet2, model), ((ReferenceItemModel)itemModel).tints(), itemModel.transformation());
         itemModel2 = itemModel2;
      } else if (itemModel instanceof CompositeItemModel) {
         List models = ((CompositeItemModel)itemModel).models();
         Iterable iterable = (Iterable)models;
         Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(iterable, 10)));

         for(Object item : iterable) {
            ItemModel p0 = (ItemModel)item;
            int value = 0;
            destination.add(obfuscateItems$obfuscateItemModel(packObfuscator, p0));
         }

         CompositeItemModel compositeItemModel = ItemModel.composite((List)destination, itemModel.transformation());
         itemModel2 = compositeItemModel;
      } else if (itemModel instanceof ConditionItemModel) {
         ItemBooleanProperty condition = ((ConditionItemModel)itemModel).condition();
         ItemModel onTrue = ((ConditionItemModel)itemModel).onTrue();
         ItemModel itemModel3 = obfuscateItems$obfuscateItemModel(packObfuscator, onTrue);
         ItemModel onFalse = ((ConditionItemModel)itemModel).onFalse();
         ConditionItemModel conditionItemModel = ItemModel.conditional(condition, itemModel3, obfuscateItems$obfuscateItemModel(packObfuscator, onFalse), itemModel.transformation());
         itemModel2 = conditionItemModel;
      } else if (itemModel instanceof SelectItemModel) {
         ItemStringProperty property = ((SelectItemModel)itemModel).property();
         List cases = ((SelectItemModel)itemModel).cases();
         cases = SequencesKt.toList(SequencesKt.map(CollectionsKt.asSequence((Iterable)cases), (a0_ -> PackObfuscator.obfuscateItems$obfuscateItemModel$lambda$1(packObfuscator, (SelectItemModel.Case)a0_))));
         ItemModel fallback = ((SelectItemModel)itemModel).fallback();
         if (fallback != null) {
            ItemModel itemModel7 = fallback;
            List list = cases;
            ItemStringProperty itemStringProperty = property;
            int value2 = 0;
            ItemModel itemModel4 = obfuscateItems$obfuscateItemModel(packObfuscator, itemModel7);
            property = itemStringProperty;
            cases = list;
            fallback = itemModel4;
         } else {
            fallback = null;
         }

         SelectItemModel selectItemModel = ItemModel.select(property, cases, fallback, itemModel.transformation());
         itemModel2 = selectItemModel;
      } else if (itemModel instanceof RangeDispatchItemModel) {
         ItemNumericProperty itemNumericProperty = ((RangeDispatchItemModel)itemModel).property();
         float scale = ((RangeDispatchItemModel)itemModel).scale();
         List entries = ((RangeDispatchItemModel)itemModel).entries();
         entries = SequencesKt.toList(SequencesKt.map(CollectionsKt.asSequence((Iterable)entries), (a0_ -> PackObfuscator.obfuscateItems$obfuscateItemModel$lambda$3(packObfuscator, (RangeDispatchItemModel.Entry)a0_))));
         ItemModel itemModel5 = ((RangeDispatchItemModel)itemModel).fallback();
         if (itemModel5 != null) {
            ItemModel p0 = itemModel5;
            List list2 = entries;
            float value3 = scale;
            ItemNumericProperty itemNumericProperty2 = itemNumericProperty;
            int value4 = 0;
            ItemModel itemModel6 = obfuscateItems$obfuscateItemModel(packObfuscator, p0);
            itemNumericProperty = itemNumericProperty2;
            scale = value3;
            entries = list2;
            itemModel5 = itemModel6;
         } else {
            itemModel5 = null;
         }

         RangeDispatchItemModel rangeDispatchItemModel = ItemModel.rangeDispatch(itemNumericProperty, scale, entries, itemModel5, itemModel.transformation());
         itemModel2 = rangeDispatchItemModel;
      } else {
         itemModel2 = itemModel;
      }

      return itemModel2;
   }

   private static final Type obfuscationType$lambda$0(String it) {
      Logs.logError("Invalid PackObfuscation type: " + it + ", defaulting to " + PackObfuscator.Type.SIMPLE, true);
      Logs.logError("Valid options are: " + CollectionsKt.joinToString((Iterable)PackObfuscator.Type.getEntries(), (CharSequence)", ", (CharSequence)"", (CharSequence)"", -1, (CharSequence)"...", (Function1)null), true);
      return PackObfuscator.Type.SIMPLE;
   }

   private static final boolean obfuscatePack$lambda$0(PackObfuscator packObfuscator, File it) {
      String name = it.getName();
      File file = packObfuscator.obfCachedPack;
      if (file == null) {
         Intrinsics.throwUninitializedPropertyAccessException("obfCachedPack");
         file = null;
      }

      return !Intrinsics.areEqual(name, file.getName());
   }

   private static final Object2ObjectOpenHashMap vanillaModelTextures_delegate$lambda$0(PackObfuscator packObfuscator) {
      Pair pair;
      Collection models = packObfuscator.resourcePack.models();
      Iterable iterable = (Iterable)models;
      Collection destination = (Collection)(new ObjectArrayList());

      for(Object element : iterable) {
         Model it = (Model)element;
         int value = 0;
         if (VanillaResourcePack.INSTANCE.getResourcePack().model(it.key()) != null) {
            destination.add(element);
         }
      }

      models = (Collection)((ObjectArrayList)destination);
      Collection collection = VanillaResourcePack.INSTANCE.getResourcePack().models();
      iterable = (Iterable)CollectionsKt.plus(models, (Iterable)collection);
      HashSet hashSet = new HashSet();
      ArrayList list = new ArrayList();

      for(Object obj : iterable) {
         Model it = (Model)obj;
         int value2 = 0;
         Object asString = it.key().asString();
         if (hashSet.add(asString)) {
            list.add(obj);
         }
      }

      iterable = (Iterable)((List)list);
      ObjectArrayList result = new ObjectArrayList();

      for(Object obj2 : iterable) {
         Model it = (Model)obj2;
         int value3 = 0;
         List layers = it.textures().layers();

         for(Object item : (Iterable)CollectionsKt.plus((Collection)CollectionsKt.plus((Collection)layers, (Iterable)CollectionsKt.listOfNotNull(it.textures().particle())), (Iterable)it.textures().variables().values())) {
            if (item != null) {
               result.add(item);
            }
         }
      }

      iterable = (Iterable)result;
      Object2ObjectOpenHashMap object2ObjectOpenHashMap = new Object2ObjectOpenHashMap(RangesKt.coerceAtLeast(CollectionUtilsKt.mapCapacity((iterable instanceof Collection ? (Collection)iterable : null) != null ? (iterable instanceof Collection ? (Collection)iterable : null).size() : 10), 16));

      for(Object obj3 : iterable) {
         block: {
            ModelTexture it = (ModelTexture)obj3;
            int value4 = 0;
            Key key2 = it.key();
            if (key2 != null) {
               key2 = KeyUtils.INSTANCE.appendSuffix(key2, ".png");
               if (key2 != null) {
                  Key key = key2;
                  Texture texture = ResourcePackUtilsKt.textureOrVanilla(packObfuscator.resourcePack, key);
                  if (texture == null) {
                     pair = null;
                  } else {
                     pair = TuplesKt.to(key, texture);
                  }
                  break block;
               }
            }

            pair = null;
         }

         if (pair != null) {
            Pair pair4 = pair;
            int value5 = 0;
            Object first = pair4.getFirst();
            if (first != null) {
               Object second = pair4.getSecond();
               if (second != null) {
                  Pair pair2 = TuplesKt.to(first, second);
                  if (pair2 != null) {
                     Pair pair3 = pair2;
                     Object component1 = pair3.component1();
                     Object component2 = pair3.component2();
                     ((Map)object2ObjectOpenHashMap).put(component1, component2);
                  }
               }
            }
         }
      }

      return object2ObjectOpenHashMap;
   }

   private static final Map vanillaFontTextures_delegate$lambda$0(PackObfuscator packObfuscator) {
      net.kyori.adventure.key.Key key;
      Collection fonts = packObfuscator.resourcePack.fonts();
      Iterable iterable = (Iterable)fonts;
      Collection destination = (Collection)(new ObjectArrayList());

      for(Object element : iterable) {
         Font it = (Font)element;
         int value = 0;
         if (VanillaResourcePack.INSTANCE.getResourcePack().font(it.key()) != null) {
            destination.add(element);
         }
      }

      fonts = (Collection)((ObjectArrayList)destination);
      Collection collection = VanillaResourcePack.INSTANCE.getResourcePack().fonts();
      iterable = (Iterable)CollectionsKt.plus(fonts, (Iterable)collection);
      HashSet hashSet = new HashSet();
      ArrayList list = new ArrayList();

      for(Object obj : iterable) {
         Font it = (Font)obj;
         int value2 = 0;
         Object asString = it.key().asString();
         if (hashSet.add(asString)) {
            list.add(obj);
         }
      }

      iterable = (Iterable)((List)list);
      ObjectArrayList result = new ObjectArrayList();

      for(Object obj2 : iterable) {
         Font f = (Font)obj2;
         int value3 = 0;
         List providers = f.providers();
         Iterable iterable2 = (Iterable)providers;
         Collection collection2 = (Collection)(new ObjectArrayList());

         for(Object obj3 : iterable2) {
            block: {
               int value4 = 0;
               FontProvider it = (FontProvider)obj3;
               int value5 = 0;
               BitMapFontProvider bitMapFontProvider = it instanceof BitMapFontProvider ? (BitMapFontProvider)it : null;
               if ((it instanceof BitMapFontProvider ? (BitMapFontProvider)it : null) != null) {
                  Key file = bitMapFontProvider.file();
                  if (file != null) {
                     key = KeyUtils.INSTANCE.appendSuffix(file, ".png");
                     break block;
                  }
               }

               key = null;
            }

            if (key != null) {
               Object obj4 = key;
               int value6 = 0;
               collection2.add(obj4);
            }
         }

         for(Object item : (Iterable)((ObjectArrayList)collection2)) {
            if (item != null) {
               result.add(item);
            }
         }
      }

      iterable = (Iterable)result;
      LinkedHashMap linkedHashMap = new LinkedHashMap(RangesKt.coerceAtLeast(MapsKt.mapCapacity(CollectionsKt.collectionSizeOrDefault(iterable, 10)), 16));
      Map map = (Map)linkedHashMap;

      for(Object obj5 : iterable) {
         Key it = (Key)obj5;
         int value7 = 0;
         ResourceContainer resourceContainer = packObfuscator.resourcePack;
         Texture texture = ResourcePackUtilsKt.textureOrVanilla(resourceContainer, it);
         map.put(obj5, texture);
      }

      return CollectionUtilsKt.filterValuesNotNull(map);
   }

   private static final SelectItemModel.Case obfuscateItems$obfuscateItemModel$lambda$1(PackObfuscator packObfuscator, SelectItemModel.Case it) {
      ItemModel model = it.model();
      return SelectItemModel.Case._case(obfuscateItems$obfuscateItemModel(packObfuscator, model), it.when());
   }

   private static final RangeDispatchItemModel.Entry obfuscateItems$obfuscateItemModel$lambda$3(PackObfuscator packObfuscator, RangeDispatchItemModel.Entry it) {
      float threshold = it.threshold();
      ItemModel model = it.model();
      return RangeDispatchItemModel.Entry.entry(threshold, obfuscateItems$obfuscateItemModel(packObfuscator, model));
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\f\u001a\u0004\u0018\u00010\u00032\u0006\u0010\r\u001a\u00020\u000eR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\b\"\u0004\b\n\u0010\u000b¨\u0006\u000f"},
      d2 = {"Lcom/nexomc/nexo/pack/PackObfuscator$ObfuscatedModel;", "", "originalModel", "Lteam/unnamed/creative/model/Model;", "obfuscatedModel", "<init>", "(Lteam/unnamed/creative/model/Model;Lteam/unnamed/creative/model/Model;)V", "getOriginalModel", "()Lteam/unnamed/creative/model/Model;", "getObfuscatedModel", "setObfuscatedModel", "(Lteam/unnamed/creative/model/Model;)V", "find", "key", "Lnet/kyori/adventure/key/Key;", "com.nexomc:core"}
   )
   private static final class ObfuscatedModel {
      @NotNull
      private final Model originalModel;
      @NotNull
      private Model obfuscatedModel;

      public ObfuscatedModel(@NotNull Model originalModel, @NotNull Model obfuscatedModel) {
         super();
         this.originalModel = originalModel;
         this.obfuscatedModel = obfuscatedModel;
      }

      @NotNull
      public final Model getOriginalModel() {
         return this.originalModel;
      }

      @NotNull
      public final Model getObfuscatedModel() {
         return this.obfuscatedModel;
      }

      public final void setObfuscatedModel(@NotNull Model model) {
         this.obfuscatedModel = model;
      }

      @Nullable
      public final Model find(@NotNull Key key) {
         Model it = this.originalModel;
         int value = 0;
         Model model = Intrinsics.areEqual(it.key(), key) ? it : null;
         if (model == null) {
            it = this.obfuscatedModel;
            value = 0;
            model = Intrinsics.areEqual(it.key(), key) ? it : null;
         }

         return model;
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\r\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u000e\u001a\u00020\u000fR\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001a\u0010\u0004\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\b\"\u0004\b\f\u0010\n¨\u0006\u0010"},
      d2 = {"Lcom/nexomc/nexo/pack/PackObfuscator$ObfuscatedTexture;", "", "originalTexture", "Lteam/unnamed/creative/texture/Texture;", "obfuscatedTexture", "<init>", "(Lteam/unnamed/creative/texture/Texture;Lteam/unnamed/creative/texture/Texture;)V", "getOriginalTexture", "()Lteam/unnamed/creative/texture/Texture;", "setOriginalTexture", "(Lteam/unnamed/creative/texture/Texture;)V", "getObfuscatedTexture", "setObfuscatedTexture", "find", "key", "Lnet/kyori/adventure/key/Key;", "com.nexomc:core"}
   )
   private static final class ObfuscatedTexture {
      @NotNull
      private Texture originalTexture;
      @NotNull
      private Texture obfuscatedTexture;

      public ObfuscatedTexture(@NotNull Texture originalTexture, @NotNull Texture obfuscatedTexture) {
         super();
         this.originalTexture = originalTexture;
         this.obfuscatedTexture = obfuscatedTexture;
         Texture.Builder toBuilder = this.originalTexture.toBuilder();
         KeyUtils keyUtils = KeyUtils.INSTANCE;
         Key key = this.originalTexture.key();
         Texture texture = toBuilder.key(keyUtils.appendSuffix(key, ".png")).build();
         this.originalTexture = texture;
         Texture.Builder builder = this.obfuscatedTexture.toBuilder();
         keyUtils = KeyUtils.INSTANCE;
         key = this.obfuscatedTexture.key();
         Texture texture2 = builder.key(keyUtils.appendSuffix(key, ".png")).build();
         this.obfuscatedTexture = texture2;
      }

      @NotNull
      public final Texture getOriginalTexture() {
         return this.originalTexture;
      }

      public final void setOriginalTexture(@NotNull Texture texture) {
         this.originalTexture = texture;
      }

      @NotNull
      public final Texture getObfuscatedTexture() {
         return this.obfuscatedTexture;
      }

      public final void setObfuscatedTexture(@NotNull Texture texture) {
         this.obfuscatedTexture = texture;
      }

      @Nullable
      public final Texture find(@NotNull Key key) {
         Texture it = this.originalTexture;
         int value = 0;
         Texture texture = Intrinsics.areEqual(it.key(), key) ? it : null;
         if (texture == null) {
            it = this.obfuscatedTexture;
            value = 0;
            texture = Intrinsics.areEqual(it.key(), key) ? it : null;
         }

         return texture;
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\n\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u000b\u001a\u00020\fR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\b¨\u0006\r"},
      d2 = {"Lcom/nexomc/nexo/pack/PackObfuscator$ObfuscatedSound;", "", "originalSound", "Lteam/unnamed/creative/sound/Sound;", "obfuscatedSound", "<init>", "(Lteam/unnamed/creative/sound/Sound;Lteam/unnamed/creative/sound/Sound;)V", "getOriginalSound", "()Lteam/unnamed/creative/sound/Sound;", "getObfuscatedSound", "find", "key", "Lnet/kyori/adventure/key/Key;", "com.nexomc:core"}
   )
   private static final class ObfuscatedSound {
      @NotNull
      private final Sound originalSound;
      @NotNull
      private final Sound obfuscatedSound;

      public ObfuscatedSound(@NotNull Sound originalSound, @NotNull Sound obfuscatedSound) {
         super();
         this.originalSound = originalSound;
         this.obfuscatedSound = obfuscatedSound;
      }

      @NotNull
      public final Sound getOriginalSound() {
         return this.originalSound;
      }

      @NotNull
      public final Sound getObfuscatedSound() {
         return this.obfuscatedSound;
      }

      @Nullable
      public final Sound find(@NotNull Key key) {
         Sound it = this.originalSound;
         int value = 0;
         Sound sound = Intrinsics.areEqual(it.key(), key) ? it : null;
         if (sound == null) {
            it = this.obfuscatedSound;
            value = 0;
            sound = Intrinsics.areEqual(it.key(), key) ? it : null;
         }

         return sound;
      }
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0002\b\u0002\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0007\u001a\u00020\b8F¢\u0006\u0006\u001a\u0004\b\u0007\u0010\tj\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/pack/PackObfuscator$Type;", "", "<init>", "(Ljava/lang/String;I)V", "SIMPLE", "FULL", "NONE", "isNone", "", "()Z", "com.nexomc:core"}
   )
   public static enum Type {
      SIMPLE,
      FULL,
      NONE;

      public final boolean isNone() {
         return this == NONE;
      }

      @NotNull
      public static EnumEntries<Type> getEntries() {
         return EnumEntriesKt.enumEntries(values());
      }
   }

   public static final class WhenMappings {
      public static final int[] $EnumSwitchMapping$0;
      static {
         int[] a_0 = new int[com.nexomc.nexo.pack.PackObfuscator.Type.values().length];
         try { a_0[com.nexomc.nexo.pack.PackObfuscator.Type.NONE.ordinal()] = 1; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.pack.PackObfuscator.Type.FULL.ordinal()] = 2; } catch (NoSuchFieldError ex) {}
         try { a_0[com.nexomc.nexo.pack.PackObfuscator.Type.SIMPLE.ordinal()] = 3; } catch (NoSuchFieldError ex) {}
         $EnumSwitchMapping$0 = a_0;
      }
   }
}
