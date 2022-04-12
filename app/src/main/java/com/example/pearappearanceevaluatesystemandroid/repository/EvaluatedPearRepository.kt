package com.example.pearappearanceevaluatesystemandroid.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.pearappearanceevaluatesystemandroid.config.GlobalConst
import com.example.pearappearanceevaluatesystemandroid.originenum.EvaluateCode
import com.example.pearappearanceevaluatesystemandroid.model.EvaluatedPearModel
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.json.responseJson
import com.github.kittinunf.result.Result
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

class EvaluatedPearRepository {

    /**
     * @args pearId 洋ナシのID
     * @return evaluatedPearModel
     */
    fun getEvaluatedPear(pearId: Int): EvaluatedPearModel {
        val requestURL = GlobalConst().defaultRequestUrl + "/pear/evaluates/$pearId"

        val (_, _, result) = requestURL.httpGet().responseJson()

        when (result) {
            is Result.Success -> {
                // レスポンスボディを取得
                val responseBody = result.get().obj()

                var evaluateCode = EvaluateCode.Not_Yet

                when (responseBody["evaluate_code"] as String) {
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

                if (evaluateCode == EvaluateCode.Not_Yet) {
                    return EvaluatedPearModel(
                        id = pearId,
                        evaluateCode = EvaluateCode.Not_Yet,
                        pearImages = ArrayList(),
                        evaluatedPearImages = ArrayList(),
                        deteriorations = ArrayList()
                    )
                }

                val evaluatedImageUrls: JSONArray = responseBody["evaluated_images"] as JSONArray
                val pearImageUrls: JSONArray = responseBody["pear_images"] as JSONArray
                val deteriorations: JSONArray = responseBody["deteriorations"] as JSONArray

                val evaluatedImageUrlList: ArrayList<String> = ArrayList()
                val pearImageUrlList: ArrayList<String> = ArrayList()
                val deteriorationList: ArrayList<JSONObject> = ArrayList()

                for (i in 0 until evaluatedImageUrls.length()) {
                    val evaluatedImageUrl = evaluatedImageUrls[i]
                    evaluatedImageUrlList.add(evaluatedImageUrl as String)
                }
                for (i in 0 until pearImageUrls.length()) {
                    val pearImageUrl = pearImageUrls[i]
                    pearImageUrlList.add(pearImageUrl as String)
                }
                for (i in 0 until deteriorations.length()) {
                    val deterioration = deteriorations.getJSONObject(i)
                    deteriorationList.add(deterioration)
                }

                val pearImages: ArrayList<Bitmap> = ArrayList()
                pearImageUrlList.forEach { pearImageUrl ->
                    val pearImage = getBitmapFromURL(pearImageUrl)
                    if (pearImage != null) {
                        pearImages.add(pearImage)
                    }
                }

                val evaluatedPearImages: ArrayList<Bitmap> = ArrayList()
                evaluatedImageUrlList.forEach { evaluatedImageUrl ->
                    val evaluatedPearImage = getBitmapFromURL(evaluatedImageUrl)
                    if (evaluatedPearImage != null) {
                        evaluatedPearImages.add(evaluatedPearImage)
                    }
                }

                return EvaluatedPearModel(
                    id = pearId,
                    evaluateCode = evaluateCode,
                    pearImages = pearImages,
                    evaluatedPearImages = evaluatedPearImages,
                    deteriorations = deteriorationList
                )
            }
            is Result.Failure -> {
                println("connection failed: " + result.error)
            }
        }
        return EvaluatedPearModel(
            id = pearId,
            evaluateCode = EvaluateCode.Not_Yet,
            pearImages = ArrayList(),
            evaluatedPearImages = ArrayList(),
            deteriorations = ArrayList()
        )
    }

    /**
     * 画像のURLからBitmapを生成する
     */
    private fun getBitmapFromURL(src: String?): Bitmap? {
        return try {
            val url = URL(src)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}
