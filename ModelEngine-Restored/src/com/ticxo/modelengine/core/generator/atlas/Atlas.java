package com.ticxo.modelengine.core.generator.atlas;

import com.ticxo.modelengine.api.utils.data.ResourceLocation;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class Atlas {
   private final List<Atlas.Source> sources = new ArrayList<>();

   public static class Directory extends Atlas.Source {
      public final String source;
      public final String prefix;

      public Directory(String location) {
         super("directory");
         this.source = location;
         this.prefix = location + "/";
      }
   }

   public static class Filter extends Atlas.Source {
      public final ResourceLocation pattern;

      public Filter(ResourceLocation pattern) {
         super("filter");
         this.pattern = pattern;
      }
   }

   public static class Single extends Atlas.Source {
      public final String resource;
      public final String sprite;

      public Single(String location) {
         super("single");
         this.resource = location;
         this.sprite = location;
      }
   }

   @AllArgsConstructor
   public static class Source {
      public final String type;
   }
}
