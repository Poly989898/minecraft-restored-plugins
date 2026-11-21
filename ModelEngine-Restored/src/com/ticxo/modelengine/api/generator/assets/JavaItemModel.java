package com.ticxo.modelengine.api.generator.assets;

import com.ticxo.modelengine.api.utils.math.TMath;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JavaItemModel {
   private static final float DIST_DIVIDER = 0.041666668F;
   private final Map<String, String> textures = new HashMap<>();
   private final List<JavaItemModel.JavaElement> elements = new ArrayList<>();
   private transient String name;
   private transient float maxDistToOrigin = 0.0F;
   private Map<String, Map<String, float[]>> display = new HashMap<>();

   public JavaItemModel() {
      this.setDisplay(JavaDisplay.GUI, JavaDisplay.Transform.ROTATION, 30.0F, 225.0F, 0.0F);
   }

   public void setDisplay(JavaDisplay display, JavaDisplay.Transform transform, float x, float y, float z) {
      float[] array = new float[]{x, y, z};
      if (!transform.isDefault(array)) {
         transform.sanitize(array);
         this.display.computeIfAbsent(display.toString(), s -> new HashMap<>()).put(transform.toString(), array);
      }
   }

   public void addElement(JavaItemModel.JavaElement element) {
      this.elements.add(element);

      for (int i = 0; i < 3; i++) {
         this.maxDistToOrigin = Math.max(Math.max(Math.abs(element.from[i] - 8.0F), Math.abs(element.to[i] - 8.0F)), this.maxDistToOrigin);
      }
   }

   public int scaleToFit() {
      if (this.maxDistToOrigin <= 24.0F) {
         return 1;
      }

      int size = (int)Math.ceil(this.maxDistToOrigin * 0.041666668F);
      float scale = 1.0F / size;

      for (JavaItemModel.JavaElement element : this.elements) {
         float[] origin = element.getRotation() == null ? null : element.getRotation().getOrigin();

         for (int i = 0; i < 3; i++) {
            element.from[i] = TMath.clamp((element.from[i] - 8.0F) * scale + 8.0F, -16.0F, 32.0F);
            element.to[i] = TMath.clamp((element.to[i] - 8.0F) * scale + 8.0F, -16.0F, 32.0F);
            if (origin != null) {
               origin[i] = (origin[i] - 8.0F) * scale + 8.0F;
            }
         }
      }

      return size;
   }

   public void finalizeModel() {
   }

   @Getter
   @Setter
   public static class JavaElement {
      private final float[] from = new float[3];
      private final float[] to = new float[3];
      private final Map<String, JavaItemModel.JavaElement.Face> faces = new HashMap<>();
      private JavaItemModel.JavaElement.JavaRotation rotation;

      public void from(float[] origin, float[] globalFrom, float inflate) {
         this.from[0] = globalFrom[0] - origin[0] + 8.0F - inflate;
         this.from[1] = globalFrom[1] - origin[1] + 8.0F - inflate;
         this.from[2] = globalFrom[2] - origin[2] + 8.0F - inflate;
      }

      public void from(float[] origin, float inflate) {
         this.from[0] = origin[0] + 8.0F - inflate;
         this.from[1] = origin[1] + 8.0F - inflate;
         this.from[2] = origin[2] + 8.0F - inflate;
      }

      public void to(float[] origin, float[] globalTo, float inflate) {
         this.to[0] = globalTo[0] - origin[0] + 8.0F + inflate;
         this.to[1] = globalTo[1] - origin[1] + 8.0F + inflate;
         this.to[2] = globalTo[2] - origin[2] + 8.0F + inflate;
      }

      public void to(float[] origin, float inflate) {
         this.to[0] = origin[0] + 8.0F + inflate;
         this.to[1] = origin[1] + 8.0F + inflate;
         this.to[2] = origin[2] + 8.0F + inflate;
      }

      public static class Face {
         @Getter
         private final float[] uv = new float[4];
         private final int tintindex = 0;
         @Getter
         private int rotation;
         @Getter @Setter
         private String texture = "";

         public void uv(int width, int height, float[] uv) {
            float factorU = 16.0F / width;
            float factorV = 16.0F / height;
            this.uv[0] = TMath.clamp(uv[0] * factorU, 0.0F, 16.0F);
            this.uv[1] = TMath.clamp(uv[1] * factorV, 0.0F, 16.0F);
            this.uv[2] = TMath.clamp(uv[2] * factorU, 0.0F, 16.0F);
            this.uv[3] = TMath.clamp(uv[3] * factorV, 0.0F, 16.0F);
         }

         public void setRotation(Integer rotation) {
            if (rotation != null) {
               this.rotation = rotation;
            }
         }

         @Generated
         public int getTintindex() {
            return 0;
         }
      }

      interface JavaRotation {
         float[] getOrigin();
      }

      @Getter
      @Setter
      public static class RotationFree implements JavaItemModel.JavaElement.JavaRotation {
         private final float[] origin = new float[]{8.0F, 8.0F, 8.0F};
         private float x;
         private float y;
         private float z;

         public void origin(float[] origin, float[] globalOrigin) {
            this.origin[0] = globalOrigin[0] - origin[0] + 8.0F;
            this.origin[1] = globalOrigin[1] - origin[1] + 8.0F;
            this.origin[2] = globalOrigin[2] - origin[2] + 8.0F;
         }

         public void origin(float[] origin) {
            this.origin[0] = origin[0] + 8.0F;
            this.origin[1] = origin[1] + 8.0F;
            this.origin[2] = origin[2] + 8.0F;
         }

         public void rotation(float[] rotation) {
            this.x = rotation[0];
            this.y = rotation[1];
            this.z = rotation[2];
         }
      }

      @Getter
      @Setter
      public static class RotationSingle implements JavaItemModel.JavaElement.JavaRotation {
         private final float[] origin = new float[]{8.0F, 8.0F, 8.0F};
         private float angle;
         private String axis = "x";

         public void origin(float[] origin, float[] globalOrigin) {
            this.origin[0] = globalOrigin[0] - origin[0] + 8.0F;
            this.origin[1] = globalOrigin[1] - origin[1] + 8.0F;
            this.origin[2] = globalOrigin[2] - origin[2] + 8.0F;
         }

         public void origin(float[] origin) {
            this.origin[0] = origin[0] + 8.0F;
            this.origin[1] = origin[1] + 8.0F;
            this.origin[2] = origin[2] + 8.0F;
         }
      }
   }
}
