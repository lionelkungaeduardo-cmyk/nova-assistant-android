package com.example.ai

import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class OnlineAIService(
    private val offlineFallback: OfflineAIService = OfflineAIService()
) : AIService {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    override suspend fun processQuery(
        query: String,
        userProfileName: String,
        isOnline: Boolean,
        knownMemories: List<String>
    ): AIResponse = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY

        // Check if query is a direct Android action intent
        val directOffline = offlineFallback.processQuery(query, userProfileName, isOnline, knownMemories)
        if (directOffline.action != null) {
            return@withContext directOffline
        }

        if (!isOnline || apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext offlineFallback.processQuery(query, userProfileName, isOnline, knownMemories)
        }

        try {
            val systemInstruction = """
                Você é a NOVA (Your Personal Intelligence), uma assistente de IA futurista, inteligente, amigável, objetiva, elegante e prestativa inspirada em interfaces holográficas de ficção científica.
                O usuário chama-se $userProfileName. Trate-o com respeito, naturalidade e objetividade.
                Fatos conhecidos da memória do usuário:
                ${knownMemories.joinToString("\n- ", prefix = "- ")}
                
                Instruções:
                1. Responda em português de forma clara, natural e sem enrolação.
                2. Para perguntas sobre programação, tecnologia, PC, Android, jogos ou estudos, dê explicações precisas e passo a passo.
                3. Se o usuário pedir para abrir um app ou criar tarefa, responda de forma concisa e confirme a ação.
            """.trimIndent()

            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("parts", JSONArray().apply {
                            put(JSONObject().put("text", query))
                        })
                    })
                }
                put("contents", contentsArray)

                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", systemInstruction))
                    })
                })

                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.7)
                    put("topK", 40)
                    put("topP", 0.95)
                })
            }

            val requestBody = jsonBody.toString().toRequestBody("application/json".toMediaType())
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent?key=$apiKey"

            val request = Request.Builder()
                .url(endpoint)
                .post(requestBody)
                .build()

            val response = okHttpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val responseString = response.body?.string() ?: ""
                val jsonResponse = JSONObject(responseString)
                val candidates = jsonResponse.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val contentObj = firstCandidate?.optJSONObject("content")
                val parts = contentObj?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text")

                if (!text.isNullOrBlank()) {
                    return@withContext AIResponse(
                        replyText = text,
                        action = null,
                        isOfflineMode = false,
                        suggestedChips = listOf("Me conte mais", "Explicar em detalhes", "Organizar em tópicos")
                    )
                }
            }
            // Fallback
            offlineFallback.processQuery(query, userProfileName, false, knownMemories)
        } catch (e: Exception) {
            offlineFallback.processQuery(query, userProfileName, false, knownMemories)
        }
    }
}
