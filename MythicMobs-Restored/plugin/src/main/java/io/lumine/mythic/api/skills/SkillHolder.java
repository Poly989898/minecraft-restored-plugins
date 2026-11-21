package io.lumine.mythic.api.skills;

import io.lumine.mythic.api.packs.Pack;

public interface SkillHolder {
   String getInternalName();

   Pack getPack();
}
