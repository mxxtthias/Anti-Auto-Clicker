package de.luzifer.core.api;

import de.luzifer.core.Core;
import de.luzifer.core.api.player.User;

import java.util.UUID;

public class API {

    public static User getUser(UUID uuid) {

        return User.get(uuid);

    }

    public static double getTPS() {

        return Core.TPS;

    }

}
