package io.lumine.mythic.core.skills.auras.components;

import io.lumine.mythic.core.skills.auras.Aura;

public interface AuraComponentFactory {
   AuraComponent create(Aura.AuraTracker auraTracker);
}
