package api.spotify

import api.OauthCredentialHelper
import api.spotify.oauth.SpotifyOauthConfiguration
import api.spotify.oauth.SpotifyScopes.PLAYLIST_MODIFY_PRIVATE
import api.spotify.oauth.SpotifyScopes.PLAYLIST_MODIFY_PUBLIC
import api.spotify.wrapper.SpotifyApiWrapper
import com.google.api.client.auth.oauth2.Credential
import com.wrapper.spotify.SpotifyApi

object SpotifyApiUtil {

    private val credentialHelper: OauthCredentialHelper = OauthCredentialHelper(SpotifyOauthConfiguration)
    private val scopes: List<String> = listOf(PLAYLIST_MODIFY_PRIVATE, PLAYLIST_MODIFY_PUBLIC).map { it.scope }

    private fun getCredential(): Credential = credentialHelper.getCredential(scopes)
    fun getApi(): SpotifyApiWrapper = SpotifyApiWrapper(getCredential())

//    private const val APPLICATION_NAME = "Music Manager"
//    private const val TOKENS_DIRECTORY_PATH = "tokens/spotify"
//    private const val CREDENTIALS_PATH = "/secrets/spotify/credentials.json"
//
//    private const val AUTHORIZATION_SERVER_URL = "https://accounts.spotify.com/authorize"
//    private const val TOKEN_SERVER_URL = "https://accounts.spotify.com/api/token"
//
//
//    private val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()
//
//    fun getCredential(transport: NetHttpTransport): Credential {
//        val flow = AuthorizationCodeFlow.Builder(
//            BearerToken.authorizationHeaderAccessMethod(),
//            transport, jsonFactory, GenericUrl(TOKEN_SERVER_URL),
//            ClientParametersAuthentication(SpotifyOauthConfiguration.clientId, SpotifyOauthConfiguration.clientSecret),
//            SpotifyOauthConfiguration.clientId, AUTHORIZATION_SERVER_URL)
//            .setScopes(scopes)
//            .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
//            .build()
//
//        val receiver = LocalServerReceiver.Builder().setPort(8889).build()
//        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
//    }

//    fun getSheetsApi(transport: NetHttpTransport): Sheets =
//        Sheets.Builder(transport, GoogleApiUtil.jsonFactory, getCredential(transport))
//            .setApplicationName(GoogleApiUtil.APPLICATION_NAME)
//            .build()

}
