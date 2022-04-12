package com.example.pearappearanceevaluatesystemandroid.repository

import com.example.pearappearanceevaluatesystemandroid.model.PearListItem
import com.example.pearappearanceevaluatesystemandroid.config.GlobalConst
import com.example.pearappearanceevaluatesystemandroid.originenum.EvaluateCode
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import org.json.JSONArray
import org.json.JSONObject

class PearListRepository {

    fun getPearList(): ArrayList<PearListItem> {
        val requestUrl = GlobalConst().defaultRequestUrl + "/pear/evaluates"

        val (_, _, result) = requestUrl.httpGet().responseJson()

        var pearList: ArrayList<PearListItem> = ArrayList()
        when (result) {
            is Result.Success -> {
                val responseBody = result.get().obj()
                val pears: JSONArray = responseBody["pears"] as JSONArray
                for (i in 0 until pears.length()) {
                    val pear: JSONObject = pears[i] as JSONObject

                    var evaluateCode = EvaluateCode.Not_Yet
                    when (pear["evaluate_code"] as String) {
                        "Not_Yet" -> {
                            evaluateCode = EvaluateCode.Not_Yet
                        }
                        "No" -> {
                            evaluateCode = EvaluateCode.No
                        }
                        "Good" -> {
                            evaluateCode = EvaluateCode.Good
                        }
                        "Blue" -> {
                            evaluateCode = EvaluateCode.Blue
                        }
                        "Red" -> {
                            evaluateCode = EvaluateCode.Red
                        }
                    }
                    val pearListItem = PearListItem(
                        pear["pear_id"] as Int,
                        evaluateCode,
                        pear["start_at"] as String
                    )
                    pearList.add(pearListItem)
                }

                return pearList
            }
            is Result.Failure -> {
                println("Response Failed: " + result.error)
            }
        }
        return pearList

    }
}