package com.example.letsfootball.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.letsfootball.Model.pointsdata
import com.example.letsfootball.Model.ptdata
import com.example.letsfootball.databinding.WldfaBinding

class ptadapater(private val list: MutableList<ptdata>):
    RecyclerView.Adapter<ptadapater.viewholder>() {
    inner class viewholder(val binding: WldfaBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewholder {
        val binding =WldfaBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return viewholder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: viewholder, position: Int) {
        with(holder){
            with(list[position]){
                binding.points.text = this.Points.toString()
            }
        }
    }

}