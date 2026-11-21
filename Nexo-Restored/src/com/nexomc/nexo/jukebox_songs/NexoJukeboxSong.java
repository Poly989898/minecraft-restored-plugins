package com.nexomc.nexo.jukebox_songs;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.utils.DurationUtilsKt;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.jukebox_playable.JukeboxPlayable;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler;
import io.papermc.paper.registry.RegistryBuilderFactory;
import io.papermc.paper.registry.data.JukeboxSongRegistryEntry;
import io.papermc.paper.registry.data.SoundEventRegistryEntry;
import io.papermc.paper.registry.event.RegistryComposeEvent;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.JukeboxSongKeys;
import java.io.File;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.time.DurationUnit;
import net.kyori.adventure.key.Key;
import org.bukkit.JukeboxSong;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007b\u0002\b\b¨\u0006\t"},
   d2 = {"Lcom/nexomc/nexo/jukebox_songs/NexoJukeboxSong;", "", "<init>", "()V", "registerJukeboxSongs", "", "context", "Lio/papermc/paper/plugin/bootstrap/BootstrapContext;", "Lkotlin/jvm/JvmStatic;", "com.nexomc:core"}
)
public final class NexoJukeboxSong {
   @NotNull
   public static final NexoJukeboxSong INSTANCE = new NexoJukeboxSong();

   private NexoJukeboxSong() {
   }

   @JvmStatic
   public static final void registerJukeboxSongs(@NotNull final BootstrapContext context) {
      final NexoJukeboxSong nexoJukeboxSong = INSTANCE;

      try {
         int value = 0;
         File toFile = context.getDataDirectory().resolve("sounds.yml").toFile();
         int value2 = 0;
         toFile.createNewFile();
         File soundsFile = toFile;

         Object obj2;
         try {
            int value3 = 0;
            obj2 = ResultShim.ok(YamlConfiguration.loadConfiguration(soundsFile));
         } catch (Throwable e) {
            obj2 = ResultShim.ok(ResultKt.createFailure(e));
         }

         YamlConfiguration yamlConfiguration = (YamlConfiguration)(ResultShim.isFailure(obj2) ? null : obj2);
         if (yamlConfiguration != null) {
            YamlConfiguration soundsYaml = yamlConfiguration;
            Collection collection = (Collection)NexoYamlKt.sectionList((ConfigurationSection)soundsYaml, "sounds");
            if (collection.isEmpty()) {
               int value4 = 0;
               return;
            }

            final List sounds = (List)collection;
            context.getLifecycleManager().registerEventHandler(RegistryEvents.JUKEBOX_SONG.compose().newHandler(new LifecycleEventHandler<RegistryComposeEvent<JukeboxSong, JukeboxSongRegistryEntry.Builder>>() {
               public final void run(RegistryComposeEvent<JukeboxSong, JukeboxSongRegistryEntry.Builder> handler) {
                  Iterable iterable = (Iterable)sounds;
                  BootstrapContext bootstrapContext = context;

                  for(Object element : iterable) {
                     ConfigurationSection section = (ConfigurationSection)element;
                     int value5 = 0;
                     Key key2 = NexoYamlKt.key(section, "id");
                     if (key2 != null) {
                        Key key = key2;
                        ConfigurationSection configurationSection = NexoYamlKt.section(section, "jukebox_playable");
                        if (configurationSection != null) {
                           ConfigurationSection jukeboxSection = configurationSection;
                           JukeboxPlayable jukeboxPlayable = new JukeboxPlayable(jukeboxSection, key);

                           Object obj3;
                           try {
                              int value6 = 0;
                              handler.registry().register(JukeboxSongKeys.create(key), b -> run$lambda$0$0$0(jukeboxPlayable, key, b));
                              bootstrapContext.getLogger().error("Registered JukeboxSong for id " + key);
                              obj3 = ResultShim.ok(Unit.INSTANCE);
                           } catch (Throwable ex) {
                              obj3 = ResultShim.ok(ResultKt.createFailure(ex));
                           }

                           Throwable throwable = ResultShim.exceptionOrNull(obj3);
                           if (throwable != null) {
                              Throwable it = throwable;
                              int value7 = 0;
                              bootstrapContext.getLogger().warn("Failed to register JukeboxSong for id " + key, it);
                           }
                        }
                     }
                  }

               }

               private static final void run$lambda$0$0$0$0(Key key3, JukeboxPlayable jukeboxPlayable2, RegistryBuilderFactory it) {
                  ((SoundEventRegistryEntry.Builder)it.empty()).location(key3).fixedRange(jukeboxPlayable2.getRange());
               }

               private static final Unit run$lambda$0$0$0(JukeboxPlayable jukeboxPlayable3, Key key4, JukeboxSongRegistryEntry.Builder builder) {
                  builder.description(jukeboxPlayable3.getDescription()).comparatorOutput(jukeboxPlayable3.getComparatorOutput()).lengthInSeconds(DurationUtilsKt.toFloat_VtjQ1oo(jukeboxPlayable3.getDuration_UwyO8pc(), DurationUnit.SECONDS)).soundEvent(it -> run$lambda$0$0$0$0(key4, jukeboxPlayable3, it));
                  return Unit.INSTANCE;
               }

               private static final void run$lambda$0$0$1(Function1 tmp0, Object p0) {
                  tmp0.invoke(p0);
               }
            }));
         }

         Object obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable ex2) {
         Object obj4 = ResultShim.ok(ResultKt.createFailure(ex2));
      }

   }
}
