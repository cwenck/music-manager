package api

import java.io.File

interface OauthConfiguration {
    val clientId: String
    val clientSecret: String

    val authorizationServerUrl: String
    val tokenServerUrl: String
    val tokenDataStoreDirectory: File
}
