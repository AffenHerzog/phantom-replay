package de.affenherzog.phantomreplay.replay;

import de.affenherzog.phantomreplay.replay.action.LeftClickAction;
import de.affenherzog.phantomreplay.replay.action.ReplayAction;
import de.affenherzog.phantomreplay.replay.action.ShowItemAction;
import de.affenherzog.phantomreplay.replay.action.SneakAction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReplaySerializerTest {

    @Test
    void testRoundTripSerialization() {
        Position pos = new Position(10.5, 64.0, -100.2, 90f, 0f, "");
        List<ReplayAction> actions = List.of(
                new LeftClickAction(true),
                new ShowItemAction("STONE_SWORD"),
                new SneakAction(true)
        );
        Frame frame = new Frame(pos, actions);
        KeyFrame originalKeyFrame = new KeyFrame(1, frame);
        List<KeyFrame> originalList = List.of(originalKeyFrame);

        String json = ReplaySerializer.toJson(originalList);
        List<KeyFrame> deserializedList = ReplaySerializer.fromJson(json);

        assertNotNull(deserializedList, "Die deserialisierte Liste darf nicht null sein");
        assertEquals(1, deserializedList.size(), "Die Liste sollte genau ein Element haben");
        assertEquals(originalList, deserializedList, "Das Objekt muss den Trip verlustfrei überstehen");
    }

    @Test
    void testEmptyListSerialization() {
        List<KeyFrame> emptyList = List.of();

        String json = ReplaySerializer.toJson(emptyList);
        List<KeyFrame> result = ReplaySerializer.fromJson(json);

        assertEquals("[]", json, "Eine leere Liste sollte als leeres JSON-Array gespeichert werden");
        assertTrue(result.isEmpty(), "Die deserialisierte Liste sollte leer sein");
    }

}
