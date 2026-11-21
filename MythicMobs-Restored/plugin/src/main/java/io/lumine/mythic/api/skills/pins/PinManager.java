package io.lumine.mythic.api.skills.pins;

import io.lumine.mythic.core.skills.pins.Pin;
import io.lumine.mythic.core.skills.pins.PinRegion;
import java.util.Optional;

public interface PinManager {
   Optional<Pin> getPin(String str);

   Optional<PinRegion> getRegion(String str);
}
