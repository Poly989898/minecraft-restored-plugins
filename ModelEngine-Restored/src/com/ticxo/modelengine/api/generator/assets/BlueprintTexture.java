package com.ticxo.modelengine.api.generator.assets;

import com.ticxo.modelengine.api.utils.data.ResourceLocation;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BlueprintTexture {
   private int id;
   private int frameWidth;
   private int frameHeight;
   private ResourceLocation path;
   private BlueprintTexture.MCMeta mcMeta;
   private String source;

   @Getter
   public static class MCMeta {
      @Setter
      private transient boolean mustGenerate;
      @Setter
      private Boolean interpolate;
      @Setter
      private Integer width;
      @Setter
      private Integer height;
      @Setter
      private Integer frametime;
      private List<Object> frames;

      public void addFrame(int frame) {
         if (this.frames == null) {
            this.frames = new ArrayList<>();
         }

         this.frames.add(frame);
      }

      public void addFrame(int index, int time) {
         if (this.frames == null) {
            this.frames = new ArrayList<>();
         }

         this.frames.add(new BlueprintTexture.MCMeta.Frame(index, time));
      }

      public record Frame(int index, int time) {
      }
   }
}
