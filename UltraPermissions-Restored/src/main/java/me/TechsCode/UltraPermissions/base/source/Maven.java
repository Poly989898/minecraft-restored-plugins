package me.TechsCode.UltraPermissions.base.source;

import com.google.common.base.Preconditions;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import me.TechsCode.UltraPermissions.base.SpigotTechPlugin;
import me.TechsCode.UltraPermissions.base.TechPlugin;

public class Maven {
   private static boolean initialized = false;
   private static TechPlugin plugin;
   private static final List<String> repositories = new ArrayList<>(Collections.singletonList("https://repo1.maven.org/maven2"));
   private static JarClassLoader classLoader;
   private static LocalRepository localRepository;
   private static Set<Dependency> loadedDependencies;
   private static Set<Dependency> failedDependencies;

   public Maven() {
   }

   public static void initialize(SpigotTechPlugin techPlugin) {
      if (!initialized) {
         plugin = techPlugin;
         classLoader = new JarClassLoader(techPlugin);
         localRepository = new LocalRepository(techPlugin);
         loadedDependencies = new HashSet<>();
         failedDependencies = new HashSet<>();
         initialized = true;
      }
   }

   public static void addRepository(String text) {
      Preconditions.checkArgument(initialized, "Call #initialize before using Maven");
      if (!repositories.contains(text)) {
         repositories.add(text);
      }
   }

   public static boolean loadDependency(String groupId, String artifactId, String version) {
      Dependency dependency = new Dependency(groupId, artifactId, version);
      return loadDependency(dependency);
   }

   public static boolean loadDependency(Dependency dependency) {
      Preconditions.checkArgument(initialized, "Call #initialize before using Maven");
      if (failedDependencies.contains(dependency)) {
         return false;
      }

      if (loadedDependencies.contains(dependency)) {
         return true;
      }

      File file = localRepository.getIfPresent(dependency);
      if (file == null) {
         localRepository.load(dependency, repositories);
         file = localRepository.getIfPresent(dependency);
      }

      if (file != null) {
         loadedDependencies.add(dependency);
         plugin.log("Loaded dependency " + dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion());
      } else {
         failedDependencies.add(dependency);
         plugin.log("Could not load " + dependency.getGroupId() + " / " + dependency.getArtifactId() + " on " + dependency.getVersion());
      }

      return file != null;
   }

   public static boolean isLoaded(Dependency dependency) {
      return loadedDependencies.contains(dependency);
   }
}
