# How to create wav files suitable for streaming

The exported wav file needs to be in the format
* 1 channel (Mono)
* Sampling rate 16000 Hz
* Sampling format 16bit

This can be setup in [Audacity](https://www.audacityteam.org/) in
the Edit->Settings dialog in the section "Devices" (select 1 channel
there), and "Quality" (select sampling rate and format there).

You can directly record the utterances then with Audacity.

Then export as Microsoft WAV file, signed 16bit PCM.

## FAQ

### Audacity (v2.3.3 and prior) on Mac OSX Catalina does not seem to record audio?
Apple started with macOS 10.15 to require notarization (signing the software) by default for all software.
According to Audacity this decision by Apple goes against the spirit of open source software which is why
they dont sign Audacity hence no recording capabilities.
There is a workaround which involves invoking Audacity via the command line.
Assuming Audacity being installed in the `/Applications` folder you can execute it in the terminal via the following command.
`/Applications/Audacity.app/Contents/MacOS/Audacity;`

You should be prompted for the permissions that way and should now be able to record audio.
Just remember that audacity needs to be started that way every time as the process cannot be successfully completed via the command line.

Alternatively you can create an automator script which spares you the hassle of running through the console:
https://forum.audacityteam.org/viewtopic.php?f=47&t=105586&sid=97826299f8a18dc6b2a0f96fb0a6f5ef&start=10