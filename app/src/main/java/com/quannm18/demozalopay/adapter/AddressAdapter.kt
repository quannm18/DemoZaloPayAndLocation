package com.quannm18.demozalopay.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.quannm18.demozalopay.databinding.ItemAddressBinding
import com.quannm18.demozalopay.model.Address

class AddressAdapter : Adapter<AddressAdapter.AddressViewHolder>() {
    private var mList: MutableList<Address> = mutableListOf()
    private val listener: MutableLiveData<Any> = MutableLiveData()

    val event: LiveData<Any> by lazy {
        listener
    }
    inner class AddressViewHolder(var itemAddressBinding: ItemAddressBinding) :
        ViewHolder(itemAddressBinding.root) {
        fun bind(item: Address) {
            itemAddressBinding.tvAddressItem.text = item.name
            itemAddressBinding.root.setOnClickListener {
                listener.postValue(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressAdapter.AddressViewHolder {
        val itemAddressBinding = ItemAddressBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return AddressViewHolder(itemAddressBinding)
    }

    override fun onBindViewHolder(holder: AddressAdapter.AddressViewHolder, position: Int) {
        holder.bind(item = mList[position])
        holder.itemView
    }

    override fun getItemCount(): Int = mList.size

    fun initData(mList: MutableList<Address>) {
        this.mList = mList
        notifyDataSetChanged()
    }
}