package io.lumine.mythic.api.drops;

public interface IMessagingDrop {
   String getRewardMessage(DropMetadata dropMetadata, double value);
}
