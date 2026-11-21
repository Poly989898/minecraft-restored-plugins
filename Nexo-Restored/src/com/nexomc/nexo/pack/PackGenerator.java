package com.nexomc.nexo.pack;
import com.nexomc.nexo.mechanics.breakable.L;
import com.nexomc.nexo.pack.AtlasGenerator;
import com.nexomc.nexo.pack.BBModelGenerator;
import com.nexomc.nexo.pack.ModelGenerator;
import com.nexomc.nexo.pack.ModernVersionPatcher;
import com.nexomc.nexo.pack.NexoPackSquash;
import com.nexomc.nexo.pack.Overlays;
import com.nexomc.nexo.pack.PackDownloader;
import com.nexomc.nexo.pack.PackGenerator;
import com.nexomc.nexo.pack.PackObfuscator;
import com.nexomc.nexo.pack.PackValidator;
import com.nexomc.nexo.pack.TextureMerger;
import com.nexomc.nexo.pack.VanillaResourcePack;
import team.unnamed.creative.overlay.Overlay;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.libs.folialib.wrapper.task.WrappedTask;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoPack;
import com.nexomc.nexo.api.events.resourcepack.NexoPackFinishedEvent;
import com.nexomc.nexo.api.events.resourcepack.NexoPackUploadEvent;
import com.nexomc.nexo.api.events.resourcepack.NexoPostPackGenerateEvent;
import com.nexomc.nexo.api.events.resourcepack.NexoPrePackGenerateEvent;
import com.nexomc.nexo.compatibilities.modelengine.ModelEngineCompatibility;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.fonts.NexoTranslator;
import com.nexomc.nexo.glyphs.Glyph;
import com.nexomc.nexo.glyphs.ReferenceGlyph;
import com.nexomc.nexo.glyphs.Shift;
import com.nexomc.nexo.glyphs.ShiftTag;
import com.nexomc.nexo.items.ItemModelParser;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockFactory;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.pack.creative.NexoPackReader;
import com.nexomc.nexo.pack.creative.NexoPackWriter;
import com.nexomc.nexo.pack.server.NexoPackServer;
import com.nexomc.nexo.pack.shaders.GifGenerator;
import com.nexomc.nexo.pack.shaders.ScoreboardBackground;
import com.nexomc.nexo.proxy.ProxyHelpers;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.AdventureUtilsKt;
import com.nexomc.nexo.utils.EventUtils;
import com.nexomc.nexo.utils.FileUtils;
import com.nexomc.nexo.utils.FileUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.PluginUtils;
import com.nexomc.nexo.utils.ResourceManager;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.nexomc.nexo.utils.StringUtilsKt;
import com.nexomc.nexo.utils.TriState;
import com.nexomc.nexo.utils.UtilsKt;
import com.nexomc.nexo.utils.VersionUtil;
import com.nexomc.nexo.utils.customarmor.ComponentCustomArmor;
import com.nexomc.nexo.utils.customarmor.CustomArmorType;
import com.nexomc.nexo.utils.customarmor.TrimsCustomArmor;
import com.nexomc.nexo.utils.jukebox_playable.JukeboxPlayableDatapack;
import com.nexomc.nexo.utils.logs.Logs;
import com.ticxo.modelengine.api.ModelEngineAPI;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.Closeable;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.unnamed.creative.BuiltResourcePack;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.base.Writable;
import team.unnamed.creative.font.Font;
import team.unnamed.creative.font.FontProvider;
import team.unnamed.creative.font.ReferenceFontProvider;
import team.unnamed.creative.item.Item;
import team.unnamed.creative.lang.Language;
import team.unnamed.creative.metadata.pack.FormatVersion;
import team.unnamed.creative.metadata.pack.PackFormat;
import team.unnamed.creative.model.ItemOverride;
import team.unnamed.creative.model.ItemPredicate;
import team.unnamed.creative.model.Model;
import team.unnamed.creative.overlay.ResourceContainer;
import team.unnamed.creative.sound.SoundRegistry;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u008c\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0005\u0018\u0000 <2\u00020\u0001:\u0001<B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010'\u001a\u00020(J\u0010\u0010)\u001a\u00020(2\b\b\u0002\u0010*\u001a\u00020+J\b\u0010,\u001a\u00020(H\u0002J\b\u0010-\u001a\u00020(H\u0002J\u0006\u0010\t\u001a\u00020\nJ\u000b\u0010\u0004\u001a\u00070\u0005¢\u0006\u0002\b\u0006J\u000e\u0010\u0004\u001a\u00020(2\u0006\u0010\u0004\u001a\u00020\u0005J\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012J\b\u0010.\u001a\u00020(H\u0002J\b\u0010/\u001a\u00020(H\u0002J\b\u00100\u001a\u00020(H\u0002J\b\u00101\u001a\u00020(H\u0002J\b\u00102\u001a\u00020(H\u0002J\b\u00103\u001a\u00020(H\u0002J\b\u00104\u001a\u00020(H\u0002J\b\u00105\u001a\u00020(H\u0002J\b\u0010:\u001a\u00020(H\u0002J\b\u0010;\u001a\u00020(H\u0002R\u0013\u0010\u0004\u001a\u00070\u0005¢\u0006\u0002\b\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0011\u001a\u0004\u0018\u00010\u0012X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0014X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u0016X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u001aX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001b\u001a\u00020\u001cX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001d\u001a\u00020\u001eX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001f\u001a\u00020 X\u0082\u0004¢\u0006\u0002\n\u0000R.\u0010$\u001a\n\u0012\u0004\u0012\u00020#\u0018\u00010\"2\u000e\u0010!\u001a\n\u0012\u0004\u0012\u00020#\u0018\u00010\"@BX\u0086\u000e¢\u0006\b\n\u0000\u001a\u0004\b%\u0010&R2\u00106\u001a&\u0012\f\u0012\n 9*\u0004\u0018\u00010808 9*\u0012\u0012\f\u0012\n 9*\u0004\u0018\u00010808\u0018\u00010707X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006="},
   d2 = {"Lcom/nexomc/nexo/pack/PackGenerator;", "", "<init>", "()V", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "Lorg/jetbrains/annotations/NotNull;", "packDownloader", "Lcom/nexomc/nexo/pack/PackDownloader;", "packObfuscator", "Lcom/nexomc/nexo/pack/PackObfuscator;", "textureMerger", "Lcom/nexomc/nexo/pack/TextureMerger;", "packSquasher", "Lcom/nexomc/nexo/pack/NexoPackSquash;", "packValidator", "Lcom/nexomc/nexo/pack/PackValidator;", "builtPack", "Lteam/unnamed/creative/BuiltResourcePack;", "trimsCustomArmor", "Lcom/nexomc/nexo/utils/customarmor/TrimsCustomArmor;", "componentCustomArmor", "Lcom/nexomc/nexo/utils/customarmor/ComponentCustomArmor;", "modelGenerator", "Lcom/nexomc/nexo/pack/ModelGenerator;", "atlasGenerator", "Lcom/nexomc/nexo/pack/AtlasGenerator;", "gifGenerator", "Lcom/nexomc/nexo/pack/shaders/GifGenerator;", "bbModelGenerator", "Lcom/nexomc/nexo/pack/BBModelGenerator;", "modernVersionPatcher", "Lcom/nexomc/nexo/pack/ModernVersionPatcher;", "value", "Ljava/util/concurrent/CompletableFuture;", "Ljava/lang/Void;", "packGenFuture", "getPackGenFuture", "()Ljava/util/concurrent/CompletableFuture;", "regeneratePack", "", "generatePack", "sendToOnline", "", "checkDefaultOversizedInGui", "sortModelOverrides", "addShiftProvider", "addGlyphFiles", "addSoundFile", "importRequiredPack", "importDefaultPack", "importExternalPacks", "importModelEnginePack", "parseLanguageFiles", "escapeMenuLangKeys", "Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", "", "kotlin.jvm.PlatformType", "parseGlobalLanguage", "removeExcludedFileExtensions", "Companion", "com.nexomc:core"}
)
public final class PackGenerator {
   @NotNull
   public static final Companion Companion = new Companion();
   @NotNull
   private final ResourcePack resourcePack;
   @NotNull
   private final PackDownloader packDownloader;
   @NotNull
   private final PackObfuscator packObfuscator;
   @NotNull
   private TextureMerger textureMerger;
   @NotNull
   private final NexoPackSquash packSquasher;
   @NotNull
   private final PackValidator packValidator;
   @Nullable
   private BuiltResourcePack builtPack;
   @Nullable
   private final TrimsCustomArmor trimsCustomArmor;
   @Nullable
   private final ComponentCustomArmor componentCustomArmor;
   @NotNull
   private final ModelGenerator modelGenerator;
   @NotNull
   private final AtlasGenerator atlasGenerator;
   @NotNull
   private final GifGenerator gifGenerator;
   @NotNull
   private final BBModelGenerator bbModelGenerator;
   @NotNull
   private final ModernVersionPatcher modernVersionPatcher;
   @Nullable
   private CompletableFuture<Void> packGenFuture;
   private final ObjectArrayList<String> escapeMenuLangKeys;
   @NotNull
   private static final Regex defaultRegex = new Regex("(Default|Required)Pack_.*");
   @NotNull
   private static File externalPacks;
   @NotNull
   private static final File assetsFolder;
   @NotNull
   private static final LinkedHashSet<String> ignoredExtensions;

   public PackGenerator() {
      ResourcePack resourcePack2 = ResourcePack.resourcePack();
      this.resourcePack = resourcePack2;
      this.packDownloader = new PackDownloader();
      this.packObfuscator = new PackObfuscator(this.resourcePack);
      this.textureMerger = new TextureMerger(this.resourcePack);
      this.packSquasher = new NexoPackSquash(this.resourcePack);
      this.packValidator = new PackValidator(this.resourcePack);
      NexoPackReader.INSTANCE.resetReader();
      NexoPackWriter.INSTANCE.resetWriter();
      String[] strs = new String[]{"menu.game", "menu.disconnect", "menu.feedback", "menu.options", "menu.playerReporting", "menu.reportBugs", "menu.returnToGame", "menu.sendFeedback", "gui.stats", "gui.advancements", "modmenu.title", "container.crafting"};
      this.escapeMenuLangKeys = ObjectArrayList.of(strs);
      Companion.generateDefaultPaths();
      VanillaResourcePack.INSTANCE.extractLatest();
      this.packDownloader.downloadRequiredPack();
      this.packDownloader.downloadDefaultPack();
      TrimsCustomArmor trimsCustomArmor2 = new TrimsCustomArmor();
      int value = 0;
      boolean flag = CustomArmorType.Companion.getSetting() == CustomArmorType.TRIMS;
      this.trimsCustomArmor = flag ? trimsCustomArmor2 : null;
      ComponentCustomArmor componentCustomArmor2 = new ComponentCustomArmor(this.resourcePack);
      value = 0;
      flag = CustomArmorType.Companion.getSetting() == CustomArmorType.COMPONENT;
      this.componentCustomArmor = flag ? componentCustomArmor2 : null;
      this.modelGenerator = new ModelGenerator(this.resourcePack);
      this.atlasGenerator = new AtlasGenerator(this.resourcePack);
      this.textureMerger = new TextureMerger(this.resourcePack);
      this.gifGenerator = new GifGenerator(this.resourcePack);
      this.bbModelGenerator = new BBModelGenerator(this.resourcePack);
      this.modernVersionPatcher = new ModernVersionPatcher(this.resourcePack);
   }

   @Nullable
   public final CompletableFuture<Void> getPackGenFuture() {
      return this.packGenFuture;
   }

   public final void regeneratePack() {
      this.generatePack(Settings.PACK_SEND_RELOAD.toBool());
   }

   public final void generatePack(boolean sendToOnline) {
      if (Settings.PACK_GENERATE_PACK.toBool()) {
         Companion.stopPackGeneration();
         NexoPack.clearPack(this.resourcePack);
         EventUtils.INSTANCE.call(new NexoPrePackGenerateEvent(this.resourcePack));
         File dataFolder = NexoPlugin.Companion.instance().getDataFolder();
         String[] strs = new String[]{"pack"};
         File packFolder = FileUtilsKt.resolve(dataFolder, strs);
         CompletableFuture[] futures = new CompletableFuture[]{VanillaResourcePack.INSTANCE.extractLatest(), this.packDownloader.downloadRequiredPack(), this.packDownloader.downloadDefaultPack(), ModelEngineCompatibility.Companion.modelEngineFuture()};
         this.packGenFuture = CompletableFuture.allOf(Arrays.copyOf(futures, futures.length)).thenRunAsync((() -> PackGenerator.generatePack$lambda$0(this, packFolder))).thenCompose(ignored -> PackGenerator.generatePack$lambda$1(this, ignored)).thenRunAsync(() -> PackGenerator.generatePack$lambda$3(this, sendToOnline));
      }
   }

   private final void checkDefaultOversizedInGui() {
      if (Settings.PACK_FORCE_OVERSIZED_IN_GUI.toBool()) {
         Collection overlays = this.resourcePack.overlays();

         for(ResourceContainer container : (java.lang.Iterable<ResourceContainer>)(java.lang.Iterable)CollectionsKt.plus(overlays, this.resourcePack)) {
            overlays = container.items();

            for(Item item : (java.lang.Iterable<Item>)(java.lang.Iterable)CollectionsKt.toList((Iterable)overlays)) {
               Pair pair = TuplesKt.to(item.handAnimationOnSwap(), item.swapAnimationScale());
               boolean h = (Boolean)pair.component1();
               float s = ((Number)pair.component2()).floatValue();
               container.item(Item.item(item.key(), item.model(), h, true, s));
            }
         }

      }
   }

   private final void sortModelOverrides() {
      sortModelOverrides$sort(this.resourcePack);
      Collection overlays = this.resourcePack.overlays();
      Iterable iterable = (Iterable)overlays;

      for(Object element : iterable) {
         ResourceContainer p0 = (ResourceContainer)element;
         int value = 0;
         sortModelOverrides$sort(p0);
      }

   }

   @NotNull
   public final PackObfuscator packObfuscator() {
      return this.packObfuscator;
   }

   @NotNull
   public final ResourcePack resourcePack() {
      return this.resourcePack;
   }

   public final void resourcePack(@NotNull ResourcePack resourcePack) {
      NexoPack.overwritePack(this.resourcePack, resourcePack);
   }

   @Nullable
   public final BuiltResourcePack builtPack() {
      return this.builtPack;
   }

   private final void addShiftProvider() {
      net.kyori.adventure.key.Key key;
      List list;
      block: {
         Font font2 = this.resourcePack.font(ShiftTag.INSTANCE.getFONT());
         if (font2 != null) {
            List providers = font2.providers();
            if (providers != null) {
               Iterable iterable = (Iterable)providers;
               Collection destination = (Collection)(new ArrayList());

               for(Object element : iterable) {
                  int value = 0;
                  FontProvider it = (FontProvider)element;
                  int value2 = 0;
                  Key key2 = (it instanceof ReferenceFontProvider ? (ReferenceFontProvider)it : null) != null ? (it instanceof ReferenceFontProvider ? (ReferenceFontProvider)it : null).id() : null;
                  if (key2 != null) {
                     Object obj = key2;
                     int value3 = 0;
                     destination.add(obj);
                  }
               }

               list = (List)destination;
               break block;
            }
         }

         list = CollectionsKt.emptyList();
      }

      List shiftFontReferences = list;
      Collection fonts = this.resourcePack.fonts();
      Iterable iterable2 = (Iterable)CollectionsKt.toList((Iterable)fonts);

      for(Object obj2 : iterable2) {
         Font font = (Font)obj2;
         int value4 = 0;
         if (!Intrinsics.areEqual(font.key(), ShiftTag.INSTANCE.getFONT()) && !shiftFontReferences.contains(font.key())) {
            font.toBuilder().addProvider(FontProvider.reference(ShiftTag.INSTANCE.getFONT())).build().addTo(this.resourcePack);
         } else {
            font.toBuilder().addProvider(Shift.INSTANCE.getFontProvider()).build().addTo(this.resourcePack);
         }
      }

      List list2;
      block: {
         key = ShiftTag.INSTANCE.getFONT();
         Font font3 = this.resourcePack.font(ShiftTag.INSTANCE.getFONT());
         if (font3 != null) {
            List list3 = font3.providers();
            if (list3 != null) {
               list2 = CollectionsKt.plus((Collection)list3, Shift.INSTANCE.getFontProvider());
               if (list2 != null) {
                  break block;
               }
            }
         }

         list2 = CollectionsKt.listOf(Shift.INSTANCE.getFontProvider());
      }

      Font.font(key, list2).addTo(this.resourcePack);
   }

   private final void addGlyphFiles() {
      Iterable iterable = (Iterable)NexoPlugin.Companion.instance().fontManager().glyphs();
      Map destination = (Map)(new LinkedHashMap());

      for(Object element : iterable) {
         Glyph it = (Glyph)element;
         int value = 0;
         Object key = it.getFont();
         Object obj2 = destination.get(key);
         Object obj;
         if (obj2 == null) {
            int value2 = 0;
            Object answer = (List)(new ArrayList());
            destination.put(key, answer);
            obj = answer;
         } else {
            obj = obj2;
         }

         List list2 = (List)obj;
         list2.add(element);
      }


      for(Map.Entry entry : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)destination.entrySet()) {
         Key font;
         List glyphs;
         List list;
         block: {
            int value3 = 0;
            font = (Key)entry.getKey();
            glyphs = (List)entry.getValue();
            Font font2 = this.resourcePack.font(font);
            if (font2 != null) {
               list = font2.providers();
               if (list != null) {
                  break block;
               }
            }

            list = CollectionsKt.emptyList();
         }

         List providers = list;
         Iterable iterable2 = (Iterable)glyphs;
         Collection collection = (Collection)(new ArrayList());

         for(Object obj3 : iterable2) {
            Glyph it = (Glyph)obj3;
            int value4 = 0;
            if (!(it instanceof ReferenceGlyph)) {
               collection.add(obj3);
            }
         }

         iterable2 = (Iterable)((List)collection);
         collection = (Collection)(new ArrayList());

         for(Object obj4 : iterable2) {
            Glyph it = (Glyph)obj4;
            int value5 = 0;
            Iterable iterable3 = (Iterable)ArraysKt.toList(it.getFontProviders());
            CollectionsKt.addAll(collection, iterable3);
         }

         List glyphProviders = (List)collection;
         if (!providers.isEmpty() || !glyphProviders.isEmpty()) {
            this.resourcePack.font(font, CollectionsKt.plus((Collection)providers, (Iterable)glyphProviders));
         }
      }

   }

   private final void addSoundFile() {
      Iterable customSoundRegistries = (Iterable)NexoPlugin.Companion.instance().soundManager().customSoundRegistries();

      for(Object element : customSoundRegistries) {
         SoundRegistry soundRegistry;
         block: {
            SoundRegistry customSoundRegistry = (SoundRegistry)element;
            int value = 0;
            soundRegistry = this.resourcePack.soundRegistry(customSoundRegistry.namespace());
            if (soundRegistry != null) {
               SoundRegistry existing = soundRegistry;
               int value2 = 0;
               soundRegistry = SoundRegistry.soundRegistry(existing.namespace(), (Collection)CollectionsKt.plus(existing.sounds(), (Iterable)customSoundRegistry.sounds()));
               if (soundRegistry != null) {
                  break block;
               }
            }

            soundRegistry = customSoundRegistry;
         }

         soundRegistry.addTo(this.resourcePack);
      }

      if (VersionUtil.INSTANCE.below("1.21.6")) {
         (new JukeboxPlayableDatapack()).createDatapack();
      }

   }

   private final void importRequiredPack() {
      PackGenerator packGenerator = this;

      Object obj2;
      try {
         block: {
            PackGenerator packGenerator2 = packGenerator;
            int value = 0;
            NexoPackReader nexoPackReader = NexoPackReader.INSTANCE;
            File[] listFiles = externalPacks.listFiles();
            if (listFiles != null) {
               File[] files = listFiles;
               NexoPackReader nexoPackReader2 = nexoPackReader;
               int value2 = 0;
               int value3 = files.length;

               Object obj;
               while(true) {
                  if (value2 >= value3) {
                     obj = null;
                     break;
                  }

                  Object element = files[value2];
                  int value4 = 0;
                  String name = ((File)element).getName();
                  if (StringsKt.startsWith(name, "RequiredPack_", false)) {
                     obj = element;
                     break;
                  }

                  ++value2;
               }

               if (obj != null) {
                  ResourcePack requiredPack = nexoPackReader2.readFile((File)obj, (PackFormat)null);
                  Collection items = VanillaResourcePack.INSTANCE.getResourcePack().items();
                  Iterable iterable = (Iterable)items;

                  for(Object obj3 : iterable) {
                     Item it = (Item)obj3;
                     value3 = 0;
                     requiredPack.removeItem(it.key());
                  }

                  NexoPack.mergePack(packGenerator2.resourcePack, requiredPack);
                  obj2 = ResultShim.ok(Unit.INSTANCE);
                  break block;
               }
            }

            return;
         }
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Throwable throwable = ResultShim.exceptionOrNull(obj2);
      if (throwable != null) {
         Throwable it = throwable;
         int value5 = 0;
         if (!Settings.DEBUG.toBool()) {
            String message = it.getMessage();
            Logs.logError(message, false);
         } else {
            it.printStackTrace();
         }
      }

   }

   private final void importDefaultPack() {
      File[] packFiles = externalPacks.listFiles();
      if (packFiles == null) {
         return;
      }

      for (File candidate : packFiles) {
         if (!candidate.getName().startsWith("DefaultPack_")) {
            continue;
         }

         Logs.logInfo("Importing DefaultPack...");
         try {
            NexoPack.mergePack(this.resourcePack, NexoPackReader.INSTANCE.readFile(candidate, (PackFormat)null));
         } catch (Throwable error) {
            Logs.logError("Failed to read Nexo's DefaultPack...", false);
            logImportFailure(error, true);
         }
         return;
      }
   }

   private final void importExternalPacks() {
      File[] packFiles = PackGenerator.externalPacks.listFiles();
      if (packFiles == null) {
         return;
      }

      List<String> externalOrder = Settings.PACK_IMPORT_EXTERNAL_PACK_ORDER.toStringList();
      Comparator<File> order = Comparator.<File>comparingInt(file -> importExternalPacks$lambda$0(externalOrder, file)).thenComparing(File::getName);
      Arrays.sort(packFiles, order);

      for (File file : packFiles) {
         if (defaultRegex.matches(file.getName())) {
            continue;
         }

         if (!file.isDirectory() && !file.getName().endsWith(".zip")) {
            Logs.logError("Skipping unknown file " + file.getName() + " in imports folder", false);
            Logs.logError("File is neither a directory nor a zip file", false);
            continue;
         }

         Logs.logInfo("Importing external-pack <aqua>" + file.getName() + "</aqua>...");
         try {
            NexoPack.mergePack(this.resourcePack, NexoPackReader.INSTANCE.readFile(file, (PackFormat)null));
         } catch (Throwable error) {
            Logs.logError("Failed to read " + file.getPath() + " to a ResourcePack...", false);
            if (Settings.DEBUG.toBool()) {
               error.printStackTrace();
            } else {
               String message = error.getMessage();
               Logs.logError(message, false);
            }
         }
      }
   }

   private final void importModelEnginePack() {
      if (!PluginUtils.INSTANCE.isModelEngineEnabled()) {
         return;
      }

      File dataFolder = ModelEngineAPI.getAPI().getDataFolder();
      File megPack = FileUtilsKt.resolve(dataFolder, new String[]{"resource pack.zip"});
      if (!megPack.exists()) {
         megPack = FileUtilsKt.resolve(dataFolder, new String[]{"resource pack"});
         if (!megPack.exists()) {
            Logs.logWarn("Could not find ModelEngine ZIP-resourcepack...", false);
            return;
         }
      }

      try {
         ResourcePack megResourcePack = NexoPackReader.INSTANCE.readFile(megPack, (PackFormat)null);
         for (Model model : megResourcePack.models()) {
            this.packObfuscator.getSkippedKeys().add(model.key());
         }

         megResourcePack.icon((Writable)null);
         NexoPack.mergePack(this.resourcePack, megResourcePack);
      } catch (Throwable error) {
         Logs.logError("Failed to read ModelEngine-ResourcePack...", false);
         if (Settings.DEBUG.toBool()) {
            Logs.logError(ExceptionsKt.stackTraceToString(error), false);
         } else {
            String message = error.getMessage();
            Logs.logWarn(message, false);
         }
         return;
      }

      Logs.logSuccess("Imported ModelEngine pack successfully!", false);
   }

   private final void parseLanguageFiles() {
      Map map3 = NexoYamlKt.childSections((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().getLanguages());

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map3.entrySet()) {
         ConfigurationSection section;
         Key key;
         Map map;
         block: {
            int value2 = 0;
            String langKey = (String)element.getKey();
            section = (ConfigurationSection)element.getValue();
            String str = langKey.toLowerCase(Locale.ROOT);
            Key key2 = Key.key(str);
            key = key2;
            Language language2 = this.resourcePack.language(key);
            if (language2 != null) {
               map = language2.translations();
               if (map != null) {
                  break block;
               }
            }

            map = (Map)(new LinkedHashMap());
         }

         Map language = map;
         map = section.getValues(true);
         Map map4 = map;
         Collection destination = (Collection)(new ArrayList());

         for(Map.Entry entry2 : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map4.entrySet()) {
            int value3 = 0;
            int value4 = 0;
            String str2 = (String)entry2.getKey();
            Object obj = entry2.getValue();
            Pair pair = obj instanceof String ? TuplesKt.to(str2, obj) : null;
            if (pair != null) {
               Object it = pair;
               int value5 = 0;
               destination.add(it);
            }
         }

         map4 = MapsKt.toMap((Iterable)((List)destination));
         language.putAll(map4);
         this.resourcePack.language(Language.language(key, language));
      }

      this.parseGlobalLanguage();
      Collection languages = this.resourcePack.languages();
      Iterable iterable = (Iterable)CollectionsKt.toList((Iterable)languages);

      for(Object obj2 : iterable) {
         Language language = (Language)obj2;
         int value6 = 0;
         Iterable iterable2 = (Iterable)(new LinkedHashSet((Collection)language.translations().entrySet()));

         for(Object obj3 : iterable2) {
            Map.Entry entry = (Map.Entry)obj3;
            int value7 = 0;
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            Map translations = language.translations();
            Map map2 = translations;
            String str3 = this.escapeMenuLangKeys.contains(key) ? AdventureUtils.parseLegacyThroughMiniMessage(value) : value;
            map2.put(key, str3);
            language.translations().remove("DO_NOT_ALTER_THIS_LINE");
         }

         this.resourcePack.language(language);
      }

   }

   private final void parseGlobalLanguage() {
      Language language2 = this.resourcePack.language(Key.key("global"));
      if (language2 != null) {
         Language globalLanguage = language2;
         Logs.logInfo("Converting global lang file to individual language files...");
         Iterable vanillaLangKeys = (Iterable)VanillaResourcePack.INSTANCE.getVanillaLangKeys();

         for(Object element : vanillaLangKeys) {
            Key langKey = (Key)element;
            int value2 = 0;
            language2 = this.resourcePack.language(langKey);
            if (language2 == null) {
               language2 = Language.language(langKey, MapsKt.emptyMap());
            }

            Language language = language2;
            LinkedHashMap newTranslations = new LinkedHashMap(language.translations());
            Iterable iterable = (Iterable)(new LinkedHashSet((Collection)globalLanguage.translations().entrySet()));

            for(Object obj : iterable) {
               Map.Entry entry = (Map.Entry)obj;
               int value3 = 0;
               String key = (String)entry.getKey();
               String value = (String)entry.getValue();
               newTranslations.putIfAbsent(key, value);
            }

            this.resourcePack.language(Language.language(langKey, (Map)newTranslations));
         }

         this.resourcePack.removeLanguage(Key.key("global"));
      }
   }

   private final void removeExcludedFileExtensions() {
      Iterable toStringList = (Iterable)Settings.PACK_EXCLUDED_FILE_EXTENSIONS.toStringList();
      Collection destination = (Collection)(new ArrayList(CollectionsKt.collectionSizeOrDefault(toStringList, 10)));

      for(Object item : toStringList) {
         String it = (String)item;
         int value = 0;
         destination.add(StringUtilsKt.prependIfMissing(it, "."));
      }

      toStringList = (Iterable)((List)destination);
      destination = (Collection)(new ArrayList());

      for(Object element : toStringList) {
         String it = (String)element;
         int value2 = 0;
         if (!ignoredExtensions.contains(it)) {
            destination.add(element);
         }
      }

      List extensions = (List)destination;
      if (!extensions.isEmpty()) {
         Collection overlays = this.resourcePack.overlays();
         toStringList = (Iterable)CollectionsKt.plus(overlays, this.resourcePack);

         for(Object obj : toStringList) {
            ResourceContainer container = (ResourceContainer)obj;
            int value3 = 0;
            Iterable iterable = (Iterable)CollectionsKt.toMutableSet((Iterable)container.unknownFiles().keySet());

            for(Object obj2 : iterable) {
               String key = (String)obj2;
               int value4 = 0;
               Iterable iterable2 = (Iterable)extensions;
               String str = key;
               boolean flag;
               if (iterable2 instanceof Collection && ((Collection)iterable2).isEmpty()) {
                  flag = false;
               } else {
                  Iterator iterator = iterable2.iterator();

                  while(true) {
                     if (!iterator.hasNext()) {
                        flag = false;
                        break;
                     }

                     Object obj3 = iterator.next();
                     String p0 = (String)obj3;
                     int value5 = 0;
                     if (StringsKt.endsWith(str, p0, false)) {
                        flag = true;
                        break;
                     }
                  }
               }

               if (flag) {
                  container.removeUnknownFile(key);
               }
            }
         }

      }
   }

   private static final void sortModelOverrides$sort(ResourceContainer container) {
      Collection models = container.models();
      Iterable iterable2 = (Iterable)CollectionsKt.toMutableList(models);

      for(Object element : iterable2) {
         Model model = (Model)element;
         int value = 0;
         Iterable iterable = (Iterable)(new LinkedHashSet((Collection)model.overrides()));
         Comparator comparator = Comparator.comparingInt((a0_ -> PackGenerator.sortModelOverrides$sort$lambda$0$0((ItemOverride)a0_)));
         List sortedOverrides = CollectionsKt.sortedWith(iterable, comparator);
         model.toBuilder().overrides(sortedOverrides).build().addTo(container);
      }

   }

   private static final void generatePack$lambda$0(PackGenerator packGenerator, File packFolder) {
      try {
         Logs.logInfo("Generating resourcepack...");
         packGenerator.importRequiredPack();
         runStep(() -> {
            if (Settings.PACK_IMPORT_DEFAULT.toBool()) {
               packGenerator.importDefaultPack();
            }
         });
         runStep(() -> {
            if (Settings.PACK_IMPORT_EXTERNAL.toBool()) {
               packGenerator.importExternalPacks();
            }
         });
         runStep(() -> {
            if (Settings.PACK_IMPORT_MODEL_ENGINE.toBool()) {
               packGenerator.importModelEnginePack();
            }
         });
         runStep(packGenerator::importPacksFromLocations);
         runStep(packGenerator::importPacksFromUrls);
         runStep(packGenerator::applyPackMeta);

         try {
            NexoPack.mergePack(packGenerator.resourcePack, NexoPackReader.INSTANCE.readFile(packFolder, (PackFormat)null));
         } catch (Throwable error) {
            Logs.logError("Failed to read Nexo/pack/assets-folder to a ResourcePack", false);
            logImportFailure(error, true);
         }

         runStep(packGenerator::generatePackContents);
      } catch (Throwable error) {
         Logs.logError("Failed to generate ResourcePack...", false);
         logImportFailure(error, false);
      }

   }

   /** Runs one generation step; a failure is printed but does not abort the remaining steps. */
   private static void runStep(Runnable step) {
      try {
         step.run();
      } catch (Throwable error) {
         UtilsKt.printOnFailure(ResultShim.failure(error), false);
      }
   }

   /** Prints the stack trace when debugging, otherwise just the message (as a warning if {@code warn}). */
   private static void logImportFailure(Throwable error, boolean warn) {
      if (Settings.DEBUG.toBool()) {
         Logs.logWarn(ExceptionsKt.stackTraceToString(error), false);
      } else {
         String message = error.getMessage();
         if (warn) {
            Logs.logError(message, false);
         } else {
            Logs.logWarn(message, false);
         }
      }
   }

   private void importPacksFromLocations() {
      for (String path : Settings.PACK_IMPORT_FROM_LOCATION.toStringListOrSingle()) {
         File file = FileUtilsKt.resolve(FileUtils.INSTANCE.getServerFolder(), new String[]{path});
         try {
            Logs.logInfo("Importing pack from <aqua>" + file.getPath());
            NexoPack.mergePack(this.resourcePack, NexoPackReader.INSTANCE.readFile(file, (PackFormat)null));
         } catch (Throwable error) {
            Logs.logError("Failed to read " + file.getPath() + " to a ResourcePack", false);
            if (Settings.DEBUG.toBool()) {
               Logs.logError(ExceptionsKt.stackTraceToString(error), false);
            } else {
               String message = error.getMessage();
               Logs.logError(message, false);
            }
         }
      }
   }

   private void importPacksFromUrls() {
      for (String url : Settings.PACK_IMPORT_FROM_URL.toStringListOrSingle()) {
         try {
            Logs.logInfo("Importing pack from <aqua>" + url);
            ResourcePack imported;
            try (InputStream stream = URI.create(url).toURL().openStream()) {
               imported = NexoPackReader.INSTANCE.readFromInputStream(stream);
            }
            NexoPack.mergePack(this.resourcePack, imported);
         } catch (Throwable error) {
            Logs.logError("Failed to read " + url + " to a ResourcePack", false);
            logImportFailure(error, true);
         }
      }
   }

   private void applyPackMeta() {
      FormatVersion packFormat = PluginUtils.INSTANCE.isViaBackwardsEnabled() && VersionUtil.atleast("1.21.11") ? FormatVersion.of(64) : NMSHandlers.handler().resourcepackFormat();
      PackFormat format = PackFormat.format(packFormat, FormatVersion.of(199));
      this.resourcePack.packMeta(format, AdventureUtilsKt.deserialize("Nexo's ResourcePack"));
   }

   private void generatePackContents() {
      CustomBlockFactory customBlockFactory = findFactory(CustomBlockFactory.class);
      if (customBlockFactory != null) {
         customBlockFactory.blockStates(this.resourcePack);
         customBlockFactory.soundRegistries(this.resourcePack);
      }

      FurnitureFactory furnitureFactory = findFactory(FurnitureFactory.class);
      if (furnitureFactory != null) {
         furnitureFactory.soundRegistries(this.resourcePack);
      }

      ItemModelParser.INSTANCE.startParsing(this.resourcePack);
      this.bbModelGenerator.parseBBModels();
      this.modelGenerator.generateModels();
      this.textureMerger.stitchTextures();
      this.gifGenerator.generateGifFiles();
      this.addGlyphFiles();
      this.addShiftProvider();
      this.addSoundFile();
      this.parseLanguageFiles();
      if (this.trimsCustomArmor != null) {
         this.trimsCustomArmor.generateTrimAssets(this.resourcePack);
      }

      if (this.componentCustomArmor != null) {
         this.componentCustomArmor.generatePackFiles();
      }

      (new ScoreboardBackground(this.resourcePack)).generateFiles();
      Overlays.INSTANCE.addToResourcepack(this.resourcePack);
      this.removeExcludedFileExtensions();
      this.sortModelOverrides();
   }

   private static <T extends MechanicFactory> T findFactory(Class<T> type) {
      for (MechanicFactory factory : MechanicsManager.INSTANCE.getFACTORIES_BY_MECHANIC_ID().values()) {
         if (type.isInstance(factory)) {
            return type.cast(factory);
         }
      }
      return null;
   }

   private static final void generatePack$lambda$1$0$0(CompletableFuture completableFuture, PackGenerator packGenerator, WrappedTask it) {
      try {
         (new NexoPostPackGenerateEvent(packGenerator.resourcePack)).callEvent();
         completableFuture.complete(null);
      } catch (Throwable error) {
         completableFuture.completeExceptionally(error);
      }

   }

   private static final CompletionStage generatePack$lambda$1(PackGenerator packGenerator, Void it) {
        CompletableFuture completableFuture;
        CompletableFuture completableFuture2 = completableFuture = new CompletableFuture();
        boolean bl = false;
        SchedulerUtils.INSTANCE.getFoliaScheduler().runNextTick(arg_0 -> PackGenerator.generatePack$lambda$1$0$0(completableFuture2, packGenerator, arg_0));
        return completableFuture;
    }

   private static final void generatePack$lambda$3(PackGenerator packGenerator, boolean sendToOnline) {
      try {
      try {
         packGenerator.packValidator.validatePack();
         packGenerator.modernVersionPatcher.convertResources();
         packGenerator.atlasGenerator.generateAtlasFile();
         packGenerator.resourcePack.items().removeIf(item -> PackGenerator.generatePack$lambda$3$0$0(packGenerator, item));
         packGenerator.resourcePack.models().removeIf(model -> PackGenerator.generatePack$lambda$3$0$2(packGenerator, model));
         packGenerator.checkDefaultOversizedInGui();
         String packHash = NexoPackWriter.INSTANCE.build(packGenerator.resourcePack).hash();
         boolean needsHash = !packGenerator.packObfuscator.getObfuscationType().isNone() || Settings.PACK_USE_PACKSQUASH.toBool();
         String initialHash = needsHash ? packHash : "";
         packGenerator.packObfuscator.obfuscatePack(initialHash);

         List<File> outputFiles = new ArrayList<>();
         for (String outputPath : Settings.PACK_OUTPUT_PATH.toStringListOrSingle()) {
            if (!outputPath.isEmpty()) {
               outputFiles.add(new File(outputPath));
            }
         }

         if (Settings.PACK_USE_PACKSQUASH.toBool() && packGenerator.packSquasher.squashPack(initialHash)) {
            File squashed = FileUtilsKt.resolve(packGenerator.packSquasher.getPackSquashCache(), new String[]{initialHash + ".zip"});
            for (File outputFile : outputFiles) {
               FilesKt.copyRecursively(squashed, outputFile, true, (java.io.File f_, java.io.IOException e_) -> { throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e_); });
            }
            packGenerator.builtPack = BuiltResourcePack.of(Writable.file(squashed), FileUtils.INSTANCE.getSha1Hash(squashed));
         } else {
            if (!outputFiles.isEmpty()) {
               SchedulerUtils.INSTANCE.getFoliaScheduler().runAsync((WrappedTask task) -> PackGenerator.generatePack$lambda$3$0$7(outputFiles, packGenerator, task));
            }
            packGenerator.builtPack = NexoPackWriter.INSTANCE.build(packGenerator.resourcePack);
         }

         for (File outputFile : outputFiles) {
            if (!Intrinsics.areEqual(outputFile.getPath(), Settings.PACK_OUTPUT_PATH.getDefaultValue().getValue())) {
               Logs.logInfo("Copied generated ResourcePack to <aqua>" + outputFile.getPath(), true);
            }
         }

         Logs.logSuccess("Finished generating resourcepack!", true);
         NexoPlugin.Companion.instance().multipackManager().reload();
         SchedulerUtils.INSTANCE.getFoliaScheduler().runNextTick((WrappedTask task) -> PackGenerator.generatePack$lambda$3$0$10(packGenerator, task));
         NexoPackServer packServer = NexoPlugin.Companion.instance().packServer();
         packServer.uploadPack().thenRun(() -> PackGenerator.generatePack$lambda$3$0$11(packServer, initialHash, packGenerator, sendToOnline));
         NexoTranslator.Companion.registerTranslations();
      } catch (Throwable error) {
         error.printStackTrace();
      }

      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   private static final boolean generatePack$lambda$3$0$0(PackGenerator packGenerator, Item it) {
      return packGenerator.modernVersionPatcher.getStandardItemModels().containsValue(it);
   }

   private static final boolean generatePack$lambda$3$0$2(PackGenerator packGenerator, Model it) {
      return packGenerator.modernVersionPatcher.getStandardModels().containsValue(it);
   }

   private static final void generatePack$lambda$3$0$7(List outputFiles, PackGenerator packGenerator, WrappedTask it) {
      File outputFile = (File)CollectionsKt.first(outputFiles);
      NexoPackWriter.INSTANCE.writeToFile(outputFile, packGenerator.resourcePack);
      Iterable iterable = (Iterable)CollectionsKt.drop((Iterable)outputFiles, 1);

      for(Object element : iterable) {
         File it_2 = (File)element;
         int value = 0;
         FilesKt.copyRecursively(outputFile, it_2, true, (kotlin.jvm.functions.Function2<java.io.File,java.io.IOException,kotlin.io.OnErrorAction>)((java.io.File f_, java.io.IOException e_) -> kotlin.io.OnErrorAction.SKIP));
      }

   }

   private static final void generatePack$lambda$3$0$10(PackGenerator packGenerator, WrappedTask it) {
      EventUtils eventUtils = EventUtils.INSTANCE;
      BuiltResourcePack builtResourcePack = packGenerator.builtPack;
      eventUtils.call(new NexoPackFinishedEvent(builtResourcePack));
   }

   private static final void generatePack$lambda$3$0$11$0(NexoPackServer packServer, PackGenerator packGenerator, WrappedTask it) {
      EventUtils eventUtils = EventUtils.INSTANCE;
      NexoPackUploadEvent nexoPackUploadEvent;
      String hash = packServer.hash();
      if (hash == null) {
         BuiltResourcePack builtResourcePack = packGenerator.builtPack;
         hash = builtResourcePack.hash();
      }

      nexoPackUploadEvent = new NexoPackUploadEvent(hash, packServer.packUrl());
      eventUtils.call(nexoPackUploadEvent);

   }

   private static final void generatePack$lambda$3$0$11(NexoPackServer packServer, String initialHash, PackGenerator packGenerator, boolean sendToOnline) {
        SchedulerUtils.INSTANCE.getFoliaScheduler().runNextTick(arg_0 -> PackGenerator.generatePack$lambda$3$0$11$0(packServer, packGenerator, arg_0));
        BuiltResourcePack builtResourcePack = packGenerator.builtPack;
        Intrinsics.checkNotNull((Object)builtResourcePack);
        String string = builtResourcePack.hash();
        Intrinsics.checkNotNullExpressionValue((Object)string, "hash(...)");
        ProxyHelpers.INSTANCE.handleNexoProxyData(packServer, initialHash, string);
        Collection collection = Bukkit.getServer().getOnlinePlayers();
        Intrinsics.checkNotNullExpressionValue((Object)collection, "getOnlinePlayers(...)");
        if (!collection.isEmpty()) {
            ProxyHelpers.INSTANCE.sendProxyPackData();
        }
        if (sendToOnline) {
            Collection collection2 = Bukkit.getOnlinePlayers();
            Intrinsics.checkNotNullExpressionValue((Object)collection2, "getOnlinePlayers(...)");
            Iterable iterable = collection2;
            for (Object element : iterable) {
                Player p0 = (Player)element;
                boolean bl = false;
                packServer.sendPack(p0);
            }
        }
    }

   private static final int importExternalPacks$lambda$0(List<String> externalOrder, File file) {
      int index = externalOrder.indexOf(file.getName());
      return index >= 0 ? index : Integer.MAX_VALUE;
   }

   private static final int sortModelOverrides$sort$lambda$0$0(ItemOverride o) {
      java.lang.Object obj;
      List predicate = o.predicate();
      Iterable iterable = (Iterable)predicate;
      Iterator iterator = iterable.iterator();

      while(true) {
         if (iterator.hasNext()) {
            Object element = iterator.next();
            ItemPredicate p = (ItemPredicate)element;
            int value = 0;
            if (!Intrinsics.areEqual(p.name(), "custom_model_data")) {
               continue;
            }

            obj = element;
            break;
         }

         obj = null;
         break;
      }

      Integer number = StringsKt.toIntOrNull(String.valueOf((ItemPredicate)obj != null ? ((ItemPredicate)obj).value() : null));
      return number != null ? number : 0;
   }

   private static final int sortModelOverrides$sort$lambda$0$1(Function1 tmp0, Object p0) {
      return ((Number)tmp0.invoke(p0)).intValue();
   }

   static {
      File dataFolder = NexoPlugin.Companion.instance().getDataFolder();
      String[] strs = new String[]{"pack/external_packs"};
      externalPacks = FileUtilsKt.resolve(dataFolder, strs);
      dataFolder = NexoPlugin.Companion.instance().getDataFolder();
      strs = new String[]{"pack/assets"};
      assetsFolder = FileUtilsKt.resolve(dataFolder, strs);
      strs = new String[]{".json", ".png", ".mcmeta"};
      ignoredExtensions = new LinkedHashSet((Collection)CollectionsKt.mutableListOf(strs));
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\r\u001a\u00020\u000eJ\b\u0010\u000f\u001a\u00020\u000eH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u000e\u0010\f\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\u0010\u001a\u0010\u0012\f\u0012\n \u0013*\u0004\u0018\u00010\u00120\u00120\u0011X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0014"},
      d2 = {"Lcom/nexomc/nexo/pack/PackGenerator$Companion;", "", "<init>", "()V", "defaultRegex", "Lkotlin/text/Regex;", "externalPacks", "Ljava/io/File;", "getExternalPacks", "()Ljava/io/File;", "setExternalPacks", "(Ljava/io/File;)V", "assetsFolder", "stopPackGeneration", "", "generateDefaultPaths", "ignoredExtensions", "Ljava/util/LinkedHashSet;", "", "kotlin.jvm.PlatformType", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      @NotNull
      public final File getExternalPacks() {
         return PackGenerator.externalPacks;
      }

      public final void setExternalPacks(@NotNull File file) {
         PackGenerator.externalPacks = file;
      }

      public final void stopPackGeneration() {
         Companion companion = this;

         try {
            Companion companion2 = companion;
            int value = 0;
            NexoPackSquash.Companion.stopPackSquash();
            PackGenerator it = NexoPlugin.Companion.instance().packGenerator();
            int value2 = 0;
            CompletableFuture completableFuture = it.getPackGenFuture();
            if (completableFuture != null) {
               CompletableFuture future = completableFuture;
               int value3 = 0;
               if (!future.isDone()) {
                  future.cancel(true);
                  Logs.logError("Cancelling generation of Nexo ResourcePack...", false);
               }
            }

            it.packGenFuture = null;
            Object obj = ResultShim.ok(Unit.INSTANCE);
         } catch (Throwable e) {
            Object obj2 = ResultShim.ok(ResultKt.createFailure(e));
         }

      }

      private final void generateDefaultPaths() {
         this.getExternalPacks().mkdirs();
         File file = PackGenerator.assetsFolder;
         String[] strs = new String[]{"minecraft", "textures"};
         FileUtilsKt.resolve(file, strs).mkdirs();
         file = PackGenerator.assetsFolder;
         strs = new String[]{"minecraft", "models"};
         FileUtilsKt.resolve(file, strs).mkdirs();
         file = PackGenerator.assetsFolder;
         strs = new String[]{"minecraft", "sounds"};
         FileUtilsKt.resolve(file, strs).mkdirs();
         file = PackGenerator.assetsFolder;
         strs = new String[]{"minecraft", "font"};
         FileUtilsKt.resolve(file, strs).mkdirs();
         file = PackGenerator.assetsFolder;
         strs = new String[]{"minecraft", "lang"};
         FileUtilsKt.resolve(file, strs).mkdirs();
         ResourceManager.INSTANCE.extractConfigsInFolder("pack/assets/minecraft/lang", "json");
      }
   }
}
