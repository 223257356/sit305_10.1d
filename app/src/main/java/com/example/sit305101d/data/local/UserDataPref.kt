package com.example.sit305101d.data.local

import android.content.SharedPreferences
import com.tencent.mmkv.MMKV
import org.koin.core.annotation.Single

interface UserDataPref : SharedPreferences

@Single
internal class UserDataPrefImpl : UserDataPref, SharedPreferences by MMKV.defaultMMKV()
