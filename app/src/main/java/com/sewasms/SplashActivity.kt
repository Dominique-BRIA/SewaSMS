package com.sewasms

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.sewasms.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivLogo.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(500)
                .start()
        }

        binding.tvAppName.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(800)
                .start()
        }

        binding.tvTagline.apply {
            alpha = 0f
            animate()
                .alpha(1f)
                .setDuration(1000)
                .start()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }, 2000)
    }
}
