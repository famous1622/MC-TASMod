package de.tr7zw.tas.duck;

import de.tr7zw.tas.Playback;
import de.tr7zw.tas.PlaybackMethod;
import net.minecraftforge.event.world.NoteBlockEvent;

public interface PlaybackInput {
    PlaybackMethod getPlayback();
    void setPlayback(PlaybackMethod newPlayback);
}
