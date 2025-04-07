package com.ys.mydynamicapp

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ys.mydynamicapp.databinding.FragmentGameOverBinding


@Deprecated(message = "ViewModel seems more fit for this process.")
class ProgressAdapter(val completionFrame: Int): RecyclerView.Adapter<ProgressAdapter.ViewHolder>() {

    inner class ViewHolder(val itemBinding : FragmentGameOverBinding) : RecyclerView.ViewHolder(itemBinding.root){}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        return 1
    }

}