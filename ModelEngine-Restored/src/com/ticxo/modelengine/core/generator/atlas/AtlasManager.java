package com.ticxo.modelengine.core.generator.atlas;

import com.ticxo.modelengine.api.utils.TFile;
import com.ticxo.modelengine.api.utils.data.ResourceLocation;
import com.ticxo.modelengine.core.generator.ModelGeneratorImpl;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import lombok.Getter;

public class AtlasManager {
   private final ModelGeneratorImpl generator;
   private final File atlases;
   private final Set<ResourceLocation> registeredPaths = new HashSet<>();
   @Getter
   private final Atlas atlas;

   public AtlasManager(ModelGeneratorImpl generator) {
      this.generator = generator;
      this.atlases = TFile.createDirectory(generator.getPackFolder(), "assets", "minecraft", "atlases");
      this.atlas = new Atlas();
      this.reset();
   }

   public void reset() {
      this.registeredPaths.clear();
      this.atlas.getSources().clear();
      this.atlas.getSources().add(new Atlas.Directory(this.generator.getTexturePath()));
      this.atlas.getSources().add(new Atlas.Filter(new ResourceLocation("minecraft", "entity/fishing_hook")));
   }

   public synchronized int addSingle(ResourceLocation location) {
      String path = location.getPath();
      if (path.startsWith("entity") || path.startsWith("item") || path.startsWith("block")) {
         return 2;
      }

      if (!this.registeredPaths.add(location)) {
         return 1;
      }

      this.atlas.getSources().add(new Atlas.Single(location.toString()));
      return 0;
   }

   public void generateFile() {
      this.atlas
         .getSources()
         .sort(
            Comparator.<Atlas.Source>comparingInt(s -> s instanceof Atlas.Single ? 1 : 0)
               .thenComparing(s -> s instanceof Atlas.Single ? ((Atlas.Single)s).resource : "")
         );

      try {
         File file = TFile.createFile(this.atlases, "blocks.json");
         FileWriter writer = new FileWriter(file);
         writer.write(this.generator.getGson().toJson(this.atlas));
         writer.close();
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
}
