package com.ticxo.modelengine.core21.mythic.utils;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ticxo.modelengine.api.ModelEngineAPI;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ISkillMechanic;
import io.lumine.mythic.api.skills.conditions.ISkillCondition;
import io.lumine.mythic.api.skills.targeters.ISkillTargeter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicReloadedEvent;
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.placeholders.Placeholder;
import io.lumine.mythic.core.utils.annotations.AnnotationUtil;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class ModelEngineComponentRegistry implements Listener {
   private final Map<String, Constructor<? extends ISkillMechanic>> mechanics = Maps.newConcurrentMap();
   private final Map<String, Constructor<? extends ISkillCondition>> conditions = Maps.newConcurrentMap();
   private final Map<String, Constructor<? extends ISkillTargeter>> targeters = Maps.newConcurrentMap();
   private final Map<String, Class<? extends Placeholder>> placeholders = Maps.newConcurrentMap();

   public ModelEngineComponentRegistry(JavaPlugin plugin, String packagePath) {
      this(plugin, Lists.newArrayList(new String[]{packagePath}));
   }

   public ModelEngineComponentRegistry(JavaPlugin plugin, Collection<String> packages) {
      for (String packagePath : packages) {
         for (Class<?> clazz : AnnotationUtil.getAnnotatedClasses(plugin, packagePath, MythicMechanic.class)) {
            try {
               MythicMechanic annotation = clazz.getAnnotation(MythicMechanic.class);
               String namespace = annotation.namespace();
               String name = annotation.name();
               String[] aliases = annotation.aliases();
               if (ISkillMechanic.class.isAssignableFrom(clazz)) {
                  Constructor<ISkillMechanic> constructor;
                  try {
                     constructor = (Constructor<ISkillMechanic>)clazz.getConstructor(MythicLineConfig.class);
                  } catch (NoSuchMethodException ex) {
                     try {
                        constructor = (Constructor<ISkillMechanic>)clazz.getConstructor(MythicMechanicLoadEvent.class);
                     } catch (NoSuchMethodException ex2) {
                        constructor = (Constructor<ISkillMechanic>)clazz.getConstructor();
                     }
                  }

                  this.mechanics.put(name.toUpperCase(), constructor);
                  this.mechanics.put(namespace.toUpperCase() + ":" + name.toUpperCase(), constructor);

                  for (String alias : aliases) {
                     this.mechanics.put(alias.toUpperCase(), constructor);
                     this.mechanics.put(namespace.toUpperCase() + ":" + alias.toUpperCase(), constructor);
                  }
               }
            } catch (Throwable ex) {
               MythicLogger.error("Failed to register custom mechanic {0}", new Object[]{clazz.getCanonicalName()});
               ex.printStackTrace();
            }
         }

         for (Class<?> clazz : AnnotationUtil.getAnnotatedClasses(plugin, packagePath, MythicCondition.class)) {
            try {
               MythicCondition annotation = clazz.getAnnotation(MythicCondition.class);
               String namespace = annotation.namespace();
               String name = annotation.name();
               String[] aliases = annotation.aliases();
               if (ISkillCondition.class.isAssignableFrom(clazz)) {
                  Constructor<ISkillCondition> constructor;
                  try {
                     constructor = (Constructor<ISkillCondition>)clazz.getConstructor(ModelEngineAPI.class, MythicConditionLoadEvent.class);
                  } catch (NoSuchMethodException ex) {
                     try {
                        constructor = (Constructor<ISkillCondition>)clazz.getConstructor(MythicLineConfig.class);
                     } catch (NoSuchMethodException ex2) {
                        constructor = (Constructor<ISkillCondition>)clazz.getConstructor();
                     }
                  }

                  this.conditions.put(name.toUpperCase(), constructor);
                  this.conditions.put(namespace.toUpperCase() + ":" + name.toUpperCase(), constructor);

                  for (String alias : aliases) {
                     this.conditions.put(alias.toUpperCase(), constructor);
                     this.conditions.put(namespace.toUpperCase() + ":" + alias.toUpperCase(), constructor);
                  }
               }
            } catch (Throwable ex) {
               MythicLogger.error("Failed to register custom condition {0}", new Object[]{clazz.getCanonicalName()});
               ex.printStackTrace();
            }
         }

         for (Class<?> clazz : AnnotationUtil.getAnnotatedClasses(plugin, packagePath, MythicTargeter.class)) {
            try {
               MythicTargeter annotation = clazz.getAnnotation(MythicTargeter.class);
               String namespace = annotation.namespace();
               String name = annotation.name();
               String[] aliases = annotation.aliases();
               if (ISkillTargeter.class.isAssignableFrom(clazz)) {
                  Constructor<ISkillTargeter> constructor;
                  try {
                     constructor = (Constructor<ISkillTargeter>)clazz.getConstructor(MythicLineConfig.class);
                  } catch (NoSuchMethodException ex) {
                     constructor = (Constructor<ISkillTargeter>)clazz.getConstructor();
                  }

                  this.targeters.put(name.toUpperCase(), constructor);
                  this.targeters.put(namespace.toUpperCase() + ":" + name.toUpperCase(), constructor);

                  for (String alias : aliases) {
                     this.targeters.put(alias.toUpperCase(), constructor);
                     this.targeters.put(namespace.toUpperCase() + ":" + alias.toUpperCase(), constructor);
                  }
               }
            } catch (Throwable ex) {
               MythicLogger.error("Failed to register custom targeter {0}", new Object[]{clazz.getCanonicalName()});
               ex.printStackTrace();
            }
         }

         for (Class<?> clazz : AnnotationUtil.getAnnotatedClasses(plugin, packagePath, MythicPlaceholder.class)) {
            try {
               MythicPlaceholder anno = clazz.getAnnotation(MythicPlaceholder.class);
               String name = anno.placeholder();
               String[] aliases = anno.aliases();
               if (Placeholder.class.isAssignableFrom(clazz)) {
                  this.placeholders.put(name, (Class<? extends Placeholder>)clazz);

                  for (String alias : aliases) {
                     this.placeholders.put(alias, (Class<? extends Placeholder>)clazz);
                  }
               }
            } catch (Exception ex) {
               MythicLogger.error("Failed to load placeholder {0}", new Object[]{clazz.getCanonicalName()});
            }
         }
      }

      Schedulers.sync().runLater(() -> this.injectPlaceholders(), 20L);
   }

   @EventHandler
   public void onMythicMechanicLoad(MythicMechanicLoadEvent event) {
      String name = event.getMechanicName().toUpperCase();
      if (this.mechanics.containsKey(name)) {
         Constructor<? extends ISkillMechanic> constructor = this.mechanics.get(name);

         try {
            if (constructor.getParameterCount() == 1) {
               if (constructor.getParameterTypes()[0] == MythicLineConfig.class) {
                  event.register(constructor.newInstance(event.getConfig()));
               } else {
                  event.register(constructor.newInstance(event));
               }
            } else {
               event.register(constructor.newInstance());
            }
         } catch (Exception e) {
            MythicLogger.error("Failed to construct mechanic {0}", new Object[]{name});
            e.printStackTrace();
         }
      }
   }

   @EventHandler
   public void onMythicConditionLoad(MythicConditionLoadEvent event) {
      String name = event.getConditionName().toUpperCase();
      if (this.conditions.containsKey(name)) {
         Constructor<? extends ISkillCondition> constructor = this.conditions.get(name);

         try {
            if (constructor.getParameterCount() == 2) {
               event.register(constructor.newInstance(ModelEngineAPI.getAPI(), event));
            } else if (constructor.getParameterCount() == 1) {
               event.register(constructor.newInstance(event.getConfig()));
            } else {
               event.register(constructor.newInstance());
            }
         } catch (Exception e) {
            MythicLogger.error("Failed to construct condition {0}", new Object[]{name});
            e.printStackTrace();
         }
      }
   }

   @EventHandler
   public void onMythicTargeterLoad(MythicTargeterLoadEvent event) {
      String name = event.getTargeterName().toUpperCase();
      if (this.targeters.containsKey(name)) {
         Constructor<? extends ISkillTargeter> constructor = this.targeters.get(name);

         try {
            if (constructor.getParameterCount() == 1) {
               event.register(constructor.newInstance(event.getConfig()));
            } else {
               event.register(constructor.newInstance());
            }
         } catch (Exception e) {
            MythicLogger.error("Failed to construct targeter {0}", new Object[]{name});
            e.printStackTrace();
         }
      }
   }

   @EventHandler
   public void onMythicReloaded(MythicReloadedEvent event) {
      this.injectPlaceholders();
   }

   private void injectPlaceholders() {
      for (Entry<String, Class<? extends Placeholder>> entry : this.placeholders.entrySet()) {
         String name = entry.getKey();
         Class<Placeholder> clazz = (Class<Placeholder>)entry.getValue();

         try {
            MythicBukkit.inst().getPlaceholderManager().register(clazz);
         } catch (Exception ignored) {
            MythicBukkit.inst().getPlaceholderManager().register(name, clazz);
         }
      }
   }
}
