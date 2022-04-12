package com.example.pearappearanceevaluatesystemandroid.model

import com.example.pearappearanceevaluatesystemandroid.originenum.EvaluateCode

data class PearListItem(
    val pearId: Int,
    val evaluateCode: EvaluateCode,
    val startAt: String
)