package com.example.letsfootball.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.letsfootball.databinding.WldfaBinding
import com.example.letsfootball.Model.pointsdata

class pointsadapter(private val list: MutableList<pointsdata>):
    RecyclerView.Adapter<pointsadapter.viewholder>() {
    inner class viewholder(val binding: WldfaBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val binding =WldfaBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewholder(binding)
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        with(holder){
            with(list[position]){
                binding.points.text = this.ponits.toString()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}