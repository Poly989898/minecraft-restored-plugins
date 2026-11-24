package io.lumine.mythic.lib.skill.handler;

public @interface SkillHandlerInfo {
   String plugin() default "mythiclib";

   String author() default "teamrequiem";

   String version() default "1.0";
}
