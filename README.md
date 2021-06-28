# Nooble-Test

Android Studio Project for the demo app of Nooble.

The App uses:
1. Spotify Auth https://github.com/spotify/android-auth
2. Spotify Android SDK (App remote player)

Requirements: 

1. Android Studio
2. Spotify App in your emulator or device
3. Internet Connection

Process to install: 

1. Clone the project in your system.
2. Open the project in your android studio and wait for the build to complete.
3. Connect your device to your system and enable USB Debugging in you device from Developer's Option
5. Since spotify provided the API in development mode, the spotify account must there in authorized account int the mentioned client ID.
4. If you want to override that then, in the project you can replace your spotify CLIENT_ID in MainActivity.java and Spotify.java, which you can get from   https://developer.spotify.com/dashboard/login and then adding the redirect URI to "http://nooble.example.com/callback" and SHA1 Key of the project in edit section wiht app name as com.example.nooble
5. Hit run while your device being connected with internet enabled.
6. The app should be asking you for login and some permission and then start playing the song.


