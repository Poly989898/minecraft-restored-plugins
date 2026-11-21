package com.nexomc.nexo.commands;

import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.configs.Message;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.dialog.NexoDialogs;
import com.nexomc.nexo.fonts.NexoTranslator;
import com.nexomc.nexo.items.ItemUpdater;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockSoundListener;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mechanics.furniture.IFurniturePacketManager;
import com.nexomc.nexo.mechanics.furniture.listeners.FurnitureSoundListener;
import com.nexomc.nexo.nms.IPacketHandler;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.pack.PackGenerator;
import com.nexomc.nexo.pack.server.NexoPackServer;
import com.nexomc.nexo.recipes.RecipesManager;
import com.nexomc.nexo.utils.AdventureUtils;
import com.nexomc.nexo.utils.SchedulerUtils;
import com.nexomc.nexo.utils.VersionUtil;
import com.nexomc.nexo.utils.logs.Logs;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.sequences.Sequence;
import kotlin.sequences.SequencesKt;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Keyed;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0007b\u0002\b\bb\u0002\b\tJ\u001c\u0010\n\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0007b\u0002\b\bb\u0002\b\tJ\u001c\u0010\u000b\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0007b\u0002\b\bb\u0002\b\tJ\u001c\u0010\f\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0007b\u0002\b\bb\u0002\b\tJ\u001c\u0010\r\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0007b\u0002\b\bb\u0002\b\tJ\u001c\u0010\u000e\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0007b\u0002\b\bb\u0002\b\t¨\u0006\u000f"},
   d2 = {"Lcom/nexomc/nexo/commands/ReloadCommand;", "", "<init>", "()V", "reloadAll", "", "sender", "Lorg/bukkit/command/CommandSender;", "Lkotlin/jvm/JvmOverloads;", "Lkotlin/jvm/JvmStatic;", "reloadItems", "reloadPack", "reloadRecipes", "reloadConfigs", "reloadDialogs", "com.nexomc:core"}
)
public final class ReloadCommand {
   @NotNull
   public static final ReloadCommand INSTANCE = new ReloadCommand();

   private ReloadCommand() {
   }

   @JvmStatic
   @JvmOverloads
   public static final void reloadAll(@Nullable CommandSender sender) {
      if (!Settings.DISABLE_NETTY_INJECT.toBool()) {
         IPacketHandler packetHandler = NMSHandlers.handler().packetHandler();
         IPacketHandler iPacketHandler = packetHandler;
         int value = 0;
         Collection onlinePlayers = Bukkit.getOnlinePlayers();
         Iterable iterable = (Iterable)onlinePlayers;

         for(Object element : iterable) {
            Player p0 = (Player)element;
            int value2 = 0;
            iPacketHandler.inject(p0);
         }
      }

      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value3 = 0;
            mechanicFactory = it;
            if (!(it instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            it = (FurnitureFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      if (mechanicFactory != null) {
         IFurniturePacketManager packetManager = ((com.nexomc.nexo.mechanics.furniture.FurnitureFactory)mechanicFactory).packetManager();
         if (packetManager != null) {
            packetManager.removeAllFurniturePackets();
         }
      }

      reloadDialogs(sender);
      reloadConfigs(sender);
      reloadItems(sender);
      reloadRecipes(sender);
      reloadPack(sender);
   }

   @JvmStatic
   @JvmOverloads
   public static final void reloadItems(@Nullable CommandSender sender) {
      NexoItems.INSTANCE.getItemConfigCache().clear();
      CustomBlockSoundListener.Companion.reload();
      FurnitureSoundListener.Companion.reload();
      MechanicsManager mechanicsManager = MechanicsManager.INSTANCE;
      Iterator iterator = ((Iterable)mechanicsManager.getFACTORIES_BY_MECHANIC_ID().values()).iterator();

      MechanicFactory mechanicFactory;
      while(true) {
         if (iterator.hasNext()) {
            MechanicFactory it = (MechanicFactory)iterator.next();
            int value = 0;
            mechanicFactory = it;
            if (!(it instanceof FurnitureFactory)) {
               mechanicFactory = null;
            }

            it = (FurnitureFactory)mechanicFactory;
            if (it == null) {
               continue;
            }

            mechanicFactory = it;
            break;
         }

         mechanicFactory = null;
         break;
      }

      mechanicFactory = mechanicFactory;
      if ((FurnitureFactory)mechanicFactory != null) {
         IFurniturePacketManager packetManager = ((com.nexomc.nexo.mechanics.furniture.FurnitureFactory)mechanicFactory).packetManager();
         if (packetManager != null) {
            packetManager.removeAllFurniturePackets();
         }
      }

      NexoItems.loadItems();
      NexoPlugin.Companion.instance().getInvManager$com_nexomc_core().reload();
      if (Settings.UPDATE_ITEMS.toBool() && Settings.UPDATE_ITEMS_ON_RELOAD.toBool()) {
         SchedulerUtils.INSTANCE._runAtWorldEntities((Function1<Entity, Unit>)((Entity p0) -> {
(ItemUpdater.Companion).updateEntityInventories(p0);
return Unit.INSTANCE;
}));
      }

      SchedulerUtils schedulerUtils = SchedulerUtils.INSTANCE;
      Function1 task = (Function1<ItemDisplay, Unit>)(p0 -> { NexoFurniture.updateFurniture(p0); return Unit.INSTANCE; });
      List worlds = Bukkit.getWorlds();
      Iterable iterable = (Iterable)worlds;

      for(Object element : iterable) {
         World world = (World)element;
         int value2 = 0;
         if (VersionUtil.INSTANCE.isFoliaServer()) {
            Chunk[] loadedChunks = world.getLoadedChunks();
            Object[] objs = loadedChunks;
            int value3 = 0;

            for(int i = objs.length; value3 < i; ++value3) {
               Object obj = objs[value3];
               Chunk chunk = (Chunk)obj;
               int value4 = 0;
               SchedulerUtils.INSTANCE.getFoliaScheduler().runAtLocation(new Location(world, (double)chunk.getX() * (double)16.0F, (double)100.0F, (double)chunk.getZ() * (double)16.0F), new ReloadCommand$reloadItems$$inlined$runAtWorldEntities$1(chunk, task));
            }
         } else {
            Collection entitiesByClass = world.getEntitiesByClass(ItemDisplay.class);
            Iterable iterable2 = (Iterable)entitiesByClass;

            for(Object obj2 : iterable2) {
               Entity entity = (Entity)obj2;
               int value5 = 0;
               task.invoke(entity);
            }
         }
      }

      Message message = Message.RELOAD;
      TagResolver[] tagResolvers = new TagResolver[]{AdventureUtils.tagResolver("reloaded", "items")};
      message.send(sender, tagResolvers);
   }

   @JvmStatic
   @JvmOverloads
   public static final void reloadPack(@Nullable CommandSender sender) {
      Message.PACK_REGENERATED.send(sender);
      NexoTranslator.Companion.registerTranslations();
      NexoPlugin.Companion.instance().packGenerator(new PackGenerator());
      NexoPlugin.Companion.instance().packGenerator().regeneratePack();
   }

   @JvmStatic
   @JvmOverloads
   public static final void reloadRecipes(@Nullable CommandSender sender) {
      java.lang.String str;
      Iterator recipeIterator = Bukkit.recipeIterator();
      Sequence sequence = SequencesKt.asSequence(recipeIterator);
      int count = 0;

      for(Object element : kotlin.sequences.SequencesKt.asIterable(sequence)) {
         block: {
            Recipe it = (Recipe)element;
            int value = 0;
            Keyed keyed = it instanceof Keyed ? (Keyed)it : null;
            if ((it instanceof Keyed ? (Keyed)it : null) != null) {
               NamespacedKey key = keyed.getKey();
               if (key != null) {
                  str = key.getNamespace();
                  break block;
               }
            }

            str = null;
         }

         if (Intrinsics.areEqual(str, "nexo")) {
            ++count;
            if (count < 0) {
               CollectionsKt.throwCountOverflow();
            }
         }
      }

      if (count >= 40) {
         Logs.logWarn("Nexo did not reload recipes due to the number of recipes!", false);
         Logs.logWarn("In modern Paper-versions this would cause the server to freeze for very long times", false);
         Logs.logWarn("Restart your server fully for recipe-changes to take effect", false);
      } else {
         RecipesManager.reload();
      }

      Message message = Message.RELOAD;
      TagResolver[] tagResolvers = new TagResolver[1];
      TagResolver.Single single = Placeholder.parsed("reloaded", "recipes");
      tagResolvers[0] = single;
      message.send(sender, tagResolvers);
   }

   @JvmStatic
   @JvmOverloads
   public static final void reloadConfigs(@Nullable CommandSender sender) {
      MechanicsManager.INSTANCE.unregisterListeners();
      MechanicsManager.INSTANCE.unregisterTasks();
      NMSHandlers.resetHandler();
      NexoPlugin.Companion.instance().reload();
      NexoPackServer.Companion.initializeServer();
      MechanicsManager.INSTANCE.registerNativeMechanics(true);
      Message message = Message.RELOAD;
      TagResolver[] tagResolvers = new TagResolver[]{AdventureUtils.tagResolver("reloaded", "configs")};
      message.send(sender, tagResolvers);
   }

   @JvmStatic
   @JvmOverloads
   public static final void reloadDialogs(@Nullable CommandSender sender) {
      if (VersionUtil.atleast("1.21.7")) {
         NexoDialogs.INSTANCE.reloadDialogs();
         Message message = Message.RELOAD;
         TagResolver[] tagResolvers = new TagResolver[]{AdventureUtils.tagResolver("reloaded", "dialogs")};
         message.send(sender, tagResolvers);
      }
   }

   @JvmStatic
   @JvmOverloads
   public static final void reloadAll() {
      reloadAll((CommandSender)null);
   }

   @JvmStatic
   @JvmOverloads
   public static final void reloadItems() {
      reloadItems((CommandSender)null);
   }

   @JvmStatic
   @JvmOverloads
   public static final void reloadPack() {
      reloadPack((CommandSender)null);
   }

   @JvmStatic
   @JvmOverloads
   public static final void reloadRecipes() {
      reloadRecipes((CommandSender)null);
   }

   @JvmStatic
   @JvmOverloads
   public static final void reloadConfigs() {
      reloadConfigs((CommandSender)null);
   }

   @JvmStatic
   @JvmOverloads
   public static final void reloadDialogs() {
      reloadDialogs((CommandSender)null);
   }
}
