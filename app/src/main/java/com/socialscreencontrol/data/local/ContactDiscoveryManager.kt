package com.socialscreencontrol.data.local

import android.content.Context
import android.provider.ContactsContract
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ContactDiscoveryManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun readPhoneContacts(): List<String> {
        val phones = mutableSetOf<String>()
        val cursor = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER),
            null,
            null,
            null
        )
        cursor?.use {
            val index = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (it.moveToNext()) {
                val raw = it.getString(index)
                phones += raw.filter { char -> char.isDigit() || char == '+' }
            }
        }
        return phones.toList()
    }
}
