package com.test.userlocation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.userlocation.DataModels.FAQItem


class FAQAdapter(private val faqList: List<FAQItem>) : RecyclerView.Adapter<FAQAdapter.FAQViewHolder>() {

    inner class FAQViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val questionTextView: TextView = itemView.findViewById(R.id.questionTextView)
        val answerTextView: TextView = itemView.findViewById(R.id.answerTextView)
        val linearLayout: LinearLayout = itemView.findViewById(R.id.linearLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FAQViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_faq, parent, false)
        return FAQViewHolder(view)
    }

    override fun onBindViewHolder(holder: FAQViewHolder, position: Int) {
        val faqItem = faqList[position]
        holder.questionTextView.text = faqItem.question
        holder.answerTextView.text = faqItem.answer

        val isExpanded = faqItem.isExpanded
        holder.answerTextView.visibility = if (isExpanded) View.VISIBLE else View.GONE

        holder.questionTextView.setOnClickListener {
            faqItem.isExpanded = !faqItem.isExpanded
            notifyItemChanged(position)
        }
    }

    override fun getItemCount(): Int {
        return faqList.size
    }
}
