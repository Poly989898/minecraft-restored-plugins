package com.nexomc.libs.folialib.enums;

import com.nexomc.libs.folialib.util.ImplementationTestsUtil;
import java.util.function.Supplier;

public enum ImplementationType {
   FOLIA("FoliaImplementation", new Supplier[0], new String[]{"io.papermc.paper.threadedregions.RegionizedServer"}),
   PAPER("PaperImplementation", new Supplier[]{ImplementationTestsUtil::isTaskConsumersSupported}, new String[]{"com.destroystokyo.paper.PaperConfig", "io.papermc.paper.configuration.Configuration"}),
   LEGACY_PAPER("LegacyPaperImplementation", new Supplier[0], new String[]{"com.destroystokyo.paper.PaperConfig", "io.papermc.paper.configuration.Configuration"}),
   SPIGOT("SpigotImplementation", new Supplier[]{ImplementationTestsUtil::isTaskConsumersSupported}, new String[]{"org.spigotmc.SpigotConfig"}),
   LEGACY_SPIGOT("LegacySpigotImplementation", new Supplier[0], new String[]{"org.spigotmc.SpigotConfig"}),
   UNKNOWN("UnsupportedImplementation", new Supplier[0], new String[0]);

   private final String implementationClassName;
   private final Supplier<Boolean>[] tests;
   private final String[] classNames;

   private ImplementationType(String implementationClassName, Supplier<Boolean>[] tests, String... classNames) {
      this.implementationClassName = implementationClassName;
      this.tests = tests;
      this.classNames = classNames;
   }

   public String getImplementationClassName() {
      return this.implementationClassName;
   }

   public Supplier<Boolean>[] getTests() {
      return this.tests;
   }

   public String[] getClassNames() {
      return this.classNames;
   }

   public boolean selfCheck() {
      for(Supplier<Boolean> test : this.getTests()) {
         if (!(Boolean)test.get()) {
            return false;
         }
      }

      String[] classNames = this.getClassNames();

      for(String className : classNames) {
         try {
            Class.forName(className);
            return true;
         } catch (ClassNotFoundException var7) {
         }
      }

      return false;
   }
}
