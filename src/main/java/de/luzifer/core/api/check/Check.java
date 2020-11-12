package de.luzifer.core.api.check;

import de.luzifer.core.api.player.User;

public abstract class Check {

    public abstract void onEnable();

    public abstract void execute(User user);

}
