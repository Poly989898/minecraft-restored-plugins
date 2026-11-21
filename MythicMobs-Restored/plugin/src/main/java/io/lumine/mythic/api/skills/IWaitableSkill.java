package io.lumine.mythic.api.skills;

import java.util.concurrent.CompletableFuture;

public interface IWaitableSkill {
   CompletableFuture<Boolean> completableExecution(SkillMetadata skillMetadata);
}
