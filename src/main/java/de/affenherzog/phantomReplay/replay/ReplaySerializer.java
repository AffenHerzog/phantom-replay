package de.affenherzog.phantomReplay.replay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import de.affenherzog.phantomReplay.replay.action.*;

public class ReplaySerializer {

    final static Gson GSON = createGson();

    private static Gson createGson() {
        RuntimeTypeAdapterFactory<ReplayAction> actionAdapterFactory =
                RuntimeTypeAdapterFactory.of(ReplayAction.class, "type")
                        .registerSubtype(SneakAction.class, "SNEAK")
                        .registerSubtype(SprintAction.class, "SPRINT")
                        .registerSubtype(LeftClickAction.class, "LEFT_CLICK")
                        .registerSubtype(ShowItemAction.class, "SHOW_ITEM");

        return new GsonBuilder()
                .registerTypeAdapterFactory(actionAdapterFactory)
                .create();
    }

    public static String toJson(Replay replay) {
        return GSON.toJson(replay);
    }

    public static Replay fromJson(String json) {
        return GSON.fromJson(json, Replay.class);
    }


}
