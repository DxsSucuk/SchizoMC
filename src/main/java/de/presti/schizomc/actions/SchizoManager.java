package de.presti.schizomc.actions;

import de.presti.schizomc.actions.impl.BlockSchizoAction;
import de.presti.schizomc.actions.impl.NPCSchizoAction;
import de.presti.schizomc.actions.impl.SchadowRealmAction;
import de.presti.schizomc.actions.impl.SoundSchizoAction;

import java.util.ArrayList;
import java.util.List;

public class SchizoManager {

    ArrayList<ISchizoAction> schizoActionList = new ArrayList<>();

    public SchizoManager() {
        schizoActionList.add(new NPCSchizoAction());
        schizoActionList.add(new SchadowRealmAction());
        schizoActionList.add(new SoundSchizoAction());
        schizoActionList.add(new BlockSchizoAction());
    }

    public List<ISchizoAction> getSchizoList() {
        return schizoActionList;
    }

    public ISchizoAction getSchizoByName(String name) {
        for (ISchizoAction action : getSchizoList()) {
            if (action.name().toLowerCase().startsWith(name.toLowerCase())) {
                return action;
            }
        }
        return null;
    }

    public List<ISchizoAction> getSchizoByTriggerSanity(float sanity) {
        List<ISchizoAction> actions = new ArrayList<>();
        getSchizoList().forEach(action -> {
            if (action.triggerSanity() >= sanity) {
                actions.add(action);
            }
        });
        return actions;
    }
}
