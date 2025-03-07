package com.manway.Toofoh.android

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import android.content.Intent
import android.content.IntentSender
import android.media.Image
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.GoogleAuthProvider
import com.manway.Toofoh.R
import com.manway.Toofoh.data.CustomerInfo
import com.manway.Toofoh.dp.supabase
import com.manway.Toofoh.ui.android.showErrorDialog
import com.manway.Toofoh.ui.channel.RDialog
import com.manway.Toofoh.ui.channel.dialogChannel
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await


//implementation ("com.google.firebase:firebase-auth-ktx:23.1.0")
//implementation ("com.google.android.gms:play-services-auth:21.2.0")

class SignInViewModel : ViewModel() {

    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update {
            it.copy(
                isSignInSuccessful = result.data != null,
                signInError = result.errorMessage
            )
        }
    }

    fun resetState() {
        _state.update { SignInState() }
    }
}


@Composable
fun GoogleSignInButton(onSignInClick: () -> Unit) {
    Button(
        onClick = onSignInClick,
        elevation = ButtonDefaults.buttonElevation(3.dp),
        shape = MaterialTheme.shapes.small,
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth(0.70f),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.google_logo),
            contentDescription = "Google Logo",
            modifier = Modifier
                .size(45.dp)
                .padding(end = 10.dp)
        )
        Text(text = "Sign in With Google", color = Color.Black)

    }
}

class GoogleSignInClient(private val context: Context, private val oneTapClient: SignInClient) {

    suspend fun signIn(): IntentSender? {
        val result = try {
            oneTapClient.beginSignIn(buildSignInRequest()).await()
        } catch (e: Exception) {
            dialogChannel.send(RDialog("Exception:SingInViewModel", e.message.toString()))
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    suspend fun signInWithIntent(intent: Intent?): HashMap<String, String>? {
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        return try {
            val photoUrl = credential.profilePictureUri
            val googleIdToken = credential.googleIdToken
            hashMapOf(
                "Token" to googleIdToken.toString(),
                "Email" to credential.id,
                "Name" to credential.displayName!!,
                "PhotoUrl" to photoUrl.toString(),
                "Phone" to "",
                "Pincode" to ""
            )
        } catch (e: Exception) {
            null
        }
    }


    suspend fun signOut() {
        try {
            supabase.auth.signOut()
            oneTapClient.signOut().await()
        } catch (e: Exception) {
            dialogChannel.send(RDialog("Exception:SingInViewModel", e.message.toString()))
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    fun getSignedInUser(): CustomerInfo? = null

    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }
}

data class SignInState(
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null,
)

data class LogInAccount(val email: String? = null, val phone: String? = null)

data class SignInResult(
    val data: CustomerInfo?,
    val token: String? = null,
    val errorMessage: String?,
)

//@Composable
//fun ProfileScreen(
//    userData: CustomerInfo?,
//    onSignOut: () -> Unit
//) {
//    Column(
//        modifier = Modifier.fillMaxSize(),
//        verticalArrangement = Arrangement.Center,
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        if(userData?.profilePictureUrl != null) {
//            AsyncImage(
//                model = userData.profilePictureUrl,
//                contentDescription = "Profile picture",
//                modifier = Modifier
//                    .size(150.dp)
//                    .clip(CircleShape),
//                contentScale = ContentScale.Crop
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//        }
//        if(userData?.username != null) {
//            Text(
//                text = userData.username,
//                textAlign = TextAlign.Center,
//                fontSize = 36.sp,
//                fontWeight = FontWeight.SemiBold
//            )
//            Spacer(modifier = Modifier.height(16.dp))
//        }
//        Button(onClick = onSignOut) {
//            Text(text = "Sign out")
//        }
//    }
//}