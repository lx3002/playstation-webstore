package com.example.webstore


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.webstore.Seller.VendorDashboard
import com.example.webstore.ui.theme.WebStoreTheme
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            WebStoreTheme() {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(topBar = {
                        TopAppBar(backgroundColor = Color.Black,
                            title = {
                                Text(
                                    text = "SONY WEBSTORE",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                            })
                    }) {
                        Column(modifier = Modifier.padding(it)) {
                             authenticationUi(LocalContext.current)
                            goToMainScreen(this@MainActivity)

                        }
                    }
                }
            }
        }
    }



    @Composable
    fun authenticationUi(context : Context){

        val phoneNumber = remember {
            mutableStateOf("")
        }
        // otp reference
        val otp = remember {
            mutableStateOf("")
        }
        // verification ID if OTP token is valid
        val verificationId = remember {
            mutableStateOf("")
        }
        // custom messages to save/store info for the user
        val message = remember {
            mutableStateOf("")
        }

        // Firebase Initialization
        var mAuth : FirebaseAuth = FirebaseAuth.getInstance()
        lateinit var callback : PhoneAuthProvider.OnVerificationStateChangedCallbacks

        // create UI : edit text , Button clicks
        Column(modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.White),
            verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally
        ){
            OutlinedTextField(
                value = phoneNumber.value,
                onValueChange = { phoneNumber.value = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text(text = "Enter your phone number", color = Color.Black)},
                modifier = Modifier
                    .background(Color.LightGray)
                    .padding(16.dp)
                    .fillMaxWidth(),
                textStyle = TextStyle(color = Color.Black, fontSize = 15.sp),
                singleLine =  true
            )
            Spacer(modifier = Modifier.height(10.dp))
            // button to generate otp / make the OTP call
            Button(onClick = {

                // check if the phonenumber variable is empty
                if (TextUtils.isEmpty(phoneNumber.value.toString())){
                    Toast.makeText(context, "Phone Number cannot be empty", Toast.LENGTH_LONG).show()
                } else {
                    // country code
                    val number = "+254${phoneNumber.value}"
                    sendVerificationCode(number,mAuth,context as Activity, callback)
                }

            }) {
                Text(text = "Get OTP" , modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))


            OutlinedTextField(
                value = otp.value,
                onValueChange = { otp.value = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text(text = "Enter your OTP code", color = Color.Black)},
                modifier = Modifier
                    .background(Color.LightGray)
                    .padding(16.dp)
                    .fillMaxWidth(),
                textStyle = TextStyle(color = Color.Black, fontSize = 15.sp,),
                singleLine =  true
            )
            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = {

                if (TextUtils.isEmpty(otp.value.toString())){
                    Toast.makeText(context, "OTP cannot be empty", Toast.LENGTH_LONG).show()
                } else {
                    val credential : PhoneAuthCredential = PhoneAuthProvider.getCredential(verificationId.value, otp.value)

                    signInWithPhoneAuthCredentials(credential,mAuth,context as Activity, context, message)
                }

            },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(text = "Verify OTP" , modifier = Modifier.padding(8.dp))
            }
            Spacer(modifier = Modifier.height(5.dp))


            Text(text = message.value, style = TextStyle(color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Bold))



            callback = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(p0: PhoneAuthCredential) {
                    message.value = "Verification Successful"
                    Toast.makeText(context, "Verification Successful", Toast.LENGTH_LONG).show()

                }

                override fun onVerificationFailed(p0: FirebaseException) {
                    message.value = "Verification failed " + p0.message
                    Toast.makeText(context, "Verification Failed... ", Toast.LENGTH_LONG).show()
                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(p0, p1)
                    verificationId.value = p0
                }
            }
        }
    }


    private fun sendVerificationCode(
        number: String,
        mAuth: FirebaseAuth,
        activity: Activity,
        callback: PhoneAuthProvider.OnVerificationStateChangedCallbacks
    ) {


        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber(number)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callback)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }


    private fun signInWithPhoneAuthCredentials(
        credential: PhoneAuthCredential,
        mAuth: FirebaseAuth,
        activity: Activity,
        context: Activity,
        message: MutableState<String>
    ) {


        mAuth.signInWithCredential(credential).addOnCompleteListener(activity) {
            if (it.isSuccessful){
                message.value = "Verification Successful"
                Toast.makeText(context, "Verification Successful", Toast.LENGTH_LONG).show()

                goToMainScreen(context)

            } else {
                if(it.exception is FirebaseAuthInvalidCredentialsException){
                    Toast.makeText(context, "Verification Failed... " + (it.exception as FirebaseAuthInvalidCredentialsException).message
                        , Toast.LENGTH_LONG).show()
                }
            }
        }

    }
}
fun goToMainScreen(context: Activity) {
    val intent = Intent(context, VendorDashboard::class.java)
    context.startActivity(intent)
}