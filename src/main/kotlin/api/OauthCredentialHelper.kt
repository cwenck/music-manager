package api

import com.google.api.client.auth.oauth2.AuthorizationCodeFlow
import com.google.api.client.auth.oauth2.BearerToken
import com.google.api.client.auth.oauth2.ClientParametersAuthentication
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.GenericUrl
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory

open class OauthCredentialHelper(val config: OauthConfiguration) {

    val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()
    val transport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()

    fun getCredential(scopes: List<String>): Credential {
        val clientId = config.clientId
        val clientSecret = config.clientSecret

        val flowBuilder = AuthorizationCodeFlow.Builder(
            BearerToken.authorizationHeaderAccessMethod(),
            transport,
            jsonFactory,
            GenericUrl(config.tokenServerUrl),
            ClientParametersAuthentication(clientId, clientSecret),
            clientId,
            config.authorizationServerUrl
        )

        val flow = flowBuilder
            .setScopes(scopes)
            .setDataStoreFactory(FileDataStoreFactory(config.tokenDataStoreDirectory))
            .build()

        val receiver = LocalServerReceiver.Builder().setPort(8889).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }
}
