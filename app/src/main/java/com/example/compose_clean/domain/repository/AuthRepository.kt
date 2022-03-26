package com.example.compose_clean.domain.repository

import com.example.compose_clean.common.model.UserData
import com.example.compose_clean.common.safeFirebaseResultWithContext
import com.example.compose_clean.common.trySendBlockingExt
import com.example.compose_clean.common.GenericResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await


// TODO: this should be interface, impl is in data layer
class AuthRepository {

    suspend fun auth(): Flow<FirebaseUser?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener {
            trySendBlockingExt(it.currentUser)
        }
        Firebase.auth.addAuthStateListener(authStateListener)
        awaitClose {
            Firebase.auth.removeAuthStateListener(authStateListener)
        }
    }

    suspend fun createAccount(userData: UserData, password: String): GenericResult<Void> = safeFirebaseResultWithContext(Dispatchers.IO) {
        Firebase.auth.createUserWithEmailAndPassword(userData.email!!, password).await()
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
        val uid = Firebase.auth.currentUser!!.uid
        usersRef.child(uid).setValue(userData).await()
    }

    suspend fun logIn(email: String, password: String): GenericResult<FirebaseUser> = safeFirebaseResultWithContext(Dispatchers.IO) {
        val data = Firebase.auth.signInWithEmailAndPassword(email, password).await()
        data.user!!
    }

}