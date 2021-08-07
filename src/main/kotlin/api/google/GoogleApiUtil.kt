package api.google

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.InputStreamReader

object GoogleApiUtil {

    private const val APPLICATION_NAME = "Music Manager"
    private const val TOKENS_DIRECTORY_PATH = "tokens"
    private const val CREDENTIALS_PATH = "/credentials.json"

    private val SCOPES = listOf(SheetsScopes.SPREADSHEETS)
    private val jsonFactory: JsonFactory = JacksonFactory.getDefaultInstance()

    private fun getCredential(transport: NetHttpTransport): Credential {
        // Load client secrets.
        val inputStream: InputStream = GoogleApiUtil::class.java.getResourceAsStream(CREDENTIALS_PATH)
            ?: throw FileNotFoundException("Resource not found: $CREDENTIALS_PATH")
        val clientSecrets = GoogleClientSecrets.load(jsonFactory, InputStreamReader(inputStream))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(transport, jsonFactory, clientSecrets, SCOPES)
            .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build()

        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    fun getSheetsApi(transport: NetHttpTransport): Sheets =
        Sheets.Builder(transport, jsonFactory, getCredential(transport))
            .setApplicationName(APPLICATION_NAME)
            .build()
}
