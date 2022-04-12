package com.example.pearappearanceevaluatesystemandroid.model

import android.graphics.Bitmap
import com.example.pearappearanceevaluatesystemandroid.originenum.EvaluateCode
import org.json.JSONObject
import java.util.ArrayList

data class EvaluatedPearModel (
    val id: Int,
    val evaluateCode: EvaluateCode,
    val pearImages: ArrayList<Bitmap>,
    val evaluatedPearImages: ArrayList<Bitmap>,
    val deteriorations: ArrayList<JSONObject>
)
