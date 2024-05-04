package com.example.letsfootball.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.letsfootball.databinding.FixturesBinding

class Fixtures_Activity : AppCompatActivity() {
    private lateinit var binding: FixturesBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FixturesBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}