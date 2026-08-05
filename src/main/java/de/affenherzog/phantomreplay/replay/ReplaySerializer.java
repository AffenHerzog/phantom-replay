package de.affenherzog.phantomreplay.replay;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import de.affenherzog.phantomreplay.replay.action.*;

import java.lang.reflect.Type;
import java.util.List;

public class ReplaySerializer {

    private ReplaySerializer() {
        /* This utility class should not be instantiated */
    }

    private static final Gson GSON = createGson();

    private static Gson createGson() {
        RuntimeTypeAdapterFactory<ReplayAction> actionAdapterFactory =
                RuntimeTypeAdapterFactory.of(ReplayAction.class, "ty")
                        .registerSubtype(SneakAction.class, "SN")
                        .registerSubtype(SprintAction.class, "SP")
                        .registerSubtype(LeftClickAction.class, "LC")
                        .registerSubtype(ShowItemAction.class, "SI");

        return new GsonBuilder()
                .registerTypeAdapterFactory(actionAdapterFactory)
                .create();
    }

    public static String toJson(List<KeyFrame> keyFrame) {
        return GSON.toJson(keyFrame);
    }

    public static List<KeyFrame> fromJson(String json) {
        Type frameListType = new TypeToken<List<KeyFrame>>() {}.getType();
        return GSON.fromJson(json, frameListType);
    }


}
