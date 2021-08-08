package api.google

import exception.ProgramConfigurationException

object GoogleApiConfiguration {
    val clientSecret = System.getenv("GOOGLE_CLIENT_SECRET")
        ?: throw ProgramConfigurationException("Missing Google API client secret. GOOGLE_CLIENT_SECRET needs to be set.")

    val clientId = System.getenv("GOOGLE_CLIENT_ID")
        ?: throw ProgramConfigurationException("Missing Google API client ID. GOOGLE_CLIENT_ID needs to be set.")
}
