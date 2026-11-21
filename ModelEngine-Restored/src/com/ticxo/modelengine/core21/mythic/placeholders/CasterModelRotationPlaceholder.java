package com.ticxo.modelengine.core21.mythic.placeholders;

import com.ticxo.modelengine.api.ModelEngineAPI;
import com.ticxo.modelengine.api.model.ModeledEntity;
import com.ticxo.modelengine.api.nms.entity.wrapper.BodyRotationController;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.core.skills.placeholders.PlaceholderContext;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholder.GenericPlaceholderArguments;
import io.lumine.mythic.core.skills.placeholders.types.GenericPlaceholderTypes.StringPlaceholder;
import io.lumine.mythic.core.utils.annotations.MythicPlaceholder;

@MythicPlaceholder(placeholder = "caster.model.yaw")
public class CasterModelRotationPlaceholder extends GenericPlaceholder<String> implements StringPlaceholder {
   public CasterModelRotationPlaceholder(GenericPlaceholderArguments arguments) {
      super(arguments);
   }

   public String applyWithMetaKeywords(PlaceholderContext placeholderContext) {
      SkillCaster caster = placeholderContext.meta().getCaster();
      ModeledEntity model = ModelEngineAPI.getModeledEntity(caster.getEntity().getUniqueId());
      if (model == null) {
         return "[No Model]";
      }

      BodyRotationController controller = model.getBase().getBodyRotationController();
      return String.valueOf(controller.getYBodyRot());
   }
}
