package com.ticxo.modelengine.api.generator.parser;

import com.ticxo.modelengine.api.error.ErrorCollector;
import com.ticxo.modelengine.api.generator.assets.ModelAssets;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;
import it.unimi.dsi.fastutil.Pair;
import java.io.File;

public interface ModelParser {
   boolean validateFile(File file);

   Pair<ModelBlueprint, ModelAssets> generate(File file, ErrorCollector collector) throws Exception;
}
