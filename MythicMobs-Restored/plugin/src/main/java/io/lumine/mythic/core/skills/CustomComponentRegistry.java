package io.lumine.mythic.core.skills;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.mythic.api.drops.IDrop;
import io.lumine.mythic.api.skills.ISkillMechanic;
import io.lumine.mythic.api.skills.conditions.ISkillCondition;
import io.lumine.mythic.api.skills.placeholders.PlaceholderManager;
import io.lumine.mythic.api.skills.targeters.ISkillTargeter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.bukkit.events.MythicConditionLoadEvent;
import io.lumine.mythic.bukkit.events.MythicDropLoadEvent;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import io.lumine.mythic.bukkit.events.MythicPlaceholdersLoadEvent;
import io.lumine.mythic.bukkit.events.MythicTargeterLoadEvent;
import io.lumine.mythic.bukkit.utils.Events;
import io.lumine.mythic.bukkit.utils.Schedulers;
import io.lumine.mythic.core.logging.MythicLogger;
import io.lumine.mythic.core.skills.placeholders.PlaceholderExecutor;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywordRegistrar;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.serialization.PlaceholderClassSerializationRegistry;
import io.lumine.mythic.core.utils.annotations.AnnotationUtil;
import io.lumine.mythic.core.utils.annotations.MythicCondition;
import io.lumine.mythic.core.utils.annotations.MythicDrop;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicTargeter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.bukkit.event.Event;
import org.bukkit.plugin.java.JavaPlugin;

public class CustomComponentRegistry {
   private final Map<String, Constructor<? extends IDrop>> drops = Maps.newConcurrentMap();
   private final Map<String, Constructor<? extends ISkillMechanic>> mechanics = Maps.newConcurrentMap();
   private final Map<String, Constructor<? extends ISkillCondition>> conditions = Maps.newConcurrentMap();
   private final Map<String, Constructor<? extends ISkillTargeter>> targeters = Maps.newConcurrentMap();
   private final Collection<Class<?>> placeholders = Lists.newArrayList();
   private final JavaPlugin plugin;
   private final List<String> pluginNamespaces = Lists.newArrayList();

   public CustomComponentRegistry(JavaPlugin plugin, String packagePath) {
      this(plugin, Lists.newArrayList(), Lists.newArrayList(new String[]{packagePath}));
   }

   public CustomComponentRegistry(JavaPlugin plugin, Collection<String> packages) {
      this(plugin, Lists.newArrayList(), packages);
   }

   public CustomComponentRegistry(JavaPlugin plugin, Collection<String> namespaces, Collection<String> packages) {
      this.plugin = plugin;
      this.pluginNamespaces.add(plugin.getName().toUpperCase());

      for (String namespace : namespaces) {
         this.pluginNamespaces.add(namespace.toUpperCase());
      }

      for (String packagePath : packages) {
         this.registerComponent(this.drops, packagePath, MythicDrop.class, IDrop.class, MythicDropLoadEvent.class);
         this.registerComponent(this.mechanics, packagePath, MythicMechanic.class, ISkillMechanic.class, MythicMechanicLoadEvent.class);
         this.registerComponent(this.conditions, packagePath, MythicCondition.class, ISkillCondition.class, MythicConditionLoadEvent.class);
         this.registerComponent(this.targeters, packagePath, MythicTargeter.class, ISkillTargeter.class, MythicTargeterLoadEvent.class);
         this.registerComponentClasses(this.placeholders, packagePath, MythicPlaceholder.class);
      }

      Events.subscribe(MythicDropLoadEvent.class).handler(event -> {
         String name = event.getDropName().toUpperCase();
         if (this.drops.containsKey(name)) {
            Constructor<? extends IDrop> constructor = this.drops.get(name);

            try {
               if (constructor.getParameterCount() == 2) {
                  event.register(constructor.newInstance(plugin, event));
               } else {
                  event.register(constructor.newInstance(event));
               }
            } catch (Exception e) {
               MythicLogger.error("Failed to construct drop {0}", name);
               e.printStackTrace();
            }
         }
      });
      Events.subscribe(MythicMechanicLoadEvent.class).handler(event -> {
         String name = event.getMechanicName().toUpperCase();
         if (this.mechanics.containsKey(name)) {
            Constructor<? extends ISkillMechanic> constructor = this.mechanics.get(name);

            try {
               if (constructor.getParameterCount() == 2) {
                  event.register(constructor.newInstance(plugin, event));
               } else {
                  event.register(constructor.newInstance(event));
               }
            } catch (Exception ex) {
               MythicLogger.error("Failed to construct mechanic {0}", name);
               ex.printStackTrace();
            }
         }
      });
      Events.subscribe(MythicConditionLoadEvent.class).handler(event -> {
         String name = event.getConditionName().toUpperCase();
         if (this.conditions.containsKey(name)) {
            Constructor<? extends ISkillCondition> constructor = this.conditions.get(name);

            try {
               if (constructor.getParameterCount() == 2) {
                  event.register(constructor.newInstance(plugin, event));
               } else {
                  event.register(constructor.newInstance(event));
               }
            } catch (Exception ex2) {
               MythicLogger.error("Failed to construct condition {0}", name);
               ex2.printStackTrace();
            }
         }
      });
      Events.subscribe(MythicTargeterLoadEvent.class).handler(event -> {
         String name = event.getTargeterName().toUpperCase();
         if (this.targeters.containsKey(name)) {
            Constructor<? extends ISkillTargeter> constructor = this.targeters.get(name);

            try {
               if (constructor.getParameterCount() == 2) {
                  event.register(constructor.newInstance(plugin, event));
               } else {
                  event.register(constructor.newInstance(event));
               }
            } catch (Exception ex3) {
               MythicLogger.error("Failed to construct targeter {0}", name);
               ex3.printStackTrace();
            }
         }
      });
      Events.subscribe(MythicPlaceholdersLoadEvent.class).handler(this::injectPlaceholders);
      Schedulers.sync().runLater(() -> {
         PlaceholderExecutor placeholderManager = MythicBukkit.inst().getPlaceholderManager();
         if (placeholderManager != null && placeholderManager.isInitialized()) {
            this.injectPlaceholders(placeholderManager);
         }
      }, 1L);
   }

   public CustomComponentRegistry registerCustomComponent(CustomComponentRegistry.MythicComponentType type, String packagePath) {
      return switch (type) {
         case DROPS -> {
            this.registerComponent(this.drops, packagePath, MythicDrop.class, IDrop.class, MythicDropLoadEvent.class);
            yield this;
         }
         case MECHANIC -> {
            this.registerComponent(this.mechanics, packagePath, MythicMechanic.class, ISkillMechanic.class, MythicMechanicLoadEvent.class);
            yield this;
         }
         case CONDITION -> {
            this.registerComponent(this.conditions, packagePath, MythicCondition.class, ISkillCondition.class, MythicConditionLoadEvent.class);
            yield this;
         }
         case TARGETER -> {
            this.registerComponent(this.targeters, packagePath, MythicTargeter.class, ISkillTargeter.class, MythicTargeterLoadEvent.class);
            yield this;
         }
         case PLACEHOLDER -> {
            this.registerComponentClasses(this.placeholders, packagePath, MythicPlaceholder.class);
            yield this;
         }
         case METAKEYWORD -> {
            MetaKeywordRegistrar.registerAll(this.plugin, packagePath);
            yield this;
         }
         case PLACEHOLDERSERIALIZER -> {
            PlaceholderClassSerializationRegistry.registerAll(this.plugin, packagePath);
            MetaKeywords.registerConversionMetaKeywords();
            yield this;
         }
      };
   }

   private void registerComponentClasses(Collection<Class<?>> collection, String packagePath, Class<? extends Annotation> annotation) {
      Collection<Class<?>> classes = AnnotationUtil.getAnnotatedClasses(this.plugin, packagePath, annotation);
      collection.addAll(classes);
   }

   private <A extends Annotation, B, E extends Event> void registerComponent(
      Map<String, Constructor<? extends B>> map, String packagePath, Class<A> annotationClass, Class<B> annotatedClass, Class<E> eventClass
   ) {
      for (Class<?> clazz : AnnotationUtil.getAnnotatedClasses(this.plugin, packagePath, annotationClass)) {
         try {
            A annotation = clazz.getAnnotation(annotationClass);
            Method nameMethod = annotationClass.getMethod("name");
            Method aliasesMethod = annotationClass.getMethod("aliases");
            String name = (String)nameMethod.invoke(annotation);
            String[] aliases = (String[])aliasesMethod.invoke(annotation);
            if (annotatedClass.isAssignableFrom(clazz)) {
               Constructor<B> constructor = null;

               try {
                  constructor = (Constructor<B>)clazz.getConstructor(this.plugin.getClass(), eventClass);
               } catch (NoSuchMethodException e) {
                  constructor = (Constructor<B>)clazz.getConstructor(eventClass);
               }

               map.put(name.toUpperCase(), constructor);

               for (String alias : aliases) {
                  map.put(alias.toUpperCase(), constructor);
               }

               for (String namespace : this.pluginNamespaces) {
                  map.put((namespace + ":" + name).toUpperCase(), constructor);

                  for (String alias : aliases) {
                     map.put((alias + ":" + name).toUpperCase(), constructor);
                  }
               }
            }
         } catch (Throwable ex) {
            MythicLogger.error("Failed to register {0} {1}", annotatedClass.getCanonicalName(), clazz.getCanonicalName());
            ex.printStackTrace();
         }
      }
   }

   private void injectPlaceholders(MythicPlaceholdersLoadEvent event) {
      this.injectPlaceholders(event.getManager());
   }

   private void injectPlaceholders(PlaceholderManager manager) {
      for (Class<?> clazz : this.placeholders) {
         manager.register(clazz);
      }
   }

   public static enum MythicComponentType {
      DROPS,
      MECHANIC,
      CONDITION,
      TARGETER,
      PLACEHOLDER,
      METAKEYWORD,
      PLACEHOLDERSERIALIZER;
   }
}
