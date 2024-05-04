package com.example.letsfootball.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.letsfootball.databinding.NamesBinding
import com.example.letsfootball.Model.dataclass

class adapter(private val list: MutableList<dataclass>) :
RecyclerView.Adapter<adapter.todoviewholder>() {
    var currentValue =1
    inner class todoviewholder(val binding: NamesBinding ): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): todoviewholder {
        val binding = NamesBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return todoviewholder(binding)
    }
    override fun onBindViewHolder(holder: todoviewholder, position: Int) {
        with(holder){
            with(list[position]){
                binding.textView.text = this.name.toString()
                binding.textView10.text = currentValue.toString() + "."
                currentValue++
                }
            }

        }
    override fun getItemCount(): Int {
        return list.size
    }
    }