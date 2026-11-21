package io.lumine.mythic.api.drops;

import io.lumine.mythic.api.adapters.AbstractLocation;

public interface ILocationDrop extends IDrop {
   void drop(AbstractLocation abstractLocation, DropMetadata dropMetadata, double value);
}
