package de.presti.schizomc.actions;

public @interface SchizoActionAnnotation {

    String name();
    int minPlayers();
    float triggerSanity();
    float triggerChance();
}
