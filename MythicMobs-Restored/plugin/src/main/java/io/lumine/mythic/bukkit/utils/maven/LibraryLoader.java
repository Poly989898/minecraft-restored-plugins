package io.lumine.mythic.bukkit.utils.maven;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import io.lumine.mythic.bukkit.utils.logging.Log;
import io.lumine.mythic.bukkit.utils.plugin.LoaderUtils;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Objects;

public final class LibraryLoader {
   private static final Supplier<URLInjector> URL_INJECTOR = Suppliers.memoize(() -> {
      try {
         Method addUrlMethod = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
         addUrlMethod.setAccessible(true);
         return (x$0, xva$1) -> addUrlMethod.invoke(x$0, xva$1);
      } catch (Exception e) {
         Log.info("Unsafe");
         return UnsafeURLInjector.create((URLClassLoader)LoaderUtils.getPlugin().getClass().getClassLoader());
      }
   });

   public static void loadAll(Object object) {
      loadAll(object.getClass());
   }

   public static void loadAll(Class<?> clazz) {
      MavenLibrary[] libs = clazz.getDeclaredAnnotationsByType(MavenLibrary.class);
      if (libs != null) {
         for (MavenLibrary lib : libs) {
            load(lib.groupId(), lib.artifactId(), lib.version(), lib.repo().url());
         }
      }
   }

   public static void load(String groupId, String artifactId, String version) {
      load(groupId, artifactId, version, "https://repo1.maven.org/maven2");
   }

   public static void load(String groupId, String artifactId, String version, String repoUrl) {
      load(new LibraryLoader.Dependency(groupId, artifactId, version, repoUrl));
   }

   public static void load(LibraryLoader.Dependency d) {
      Log.info(String.format("Loading dependency %s:%s:%s from %s", d.getGroupId(), d.getArtifactId(), d.getVersion(), d.getRepoUrl()));
      String name = d.getArtifactId() + "-" + d.getVersion();
      File saveLocation = new File(getLibFolder(), name + ".jar");
      if (!saveLocation.exists()) {
         try {
            Log.info("Dependency '" + name + "' is not already in the libraries folder. Attempting to download...");
            URL url = d.getUrl();

            try (InputStream is = url.openStream()) {
               Files.copy(is, saveLocation.toPath());
            }
         } catch (Exception e) {
            e.printStackTrace();
         }

         Log.info("Dependency '" + name + "' successfully downloaded.");
      }

      if (!saveLocation.exists()) {
         throw new RuntimeException("Unable to download dependency: " + d.toString());
      } else {
         URLClassLoader classLoader = (URLClassLoader)LoaderUtils.getPlugin().getClass().getClassLoader();

         try {
            (URL_INJECTOR.get()).addURL(classLoader, saveLocation.toURI().toURL());
         } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Unable to load dependency: " + saveLocation.toString(), ex);
         }

         Log.info("Loaded dependency '" + name + "' successfully.");
      }
   }

   private static File getLibFolder() {
      File pluginDataFolder = LoaderUtils.getPlugin().getDataFolder();
      File pluginsDir = pluginDataFolder.getParentFile();
      File loaderDir = new File(pluginsDir, "LumineUtils");
      File libs = new File(loaderDir, "libraries");
      libs.mkdirs();
      return libs;
   }

   public static final class Dependency {
      private final String groupId;
      private final String artifactId;
      private final String version;
      private final String repoUrl;

      public Dependency(String groupId, String artifactId, String version, String repoUrl) {
         this.groupId = Objects.requireNonNull(groupId, "groupId");
         this.artifactId = Objects.requireNonNull(artifactId, "artifactId");
         this.version = Objects.requireNonNull(version, "version");
         this.repoUrl = Objects.requireNonNull(repoUrl, "repoUrl");
      }

      public String getGroupId() {
         return this.groupId;
      }

      public String getArtifactId() {
         return this.artifactId;
      }

      public String getVersion() {
         return this.version;
      }

      public String getRepoUrl() {
         return this.repoUrl;
      }

      public URL getUrl() throws MalformedURLException {
         String repo = this.repoUrl;
         if (!repo.endsWith("/")) {
            repo = repo + "/";
         }

         repo = repo + "%s/%s/%s/%s-%s.jar";
         String url = String.format(repo, this.groupId.replace(".", "/"), this.artifactId, this.version, this.artifactId, this.version);
         return new URL(url);
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else {
            return !(o instanceof LibraryLoader.Dependency other)
               ? false
               : this.getGroupId().equals(other.getGroupId())
                  && this.getArtifactId().equals(other.getArtifactId())
                  && this.getVersion().equals(other.getVersion())
                  && this.getRepoUrl().equals(other.getRepoUrl());
         }
      }

      @Override
      public int hashCode() {
         int PRIME = 59;
         int result = 1;
         result = result * 59 + this.getGroupId().hashCode();
         result = result * 59 + this.getArtifactId().hashCode();
         result = result * 59 + this.getVersion().hashCode();
         return result * 59 + this.getRepoUrl().hashCode();
      }

      @Override
      public String toString() {
         return "LibraryLoader.Dependency(groupId="
            + this.getGroupId()
            + ", artifactId="
            + this.getArtifactId()
            + ", version="
            + this.getVersion()
            + ", repoUrl="
            + this.getRepoUrl()
            + ")";
      }
   }
}
