package com.example.prmproj2.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.prmproj2.databinding.EntryContentBinding
import com.example.prmproj2.databinding.FragmentEntryBinding
import com.example.prmproj2.model.entry

class EntryObject(private val entryViewBinding: EntryContentBinding ) : RecyclerView.ViewHolder(entryViewBinding.root){

    fun onBind(entryObject: entry) = with(entryViewBinding){
        noteContent.text = entryObject.note
    }

}

class EntryListAdapter() : RecyclerView.Adapter<EntryObject>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryObject {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = EntryContentBinding.inflate(layoutInflater, parent, false)

        return EntryObject(binding)
    }

    var entryList: List<entry> = mutableListOf()

    override fun getItemCount(): Int {
        return entryList.size
    }

    override fun onBindViewHolder(holder: EntryObject, position: Int) {
        holder.onBind(entryList[position])
    }

}