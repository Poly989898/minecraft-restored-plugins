package com.nexomc.libs.folialib;

import com.nexomc.libs.folialib.enums.ImplementationType;
import com.nexomc.libs.folialib.impl.PlatformScheduler;
import com.nexomc.libs.folialib.util.FoliaLibOptions;
import com.nexomc.libs.folialib.util.InvalidTickDelayNotifier;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;

public class FoliaLib {
   private final Plugin plugin;
   private final ImplementationType implementationType;
   private final PlatformScheduler scheduler;
   private FoliaLibOptions options;
   private InvalidTickDelayNotifier invalidTickDelayNotifier;

   public FoliaLib(Plugin plugin) {
      this(plugin, new FoliaLibOptions());
   }

   public FoliaLib(Plugin plugin, FoliaLibOptions customOptions) {
      this.plugin = plugin;
      this.options = customOptions;
      this.invalidTickDelayNotifier = new InvalidTickDelayNotifier(plugin.getLogger(), this.options);
      ImplementationType foundType = ImplementationType.UNKNOWN;

      for(ImplementationType type : ImplementationType.values()) {
         if (type.selfCheck()) {
            foundType = type;
            break;
         }
      }

      this.implementationType = foundType;
      this.scheduler = this.createServerImpl(this.implementationType.getImplementationClassName());
      if (this.scheduler == null) {
         throw new IllegalStateException("Failed to create server implementation. Please report this to the FoliaLib GitHub issues page. Forks of server software may not all be supported. If you are using an unofficial fork, please report this to the fork's developers first.");
      } else {
         String originalLocation = "com,tcoded,folialib,".replace(",", ".");
         if (this.getClass().getName().startsWith(originalLocation)) {
            Logger logger = this.plugin.getLogger();
            logger.severe("****************************************************************");
            logger.severe("FoliaLib is not relocated correctly! This will cause conflicts");
            logger.severe("with other plugins using FoliaLib. Please contact the developers");
            logger.severe(String.format("of '%s' and inform them of this issue immediately!", this.plugin.getDescription().getName()));
            logger.severe("****************************************************************");
         }

      }
   }

   public ImplementationType getImplType() {
      return this.implementationType;
   }

   /** @deprecated */
   @Deprecated
   public PlatformScheduler getImpl() {
      return this.getScheduler();
   }

   public PlatformScheduler getScheduler() {
      return this.scheduler;
   }

   public boolean isFolia() {
      return this.implementationType == ImplementationType.FOLIA;
   }

   public boolean isPaper() {
      return this.implementationType == ImplementationType.PAPER || this.implementationType == ImplementationType.LEGACY_PAPER;
   }

   public boolean isSpigot() {
      return this.implementationType == ImplementationType.SPIGOT || this.implementationType == ImplementationType.LEGACY_SPIGOT;
   }

   public boolean isUnsupported() {
      return this.implementationType == ImplementationType.UNKNOWN;
   }

   public Plugin getPlugin() {
      return this.plugin;
   }

   /** @deprecated */
   @Deprecated
   public void disableInvalidTickValueWarning() {
      this.getOptions().disableNotifications();
   }

   /** @deprecated */
   @Deprecated
   public void enableInvalidTickValueDebug() {
      this.getOptions().enableInvalidTickDebugMode();
   }

   public FoliaLibOptions getOptions() {
      return this.options;
   }

   /** @deprecated */
   @Deprecated
   public InvalidTickDelayNotifier getInvalidTickDelayNotifier() {
      return this.invalidTickDelayNotifier;
   }

   private PlatformScheduler createServerImpl(String implName) {
      String basePackage = this.getClass().getPackage().getName() + ".impl.";

      try {
         return (PlatformScheduler)Class.forName(basePackage + implName).getConstructor(this.getClass()).newInstance(this);
      } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException | InstantiationException e) {
         ((ReflectiveOperationException)e).printStackTrace();
         return null;
      }
   }
}
