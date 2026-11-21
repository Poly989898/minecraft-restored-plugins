package com.nexomc.nexo.mechanics.furniture;

import com.nexomc.nexo.decomp.ResultShim;
import com.nexomc.libs.folialib.wrapper.task.WrappedBukkitTask;
import com.nexomc.nexo.NexoPlugin;
import com.nexomc.nexo.configs.Settings;
import com.nexomc.nexo.converter.ItemsAdderConverterListener;
import com.nexomc.nexo.converter.OraxenConverterListener;
import com.nexomc.nexo.mechanics.Mechanic;
import com.nexomc.nexo.mechanics.MechanicFactory;
import com.nexomc.nexo.mechanics.MechanicsManager;
import com.nexomc.nexo.mechanics.furniture.compatibility.ModelEngineCompatibility;
import com.nexomc.nexo.mechanics.furniture.compatibility.SpartanCompatibility;
import com.nexomc.nexo.mechanics.furniture.compatibility.VacanCompatibility;
import com.nexomc.nexo.mechanics.furniture.compatibility.VulcanCompatibility;
import com.nexomc.nexo.mechanics.furniture.culling.FurnitureCullingManager;
import com.nexomc.nexo.mechanics.furniture.door.FurnitureDoorListener;
import com.nexomc.nexo.mechanics.furniture.evolution.EvolutionListener;
import com.nexomc.nexo.mechanics.furniture.evolution.EvolutionTask;
import com.nexomc.nexo.mechanics.furniture.jukebox.JukeboxListener;
import com.nexomc.nexo.mechanics.furniture.listeners.FurnitureBarrierHitboxListener;
import com.nexomc.nexo.mechanics.furniture.listeners.FurnitureBreakListener;
import com.nexomc.nexo.mechanics.furniture.listeners.FurnitureListener;
import com.nexomc.nexo.mechanics.furniture.listeners.FurniturePacketListener;
import com.nexomc.nexo.mechanics.furniture.listeners.FurnitureSoundListener;
import com.nexomc.nexo.mechanics.furniture.states.FurnitureStateListener;
import com.nexomc.nexo.nms.NMSHandlers;
import com.nexomc.nexo.utils.NexoYaml;
import com.nexomc.nexo.utils.NexoYamlKt;
import com.nexomc.nexo.utils.PluginUtils;
import com.nexomc.nexo.utils.VersionUtil;
import com.nexomc.nexo.utils.blocksounds.BlockSounds;
import java.util.Collection;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.ScheduledForRemoval;
import team.unnamed.creative.ResourcePack;
import team.unnamed.creative.sound.SoundRegistry;

@Metadata(
   mv = {2, 4, 0},
   k = 1,
   xi = 48,
   d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 +2\u00020\u0001:\u0001+B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000e\u0010\u001c\u001a\u00020\u001d2\u0006\u0010\u001e\u001a\u00020\u001fJ\u0006\u0010 \u001a\u00020!J\u0010\u0010\"\u001a\u00020#2\u0006\u0010\u0002\u001a\u00020\u0003H\u0016J\u0010\u0010$\u001a\u00020\u00032\u0006\u0010\u0002\u001a\u00020\u0003H\u0002J\u0006\u0010%\u001a\u00020\u001dJ\u0014\u0010&\u001a\u0004\u0018\u00010#2\b\u0010'\u001a\u0004\u0018\u00010(H\u0016J\u0014\u0010&\u001a\u0004\u0018\u00010#2\b\u0010)\u001a\u0004\u0018\u00010*H\u0016R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u000e\u0010\f\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\r\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000bR\u0011\u0010\u000f\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000bR\u0011\u0010\u0011\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000bR\u0011\u0010\u0013\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0016\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u000bR\u0013\u0010\u0018\u001a\u0004\u0018\u00010\u0019¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001b¨\u0006,"},
   d2 = {"Lcom/nexomc/nexo/mechanics/furniture/FurnitureFactory;", "Lcom/nexomc/nexo/mechanics/MechanicFactory;", "section", "Lorg/bukkit/configuration/ConfigurationSection;", "<init>", "(Lorg/bukkit/configuration/ConfigurationSection;)V", "evolutionCheckDelay", "", "customSounds", "", "getCustomSounds", "()Z", "evolvingFurnitures", "defaultRotatableOnSneak", "getDefaultRotatableOnSneak", "handleNonPlayerBarrierCollision", "getHandleNonPlayerBarrierCollision", "tryPreventingBarrierKick", "getTryPreventingBarrierKick", "defaultProperties", "getDefaultProperties", "()Lorg/bukkit/configuration/ConfigurationSection;", "lightLookupOnRemove", "getLightLookupOnRemove", "cullingManager", "Lcom/nexomc/nexo/mechanics/furniture/culling/FurnitureCullingManager;", "getCullingManager", "()Lcom/nexomc/nexo/mechanics/furniture/culling/FurnitureCullingManager;", "soundRegistries", "", "resourcePack", "Lteam/unnamed/creative/ResourcePack;", "packetManager", "Lcom/nexomc/nexo/mechanics/furniture/IFurniturePacketManager;", "parse", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureMechanic;", "mergeDefaultProperties", "registerEvolution", "getMechanic", "itemID", "", "itemStack", "Lorg/bukkit/inventory/ItemStack;", "Companion", "com.nexomc:core"}
)
public final class FurnitureFactory extends MechanicFactory {
   @NotNull
   public static final Companion Companion = new Companion();
   private final int evolutionCheckDelay;
   private final boolean customSounds;
   private boolean evolvingFurnitures;
   private final boolean defaultRotatableOnSneak;
   private final boolean handleNonPlayerBarrierCollision;
   private final boolean tryPreventingBarrierKick;
   @NotNull
   private final ConfigurationSection defaultProperties;
   private final boolean lightLookupOnRemove;
   @Nullable
   private final FurnitureCullingManager cullingManager;
   @Nullable
   private static FurnitureFactory instance;
   @Nullable
   private static EvolutionTask evolutionTask;

   public FurnitureFactory(@NotNull ConfigurationSection section) {
      super(section);
      this.evolutionCheckDelay = section.getInt("evolution_check_delay");
      this.customSounds = section.getBoolean("custom_block_sounds", true);
      this.defaultRotatableOnSneak = section.getBoolean("default_rotatable_on_sneak", false);
      this.handleNonPlayerBarrierCollision = section.getBoolean("handle_non_player_barrier_collision", false);
      this.tryPreventingBarrierKick = section.getBoolean("try_preventing_barrier_kick", false);
      ConfigurationSection configurationSection = NexoYamlKt.section(section, "default_properties");
      if (configurationSection == null) {
         configurationSection = (ConfigurationSection)(new YamlConfiguration());
      }

      this.defaultProperties = configurationSection;
      this.lightLookupOnRemove = section.getBoolean("lookup_block_on_light_remove", VersionUtil.INSTANCE.isFoliaServer());
      FurnitureFactory furnitureFactory = this;
      configurationSection = NexoYamlKt.section(section, "culling");
      FurnitureCullingManager furnitureCullingManager;
      if (configurationSection != null) {
         ConfigurationSection p0 = configurationSection;
         int value = 0;
         furnitureCullingManager = new FurnitureCullingManager(p0);
         furnitureFactory = this;
      } else {
         furnitureCullingManager = null;
      }

      this.cullingManager = furnitureCullingManager;
      instance = this;
      Listener[] listeners = new Listener[]{new FurnitureListener(), new FurniturePacketListener(), new FurnitureBarrierHitboxListener(this), new FurnitureBreakListener(), new EvolutionListener(), new JukeboxListener(), new FurnitureDoorListener(), new FurnitureStateListener()};
      this.registerListeners(listeners);
      this.evolvingFurnitures = false;
      if (Settings.SPARTAN_FURNITURE_HOOK.toBool() && PluginUtils.INSTANCE.isVacanEnabled()) {
         listeners = new Listener[]{new VacanCompatibility()};
         this.registerListeners(listeners);
      }

      if (Settings.SPARTAN_FURNITURE_HOOK.toBool() && PluginUtils.INSTANCE.isSpartanEnabled()) {
         listeners = new Listener[]{new SpartanCompatibility()};
         this.registerListeners(listeners);
      }

      if (Settings.VULCAN_FURNITURE_HOOK.toBool() && PluginUtils.INSTANCE.isVulcanEnabled()) {
         listeners = new Listener[]{new VulcanCompatibility()};
         this.registerListeners(listeners);
      }

      if (PluginUtils.INSTANCE.isModelEngineEnabled()) {
         listeners = new Listener[]{new ModelEngineCompatibility()};
         this.registerListeners(listeners);
      }

      if (NexoPlugin.Companion.instance().converter().getOraxenConverter().getConvertFurnitureOnLoad()) {
         listeners = new Listener[]{new OraxenConverterListener()};
         this.registerListeners(listeners);
      }

      if (NexoPlugin.Companion.instance().converter().getItemsadderConverter().getConvertFurnitureOnLoad()) {
         listeners = new Listener[]{new ItemsAdderConverterListener()};
         this.registerListeners(listeners);
      }

      if (this.customSounds) {
         listeners = new Listener[]{new FurnitureSoundListener()};
         this.registerListeners(listeners);
      }

      FurnitureCullingManager furnitureCullingManager2 = this.cullingManager;
      if (furnitureCullingManager2 != null) {
         FurnitureCullingManager it = furnitureCullingManager2;
         int value2 = 0;
         Listener[] listeners2 = new Listener[]{it};
         this.registerListeners(listeners2);
      }

      furnitureCullingManager2 = this.cullingManager;
      if (furnitureCullingManager2 != null) {
         furnitureCullingManager2.startTask(this.getMechanicID());
      }

   }

   public final boolean getCustomSounds() {
      return this.customSounds;
   }

   public final boolean getDefaultRotatableOnSneak() {
      return this.defaultRotatableOnSneak;
   }

   public final boolean getHandleNonPlayerBarrierCollision() {
      return this.handleNonPlayerBarrierCollision;
   }

   public final boolean getTryPreventingBarrierKick() {
      return this.tryPreventingBarrierKick;
   }

   @NotNull
   public final ConfigurationSection getDefaultProperties() {
      return this.defaultProperties;
   }

   public final boolean getLightLookupOnRemove() {
      return this.lightLookupOnRemove;
   }

   @Nullable
   public final FurnitureCullingManager getCullingManager() {
      return this.cullingManager;
   }

   public final void soundRegistries(@NotNull ResourcePack resourcePack) {
      if (this.customSounds) {
         SoundRegistry[] soundRegistries = new SoundRegistry[]{BlockSounds.NEXO_STONE_SOUND_REGISTRY, BlockSounds.VANILLA_STONE_SOUND_REGISTRY};
         Object[] objs = soundRegistries;
         int value = 0;

         for(int i = soundRegistries.length; value < i; ++value) {
            Object obj;
            block: {
               Object element = objs[value];
               int value2 = 0;
               SoundRegistry soundRegistry = resourcePack.soundRegistry(((SoundRegistry)element).namespace());
               if (soundRegistry != null) {
                  SoundRegistry it = soundRegistry;
                  int value3 = 0;
                  obj = SoundRegistry.soundRegistry().sounds((Collection)CollectionsKt.plus(((SoundRegistry)element).sounds(), (Iterable)it.sounds())).namespace(((SoundRegistry)element).namespace()).build();
                  if (obj != null) {
                     break block;
                  }
               }

               obj = element;
            }

            ((SoundRegistry)obj).addTo(resourcePack);
         }
      }

   }

   @NotNull
   public final IFurniturePacketManager packetManager() {
      return NMSHandlers.handler().furniturePacketManager();
   }

   @NotNull
   public FurnitureMechanic parse(@NotNull ConfigurationSection section) {
      FurnitureMechanic furnitureMechanic = new FurnitureMechanic(this, this.mergeDefaultProperties(section));
      Mechanic p0 = furnitureMechanic;
      int value = 0;
      this.addToImplemented(p0);
      return furnitureMechanic;
   }

   private final ConfigurationSection mergeDefaultProperties(ConfigurationSection section) {
      ConfigurationSection configurationSection = (new YamlConfiguration()).createSection("properties");
      ConfigurationSection properties = configurationSection;
      NexoYaml.Companion.copyConfigurationSection(this.defaultProperties, properties);
      configurationSection = NexoYamlKt.section(section, "properties");
      if (configurationSection != null) {
         ConfigurationSection it = configurationSection;
         int value = 0;
         NexoYaml.Companion.copyConfigurationSection(it, properties);
      }

      section.set("properties", properties);
      return section;
   }

   public final void registerEvolution() {
      if (!this.evolvingFurnitures) {
         FurnitureFactory furnitureFactory = this;

         try {
            FurnitureFactory furnitureFactory2 = furnitureFactory;
            int value = 0;
            EvolutionTask evolutionTask2 = evolutionTask;
            Unit unit;
            if (evolutionTask2 != null) {
               evolutionTask2.cancel();
               unit = Unit.INSTANCE;
            } else {
               unit = null;
            }

            Object obj = ResultShim.ok(unit);
         } catch (Throwable e) {
            Object obj2 = ResultShim.ok(ResultKt.createFailure(e));
         }

         evolutionTask = new EvolutionTask(this, this.evolutionCheckDelay);
         EvolutionTask evolutionTask3 = evolutionTask;
         BukkitTask bukkitTask = evolutionTask3.runTaskTimer((Plugin)NexoPlugin.Companion.instance(), 0L, (long)this.evolutionCheckDelay);
         BukkitTask task = bukkitTask;
         MechanicsManager.INSTANCE.registerTask(this.getMechanicID(), new WrappedBukkitTask(task));
         this.evolvingFurnitures = true;
      }
   }

   @Nullable
   public FurnitureMechanic getMechanic(@Nullable String itemID) {
      Mechanic mechanic = super.getMechanic(itemID);
      return mechanic instanceof FurnitureMechanic ? (FurnitureMechanic)mechanic : null;
   }

   @Nullable
   public FurnitureMechanic getMechanic(@Nullable ItemStack itemStack) {
      Mechanic mechanic = super.getMechanic(itemStack);
      return mechanic instanceof FurnitureMechanic ? (FurnitureMechanic)mechanic : null;
   }

   @Metadata(
      mv = {2, 4, 0},
      k = 1,
      xi = 48,
      d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003JJ\u0010\u0004\u001a\u0004\u0018\u00010\u0005H\u0007b0\b\u000b\u0012\b\b\f\u0012\u0004\b\b(\r\u0012\"\b\u000e\u0012\u001e\b\u000bB\u001a\b\u000f\u0012\b\b\u0010\u0012\u0004\b\b(\u0011\u0012\f\b\u0012\u0012\b\b\fJ\u0004\b\b(\u0013b\f\b\u0014\u0012\b\b\u0015\u0012\u0004\b\b(\u0016J\u0006\u0010\u0017\u001a\u00020\u0018R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\b\u001a\u00020\t8F¢\u0006\u0006\u001a\u0004\b\b\u0010\n¨\u0006\u0019"},
      d2 = {"Lcom/nexomc/nexo/mechanics/furniture/FurnitureFactory$Companion;", "", "<init>", "()V", "instance", "Lcom/nexomc/nexo/mechanics/furniture/FurnitureFactory;", "evolutionTask", "Lcom/nexomc/nexo/mechanics/furniture/evolution/EvolutionTask;", "isEnabled", "", "()Z", "Lkotlin/Deprecated;", "message", "Use MechanicsManager.factory<FurnitureFactory>() instead.", "replaceWith", "Lkotlin/ReplaceWith;", "expression", "MechanicsManager.factory<FurnitureFactory>()", "imports", "com.nexomc.nexo.mechanics.MechanicsManager", "Lorg/jetbrains/annotations/ApiStatus$ScheduledForRemoval;", "inVersion", "1.24.0", "unregisterEvolution", "", "com.nexomc:core"}
   )
   public static final class Companion {
      private Companion() {
      }

      public final boolean isEnabled() {
         return FurnitureFactory.instance != null;
      }

      /** @deprecated */
      @Deprecated(
         message = "Use MechanicsManager.factory<FurnitureFactory>() instead.",
         replaceWith = @ReplaceWith(
   expression = "MechanicsManager.factory<FurnitureFactory>()",
   imports = {"com.nexomc.nexo.mechanics.MechanicsManager"}
)
      )
      @ScheduledForRemoval(
         inVersion = "1.24.0"
      )
      @Nullable
      public final FurnitureFactory instance() {
         return FurnitureFactory.instance;
      }

      public final void unregisterEvolution() {
         if (FurnitureFactory.evolutionTask != null) {
            EvolutionTask evolutionTask2 = FurnitureFactory.evolutionTask;
            evolutionTask2.cancel();
         }

      }
   }
}
