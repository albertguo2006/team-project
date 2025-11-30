package view;

import javazoom.jl.decoder.Bitstream;
import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.SampleBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AudioManager handles all audio playback using JLayer for MP3 decoding
 * and Java Sound API with SourceDataLine for output.
 *
 * Using SourceDataLine (instead of Clip) allows the audio to appear
 * in system volume mixers on Linux (PulseAudio/PipeWire).
 *
 * This is a singleton class to ensure consistent audio management across the game.
 */
public class AudioManager {

    private static AudioManager instance;

    private volatile Thread backgroundMusicThread;
    private volatile boolean stopBackgroundMusic = false;
    private volatile String currentMusicPath;
    private volatile SourceDataLine currentLine;
    private double masterVolume = 0.5;
    private final Object musicLock = new Object();

    /**
     * Private constructor for singleton pattern.
     */
    private AudioManager() {
        printAvailableMixers();
    }

    /**
     * Gets the singleton instance of AudioManager.
     * @return the AudioManager instance
     */
    public static synchronized AudioManager getInstance() {
        if (instance == null) {
            instance = new AudioManager();
        }
        return instance;
    }

    /**
     * Prints available audio mixers for debugging.
     */
    private void printAvailableMixers() {
        System.out.println("=== Available Audio Mixers ===");
        boolean foundPipeWire = false;
        boolean foundPulse = false;
        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            String name = mixerInfo.getName().toLowerCase();
            String desc = mixerInfo.getDescription().toLowerCase();
            System.out.println("  - " + mixerInfo.getName() + ": " + mixerInfo.getDescription());
            if (name.contains("pipewire") || desc.contains("pipewire")) foundPipeWire = true;
            if (name.contains("pulse") || desc.contains("pulse")) foundPulse = true;
        }
        System.out.println("==============================");

        // Print helpful message if PipeWire/PulseAudio not detected
        if (!foundPipeWire && !foundPulse) {
            System.out.println("NOTE: PipeWire/PulseAudio mixer not detected by Java.");
            System.out.println("      Audio may not appear in system volume mixer.");
            System.out.println("      On Arch Linux, try: sudo pacman -S pipewire-alsa");
            System.out.println("      This routes ALSA audio through PipeWire for mixer integration.");
        }
    }

    /**
     * Sets the master volume for all audio.
     * @param volume volume level from 0.0 (muted) to 1.0 (full)
     */
    public void setMasterVolume(double volume) {
        this.masterVolume = Math.max(0.0, Math.min(1.0, volume));
        applyVolumeToCurrentLine();
    }

    /**
     * Applies the current master volume to the active audio line.
     */
    private void applyVolumeToCurrentLine() {
        SourceDataLine line = currentLine;
        if (line != null && line.isOpen()) {
            try {
                if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                    // Convert linear volume (0.0-1.0) to decibels
                    // -80dB is essentially silent, 0dB is full volume
                    float dB;
                    if (masterVolume <= 0.0) {
                        dB = gainControl.getMinimum();
                    } else {
                        dB = (float) (20.0 * Math.log10(masterVolume));
                        dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
                    }
                    gainControl.setValue(dB);
                }
            } catch (Exception e) {
                System.err.println("Could not apply volume: " + e.getMessage());
            }
        }
    }

    /**
     * Gets the current master volume.
     * @return volume level from 0.0 to 1.0
     */
    public double getMasterVolume() {
        return masterVolume;
    }

    /**
     * Plays background music in a loop.
     * If the same music is already playing, does nothing.
     * If different music is playing, stops it and starts the new track.
     *
     * @param resourcePath the path to the music file (e.g., "/audio/music.mp3")
     */
    public void playBackgroundMusic(String resourcePath) {
        if (resourcePath == null || resourcePath.isEmpty()) {
            stopBackgroundMusic();
            return;
        }

        synchronized (musicLock) {
            // Check if same music is already playing
            if (resourcePath.equals(currentMusicPath) && backgroundMusicThread != null && backgroundMusicThread.isAlive()) {
                System.out.println("Music already playing: " + resourcePath);
                return;
            }

            // Stop current music
            stopBackgroundMusicInternal();
            currentMusicPath = resourcePath;
            stopBackgroundMusic = false;

            // Start new music thread
            backgroundMusicThread = new Thread(() -> {
                playMusicLoop(resourcePath);
            }, "BackgroundMusic");
            backgroundMusicThread.setDaemon(true);
            backgroundMusicThread.start();
        }
    }

    /**
     * Plays music in a loop until stopped.
     */
    private void playMusicLoop(String resourcePath) {
        System.out.println("Starting music loop for: " + resourcePath);

        while (!stopBackgroundMusic) {
            try {
                playMP3Once(resourcePath);
            } catch (Exception e) {
                if (!stopBackgroundMusic) {
                    System.err.println("Error playing music: " + e.getMessage());
                    e.printStackTrace();
                }
                break;
            }

            // Small delay before looping
            if (!stopBackgroundMusic) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

        System.out.println("Music loop ended for: " + resourcePath);
    }

    /**
     * Plays an MP3 file once using JLayer decoder and SourceDataLine.
     */
    private void playMP3Once(String resourcePath) throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new Exception("Resource not found: " + resourcePath);
        }

        Bitstream bitstream = new Bitstream(inputStream);
        Decoder decoder = new Decoder();
        SourceDataLine line = null;

        try {
            Header header;
            boolean lineInitialized = false;

            while (!stopBackgroundMusic && (header = bitstream.readFrame()) != null) {
                // Decode the frame
                SampleBuffer output = (SampleBuffer) decoder.decodeFrame(header, bitstream);

                // Initialize audio line on first frame
                if (!lineInitialized) {
                    AudioFormat format = new AudioFormat(
                            decoder.getOutputFrequency(),
                            16,
                            decoder.getOutputChannels(),
                            true,  // signed
                            false  // little-endian
                    );

                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

                    // Try to find a mixer that supports this format
                    line = findBestSourceDataLine(info, format);
                    if (line == null) {
                        throw new LineUnavailableException("No suitable audio output found");
                    }

                    line.open(format);
                    currentLine = line;
                    applyVolumeToCurrentLine();
                    line.start();
                    lineInitialized = true;

                    System.out.println("Audio line opened: " + line.getLineInfo());
                    System.out.println("Audio format: " + format);
                }

                // Convert samples to bytes and write to line
                short[] samples = output.getBuffer();
                int length = output.getBufferLength();
                byte[] buffer = new byte[length * 2];

                for (int i = 0; i < length; i++) {
                    short sample = samples[i];
                    buffer[i * 2] = (byte) (sample & 0xFF);
                    buffer[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
                }

                if (line != null && !stopBackgroundMusic) {
                    line.write(buffer, 0, buffer.length);
                }

                bitstream.closeFrame();
            }

        } finally {
            if (line != null) {
                line.drain();
                line.stop();
                line.close();
                if (currentLine == line) {
                    currentLine = null;
                }
            }
            bitstream.close();
            inputStream.close();
        }
    }

    /**
     * Finds the best SourceDataLine for the given format.
     * Prefers PulseAudio/PipeWire mixers for better system integration.
     */
    private SourceDataLine findBestSourceDataLine(DataLine.Info info, AudioFormat format) {
        // First, try to find a PulseAudio or PipeWire mixer explicitly
        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            String name = mixerInfo.getName().toLowerCase();
            String desc = mixerInfo.getDescription().toLowerCase();

            // Prefer PulseAudio or PipeWire for better system integration
            if (name.contains("pulse") || name.contains("pipewire") ||
                desc.contains("pulse") || desc.contains("pipewire")) {
                try {
                    Mixer mixer = AudioSystem.getMixer(mixerInfo);
                    if (mixer.isLineSupported(info)) {
                        System.out.println("Using mixer: " + mixerInfo.getName());
                        return (SourceDataLine) mixer.getLine(info);
                    }
                } catch (Exception e) {
                    // Try next mixer
                }
            }
        }

        // Try to find ALSA "default" device which should route through PipeWire
        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            String name = mixerInfo.getName().toLowerCase();
            // Look for "default" ALSA device - this routes through PipeWire on modern systems
            if (name.contains("default") || name.equals("default [default]")) {
                try {
                    Mixer mixer = AudioSystem.getMixer(mixerInfo);
                    if (mixer.isLineSupported(info)) {
                        System.out.println("Using ALSA default (should route through PipeWire): " + mixerInfo.getName());
                        return (SourceDataLine) mixer.getLine(info);
                    }
                } catch (Exception e) {
                    // Try next mixer
                }
            }
        }

        // Fall back to default system mixer
        try {
            if (AudioSystem.isLineSupported(info)) {
                System.out.println("Using default audio system");
                return (SourceDataLine) AudioSystem.getLine(info);
            }
        } catch (Exception e) {
            System.err.println("Could not get default line: " + e.getMessage());
        }

        // Last resort: try any available mixer
        for (Mixer.Info mixerInfo : AudioSystem.getMixerInfo()) {
            try {
                Mixer mixer = AudioSystem.getMixer(mixerInfo);
                if (mixer.isLineSupported(info)) {
                    System.out.println("Using fallback mixer: " + mixerInfo.getName());
                    return (SourceDataLine) mixer.getLine(info);
                }
            } catch (Exception e) {
                // Try next mixer
            }
        }

        return null;
    }

    /**
     * Stops the currently playing background music.
     */
    public void stopBackgroundMusic() {
        synchronized (musicLock) {
            stopBackgroundMusicInternal();
            currentMusicPath = null;
        }
    }

    /**
     * Internal method to stop background music.
     * Must be called within synchronized(musicLock) block.
     */
    private void stopBackgroundMusicInternal() {
        stopBackgroundMusic = true;

        // Close the current line to interrupt playback
        SourceDataLine line = currentLine;
        if (line != null) {
            try {
                line.stop();
                line.close();
            } catch (Exception e) {
                // Ignore
            }
            currentLine = null;
        }

        // Wait for thread to finish
        Thread thread = backgroundMusicThread;
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            try {
                thread.join(1000);
            } catch (InterruptedException e) {
                // Ignore
            }
        }
        backgroundMusicThread = null;
    }

    /**
     * Plays a one-shot sound effect.
     *
     * @param resourcePath the path to the sound file (e.g., "/audio/siren.mp3")
     */
    public void playSoundEffect(String resourcePath) {
        playSoundEffect(resourcePath, null);
    }

    /**
     * Plays a one-shot sound effect with a callback when complete.
     *
     * @param resourcePath the path to the sound file
     * @param onComplete callback to run when the sound finishes (can be null)
     */
    public void playSoundEffect(String resourcePath, Runnable onComplete) {
        if (resourcePath == null || resourcePath.isEmpty()) {
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        Thread sfxThread = new Thread(() -> {
            try {
                playSFXOnce(resourcePath);
            } catch (Exception e) {
                System.err.println("Error playing sound effect: " + e.getMessage());
            } finally {
                if (onComplete != null) {
                    onComplete.run();
                }
            }
        }, "SoundEffect");
        sfxThread.setDaemon(true);
        sfxThread.start();
    }

    /**
     * Plays a sound effect MP3 once.
     */
    private void playSFXOnce(String resourcePath) throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new Exception("Resource not found: " + resourcePath);
        }

        Bitstream bitstream = new Bitstream(inputStream);
        Decoder decoder = new Decoder();
        SourceDataLine line = null;

        try {
            Header header;
            boolean lineInitialized = false;

            while ((header = bitstream.readFrame()) != null) {
                SampleBuffer output = (SampleBuffer) decoder.decodeFrame(header, bitstream);

                if (!lineInitialized) {
                    AudioFormat format = new AudioFormat(
                            decoder.getOutputFrequency(),
                            16,
                            decoder.getOutputChannels(),
                            true,
                            false
                    );

                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                    line = findBestSourceDataLine(info, format);
                    if (line == null) {
                        throw new LineUnavailableException("No suitable audio output found");
                    }

                    line.open(format);

                    // Apply volume
                    if (line.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                        FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                        float dB;
                        if (masterVolume <= 0.0) {
                            dB = gainControl.getMinimum();
                        } else {
                            dB = (float) (20.0 * Math.log10(masterVolume));
                            dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
                        }
                        gainControl.setValue(dB);
                    }

                    line.start();
                    lineInitialized = true;
                }

                short[] samples = output.getBuffer();
                int length = output.getBufferLength();
                byte[] buffer = new byte[length * 2];

                for (int i = 0; i < length; i++) {
                    short sample = samples[i];
                    buffer[i * 2] = (byte) (sample & 0xFF);
                    buffer[i * 2 + 1] = (byte) ((sample >> 8) & 0xFF);
                }

                if (line != null) {
                    line.write(buffer, 0, buffer.length);
                }

                bitstream.closeFrame();
            }

        } finally {
            if (line != null) {
                line.drain();
                line.stop();
                line.close();
            }
            bitstream.close();
            inputStream.close();
        }
    }

    /**
     * Stops all audio playback and releases resources.
     * Call this when the game is closing.
     */
    public void shutdown() {
        stopBackgroundMusic();
        System.out.println("AudioManager shutdown complete");
    }

    /**
     * Checks if background music is currently playing.
     * @return true if music is playing
     */
    public boolean isBackgroundMusicPlaying() {
        Thread thread = backgroundMusicThread;
        return thread != null && thread.isAlive();
    }
}
