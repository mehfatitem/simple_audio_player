package com.example.monster.sampleproject.Interface;

import com.google.android.exoplayer.ExoPlaybackException;

public interface IPlayer
{
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState);
    public  void onPlayWhenReadyCommitted();
    public void onPlayerError(ExoPlaybackException error);
}
