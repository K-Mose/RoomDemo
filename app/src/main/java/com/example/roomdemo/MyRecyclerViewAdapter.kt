package com.example.roomdemo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.roomdemo.databinding.ListItemBinding
import com.example.roomdemo.db.Subscriber

class MyRecyclerViewAdapter(private val subscriberList: List<Subscriber>) : RecyclerView.Adapter<MyRecyclerViewAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyRecyclerViewAdapter.MyViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        val binding: ListItemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.list_item, parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = subscriberList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(subscriberList[position])
    }

    class MyViewHolder(val binding: ListItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(subscriber: Subscriber) {
            binding.tvName.text = subscriber.name
            binding.tvEmail.text = subscriber.email
        }
    }

}