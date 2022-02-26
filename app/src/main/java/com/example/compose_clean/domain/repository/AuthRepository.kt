package com.example.compose_clean.domain.repository

import com.example.compose_clean.common.model.UserData
import com.example.compose_clean.common.safeResultWithContext
import com.example.compose_clean.ui.view.states.GenericResult
import com.example.compose_clean.common.trySendBlockingExt
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


    suspend fun createAccount(userData: UserData, password: String): GenericResult<Void> = safeResultWithContext(Dispatchers.IO) {
        val result = Firebase.auth.createUserWithEmailAndPassword(userData.email!!, password).await()
        if (result.user != null) {
            val usersRef = FirebaseDatabase.getInstance().reference.child("Users")
            val uid = Firebase.auth.currentUser!!.uid
            usersRef.child(uid).setValue(userData).await()
        } else {
            // error creating user
            throw Throwable("Error creating user")
        }
    }

    suspend fun logIn(email: String, password: String) : GenericResult<FirebaseUser> = safeResultWithContext(Dispatchers.IO) {
        val data = Firebase.auth.signInWithEmailAndPassword(email, password).await()
        data.user!!
    }


}