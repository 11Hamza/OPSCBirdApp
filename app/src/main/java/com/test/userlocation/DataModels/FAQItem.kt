package com.test.userlocation.DataModels

data class FAQItem(
    val question: String,
    val answer: String,
    var isExpanded: Boolean = false
)
