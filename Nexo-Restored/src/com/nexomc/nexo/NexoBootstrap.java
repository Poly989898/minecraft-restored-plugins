package com.nexomc.nexo;

import com.nexomc.nexo.commands.NexoCommand;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.handler.LifecycleEventHandler;
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent;
import java.util.List;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.dialog.NexoDialogs;
import com.nexomc.nexo.glyphs.GlyphTag;
import com.nexomc.nexo.glyphs.ShiftTag;
import com.nexomc.nexo.jukebox_songs.NexoJukeboxSong;
import com.nexomc.nexo.paintings.NexoPaintings;
import com.nexomc.nexo.tags.NexoTags;
import com.nexomc.nexo.utils.VersionUtil;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEventType;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import java.io.File;
import java.lang.reflect.Field;
import kotlin.ExceptionsKt;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.jvm.internal.DefaultConstructorMarker;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \f2\u00020\u0001:\u0001\fB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016J\u0010\u0010\b\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00020\nH\u0016J\u0010\u0010\u000b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0002¨\u0006\r"},
   d2 = {"Lcom/nexomc/nexo/NexoBootstrap;", "Lio/papermc/paper/plugin/bootstrap/PluginBootstrap;", "<init>", "()V", "bootstrap", "", "context", "Lio/papermc/paper/plugin/bootstrap/BootstrapContext;", "createPlugin", "Lorg/bukkit/plugin/java/JavaPlugin;", "Lio/papermc/paper/plugin/bootstrap/PluginProviderContext;", "injectNexoTagResolvers", "Companion", "com.nexomc:core"}
)
public final class NexoBootstrap implements PluginBootstrap {
   @NotNull
   public static final Companion Companion = new Companion();
   private static boolean bootsStrung;

   public void bootstrap(@NotNull BootstrapContext context) {
      Settings.Companion companion = Settings.Companion;
      File toFile = context.getDataDirectory().toFile();
      companion.initialize(toFile);
      NexoBootstrap nexoBootstrap = this;

      Object obj;
      try {
         NexoBootstrap nexoBootstrap2 = nexoBootstrap;
         int value = 0;
         NexoTags.registerTags(context);
         NexoPaintings.registerCustomPaintings(context);
         if (VersionUtil.atleast("1.21.6")) {
            NexoJukeboxSong.registerJukeboxSongs(context);
         }

         if (VersionUtil.atleast("1.21.7")) {
            NexoDialogs.registerDialogs(context);
         }

         bootsStrung = true;
         obj = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj = ResultShim.ok(ResultKt.createFailure(e));
      }

      Throwable throwable = ResultShim.exceptionOrNull(obj);
      if (throwable != null) {
         Throwable it = throwable;
         int value2 = 0;
         context.getLogger().error("Failed to bootstrap Nexo: " + it.getMessage());
         context.getLogger().error(ExceptionsKt.stackTraceToString(it));
      }

      nexoBootstrap = this;

      try {
         NexoBootstrap nexoBootstrap3 = nexoBootstrap;
         int it = 0;
         context.getLifecycleManager().registerEventHandler((LifecycleEventType)LifecycleEvents.COMMANDS, (LifecycleEventHandler<ReloadableRegistrarEvent<Commands>>)(commands -> {
               Commands registrar = (Commands)commands.registrar();
registrar.register(NexoCommand.INSTANCE.createCommand("nexo"), "Nexo command", List.of("nx", "n"));
            }));
         Object it_2 = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable ex) {
         obj = ResultShim.ok(ResultKt.createFailure(ex));
      }

   }

   @NotNull
   public JavaPlugin createPlugin(@NotNull PluginProviderContext context) {
      return new NexoPlugin();
   }

   private final void injectNexoTagResolvers(BootstrapContext context) {
      NexoBootstrap nexoBootstrap = this;

      Object obj2;
      try {
         NexoBootstrap nexoBootstrap2 = nexoBootstrap;
         int value = 0;
         Class instancesClass = Class.forName("net.kyori.adventure.text.minimessage.MiniMessageImpl$Instances");
         Field declaredField = instancesClass.getDeclaredField("INSTANCE");
         int value5 = 0;
         declaredField.setAccessible(true);
         Object miniMessageImpl = declaredField.get((Object)null);
         Field resolverField = miniMessageImpl.getClass().getDeclaredField("parser");
         int value2 = 0;
         resolverField.setAccessible(true);
         Object parser = resolverField.get(miniMessageImpl);
         Field field = parser.getClass().getDeclaredField("tagResolver");
         int value3 = 0;
         field.setAccessible(true);
         resolverField = field;
         Object obj = field.get(parser);
         TagResolver oldResolver = (TagResolver)obj;
         TagResolver[] tagResolvers = new TagResolver[]{oldResolver, GlyphTag.INSTANCE.getRESOLVER(), ShiftTag.INSTANCE.getRESOLVER()};
         TagResolver tagResolver = TagResolver.resolver(tagResolvers);
         TagResolver combinedResolver = tagResolver;
         resolverField.set(parser, combinedResolver);
         obj2 = ResultShim.ok(Unit.INSTANCE);
      } catch (Throwable e) {
         obj2 = ResultShim.ok(ResultKt.createFailure(e));
      }

      Throwable throwable = ResultShim.exceptionOrNull(obj2);
      if (throwable != null) {
         Throwable it = throwable;
         int value4 = 0;
         context.getLogger().error("Failed to edit MiniMessage resolver: " + it.getMessage());
         context.getLogger().error(ExceptionsKt.stackTraceToString(it));
      }

   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0005\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\t¨\u0006\n"},
      d2 = {"Lcom/nexomc/nexo/NexoBootstrap$Companion;", "", "<init>", "()V", "bootsStrung", "", "getBootsStrung", "()Z", "setBootsStrung", "(Z)V", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      public final boolean getBootsStrung() {
         return NexoBootstrap.bootsStrung;
      }

      public final void setBootsStrung(boolean flag) {
         NexoBootstrap.bootsStrung = flag;
      }
   }
}
