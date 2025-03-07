package com.manway.Toofoh.android

import Ui.data.Address
import android.content.Context
import android.content.SharedPreferences
import coil.memory.MemoryCache
import com.manway.Toofoh.ui.enums.LoginMethod
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


class LocalDb(context: Context) {
    private var sharedPreferences: SharedPreferences

    init {
        sharedPreferences = context.getSharedPreferences("local_db", Context.MODE_PRIVATE)
    }

    private fun setValue(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).commit()
    }

    private fun getValue(key: String): String? {
        return sharedPreferences.getString(key, "")
    }

    private fun removeValue(key: String) {
        sharedPreferences.edit().remove(key).commit()
    }

    enum class keys {
        signIn, signMethod, location
    }


    //SignInData
    fun setSignIn(loginPair: Pair<LoginMethod, String?>) {
        setValue(keys.signMethod.name, loginPair.first.name)
        setValue(keys.signIn.name, loginPair.second.toString())
    }

    fun getSignIn(): Pair<LoginMethod, String?> {
        return when (getValue(keys.signMethod.name)) {
            LoginMethod.Google.name -> LoginMethod.Google
            LoginMethod.PhoneNumber.name -> LoginMethod.PhoneNumber
            else -> LoginMethod.None
        } to getValue(keys.signIn.name)
    }

    //Location Data
    fun setLocation(address: Address) {
        val dataString = Json.encodeToString(address)
        setValue(keys.location.name, dataString)
    }

    fun gerLocation(): Address? {
        return try {
            Json.decodeFromString(getValue(keys.location.name).toString())
        } catch (e: Exception) {
            null
        }
    }


}