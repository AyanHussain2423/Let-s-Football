package com.example.letsfootball.Adapters

import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.letsfootball.Model.Fixture1model
import com.example.letsfootball.Model.Fixture2model
import com.example.letsfootball.Model.Score
import com.example.letsfootball.Model.datemodel
import com.example.letsfootball.Model.timemodel
import com.example.letsfootball.databinding.ActivityMainBinding
import com.example.letsfootball.databinding.ActivityMainBinding.bind

import com.example.letsfootball.databinding.FixturesBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class FixersAdpater(private val listfix1: MutableList<Fixture1model>
                    ,private val listfix2: MutableList<Fixture2model>,
                    private val listscore: MutableList<Score>
                    ,private  val timelist : MutableList<timemodel>
                    ,private val datelist : MutableList<datemodel>):
    RecyclerView.Adapter<FixersAdpater.viewholder>() {
    inner class viewholder(val binding: FixturesBinding) : RecyclerView.ViewHolder(binding.root){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val binding = FixturesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewholder(binding)
    }

    override fun getItemCount(): Int {
        return maxOf(listfix1.size, listfix2.size, listscore.size,timelist.size,datelist.size)
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {

        if (position < listfix1.size) {
            with(holder) {
                with(listfix1[position]) {
                    binding.team1.text = this.fix1.toString()
                }
            }
        }
        if (position < listfix2.size) {
            with(holder) {
                with(listfix2[position]) {
                    binding.team2.text = this.fix2.toString()
                }
            }
        }
        if (position < listscore.size) {
            with(holder) {
                with(listscore[position]) {
                    binding.score.text = this.scores.toString()
                }
            }
        }
        if (position < timelist.size) {
            with(holder) {
                with(timelist[position]) {
                    binding.time.text = this.matchtime.toString()
                }
            }
        }
        if (position < datelist.size) {
            with(holder) {
                with(datelist[position]) {
                    binding.matchdate.text = this.matchdate.toString()
                }
            }
        }
    }
}
