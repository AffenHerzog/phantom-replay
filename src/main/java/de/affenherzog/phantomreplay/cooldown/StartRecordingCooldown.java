package de.affenherzog.phantomreplay.cooldown;

import de.affenherzog.phantomreplay.util.PluginSettings;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.TitlePart;

@RequiredArgsConstructor
public class StartRecordingCooldown implements PhantomCooldown {

    private static final Component cooldownFinishedTitleComponent = Component
            .text("Kamera Läuft!")
            .color(NamedTextColor.DARK_PURPLE);

    private static final Component cooldownFinishedSubtitleComponent = Component
            .text("Du wirst jetzt aufgezeichnet")
            .color(NamedTextColor.GRAY);

    private static final int SECONDS = PluginSettings.get().getRecordingCooldownInSeconds();

    private final Audience audience;

    @Override
    public int getSeconds() {
        return SECONDS;
    }

    @Override
    public void onCooldownFinished() {
        audience.sendTitlePart(TitlePart.TITLE, cooldownFinishedTitleComponent);
        audience.sendTitlePart(TitlePart.SUBTITLE, cooldownFinishedSubtitleComponent);
        CooldownSoundUtil.playCooldownFinishSound(audience);
    }

    @Override
    public void onCooldownTick(int secondsLeft) {
        Component secondsLeftComponent = Component.text(secondsLeft).color(NamedTextColor.GRAY);
        audience.sendTitlePart(TitlePart.TITLE, secondsLeftComponent);
        CooldownSoundUtil.playCooldownTickSound(audience);
    }
}
