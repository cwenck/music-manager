package api.spotify.wrapper.endpoints.playlist.tracks.replace

import api.CredentialExtensions.refreshTokenIfRequired
import api.exception.ApiException
import api.spotify.wrapper.ApiEndpoint
import api.spotify.wrapper.endpoints.common.SpotifyError
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.jackson.objectBody
import com.google.api.client.auth.oauth2.Credential
import org.apache.http.HttpStatus

class ReplacePlaylistTracksEndpoint(private val credential: Credential) :
    ApiEndpoint<ReplacePlaylistTracksRequest, ReplacePlaylistTracksResponse>() {

    private val successfulStatusCodes = setOf(HttpStatus.SC_CREATED)

    override fun call(request: ReplacePlaylistTracksRequest): ReplacePlaylistTracksResponse {
        credential.refreshTokenIfRequired()
        val (_request, response, result) = Fuel.put("https://api.spotify.com/v1/playlists/${request.playlistId}/tracks")
            .authentication()
            .bearer(credential.accessToken)
            .objectBody(request)
            .responseString()

        val (content, _error) = result
        if (content == null) throw ApiException("Expected the API response to have a body")
        checkResponseStatus<SpotifyError>(content, response.statusCode, successfulStatusCodes)
        return deserialize(content)
    }
}
