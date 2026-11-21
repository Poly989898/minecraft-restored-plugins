package com.ticxo.modelengine.core.generator.processed;

import com.ticxo.modelengine.api.generator.assets.JavaItemModel;
import com.ticxo.modelengine.api.generator.assets.ModelAssets;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchModel;
import com.ticxo.modelengine.api.utils.math.Direction;
import com.ticxo.modelengine.api.utils.math.TMath;
import com.ticxo.modelengine.core.generator.util.ItemGroup;
import com.ticxo.modelengine.core.generator.util.RotationSolver;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;

@Getter
@RequiredArgsConstructor
public class ProcessedBone {
   private final String name;
   private final Vector3f boneOrigin;
   private final Vector3f rotation;
   private final Set<ProcessedBone.Cube> cubes = new LinkedHashSet<>();
   private final Set<ItemGroup> groups = new LinkedHashSet<>();
   private final Set<JavaItemModel> models = new LinkedHashSet<>();
   private int scale;

   public void splitModels(BlockbenchModel model, ModelAssets assets) {
      RotationSolver.solve(this.groups, this.cubes);
      float maxDistToOrigin = 0.0F;

      for (ItemGroup group : this.groups) {
         JavaItemModel jiModel = group.toJavaItemModel(this.name, model, assets);
         maxDistToOrigin = Math.max(jiModel.getMaxDistToOrigin(), maxDistToOrigin);
         this.models.add(jiModel);
      }

      for (JavaItemModel m : this.models) {
         m.setMaxDistToOrigin(maxDistToOrigin);
         this.scale = m.scaleToFit();
      }
   }

   @Getter
   public static class Cube {
      private final String name;
      private final Vector3d origin;
      private final Vector3d rotation;
      private final Quaterniond quaternion;
      private final Vector3d from;
      private final Vector3d to;
      private final Map<Direction, ProcessedBone.Face> faces;
      private final float inflate;

      public Cube(String name, Vector3d origin, Vector3d rotation, Vector3d from, Vector3d to, Map<Direction, ProcessedBone.Face> faces, float inflate) {
         this.name = name;
         this.origin = new Vector3d(origin);
         this.rotation = new Vector3d(rotation);
         this.quaternion = TMath.fromEulerZYX(rotation);
         this.from = new Vector3d(from);
         this.to = new Vector3d(to);
         this.faces = faces;
         this.inflate = inflate;
      }

      @Override
      public String toString() {
         return this.name + ": " + this.rotation.toString(NumberFormat.getInstance());
      }

      public List<Vector3d> getCorners() {
         return new ArrayList<Vector3d>() {
            {
               this.add(new Vector3d(Cube.this.from.x, Cube.this.from.y, Cube.this.from.z));
               this.add(new Vector3d(Cube.this.from.x, Cube.this.from.y, Cube.this.to.z));
               this.add(new Vector3d(Cube.this.from.x, Cube.this.to.y, Cube.this.from.z));
               this.add(new Vector3d(Cube.this.from.x, Cube.this.to.y, Cube.this.to.z));
               this.add(new Vector3d(Cube.this.to.x, Cube.this.from.y, Cube.this.from.z));
               this.add(new Vector3d(Cube.this.to.x, Cube.this.from.y, Cube.this.to.z));
               this.add(new Vector3d(Cube.this.to.x, Cube.this.to.y, Cube.this.from.z));
               this.add(new Vector3d(Cube.this.to.x, Cube.this.to.y, Cube.this.to.z));
            }
         };
      }

      public void rotate(Quaterniond rot) {
         this.from.sub(this.origin);
         this.to.sub(this.origin);
         this.origin.rotate(rot);
         this.quaternion.premul(rot);
         this.rotation.set(TMath.fixEuler(TMath.toEulerZYX(this.quaternion)));
         this.from.add(this.origin);
         this.to.add(this.origin);
      }

      @Nullable
      public JavaItemModel.JavaElement.RotationSingle rotation() {
         int zeros = 0;
         float[] origin = TMath.unwrap(this.origin);
         float[] rotation = TMath.unwrap(this.rotation);

         for (float angle : rotation) {
            zeros += angle == 0.0F ? 1 : 0;
         }

         if (zeros == 3) {
            return null;
         }

         JavaItemModel.JavaElement.RotationSingle javaRotation = new JavaItemModel.JavaElement.RotationSingle();
         int i = TMath.absMax(rotation[0], rotation[1], rotation[2]);

         javaRotation.setAxis(switch (i) {
            case 1 -> "y";
            case 2 -> "z";
            default -> "x";
         });
         float angle = Math.round(rotation[i] / 22.5F) * 22.5F;
         javaRotation.setAngle(angle);
         javaRotation.origin(origin);
         return javaRotation;
      }
   }

   public record Face(ProcessedBone.UV uv, int texture) {
      public boolean isEmpty() {
         return TMath.isSimilar(this.uv.u1(), this.uv.u2()) || TMath.isSimilar(this.uv.v1, this.uv.v2);
      }
   }

   public record UV(float u1, float v1, float u2, float v2, int rotation) {
   }
}
