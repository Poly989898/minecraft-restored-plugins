package io.lumine.mythic.api.drops;

import io.lumine.mythic.api.adapters.AbstractPlayer;

public interface ICommandDrop extends IDrop {
   void dispatchCommand(AbstractPlayer abstractPlayer, DropMetadata dropMetadata, double value);
}
