package api.spotify.oauth

import api.OauthConfiguration
import exception.ProgramConfigurationException
import java.io.File

object SpotifyOauthConfiguration : OauthConfiguration {
    override val clientSecret = System.getenv("SPOTIFY_CLIENT_SECRET")
        ?: throw ProgramConfigurationException("Missing Spotify API client secret. SPOTIFY_CLIENT_SECRET needs to be set.")

    override val clientId = System.getenv("SPOTIFY_CLIENT_ID")
        ?: throw ProgramConfigurationException("Missing Spotify API client ID. SPOTIFY_CLIENT_ID needs to be set.")

    override val authorizationServerUrl: String = "https://accounts.spotify.com/authorize"
    override val tokenServerUrl: String = "https://accounts.spotify.com/api/token"
    override val tokenDataStoreDirectory: File = File("tokens/spotify")
}
