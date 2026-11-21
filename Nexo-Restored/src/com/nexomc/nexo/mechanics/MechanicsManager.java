package com.nexomc.nexo.mechanics;

import com.nexomc.libs.folialib.impl.PlatformScheduler;
import com.nexomc.libs.folialib.wrapper.task.WrappedTask;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.api.events.NexoMechanicsRegisteredEvent;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockFactory;
import com.nexomc.nexo.mechanics.custom_block.chorusblock.ChorusBlockFactory;
import com.nexomc.nexo.mechanics.custom_block.noteblock.NoteBlockMechanicFactory;
import com.nexomc.nexo.mechanics.custom_block.stringblock.StringBlockMechanicFactory;
import com.nexomc.nexo.mechanics.farming.harvesting.HarvestingMechanicFactory;
import com.nexomc.nexo.mechanics.farming.smelting.SmeltingMechanicFactory;
import com.nexomc.nexo.mechanics.furniture.FurnitureFactory;
import com.nexomc.nexo.mechanics.misc.armor_effects.ArmorEffectsFactory;
import com.nexomc.nexo.mechanics.misc.backpack.BackpackMechanicFactory;
import com.nexomc.nexo.mechanics.misc.commands.CommandsMechanicFactory;
import com.nexomc.nexo.mechanics.misc.custom.CustomMechanicFactory;
import com.nexomc.nexo.mechanics.misc.itemtype.ItemTypeMechanicFactory;
import com.nexomc.nexo.mechanics.misc.misc.MiscMechanicFactory;
import com.nexomc.nexo.mechanics.misc.soulbound.SoulBoundMechanicFactory;
import com.nexomc.nexo.mechanics.repair.RepairMechanicFactory;
import com.nexomc.nexo.mechanics.trident.TridentFactory;
import com.nexomc.nexo.utils.EventUtils;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.SchedulerUtils;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000p\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001:\u00015B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0011\u001a\u0004\u0018\u0001H\u0012\"\n\b\u0000\u0010\u0012\u0018\u0001*\u00020\u0007H\u0086\b¢\u0006\u0002\u0010\u0013J\u000e\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0017J\u0016\u0010\u0018\u001a\u00020\u00152\u0006\u0010\u0011\u001a\u00020\u00072\u0006\u0010\u0019\u001a\u00020\u0017J\u001d\u0010\u0018\u001a\u00020\u0015\"\n\b\u0000\u0010\u0012\u0018\u0001*\u00020\u00072\u0006\u0010\u0019\u001a\u00020\u0017H\u0086\bJ\u000e\u0010\u001a\u001a\u00020\u00152\u0006\u0010\u001b\u001a\u00020\u0006J'\u0010\u0018\u001a\u00020\u0015\"\n\b\u0000\u0010\u0012\u0018\u0001*\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u00062\b\b\u0002\u0010\u001c\u001a\u00020\u0006H\u0082\bJ\"\u0010\u0018\u001a\u00020\u00152\u0006\u0010\u001b\u001a\u00020\u00062\u0006\u0010\u001d\u001a\u00020\u001e2\b\b\u0002\u0010\u001c\u001a\u00020\u0006H\u0002J\u0016\u0010\u001f\u001a\u00020\u00152\u0006\u0010\u001b\u001a\u00020\u00062\u0006\u0010 \u001a\u00020\u000eJ\u0006\u0010!\u001a\u00020\u0015J\u000e\u0010!\u001a\u00020\u00152\u0006\u0010\u001b\u001a\u00020\u0006J/\u0010\"\u001a\u00020\u00152\u0006\u0010#\u001a\u00020$2\u0006\u0010\u001b\u001a\u00020\u00062\u0012\u0010%\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00100&\"\u00020\u0010¢\u0006\u0002\u0010'J\u0006\u0010(\u001a\u00020\u0015J\u0010\u0010(\u001a\u00020\u00152\b\u0010\u001b\u001a\u0004\u0018\u00010\u0006J@\u0010)\u001a\u0004\u0018\u00010\u00072\b\u0010*\u001a\u0004\u0018\u00010\u0006H\u0007b*\b+\u0012\b\b,\u0012\u0004\b\b(-\u0012\u001c\b.\u0012\u0018\b\u000bB\u0014\b/\u0012\b\b0\u0012\u0004\b\b(1\u0012\u0006\b2\u0012\u0002\b\fJ\u0018\u00101\u001a\u0004\u0018\u00010\u00072\b\u00103\u001a\u0004\u0018\u00010\u0006H\u0007b\u0002\b4R,\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u00058\u0000X\u0081\u0004r\u0002\b\u000b¢\u0006\u000e\n\u0000\u0012\u0004\b\b\u0010\u0003\u001a\u0004\b\t\u0010\nR \u0010\f\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u000e0\r0\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R \u0010\u000f\u001a\u0014\u0012\u0004\u0012\u00020\u0006\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\r0\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u00066"},
   d2 = {"Lcom/nexomc/nexo/mechanics/MechanicsManager;", "", "<init>", "()V", "FACTORIES_BY_MECHANIC_ID", "", "", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "getFACTORIES_BY_MECHANIC_ID$annotations", "getFACTORIES_BY_MECHANIC_ID", "()Ljava/util/Map;", "Lkotlin/PublishedApi;", "MECHANIC_TASKS", "", "Lcom/nexomc/libs/folialib/wrapper/task/WrappedTask;", "MECHANICS_LISTENERS", "Lorg/bukkit/event/Listener;", "factory", "T", "()Lcom/nexomc/nexo/mechanics/MechanicFactory;", "registerNativeMechanics", "", "reload", "", "registerMechanicFactory", "enabled", "unregisterMechanicFactory", "mechanicId", "configPath", "constructor", "Lcom/nexomc/nexo/mechanics/MechanicsManager$FactoryConstructor;", "registerTask", "task", "unregisterTasks", "registerListeners", "plugin", "Lorg/bukkit/plugin/java/JavaPlugin;", "listeners", "", "(Lorg/bukkit/plugin/java/JavaPlugin;Ljava/lang/String;[Lorg/bukkit/event/Listener;)V", "unregisterListeners", "getMechanicFactory", "mechanicID", "Lkotlin/Deprecated;", "message", "", "replaceWith", "Lkotlin/ReplaceWith;", "expression", "mechanicFactory", "imports", "factoryId", "Ljavax/annotation/Nullable;", "FactoryConstructor", "com.nexomc:core"}
)
public final class MechanicsManager {
   @NotNull
   public static final MechanicsManager INSTANCE = new MechanicsManager();
   @NotNull
   private static final Map<String, MechanicFactory> FACTORIES_BY_MECHANIC_ID = (Map)(new LinkedHashMap());
   @NotNull
   private static final Map<String, List<WrappedTask>> MECHANIC_TASKS = (Map)(new LinkedHashMap());
   @NotNull
   private static final Map<String, List<Listener>> MECHANICS_LISTENERS = (Map)(new LinkedHashMap());

   private MechanicsManager() {
   }

   @NotNull
   public final Map<String, MechanicFactory> getFACTORIES_BY_MECHANIC_ID() {
      return FACTORIES_BY_MECHANIC_ID;
   }

   public final void registerNativeMechanics(boolean reload) {
      try {
      String mechanicId = "armor_effects";
      ConfigurationSection configurationSection = NexoYamlKt.section((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().getMechanics(), mechanicId);
      if (configurationSection != null) {
         ConfigurationSection factorySection = configurationSection;
         if (factorySection.getBoolean("enabled")) {
            Class[] clazzs = new Class[]{ConfigurationSection.class};
            Constructor clazz = ArmorEffectsFactory.class.getDeclaredConstructor(clazzs);
            Map factorieS_BY_MECHANIC_ID = this.getFACTORIES_BY_MECHANIC_ID();
            Object obj = new Object[]{factorySection};
            obj = clazz.newInstance((Object[])obj);
            factorieS_BY_MECHANIC_ID.put(mechanicId, obj);
         }
      }

      mechanicId = "soulbound";
      configurationSection = NexoYamlKt.section((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().getMechanics(), mechanicId);
      if (configurationSection != null) {
         ConfigurationSection configurationSection2 = configurationSection;
         if (configurationSection2.getBoolean("enabled")) {
            Class[] clazzs2 = new Class[]{ConfigurationSection.class};
            Constructor declaredConstructor = SoulBoundMechanicFactory.class.getDeclaredConstructor(clazzs2);
            Map map = this.getFACTORIES_BY_MECHANIC_ID();
            Object obj2 = new Object[]{configurationSection2};
            obj2 = declaredConstructor.newInstance((Object[])obj2);
            map.put(mechanicId, obj2);
         }
      }

      mechanicId = "itemtype";
      configurationSection = NexoYamlKt.section((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().getMechanics(), mechanicId);
      if (configurationSection != null) {
         ConfigurationSection configurationSection3 = configurationSection;
         if (configurationSection3.getBoolean("enabled")) {
            Class[] clazzs3 = new Class[]{ConfigurationSection.class};
            Constructor constructor = ItemTypeMechanicFactory.class.getDeclaredConstructor(clazzs3);
            Map map2 = this.getFACTORIES_BY_MECHANIC_ID();
            Object obj3 = new Object[]{configurationSection3};
            obj3 = constructor.newInstance((Object[])obj3);
            map2.put(mechanicId, obj3);
         }
      }

      mechanicId = "custom";
      configurationSection = NexoYamlKt.section((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().getMechanics(), mechanicId);
      if (configurationSection != null) {
         ConfigurationSection configurationSection4 = configurationSection;
         if (configurationSection4.getBoolean("enabled")) {
            Class[] clazzs4 = new Class[]{ConfigurationSection.class};
            Constructor constructor2 = CustomMechanicFactory.class.getDeclaredConstructor(clazzs4);
            Map map3 = this.getFACTORIES_BY_MECHANIC_ID();
            Object obj4 = new Object[]{configurationSection4};
            obj4 = constructor2.newInstance((Object[])obj4);
            map3.put(mechanicId, obj4);
         }
      }

      mechanicId = "commands";
      configurationSection = NexoYamlKt.section((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().getMechanics(), mechanicId);
      if (configurationSection != null) {
         ConfigurationSection configurationSection5 = configurationSection;
         if (configurationSection5.getBoolean("enabled")) {
            Class[] clazzs5 = new Class[]{ConfigurationSection.class};
            Constructor constructor3 = CommandsMechanicFactory.class.getDeclaredConstructor(clazzs5);
            Map map4 = this.getFACTORIES_BY_MECHANIC_ID();
            Object obj5 = new Object[]{configurationSection5};
            obj5 = constructor3.newInstance((Object[])obj5);
            map4.put(mechanicId, obj5);
         }
      }

      mechanicId = "backpack";
      configurationSection = NexoYamlKt.section((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().getMechanics(), mechanicId);
      if (configurationSection != null) {
         ConfigurationSection configurationSection6 = configurationSection;
         if (configurationSection6.getBoolean("enabled")) {
            Class[] clazzs6 = new Class[]{ConfigurationSection.class};
            Constructor constructor4 = BackpackMechanicFactory.class.getDeclaredConstructor(clazzs6);
            Map map5 = this.getFACTORIES_BY_MECHANIC_ID();
            Object obj6 = new Object[]{configurationSection6};
            obj6 = constructor4.newInstance((Object[])obj6);
            map5.put(mechanicId, obj6);
         }
      }

      mechanicId = "misc";
      configurationSection = NexoYamlKt.section((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().getMechanics(), mechanicId);
      if (configurationSection != null) {
         ConfigurationSection configurationSection7 = configurationSection;
         if (configurationSection7.getBoolean("enabled")) {
            Class[] clazzs7 = new Class[]{ConfigurationSection.class};
            Constructor constructor5 = MiscMechanicFactory.class.getDeclaredConstructor(clazzs7);
            Map map6 = this.getFACTORIES_BY_MECHANIC_ID();
            Object obj7 = new Object[]{configurationSection7};
            obj7 = constructor5.newInstance((Object[])obj7);
            map6.put(mechanicId, obj7);
         }
      }

      mechanicId = "trident";
      configurationSection = NexoYamlKt.section((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().getMechanics(), mechanicId);
      if (configurationSection != null) {
         ConfigurationSection configurationSection8 = configurationSection;
         if (configurationSection8.getBoolean("enabled")) {
            Class[] clazzs8 = new Class[]{ConfigurationSection.class};
            Constructor constructor6 = TridentFactory.class.getDeclaredConstructor(clazzs8);
            Map map7 = this.getFACTORIES_BY_MECHANIC_ID();
            Object obj8 = new Object[]{configurationSection8};
            obj8 = constructor6.newInstance((Object[])obj8);
            map7.put(mechanicId, obj8);
         }
      }

      mechanicId = "furniture";
      configurationSection = NexoYamlKt.section((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().getMechanics(), mechanicId);
      if (configurationSection != null) {
         ConfigurationSection configurationSection9 = configurationSection;
         if (configurationSection9.getBoolean("enabled")) {
            Class[] clazzs9 = new Class[]{ConfigurationSection.class};
            Constructor constructor7 = FurnitureFactory.class.getDeclaredConstructor(clazzs9);
            Map map8 = this.getFACTORIES_BY_MECHANIC_ID();
            Object obj9 = new Object[]{configurationSection9};
            obj9 = constructor7.newInstance((Object[])obj9);
            map8.put(mechanicId, obj9);
         }
      }

      mechanicId = "noteblock";
      String configPath = "custom_blocks.noteblock";
      configurationSection = NexoYamlKt.section((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().getMechanics(), configPath);
      if (configurationSection != null) {
         ConfigurationSection configurationSection10 = configurationSection;
         if (configurationSection10.getBoolean("enabled")) {
            Class[] clazzs10 = new Class[]{ConfigurationSection.class};
            Constructor constructor8 = NoteBlockMechanicFactory.class.getDeclaredConstructor(clazzs10);
            Map map9 = this.getFACTORIES_BY_MECHANIC_ID();
            Object obj10 = new Object[]{configurationSection10};
            obj10 = constructor8.newInstance((Object[])obj10);
            map9.put(mechanicId, obj10);
         }
      }

      mechanicId = "stringblock";
      configPath = "custom_blocks.stringblock";
      configurationSection = NexoYamlKt.section((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().getMechanics(), configPath);
      if (configurationSection != null) {
         ConfigurationSection configurationSection11 = configurationSection;
         if (configurationSection11.getBoolean("enabled")) {
            Class[] clazzs11 = new Class[]{ConfigurationSection.class};
            Constructor constructor9 = StringBlockMechanicFactory.class.getDeclaredConstructor(clazzs11);
            Map map10 = this.getFACTORIES_BY_MECHANIC_ID();
            Object obj11 = new Object[]{configurationSection11};
            obj11 = constructor9.newInstance((Object[])obj11);
            map10.put(mechanicId, obj11);
         }
      }

      mechanicId = "chorusblock";
      configPath = "custom_blocks.chorusblock";
      configurationSection = NexoYamlKt.section((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().getMechanics(), configPath);
      if (configurationSection != null) {
         ConfigurationSection configurationSection12 = configurationSection;
         if (configurationSection12.getBoolean("enabled")) {
            Class[] clazzs12 = new Class[]{ConfigurationSection.class};
            Constructor constructor10 = ChorusBlockFactory.class.getDeclaredConstructor(clazzs12);
            Map map11 = this.getFACTORIES_BY_MECHANIC_ID();
            Object obj12 = new Object[]{configurationSection12};
            obj12 = constructor10.newInstance((Object[])obj12);
            map11.put(mechanicId, obj12);
         }
      }

      mechanicId = "custom_block";
      configPath = "custom_blocks";
      configurationSection = NexoYamlKt.section((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().getMechanics(), configPath);
      if (configurationSection != null) {
         ConfigurationSection configurationSection13 = configurationSection;
         if (configurationSection13.getBoolean("enabled")) {
            Class[] clazzs13 = new Class[]{ConfigurationSection.class};
            Constructor constructor11 = CustomBlockFactory.class.getDeclaredConstructor(clazzs13);
            Map map12 = this.getFACTORIES_BY_MECHANIC_ID();
            Object obj13 = new Object[]{configurationSection13};
            obj13 = constructor11.newInstance((Object[])obj13);
            map12.put(mechanicId, obj13);
         }
      }

      mechanicId = "smelting";
      configurationSection = NexoYamlKt.section((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().getMechanics(), mechanicId);
      if (configurationSection != null) {
         ConfigurationSection configurationSection14 = configurationSection;
         if (configurationSection14.getBoolean("enabled")) {
            Class[] clazzs14 = new Class[]{ConfigurationSection.class};
            Constructor constructor12 = SmeltingMechanicFactory.class.getDeclaredConstructor(clazzs14);
            Map map13 = this.getFACTORIES_BY_MECHANIC_ID();
            Object obj14 = new Object[]{configurationSection14};
            obj14 = constructor12.newInstance((Object[])obj14);
            map13.put(mechanicId, obj14);
         }
      }

      mechanicId = "harvesting";
      configurationSection = NexoYamlKt.section((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().getMechanics(), mechanicId);
      if (configurationSection != null) {
         ConfigurationSection configurationSection15 = configurationSection;
         if (configurationSection15.getBoolean("enabled")) {
            Class[] clazzs15 = new Class[]{ConfigurationSection.class};
            Constructor constructor13 = HarvestingMechanicFactory.class.getDeclaredConstructor(clazzs15);
            Map map14 = this.getFACTORIES_BY_MECHANIC_ID();
            Object obj15 = new Object[]{configurationSection15};
            obj15 = constructor13.newInstance((Object[])obj15);
            map14.put(mechanicId, obj15);
         }
      }

      mechanicId = "repair";
      configurationSection = NexoYamlKt.section((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().getMechanics(), mechanicId);
      if (configurationSection != null) {
         ConfigurationSection configurationSection16 = configurationSection;
         if (configurationSection16.getBoolean("enabled")) {
            Class[] clazzs16 = new Class[]{ConfigurationSection.class};
            Constructor constructor14 = RepairMechanicFactory.class.getDeclaredConstructor(clazzs16);
            Map map15 = this.getFACTORIES_BY_MECHANIC_ID();
            Object obj16 = new Object[]{configurationSection16};
            obj16 = constructor14.newInstance((Object[])obj16);
            map15.put(mechanicId, obj16);
         }
      }

      if (reload) {
         EventUtils.INSTANCE.call(new NexoMechanicsRegisteredEvent());
      } else {
         SchedulerUtils.INSTANCE.syncDelayedTask(0L, MechanicsManager::registerNativeMechanics$lambda$0);
      }

      } catch (Exception e) {
         throw com.nexomc.nexo.utils.SneakyThrow.sneaky(e);
      }
   }

   public final void registerMechanicFactory(@NotNull MechanicFactory factory, boolean enabled) {
      if (enabled) {
         FACTORIES_BY_MECHANIC_ID.put(factory.getMechanicID(), factory);
      }

   }

   public final void unregisterMechanicFactory(@NotNull String mechanicId) {
      FACTORIES_BY_MECHANIC_ID.remove(mechanicId);
      this.unregisterListeners(mechanicId);
      this.unregisterTasks(mechanicId);
   }

   private final void registerMechanicFactory(String mechanicId, FactoryConstructor constructor, String configPath) {
      ConfigurationSection configurationSection = NexoYamlKt.section((ConfigurationSection)NexoPlugin.Companion.instance().configsManager().getMechanics(), configPath);
      if (configurationSection != null) {
         ConfigurationSection factorySection = configurationSection;
         if (factorySection.getBoolean("enabled")) {
            FACTORIES_BY_MECHANIC_ID.put(mechanicId, constructor.create(factorySection));
         }

      }
   }

   public final void registerTask(@NotNull String mechanicId, @NotNull WrappedTask task) {
      MECHANIC_TASKS.compute(mechanicId, (k, v) -> MechanicsManager.registerTask$lambda$0(task, (String)k, (List)v));
   }

   public final void unregisterTasks() {
      Map map = MECHANIC_TASKS;
      int value = 0;

      for(Map.Entry element : (java.lang.Iterable<Map.Entry>)(java.lang.Iterable)map.entrySet()) {
         int value2 = 0;
         Iterable iterable = (Iterable)element.getValue();
         PlatformScheduler foliaScheduler = SchedulerUtils.INSTANCE.getFoliaScheduler();

         for(Object obj : iterable) {
            WrappedTask p0 = (WrappedTask)obj;
            int value3 = 0;
            foliaScheduler.cancelTask(p0);
         }
      }

      map.clear();
   }

   public final void unregisterTasks(@NotNull String mechanicId) {
      MECHANIC_TASKS.computeIfPresent(mechanicId, (key, tasks) -> MechanicsManager.unregisterTasks$lambda$1((String)key, (List)tasks));
   }

   public final void registerListeners(@NotNull JavaPlugin plugin, @NotNull String mechanicId, @NotNull Listener... listeners) {
      int value = 0;

      for(int i = listeners.length; value < i; ++value) {
         Listener listener = listeners[value];
         Bukkit.getPluginManager().registerEvents(listener, (Plugin)plugin);
      }

      MECHANICS_LISTENERS.compute(mechanicId, (k, v) -> MechanicsManager.registerListeners$lambda$0(listeners, (String)k, (List)v));
   }

   public final void unregisterListeners() {
      Iterable iterable = (Iterable)CollectionsKt.flatten((Iterable)MECHANICS_LISTENERS.values());

      for(Object element : iterable) {
         Listener p0 = (Listener)element;
         int value = 0;
         HandlerList.unregisterAll(p0);
      }

   }

   public final void unregisterListeners(@Nullable String mechanicId) {
      List list = (List)TypeIntrinsics.asMutableMap(MECHANICS_LISTENERS).remove(mechanicId);
      if (list != null) {
         Iterable iterable = (Iterable)list;

         for(Object element : iterable) {
            Listener p0 = (Listener)element;
            int value = 0;
            HandlerList.unregisterAll(p0);
         }
      }

   }

   /** @deprecated */
   @Deprecated(
      message = "",
      replaceWith = @ReplaceWith(
   expression = "mechanicFactory",
   imports = {}
)
   )
   @Nullable
   public final MechanicFactory getMechanicFactory(@Nullable String mechanicID) {
      return (MechanicFactory)FACTORIES_BY_MECHANIC_ID.get(mechanicID);
   }

   @javax.annotation.Nullable
   @Nullable
   public final MechanicFactory mechanicFactory(@Nullable String factoryId) {
      return (MechanicFactory)FACTORIES_BY_MECHANIC_ID.get(factoryId);
   }

   private static final Unit registerNativeMechanics$lambda$0() {
      EventUtils.INSTANCE.call(new NexoMechanicsRegisteredEvent());
      return Unit.INSTANCE;
   }

   private static final List registerTask$lambda$0(WrappedTask task, String str, List value) {
      List list = value;
      if (value == null) {
         list = (List)(new ArrayList());
      }

      List it = list;
      int value2 = 0;
      ((Collection)it).add(task);
      return it;
   }

   private static final List registerTask$lambda$1(Function2 tmp0, Object p0, Object p1) {
      return (List)tmp0.invoke(p0, p1);
   }

   private static final List unregisterTasks$lambda$1(String str, List value) {
      Iterable iterable = (Iterable)value;

      for(Object element : iterable) {
         WrappedTask taskId = (WrappedTask)element;
         int value2 = 0;
         SchedulerUtils.INSTANCE.getFoliaScheduler().cancelTask(taskId);
      }

      return (List)(new ArrayList());
   }

   private static final List unregisterTasks$lambda$2(Function2 tmp0, Object p0, Object p1) {
      return (List)tmp0.invoke(p0, p1);
   }

   private static final List registerListeners$lambda$0(Listener[] listeners, String str, List value) {
      List list = value;
      if (value == null) {
         list = (List)(new ArrayList());
      }

      List it = list;
      int value2 = 0;
      CollectionsKt.addAll((Collection)it, listeners);
      return it;
   }

   private static final List registerListeners$lambda$1(Function2 tmp0, Object p0, Object p1) {
      return (List)tmp0.invoke(p0, p1);
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bæ\u0080\u0001\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&¨\u0006\u0006À\u0006\u0003"},
      d2 = {"Lcom/nexomc/nexo/mechanics/MechanicsManager$FactoryConstructor;", "", "create", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "com.nexomc:core"}
   )
   public interface FactoryConstructor {
      @NotNull
      MechanicFactory create(@NotNull ConfigurationSection configurationSection);
   }
}
