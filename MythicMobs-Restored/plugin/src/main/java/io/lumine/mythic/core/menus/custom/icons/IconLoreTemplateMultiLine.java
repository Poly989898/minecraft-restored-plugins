package io.lumine.mythic.core.menus.custom.icons;

import io.lumine.mythic.core.menus.custom.CustomMenuContext;
import java.util.List;

public interface IconLoreTemplateMultiLine extends IconLoreTemplateLine {
   List<String> getLine(CustomMenuContext customMenuContext);
}
