package com.ticxo.modelengine.api.generator.parser.blockbench;

import com.ticxo.modelengine.api.error.ErrorCollector;
import com.ticxo.modelengine.api.generator.blueprint.BlueprintBone;
import com.ticxo.modelengine.api.generator.blueprint.ModelBlueprint;

public interface BlockbenchBehaviorParser {
   void processModel(ErrorCollector collector, BlockbenchModel model, ModelBlueprint blueprint);

   void processBone(ErrorCollector collector, BlockbenchModel model, BlockbenchModel.Group group, BlueprintBone bone);
}
