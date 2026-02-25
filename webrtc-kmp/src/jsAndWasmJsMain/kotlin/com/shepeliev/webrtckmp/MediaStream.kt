package com.shepeliev.webrtckmp

import com.shepeliev.webrtckmp.externals.PlatformMediaStream
import com.shepeliev.webrtckmp.externals.getTracks
import com.shepeliev.webrtckmp.internal.AudioTrackImpl
import com.shepeliev.webrtckmp.internal.VideoTrackImpl

public actual class MediaStream internal constructor(
    public val js: PlatformMediaStream,
) {
    public actual constructor() : this(PlatformMediaStream())

    public actual val id: String get() = js.id

    private val _trackCache = mutableMapOf<String, MediaStreamTrack>()

    public actual val tracks: List<MediaStreamTrack>
        get() {
            val liveTracks = js.getTracks()
            val liveIds = liveTracks.map { it.id }.toSet()
            // Evict stale entries
            _trackCache.keys.retainAll(liveIds)
            // Add new tracks to cache
            liveTracks.forEach { platform ->
                _trackCache.getOrPut(platform.id) {
                    when (platform.kind) {
                        "audio" -> AudioTrackImpl(platform)
                        "video" -> VideoTrackImpl(platform)
                        else -> throw IllegalArgumentException("Unknown track kind: ${platform.kind}")
                    }
                }
            }
            return liveTracks.map { _trackCache.getValue(it.id) }
        }

    public actual fun addTrack(track: MediaStreamTrack) {
        require(track is MediaStreamTrackImpl)
        js.addTrack(track.platform)
        _trackCache[track.id] = track
    }

    public actual fun getTrackById(id: String): MediaStreamTrack? =
        _trackCache[id] ?: js.getTrackById(id)?.let { platform ->
            when (platform.kind) {
                "audio" -> AudioTrackImpl(platform)
                "video" -> VideoTrackImpl(platform)
                else -> throw IllegalArgumentException("Unknown track kind: ${platform.kind}")
            }.also { _trackCache[id] = it }
        }

    public actual fun removeTrack(track: MediaStreamTrack) {
        require(track is MediaStreamTrackImpl)
        js.removeTrack(track.platform)
        _trackCache.remove(track.id)
    }

    public actual fun release() {
        val cachedTracks = _trackCache.values.toList()
        val liveTracks = tracks

        liveTracks.forEach(MediaStreamTrack::stop)
        cachedTracks
            .filter { cachedTrack -> liveTracks.none { liveTrack -> liveTrack.id == cachedTrack.id } }
            .forEach(MediaStreamTrack::stop)

        _trackCache.clear()
    }
}
