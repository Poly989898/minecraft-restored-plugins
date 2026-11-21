package io.lumine.mythic.api.drops;

import io.lumine.mythic.api.adapters.AbstractPlayer;

public interface IIntangibleDrop extends IDrop {
   void giveDrop(AbstractPlayer abstractPlayer, DropMetadata dropMetadata, double value);
}
