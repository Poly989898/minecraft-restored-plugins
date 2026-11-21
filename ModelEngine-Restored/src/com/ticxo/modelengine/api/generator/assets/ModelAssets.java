package com.ticxo.modelengine.api.generator.assets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModelAssets {
   private final List<BlueprintTexture> textures = new ArrayList<>();
   private final Map<String, Collection<JavaItemModel>> models = new HashMap<>();
   private String name;
}
