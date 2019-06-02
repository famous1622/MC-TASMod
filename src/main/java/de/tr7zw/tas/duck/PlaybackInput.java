package de.tr7zw.tas.duck;

import de.tr7zw.tas.Playback;
import net.minecraftforge.event.world.NoteBlockEvent;

public interface PlaybackInput {
    Playback getPlayback();
    void setPlayback(Playback newPlayback);
}
