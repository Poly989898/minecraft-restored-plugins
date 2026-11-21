package com.nexomc.nexo.paintings;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.utils.KeyUtils;
import com.nexomc.nexo.utils.NexoYamlKt;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import io.papermc.paper.registry.RegistryKey;
import io.papermc.paper.registry.TypedKey;
import io.papermc.paper.registry.data.PaintingVariantRegistryEntry;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.PaintingVariantKeys;
import io.papermc.paper.registry.tag.TagKey;
import io.papermc.paper.tag.PostFlattenTagRegistrar;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.ranges.ClosedRange;
import kotlin.ranges.IntRange;
import kotlin.ranges.RangesKt;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Art;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007b\u0002\b\b¨\u0006\t"},
   d2 = {"Lcom/nexomc/nexo/paintings/NexoPaintings;", "", "<init>", "()V", "registerCustomPaintings", "", "context", "Lio/papermc/paper/plugin/bootstrap/BootstrapContext;", "Lkotlin/jvm/JvmStatic;", "com.nexomc:core"}
)
public final class NexoPaintings {
   @NotNull
   public static final NexoPaintings INSTANCE = new NexoPaintings();

   private NexoPaintings() {
   }

   @JvmStatic
   public static final void registerCustomPaintings(@NotNull final BootstrapContext context) {
      final List randomPlacePaintings = (List)(new ArrayList());
      final NexoPaintings nexoPaintings = INSTANCE;

      Object obj;
      try {
         int value = 0;
         Path dataDirectory = context.getDataDirectory();
         Path path = dataDirectory;
         LinkOption[] linkOptions = new LinkOption[0];
         if (!Files.notExists(path, (LinkOption[])Arrays.copyOf(linkOptions, linkOptions.length))) {
            File toFile = context.getDataDirectory().resolve("paintings.yml").toFile();
            int value2 = 0;
            toFile.createNewFile();
            File paintingFile = toFile;

            Object obj2;
            try {
               int value3 = 0;
               obj2 = ResultShim.ok(YamlConfiguration.loadConfiguration(paintingFile));
            } catch (Throwable e) {
               obj2 = ResultShim.ok(ResultKt.createFailure(e));
            }

            YamlConfiguration yamlConfiguration = (YamlConfiguration)(ResultShim.isFailure(obj2) ? null : obj2);
            if (yamlConfiguration != null) {
               YamlConfiguration paintingsYaml = yamlConfiguration;
               ConfigurationSection configurationSection = NexoYamlKt.section((ConfigurationSection)paintingsYaml, "paintings");
               if (configurationSection == null) {
                  return;
               }

               final ConfigurationSection paintings = configurationSection;
               context.getLifecycleManager().registerEventHandler(RegistryEvents.PAINTING_VARIANT.compose().newHandler(new LifecycleEventHandler<RegistryComposeEvent<Art, PaintingVariantRegistryEntry.Builder>>() {
                  net.kyori.adventure.key.Key var29;
                  public final void run(RegistryComposeEvent<Art, PaintingVariantRegistryEntry.Builder> handler) {
                     Map map = NexoYamlKt.childSections(paintings);
                     List list = randomPlacePaintings;
                     BootstrapContext bootstrapContext = context;

                     for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
                        int value4 = 0;
                        String keyId = (String)element.getKey();
                        ConfigurationSection section = (ConfigurationSection)element.getValue();
                        Key key2 = Key.key(keyId);
                        Key key = key2;
                        Component richMessage = section.getRichMessage("author");
                        if (richMessage == null) {
                           TextComponent textComponent = Component.text("boy0000");
                           richMessage = (Component)textComponent;
                        }

                        Component author = richMessage;
                        richMessage = section.getRichMessage("title");
                        if (richMessage == null) {
                           TextComponent textComponent2 = Component.text(keyId);
                           richMessage = (Component)textComponent2;
                        }

                        Component title;
                        block: {
                           title = richMessage;
                           Key key3 = NexoYamlKt.key(section, "asset_id");
                           if (key3 != null) {
                              var29 = KeyUtils.INSTANCE.removeSuffix(key3, ".png");
                              if (var29 != null) {
                                 break block;
                              }
                           }

                           var29 = key;
                        }

                        Key assetId = var29;
                        TypedKey typedKey = PaintingVariantKeys.create(key);
                        TypedKey variantKey = typedKey;
                        if (section.getBoolean("random_place")) {
                           TypedKey typedKey2 = TypedKey.create(RegistryKey.PAINTING_VARIANT, key);
                           list.add(typedKey2);
                        }

                        Object obj3;
                        try {
                           int value5 = 0;
                           handler.registry().register(variantKey, b -> run$lambda$0$0$0(author, title, assetId, section, b));
                           obj3 = ResultShim.ok(Unit.INSTANCE);
                        } catch (Throwable ex) {
                           obj3 = ResultShim.ok(ResultKt.createFailure(ex));
                        }

                        Throwable throwable = ResultShim.exceptionOrNull(obj3);
                        if (throwable != null) {
                           Throwable it = throwable;
                           int value6 = 0;
                           bootstrapContext.getLogger().warn("Failed to register Custom Painting " + key, it);
                        }
                     }

                  }

                  private static final Unit run$lambda$0$0$0(Component component, Component component2, Key key4, ConfigurationSection configurationSection2, PaintingVariantRegistryEntry.Builder builder) {
                     builder.author(component).title(component2).assetId(key4).width(RangesKt.coerceIn(configurationSection2.getInt("width"), 1, 16)).height(RangesKt.coerceIn(configurationSection2.getInt("height"), 1, 16));
                     return Unit.INSTANCE;
                  }

                  private static final void run$lambda$0$0$1(Function1 tmp0, Object p0) {
                     tmp0.invoke(p0);
                  }
               }));
            }
         }

         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable ex2) {
         obj = ResultShim.ok(ResultKt.createFailure(ex2));
      }

      if (ResultShim.exceptionOrNull(obj) != null) {
         int value7 = 0;
         context.getLogger().error("Failed to load paintings...");
      }

      obj = INSTANCE;

      try {
         int it = 0;
         context.getLifecycleManager().registerEventHandler((LifecycleEventType)LifecycleEvents.TAGS.postFlatten(RegistryKey.PAINTING_VARIANT), new LifecycleEventHandler<ReloadableRegistrarEvent<PostFlattenTagRegistrar<Art>>>() {
            public final void run(ReloadableRegistrarEvent<PostFlattenTagRegistrar<Art>> event) {
               ((PostFlattenTagRegistrar)event.registrar()).addToTag(TagKey.create(RegistryKey.PAINTING_VARIANT, Key.key("placeable")), (Collection)randomPlacePaintings);
            }
         });
         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable ex3) {
         obj = ResultShim.ok(ResultKt.createFailure(ex3));
      }

      Throwable throwable2 = ResultShim.exceptionOrNull(obj);
      if (throwable2 != null) {
         Throwable it = throwable2;
         int value8 = 0;
         context.getLogger().error("Failed to load paintings...", it);
      }

   }
}
