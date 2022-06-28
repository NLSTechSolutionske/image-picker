package com.tabasumu.libraries.image_picker

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer

/**
 * @author Mambo Bryan
 * @email mambobryan@gmail.com
 * Created 08/06/22 at 03:00 PM
 */
object StoreManager {

    private const val PERMISSIONS = "rationale_permissions"

    private fun Context.manager(): SharedPreferences {
        val mainKey = MasterKey.Builder(this, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            this.applicationContext,
            this.applicationContext.packageName.plus("_app_manager"),
            mainKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    object StringListSerializer : JsonTransformingSerializer<List<String>>(ListSerializer(String.serializer())) {
        // If response is not an array, then it is a single object that should be wrapped into the array
        override fun transformDeserialize(element: JsonElement): JsonElement =
            if (element !is JsonArray) JsonArray(listOf(element)) else element
    }

    fun rationalesList(context: Context): List<String> {
        val s = context.manager().getString(PERMISSIONS, "")
        return if (s.isNullOrBlank()) listOf()
        else Json.decodeFromString(StringListSerializer, s)
    }

    fun rationaleShown(context: Context, permission: String): Boolean {

        val list = rationalesList(context).toMutableList()
        list.add(permission)

        val s = Json.encodeToString(StringListSerializer, list)
        return context.manager().edit().apply {
            putString(PERMISSIONS, s)
        }.commit()

    }

}