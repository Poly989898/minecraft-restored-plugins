package io.lumine.mythic.lib.manager;

import io.lumine.mythic.lib.MythicLib;
import io.lumine.mythic.lib.UtilityMethods;
import io.lumine.mythic.lib.script.Script;
import io.lumine.mythic.lib.script.condition.Condition;
import io.lumine.mythic.lib.script.condition.generic.BooleanCondition;
import io.lumine.mythic.lib.script.condition.generic.CompareCondition;
import io.lumine.mythic.lib.script.condition.generic.InBetweenCondition;
import io.lumine.mythic.lib.script.condition.generic.StringEqualsCondition;
import io.lumine.mythic.lib.script.condition.location.BiomeCondition;
import io.lumine.mythic.lib.script.condition.location.CuboidCondition;
import io.lumine.mythic.lib.script.condition.location.DistanceCondition;
import io.lumine.mythic.lib.script.condition.location.WorldCondition;
import io.lumine.mythic.lib.script.condition.misc.CanTargetCondition;
import io.lumine.mythic.lib.script.condition.misc.CooldownCondition;
import io.lumine.mythic.lib.script.condition.misc.FoodCondition;
import io.lumine.mythic.lib.script.condition.misc.HasAmmoMechanic;
import io.lumine.mythic.lib.script.condition.misc.HasDamageTypeCondition;
import io.lumine.mythic.lib.script.condition.misc.IsLivingCondition;
import io.lumine.mythic.lib.script.condition.misc.OnFireCondition;
import io.lumine.mythic.lib.script.condition.misc.PermissionCondition;
import io.lumine.mythic.lib.script.condition.misc.TimeCondition;
import io.lumine.mythic.lib.script.mechanic.Mechanic;
import io.lumine.mythic.lib.script.mechanic.buff.FeedMechanic;
import io.lumine.mythic.lib.script.mechanic.buff.HealMechanic;
import io.lumine.mythic.lib.script.mechanic.buff.ReduceCooldownMechanic;
import io.lumine.mythic.lib.script.mechanic.buff.SaturateMechanic;
import io.lumine.mythic.lib.script.mechanic.buff.stat.AddStatModifierMechanic;
import io.lumine.mythic.lib.script.mechanic.buff.stat.RemoveStatModifierMechanic;
import io.lumine.mythic.lib.script.mechanic.misc.ApplyCooldownMechanic;
import io.lumine.mythic.lib.script.mechanic.misc.DelayMechanic;
import io.lumine.mythic.lib.script.mechanic.misc.DispatchCommandMechanic;
import io.lumine.mythic.lib.script.mechanic.misc.EntityEffectMechanic;
import io.lumine.mythic.lib.script.mechanic.misc.LightningStrikeMechanic;
import io.lumine.mythic.lib.script.mechanic.misc.ScriptMechanic;
import io.lumine.mythic.lib.script.mechanic.movement.TeleportMechanic;
import io.lumine.mythic.lib.script.mechanic.movement.VelocityMechanic;
import io.lumine.mythic.lib.script.mechanic.offense.AdditiveDamageBuffMechanic;
import io.lumine.mythic.lib.script.mechanic.offense.DamageMechanic;
import io.lumine.mythic.lib.script.mechanic.offense.MultiplyDamageMechanic;
import io.lumine.mythic.lib.script.mechanic.offense.PotionMechanic;
import io.lumine.mythic.lib.script.mechanic.offense.RemovePotionMechanic;
import io.lumine.mythic.lib.script.mechanic.offense.SetOnFireMechanic;
import io.lumine.mythic.lib.script.mechanic.player.GiveItemMechanic;
import io.lumine.mythic.lib.script.mechanic.player.SudoMechanic;
import io.lumine.mythic.lib.script.mechanic.projectile.ShootArrowMechanic;
import io.lumine.mythic.lib.script.mechanic.projectile.ShulkerBulletMechanic;
import io.lumine.mythic.lib.script.mechanic.raytrace.RayTraceBlocksMechanic;
import io.lumine.mythic.lib.script.mechanic.raytrace.RayTraceEntitiesMechanic;
import io.lumine.mythic.lib.script.mechanic.shaped.HelixMechanic;
import io.lumine.mythic.lib.script.mechanic.shaped.LineMechanic;
import io.lumine.mythic.lib.script.mechanic.shaped.ParabolaMechanic;
import io.lumine.mythic.lib.script.mechanic.shaped.ProjectileMechanic;
import io.lumine.mythic.lib.script.mechanic.shaped.RayTraceMechanic;
import io.lumine.mythic.lib.script.mechanic.shaped.SlashMechanic;
import io.lumine.mythic.lib.script.mechanic.shaped.SphereMechanic;
import io.lumine.mythic.lib.script.mechanic.variable.IncrementMechanic;
import io.lumine.mythic.lib.script.mechanic.variable.SetBooleanMechanic;
import io.lumine.mythic.lib.script.mechanic.variable.SetDoubleMechanic;
import io.lumine.mythic.lib.script.mechanic.variable.SetIntegerMechanic;
import io.lumine.mythic.lib.script.mechanic.variable.SetStringMechanic;
import io.lumine.mythic.lib.script.mechanic.variable.SetVectorMechanic;
import io.lumine.mythic.lib.script.mechanic.variable.vector.AddVectorMechanic;
import io.lumine.mythic.lib.script.mechanic.variable.vector.CopyVectorMechanic;
import io.lumine.mythic.lib.script.mechanic.variable.vector.CrossProductMechanic;
import io.lumine.mythic.lib.script.mechanic.variable.vector.DotProductMechanic;
import io.lumine.mythic.lib.script.mechanic.variable.vector.HadamardProductMechanic;
import io.lumine.mythic.lib.script.mechanic.variable.vector.MultiplyVectorMechanic;
import io.lumine.mythic.lib.script.mechanic.variable.vector.NormalizeVectorMechanic;
import io.lumine.mythic.lib.script.mechanic.variable.vector.OrientVectorMechanic;
import io.lumine.mythic.lib.script.mechanic.variable.vector.SetXMechanic;
import io.lumine.mythic.lib.script.mechanic.variable.vector.SetYMechanic;
import io.lumine.mythic.lib.script.mechanic.variable.vector.SetZMechanic;
import io.lumine.mythic.lib.script.mechanic.variable.vector.SubtractVectorMechanic;
import io.lumine.mythic.lib.script.mechanic.visual.ParticleMechanic;
import io.lumine.mythic.lib.script.mechanic.visual.PlayerSoundMechanic;
import io.lumine.mythic.lib.script.mechanic.visual.SoundMechanic;
import io.lumine.mythic.lib.script.mechanic.visual.TellMechanic;
import io.lumine.mythic.lib.script.targeter.EntityTargeter;
import io.lumine.mythic.lib.script.targeter.LocationTargeter;
import io.lumine.mythic.lib.script.targeter.entity.CasterTargeter;
import io.lumine.mythic.lib.script.targeter.entity.ConeTargeter;
import io.lumine.mythic.lib.script.targeter.entity.LookingAtTargeter;
import io.lumine.mythic.lib.script.targeter.entity.NearbyEntitiesTargeter;
import io.lumine.mythic.lib.script.targeter.entity.NearestEntityTargeter;
import io.lumine.mythic.lib.script.targeter.entity.TargetTargeter;
import io.lumine.mythic.lib.script.targeter.entity.VariableEntityTargeter;
import io.lumine.mythic.lib.script.targeter.location.CasterLocationTargeter;
import io.lumine.mythic.lib.script.targeter.location.CircleLocationTargeter;
import io.lumine.mythic.lib.script.targeter.location.CustomLocationTargeter;
import io.lumine.mythic.lib.script.targeter.location.SourceLocationTargeter;
import io.lumine.mythic.lib.script.targeter.location.TargetEntityLocationTargeter;
import io.lumine.mythic.lib.script.targeter.location.TargetLocationTargeter;
import io.lumine.mythic.lib.script.targeter.location.VariableLocationTargeter;
import io.lumine.mythic.lib.skill.handler.FabledSkillHandler;
import io.lumine.mythic.lib.skill.handler.MythicLibSkillHandler;
import io.lumine.mythic.lib.skill.handler.MythicMobsSkillHandler;
import io.lumine.mythic.lib.skill.handler.SkillHandler;
import io.lumine.mythic.lib.util.FileUtils;
import io.lumine.mythic.lib.util.PostLoadException;
import io.lumine.mythic.lib.util.configobject.ConfigObject;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SkillManager {
   private final Map<String, Function<ConfigObject, Mechanic>> mechanics = new HashMap<>();
   private final Map<String, Function<ConfigObject, Condition>> conditions = new HashMap<>();
   private final Map<String, Function<ConfigObject, EntityTargeter>> entityTargets = new HashMap<>();
   private final Map<String, Function<ConfigObject, LocationTargeter>> locationTargets = new HashMap<>();
   private final Map<String, Script> scripts = new HashMap<>();
   private final Map<String, SkillHandler> handlers = new HashMap<>();
   private final Map<Predicate<ConfigurationSection>, Function<ConfigurationSection, SkillHandler>> skillHandlerTypes = new HashMap<>();
   private boolean registration = true;

   public SkillManager() {
      this.registerMechanic("add_stat", config -> new AddStatModifierMechanic(config));
      this.registerMechanic("remove_stat", config -> new RemoveStatModifierMechanic(config));
      this.registerMechanic("feed", config -> new FeedMechanic(config));
      this.registerMechanic("heal", config -> new HealMechanic(config));
      this.registerMechanic("reduce_cooldown", config -> new ReduceCooldownMechanic(config));
      this.registerMechanic("saturate", config -> new SaturateMechanic(config));
      this.registerMechanic("apply_cooldown", ApplyCooldownMechanic::new);
      this.registerMechanic("delay", DelayMechanic::new);
      this.registerMechanic("dispatch_command", DispatchCommandMechanic::new);
      this.registerMechanic("entity_effect", EntityEffectMechanic::new);
      this.registerMechanic("lightning", config -> new LightningStrikeMechanic(config));
      this.registerMechanic("script", config -> new ScriptMechanic(config), "skill", "cast");
      this.registerMechanic("teleport", config -> new TeleportMechanic(config));
      this.registerMechanic("set_velocity", config -> new VelocityMechanic(config));
      this.registerMechanic("additive_damage_buff", config -> new AdditiveDamageBuffMechanic(config));
      this.registerMechanic("damage", config -> new DamageMechanic(config));
      this.registerMechanic("multiply_damage", config -> new MultiplyDamageMechanic(config));
      this.registerMechanic("potion", config -> new PotionMechanic(config));
      this.registerMechanic("remove_potion", config -> new RemovePotionMechanic(config));
      this.registerMechanic("set_on_fire", config -> new SetOnFireMechanic(config));
      this.registerMechanic("give_item", config -> new GiveItemMechanic(config));
      this.registerMechanic("sudo", config -> new SudoMechanic(config));
      this.registerMechanic("shoot_arrow", config -> new ShootArrowMechanic(config), "fire_arrow", "bowshoot", "bow_shoot", "shoot_bow");
      this.registerMechanic("shulker_bullet", config -> new ShulkerBulletMechanic(config));
      this.registerMechanic("raytrace_blocks", config -> new RayTraceBlocksMechanic(config));
      this.registerMechanic("raytrace_entities", config -> new RayTraceEntitiesMechanic(config));
      this.registerMechanic("draw_helix", config -> new HelixMechanic(config), "helix");
      this.registerMechanic("draw_line", LineMechanic::new, "line");
      this.registerMechanic("draw_parabola", ParabolaMechanic::new, "parabola", "spawn_parabola");
      this.registerMechanic("projectile", config -> new ProjectileMechanic(config));
      this.registerMechanic("ray_trace", config -> new RayTraceMechanic(config), "cast_ray", "raytrace", "ray_cast", "raycast");
      this.registerMechanic("slash", config -> new SlashMechanic(config));
      this.registerMechanic("draw_sphere", config -> new SphereMechanic(config), "sphere");
      this.registerMechanic("add_vector", config -> new AddVectorMechanic(config), "add_vec");
      this.registerMechanic("cross_product", config -> new CrossProductMechanic(config));
      this.registerMechanic("dot_product", config -> new DotProductMechanic(config));
      this.registerMechanic("hadamard_product", config -> new HadamardProductMechanic(config));
      this.registerMechanic("multiply_vector", config -> new MultiplyVectorMechanic(config));
      this.registerMechanic("normalize_vector", config -> new NormalizeVectorMechanic(config), "normalize");
      this.registerMechanic("orient_vector", config -> new OrientVectorMechanic(config), "orient_vec");
      this.registerMechanic("save_vector", config -> new CopyVectorMechanic(config), "save_vec", "copy_vec", "copy_vector");
      this.registerMechanic("set_x", config -> new SetXMechanic(config));
      this.registerMechanic("set_y", config -> new SetYMechanic(config));
      this.registerMechanic("set_z", config -> new SetZMechanic(config));
      this.registerMechanic("subtract_vector", config -> new SubtractVectorMechanic(config), "sub_vec", "sub_vector", "subvec");
      this.registerMechanic("increment", config -> new IncrementMechanic(config), "incr");
      this.registerMechanic("set_boolean", config -> new SetBooleanMechanic(config), "set_bool");
      this.registerMechanic("set_double", config -> new SetDoubleMechanic(config), "set_float");
      this.registerMechanic("set_integer", config -> new SetIntegerMechanic(config), "set_int");
      this.registerMechanic("set_string", config -> new SetStringMechanic(config), "set_str");
      this.registerMechanic("set_vector", config -> new SetVectorMechanic(config), "set_vec");
      this.registerMechanic("particle", config -> new ParticleMechanic(config), "spawn_particle", "par");
      this.registerMechanic("sound", config -> new SoundMechanic(config), "play_world_sound", "play_sound", "world_sound");
      this.registerMechanic("player_sound", config -> new PlayerSoundMechanic(config), "play_player_sound");
      this.registerMechanic("tell", config -> new TellMechanic(config), "message", "msg", "send", "send_message", "send_msg");
      this.registerEntityTargeter("caster", config -> new CasterTargeter());
      this.registerEntityTargeter("cone", config -> new ConeTargeter(config));
      this.registerEntityTargeter("nearby_entities", config -> new NearbyEntitiesTargeter(config));
      this.registerEntityTargeter("nearest_entity", config -> new NearestEntityTargeter(config));
      this.registerEntityTargeter("target", config -> new TargetTargeter());
      this.registerEntityTargeter("variable", config -> new VariableEntityTargeter(config));
      this.registerEntityTargeter("looking_at", config -> new LookingAtTargeter(config));
      this.registerLocationTargeter("caster", config -> new CasterLocationTargeter(config));
      this.registerLocationTargeter("circle", config -> new CircleLocationTargeter(config));
      this.registerLocationTargeter("custom", config -> new CustomLocationTargeter(config));
      this.registerLocationTargeter("looking_at", config -> new io.lumine.mythic.lib.script.targeter.location.LookingAtTargeter(config));
      this.registerLocationTargeter("source_location", config -> new SourceLocationTargeter());
      this.registerLocationTargeter("target", config -> new TargetEntityLocationTargeter(config));
      this.registerLocationTargeter("target_location", config -> new TargetLocationTargeter());
      this.registerLocationTargeter("variable", config -> new VariableLocationTargeter(config));
      this.registerCondition("boolean", config -> new BooleanCondition(config));
      this.registerCondition("compare", config -> new CompareCondition(config));
      this.registerCondition("in_between", config -> new InBetweenCondition(config));
      this.registerCondition("string_equals", config -> new StringEqualsCondition(config));
      this.registerCondition("biome", config -> new BiomeCondition(config));
      this.registerCondition("cuboid", config -> new CuboidCondition(config));
      this.registerCondition("distance", config -> new DistanceCondition(config));
      this.registerCondition("world", config -> new WorldCondition(config));
      this.registerCondition("can_target", config -> new CanTargetCondition(config), "can_tgt", "cantarget", "ctgt");
      this.registerCondition("cooldown", config -> new CooldownCondition(config));
      this.registerCondition("food", config -> new FoodCondition(config));
      this.registerCondition("ammo", config -> new HasAmmoMechanic(config));
      this.registerCondition("has_damage_type", config -> new HasDamageTypeCondition(config));
      this.registerCondition("is_living", config -> new IsLivingCondition(config));
      this.registerCondition("on_fire", config -> new OnFireCondition(config));
      this.registerCondition("permission", config -> new PermissionCondition(config));
      this.registerCondition("time", config -> new TimeCondition(config));
      this.registerSkillHandlerType(
         config -> config.contains("mythiclib-skill-id"),
         config -> new MythicLibSkillHandler(config, this.getScriptOrThrow(config.getString("mythiclib-skill-id")))
      );
      this.registerSkillHandlerType(config -> config.contains("mechanics"), config -> new MythicLibSkillHandler(new Script(config)));
   }

   public void registerSkillHandlerType(Predicate<ConfigurationSection> matcher, Function<ConfigurationSection, SkillHandler> provider) {
      Validate.notNull(matcher);
      Validate.notNull(provider);
      this.skillHandlerTypes.put(matcher, provider);
   }

   @NotNull
   public SkillHandler<?> loadSkillHandler(Object obj) throws IllegalArgumentException, IllegalStateException {
      if (obj instanceof String) {
         return this.getHandlerOrThrow(UtilityMethods.enumName((String)obj));
      }

      if (obj instanceof ConfigurationSection) {
         ConfigurationSection config = (ConfigurationSection)obj;

         for (Entry<Predicate<ConfigurationSection>, Function<ConfigurationSection, SkillHandler>> type : this.skillHandlerTypes.entrySet()) {
            if (type.getKey().test(config)) {
               return type.getValue().apply(config);
            }
         }

         throw new IllegalArgumentException("Could not match handler type to config");
      } else {
         throw new IllegalArgumentException("Provide either a string or configuration section instead of " + obj.getClass().getSimpleName());
      }
   }

   public void registerSkillHandler(SkillHandler<?> handler) {
      Validate.isTrue(this.handlers.putIfAbsent(handler.getId(), handler) == null, "A skill handler with the same name already exists");
      if (!this.registration && handler instanceof Listener) {
         Bukkit.getPluginManager().registerEvents((Listener)handler, MythicLib.plugin);
      }
   }

   @NotNull
   public SkillHandler<?> getHandlerOrThrow(String id) {
      return Objects.requireNonNull(this.handlers.get(id), "Could not find handler with ID '" + id + "'");
   }

   public Collection<SkillHandler> getHandlers() {
      return this.handlers.values();
   }

   @Nullable
   public SkillHandler getHandler(String handlerId) {
      return this.handlers.get(handlerId);
   }

   public void registerScript(@NotNull Script script) {
      Validate.isTrue(!this.scripts.containsKey(script.getId()), "A script with the same name already exists");
      this.scripts.put(script.getId(), script);
   }

   @NotNull
   public Script getScriptOrThrow(String name) {
      return Objects.requireNonNull(this.scripts.get(name), "Could not find script with name '" + name + "'");
   }

   @NotNull
   public Script loadScript(Object obj) {
      if (obj instanceof String) {
         return this.getScriptOrThrow(obj.toString());
      } else if (obj instanceof ConfigurationSection) {
         Script skill = new Script((ConfigurationSection)obj);
         skill.getPostLoadAction().performAction();
         return skill;
      } else {
         throw new IllegalArgumentException("Provide either a string or configuration section");
      }
   }

   @NotNull
   public Collection<Script> getScripts() {
      return this.scripts.values();
   }

   @NotNull
   private String findEffectiveObjectType(String objectType, ConfigObject config) {
      if (config.contains("type")) {
         return config.getString("type");
      } else if (config.hasKey()) {
         return config.getKey();
      } else {
         throw new IllegalArgumentException("Could not find " + objectType + " type");
      }
   }

   public void registerCondition(String name, Function<ConfigObject, Condition> condition, String... aliases) {
      Validate.isTrue(this.registration, "Condition registration is disabled");
      Validate.isTrue(!this.conditions.containsKey(name), "A condition with the same name already exists");
      Validate.notNull(condition, "Function cannot be null");
      this.conditions.put(name, condition);

      for (String alias : aliases) {
         this.registerCondition(alias, condition);
      }
   }

   @NotNull
   public Condition loadCondition(ConfigObject config) {
      String key = this.findEffectiveObjectType("condition", config);
      Function<ConfigObject, Condition> supplier = this.conditions.get(key);
      Validate.notNull(supplier, "Could not match condition to '" + key + "'");
      return supplier.apply(config);
   }

   public void registerMechanic(@NotNull String name, @NotNull Function<ConfigObject, Mechanic> mechanic, String... aliases) {
      Validate.isTrue(this.registration, "Mechanic registration is disabled");
      Validate.isTrue(!this.mechanics.containsKey(name), "A mechanic with the name '" + name + "' already exists");
      Validate.notNull(mechanic, "Function cannot be null");
      this.mechanics.put(name, mechanic);

      for (String alias : aliases) {
         this.registerMechanic(alias, mechanic);
      }
   }

   @NotNull
   public Mechanic loadMechanic(ConfigObject config) {
      String key = this.findEffectiveObjectType("mechanic", config);
      Function<ConfigObject, Mechanic> supplier = this.mechanics.get(key);
      Validate.notNull(supplier, "Could not match mechanic to '" + key + "'");
      return supplier.apply(config);
   }

   public void registerEntityTargeter(String name, Function<ConfigObject, EntityTargeter> entityTarget) {
      Validate.isTrue(this.registration, "Targeter registration is disabled");
      Validate.isTrue(!this.entityTargets.containsKey(name), "A targeter with the same name already exists");
      Validate.notNull(entityTarget, "Function cannot be null");
      this.entityTargets.put(name, entityTarget);
   }

   @NotNull
   public EntityTargeter loadEntityTargeter(ConfigObject config) {
      String key = this.findEffectiveObjectType("targeter", config);
      Function<ConfigObject, EntityTargeter> supplier = this.entityTargets.get(key);
      Validate.notNull(supplier, "Could not match targeter to '" + key + "'");
      return supplier.apply(config);
   }

   public void registerLocationTargeter(String name, Function<ConfigObject, LocationTargeter> locationTarget) {
      Validate.isTrue(this.registration, "Targeter registration is disabled");
      Validate.isTrue(!this.locationTargets.containsKey(name), "A targeter with the same name already exists");
      Validate.notNull(locationTarget, "Function cannot be null");
      this.locationTargets.put(name, locationTarget);
   }

   @NotNull
   public LocationTargeter loadLocationTargeter(ConfigObject config) {
      String key = this.findEffectiveObjectType("targeter", config);
      Function<ConfigObject, LocationTargeter> supplier = this.locationTargets.get(key);
      Validate.notNull(supplier, "Could not match targeter to '" + key + "'");
      return supplier.apply(config);
   }

   public void initialize(boolean clearBefore) {
      if (clearBefore) {
         for (SkillHandler<?> handler : this.handlers.values()) {
            if (handler instanceof Listener) {
               HandlerList.unregisterAll((Listener)handler);
            }
         }

         this.handlers.clear();
         this.scripts.clear();
      } else {
         this.registration = false;
         File skillsFolder = new File(MythicLib.plugin.getDataFolder() + "/skill");
         if (!skillsFolder.exists()) {
            skillsFolder.mkdir();
         }

         File scriptFolder = new File(MythicLib.plugin.getDataFolder() + "/script");
         if (!scriptFolder.exists()) {
            UtilityMethods.loadDefaultFile("script", "elemental_attacks.yml");
            UtilityMethods.loadDefaultFile("script", "mmoitems_scripts.yml");
            UtilityMethods.loadDefaultFile("script", "example_skills.yml");
         }

         if (Bukkit.getPluginManager().getPlugin("MythicMobs") != null) {
            this.registerSkillHandlerType(config -> config.contains("mythicmobs-skill-id"), MythicMobsSkillHandler::new);
         }

         if (Bukkit.getPluginManager().getPlugin("Fabled") != null) {
            this.registerSkillHandlerType(config -> config.contains("fabled-skill-id") || config.contains("skillapi-skill-id"), FabledSkillHandler::new);
         }
      }

      try {
         JarFile file = new JarFile(MythicLib.plugin.getJarFile());
         Enumeration<JarEntry> enu = file.entries();

         while (enu.hasMoreElements()) {
            String name = enu.nextElement().getName().replace("/", ".");
            if (!name.contains("$") && name.endsWith(".class") && name.startsWith("io.lumine.mythic.lib.skill.handler.def.")) {
               SkillHandler<?> ability = (SkillHandler<?>)Class.forName(name.substring(0, name.length() - 6)).getDeclaredConstructor().newInstance();
               this.registerSkillHandler(ability);
            }
         }

         file.close();
      } catch (IOException | InstantiationException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException exception) {
         exception.printStackTrace();
      }

      FileUtils.loadObjectsFromFolder(
         MythicLib.plugin,
         "script",
         false,
         (key, config) -> this.registerScript(new Script(Objects.requireNonNull(config, "Config is null"))),
         "Could not load script '%s' from file '%s': '%s'"
      );

      for (Script script : this.scripts.values()) {
         try {
            ConfigurationSection config = script.getPostLoadAction().getCachedConfig();
            script.getPostLoadAction().performAction();
            this.registerSkillHandler(new MythicLibSkillHandler(config, script));
         } catch (PostLoadException var6) {
         } catch (RuntimeException exception) {
            MythicLib.plugin.getLogger().log(Level.WARNING, "Could not load script '" + script.getId() + "': " + exception.getMessage());
         }
      }

      FileUtils.loadObjectsFromFolderRaw(
         MythicLib.plugin,
         "skill",
         file -> {
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            if (config.contains("modifiers")) {
               try {
                  this.registerSkillHandler(this.loadSkillHandler(YamlConfiguration.loadConfiguration(file)));
               } catch (RuntimeException exception) {
                  MythicLib.plugin.getLogger().log(Level.WARNING, "Could not load skill handler '" + file.getName() + "': " + exception.getMessage());
               }
            } else {
               for (String key : config.getKeys(false)) {
                  try {
                     this.registerSkillHandler(this.loadSkillHandler(config.getConfigurationSection(key)));
                  } catch (RuntimeException exception) {
                     MythicLib.plugin
                        .getLogger()
                        .log(Level.WARNING, "Could not load skill handler '" + key + "' from file '" + file.getName() + "': " + exception.getMessage());
                  }
               }
            }
         },
         "Could not load skill '%s': %s"
      );
   }
}
