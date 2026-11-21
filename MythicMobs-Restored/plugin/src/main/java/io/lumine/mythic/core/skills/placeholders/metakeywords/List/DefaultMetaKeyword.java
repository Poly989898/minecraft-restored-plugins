package io.lumine.mythic.core.skills.placeholders.metakeywords.List;

import com.google.common.primitives.Ints;
import io.lumine.mythic.core.skills.placeholders.metakeywords.AbstractMetaKeyword;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MetaKeywords;
import io.lumine.mythic.core.skills.placeholders.metakeywords.MythicMetaKeyword;
import io.lumine.mythic.core.skills.variables.types.ListVariable;
import java.util.List;

@MythicMetaKeyword(
   name = "default"
)
public class DefaultMetaKeyword extends AbstractMetaKeyword<List<String>, String> {
   public String extract(MetaKeywords.ExtractionHandler<List<String>, String> handler) {
      Integer ret = Ints.tryParse(handler.context().getString(0));
      return ret == null ? ListVariable.getWithPipeDefault(handler.value(), handler.context().getString(0)) : handler.value().get(ret);
   }
}
