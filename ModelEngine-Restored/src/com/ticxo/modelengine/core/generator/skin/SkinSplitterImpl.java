package com.ticxo.modelengine.core.generator.skin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchDeserializer;
import com.ticxo.modelengine.api.generator.parser.blockbench.BlockbenchModel;
import com.ticxo.modelengine.api.generator.skin.LimbData;
import com.ticxo.modelengine.api.generator.skin.SkinSplitter;
import com.ticxo.modelengine.api.utils.TFile;
import com.ticxo.modelengine.api.utils.math.TMath;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import javax.imageio.ImageIO;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

@RequiredArgsConstructor
public class SkinSplitterImpl implements SkinSplitter {
   private static final String PLACEHOLDER_SKIN = "<placeholder.skin>";
   private final Gson gson = new GsonBuilder().registerTypeAdapter(BlockbenchModel.class, new BlockbenchDeserializer()).create();
   private final File internalSteveMapped;
   private final File internalAlexMapped;
   private PlayerMapper steveMapper;
   private PlayerMapper alexMapper;

   public void copyMapped(ModelEngineAPI plugin) {
      TFile.copyResource(plugin, this.internalSteveMapped, "internal/steve_mapped.bbmodel");
      TFile.copyResource(plugin, this.internalAlexMapped, "internal/alex_mapped.bbmodel");
   }

   @Override
   public void reloadMapping() {
      try {
         this.steveMapper = this.createSkinMaps(this.gson.fromJson(new FileReader(this.internalSteveMapped), BlockbenchModel.class));
         this.alexMapper = this.createSkinMaps(this.gson.fromJson(new FileReader(this.internalAlexMapped), BlockbenchModel.class));
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private PlayerMapper createSkinMaps(BlockbenchModel model) {
      PlayerMapper playerTemplate = new PlayerMapper();
      HashMap<Integer, String> texMap = new HashMap<>();

      for (Entry<Integer, BlockbenchModel.Texture> entry : model.getTextures().entrySet()) {
         BlockbenchModel.Texture texture = entry.getValue();
         if (!texture.getName().equals("<placeholder.skin>")) {
            BufferedImage image = TFile.toImage(texture.getSource());
            playerTemplate.registerTextureSource(texture.getName(), image);
         }

         texMap.put(entry.getKey(), texture.getName());
      }

      Map<String, BlockbenchModel.Group> groups = model.getFlatOutlinerByName();

      for (Entry<String, BlockbenchModel.Group> entry : groups.entrySet()) {
         String boneId = entry.getKey();
         if (boneId.equals("head")) {
            playerTemplate.registerLimbData(new LimbData(boneId, new Vector3f(0.0F, 0.5F, 0.0F), new Vector3f(1.0F), true));
         } else {
            BlockbenchModel.Group group = entry.getValue();
            if (!group.getElement().isEmpty()) {
               List<BlockbenchModel.Cube> cubes = group.getElement()
                  .stream()
                  .map(uuid -> model.getElements().get(uuid))
                  .map(element -> element instanceof BlockbenchModel.Cube cubex ? cubex : null)
                  .filter(Objects::nonNull)
                  .toList();
               if (!cubes.isEmpty()) {
                  Vector3f origin = new Vector3f(0.0F);
                  Vector3f scale = new Vector3f(1.0F);

                  for (BlockbenchModel.Cube cube : cubes) {
                     boolean hatLayer = cube.getName().startsWith("hat_");
                     if (!hatLayer) {
                        origin.set(cube.centerX(), cube.maxY(), cube.centerZ()).sub(TMath.wrap(group.getOrigin())).mul(-0.0625F, 0.0625F, -0.0625F);
                        scale.set(cube.width(), cube.height(), cube.depth()).mul(0.125F);
                     }

                     for (Entry<String, BlockbenchModel.Face> faceEntry : cube.getFaces().entrySet()) {
                        String texName = texMap.get(faceEntry.getValue().getTexture());
                        if (texName != null) {
                           FaceMapper.Face face = FaceMapper.Face.getFace(faceEntry.getKey(), hatLayer);
                           Float[] uv = faceEntry.getValue().getUv();
                           FaceMapper faceMapper = new FaceMapper(texName, new float[]{uv[0], uv[1], uv[2], uv[3]}, face);
                           playerTemplate.registerFaceMapper(boneId, faceMapper);
                        }
                     }
                  }

                  playerTemplate.registerLimbData(new LimbData(boneId, origin, scale, false));
               }
            }
         }
      }

      return playerTemplate;
   }

   @Override
   public LimbData getLimb(String id, boolean slim) {
      return (slim ? this.alexMapper : this.steveMapper).getLimbData(id);
   }

   @Override
   public Map<String, byte[]> splitSkin(byte[] skinData, boolean slim) {
      return this.split(skinData, slim, null);
   }

   @Override
   public Map<String, byte[]> splitSkin(byte[] skinData, boolean slim, Collection<String> limbTypes) {
      return this.split(skinData, slim, limbTypes);
   }

   private Map<String, byte[]> split(byte[] skinData, boolean slim, @Nullable Collection<String> limbTypes) {
      try {
         BufferedImage skinImage = ImageIO.read(new ByteArrayInputStream(skinData));
         HashMap<String, BufferedImage> placeholder = new HashMap<>();
         placeholder.put("<placeholder.skin>", skinImage);
         PlayerMapper mapper = slim ? this.alexMapper : this.steveMapper;
         return limbTypes == null ? mapper.generateAll(placeholder) : mapper.generate(limbTypes, placeholder);
      } catch (IOException e) {
         e.printStackTrace();
         return new HashMap<>();
      }
   }
}
