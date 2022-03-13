package com.example.flightmobileapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UrlListAdapter internal constructor(
    context: Context, private val itemClickListener: OnItemClickListener
) : RecyclerView.Adapter<UrlListAdapter.UrlViewHolder>() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var urls = emptyList<Url>() // Cached copy of words

    inner class UrlViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val urlsItemView: TextView = itemView.findViewById(R.id.textView)
        fun bind(url: Url, clickListener: OnItemClickListener) {
            itemView.setOnClickListener {
                clickListener.onItemClick(url)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UrlViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_item, parent, false)
        return UrlViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UrlViewHolder, position: Int) {
        val current = urls[position]
        holder.urlsItemView.text = current.url
        holder.bind(current, itemClickListener)
    }

    internal fun setUrls(urls: List<Url>) {
        this.urls = urls
        notifyDataSetChanged()
    }

    override fun getItemCount() = urls.size
}