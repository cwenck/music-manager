package api

import com.google.api.client.auth.oauth2.Credential
import java.time.Duration

object CredentialExtensions {

    fun Credential.refreshTokenIfRequired(threshold: Duration = Duration.ofSeconds(60)) {
        val expiresIn = this.expiresInSeconds ?: return
        if (expiresIn < threshold.seconds) refreshToken()
    }
}
