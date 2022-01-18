package com.example.compose_clean.data.dao.model

import com.google.firebase.database.Exclude

data class UserData(
  @Exclude
  var id: String? = null,
  var email: String? = null,
  var userName: String? = null,
)