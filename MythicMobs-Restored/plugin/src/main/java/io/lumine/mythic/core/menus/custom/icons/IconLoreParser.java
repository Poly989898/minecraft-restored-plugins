package io.lumine.mythic.core.menus.custom.icons;

import java.util.Optional;

public interface IconLoreParser {
   Optional<IconLoreTemplateLine> parseLine(String str);
}
