package de.affenherzog.phantomreplay.cooldown;

public interface PhantomCooldown {

    int getSeconds();

    void onCooldownFinished();

    void onCooldownTick(int secondsLeft);

}
