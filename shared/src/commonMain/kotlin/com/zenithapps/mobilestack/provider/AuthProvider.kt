package com.zenithapps.mobilestack.provider

import com.zenithapps.mobilestack.provider.AuthProvider.AuthUser
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface AuthProvider {
    val authUser: Flow<AuthUser?>
    suspend fun isLoggedIn(): Boolean
    suspend fun getAuthUser(): AuthUser?
    suspend fun signUpAnonymously(): AuthUser
    suspend fun signUpWithEmailPassword(email: String, password: String): AuthUser
    suspend fun signInWithEmailPassword(email: String, password: String): AuthUser
    suspend fun resetPassword(email: String)
    suspend fun signOut()

    suspend fun deleteAccount()
    data class AuthUser(val id: String, val email: String?, val isAnonymous: Boolean)
}

class FirebaseAuthProvider(
    private val firebaseAuth: FirebaseAuth
) : AuthProvider {
    override val authUser = firebaseAuth.authStateChanged.map { it?.toModel() }
    override suspend fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    override suspend fun getAuthUser(): AuthUser? {
        return firebaseAuth.currentUser?.toModel()
    }

    override suspend fun signUpAnonymously(): AuthUser {
        firebaseAuth.signInAnonymously()
        return firebaseAuth.currentUser!!.toModel()
    }

    override suspend fun signUpWithEmailPassword(email: String, password: String): AuthUser {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
        return firebaseAuth.currentUser!!.toModel()
    }

    override suspend fun signInWithEmailPassword(email: String, password: String): AuthUser {
        firebaseAuth.signInWithEmailAndPassword(email, password)
        return firebaseAuth.currentUser!!.toModel()
    }

    override suspend fun resetPassword(email: String) {
        firebaseAuth.sendPasswordResetEmail(email)
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun deleteAccount() {
        firebaseAuth.currentUser?.delete()
    }

    private fun FirebaseUser.toModel(): AuthUser {
        return AuthUser(uid, email, isAnonymous)
    }
}