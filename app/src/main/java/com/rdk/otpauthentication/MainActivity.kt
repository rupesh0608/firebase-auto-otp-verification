package com.rdk.otpauthentication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks
import com.rdk.otpauthentication.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
lateinit var auth:FirebaseAuth
    private var verificationId: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth= FirebaseAuth.getInstance();
        binding.idBtnGetOtp.setOnClickListener{
                if (TextUtils.isEmpty(binding.idEdtPhoneNumber.text.toString())) {
                    // when mobile number text field is empty
                    // displaying a toast message.
                    Toast.makeText(
                        this@MainActivity,
                        "Please enter a valid phone number.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    // if the text field is not empty we are calling our
                    // send OTP method for getting OTP from Firebase.
                    val phone = "+91" + binding.idEdtPhoneNumber.text.toString()
                    sendVerificationCode(phone)
                }
            }

        // initializing on click listener
        // for verify otp button

        // initializing on click listener
        // for verify otp button
        binding.idBtnVerify.setOnClickListener{
                // validating if the OTP text field is empty or not.
                if (TextUtils.isEmpty(binding.idEdtOtp.text.toString())) {
                    // if the OTP text field is empty display
                    // a message to user to enter OTP
                    Toast.makeText(this@MainActivity, "Please enter OTP", Toast.LENGTH_SHORT).show()
                } else {
                    // if OTP field is not empty calling
                    // method to verify the OTP.
                    verifyCode(binding.idEdtOtp.text.toString())
                }
            }
    }

    private fun sendVerificationCode(number: String) {
        // this method is used for getting
        // OTP on user phone number.
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(number) // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)
            .setCallbacks(mCallBack) // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }
    private fun signInWithCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(OnCompleteListener<AuthResult?> { task ->
                if (task.isSuccessful) {
                    val i = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(i)
                    finish()
                } else {
                    Toast.makeText(
                        this@MainActivity,
                        task.exception?.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
    private val  mCallBack: OnVerificationStateChangedCallbacks =
        object : OnVerificationStateChangedCallbacks() {
            override fun onCodeSent(s: String, forceResendingToken: ForceResendingToken) {
                super.onCodeSent(s, forceResendingToken)
                verificationId = s
                Toast.makeText(this@MainActivity,"OTP Sent Successfully...",Toast.LENGTH_SHORT).show()
            }
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                val code = phoneAuthCredential.smsCode
//                Toast.makeText(this@MainActivity,code.toString(),Toast.LENGTH_SHORT).show()
                    binding.idEdtOtp.setText(code)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Toast.makeText(this@MainActivity, e.message, Toast.LENGTH_LONG).show()
            }
        }
    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
        signInWithCredential(credential)
    }
}