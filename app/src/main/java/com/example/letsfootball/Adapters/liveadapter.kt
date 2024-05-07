package com.example.letsfootball.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.letsfootball.Model.awaylive
import com.example.letsfootball.Model.commencemodel
import com.example.letsfootball.Model.completelive
import com.example.letsfootball.Model.homelive
import com.example.letsfootball.Model.scoreslive
import com.example.letsfootball.Model.updatelive
import com.example.letsfootball.databinding.LiveMatchesUpdateBinding

class liveadapter(private val homelist: MutableList<homelive>,
                  private val scoreslist:MutableList<scoreslive>,
                  private val awaylist:MutableList<awaylive>,
                  private val updatelist:MutableList<updatelive>,
                  private val commencelist:MutableList<commencemodel>,
                  private val completelist:MutableList<completelive>):
    RecyclerView.Adapter<liveadapter.viewholder>() {
    inner class viewholder(val binding: LiveMatchesUpdateBinding): RecyclerView.ViewHolder(binding.root)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): liveadapter.viewholder {
        val binding = LiveMatchesUpdateBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewholder(binding)
    }

    override fun onBindViewHolder(holder: liveadapter.viewholder, position: Int) {
        if (position < homelist.size) {
            with(holder) {
                with(homelist[position]) {
                    binding.homelive.text = this.hometeam.toString()
                }
            }
        }
        if (position < scoreslist.size) {
            with(holder) {
                with(scoreslist[position]) {
                    binding.scoreslive.text = this.score.toString()
                }
            }
        }
        if (position < awaylist.size) {
            with(holder) {
                with(awaylist[position]) {
                    binding.awaylive.text = this.awayteam.toString()
                }
            }
        }
        if (position < updatelist.size) {
            with(holder) {
                with(updatelist[position]) {
                    binding.liveupdates.text = this.updateliv.toString()
                    if (this.updateliv.toString()=="null"){
                        binding.constrainliv.visibility = View.GONE
                    }
                    else{
                        binding.constrainliv.visibility = View.VISIBLE
                    }
                }
            }
        }
        if (position < completelist.size) {
            with(holder) {
                with(completelist[position]) {
                    if (this.complete.toString()=="true"){
                        binding.constrainliv.visibility = View.GONE
                    }
                }
            }
        }
        if (position < commencelist.size) {
            with(holder) {
                with(commencelist[position]) {
                    binding.commencetime.text = this.commencetime.toString()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return maxOf(homelist.size,scoreslist.size,awaylist.size,updatelist.size,commencelist.size,completelist.size)
    }

}