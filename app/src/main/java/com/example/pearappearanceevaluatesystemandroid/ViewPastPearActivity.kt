package com.example.pearappearanceevaluatesystemandroid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.pearappearanceevaluatesystemandroid.adapter.PearImageAdapter
import com.example.pearappearanceevaluatesystemandroid.model.EvaluatedPearModel
import com.example.pearappearanceevaluatesystemandroid.originenum.EvaluateCode
import com.example.pearappearanceevaluatesystemandroid.repository.EvaluatedPearRepository
import com.example.pearappearanceevaluatesystemandroid.ui.CustomListView
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Exception


class ViewPastPearActivity : AppCompatActivity() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)
    private val repository = EvaluatedPearRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val pearId = intent.getIntExtra("pearId", 0)
        setContentView(R.layout.activity_view_past_pear)

        val progressBar = findViewById<ProgressBar>(R.id.view_past_pear_progress_bar)
        val progressEvaluateText: TextView = findViewById(R.id.progress_evaluate_tex)
        val evaluateDescription: TextView = findViewById(R.id.evaluate_description)
        val evaluateCodeEnd: TextView = findViewById(R.id.evaluate_code_end)
        val evaluateCodeTextView: TextView = findViewById(R.id.evaluate_code)
        val tableLayout: TableLayout = findViewById(R.id.pear_deterioration_table)

        scope.launch {
            coroutineScope {
                try {
                    val mainHandler = Handler(Looper.getMainLooper())
                    while (true) {
                        val evaluatedPearModel = repository.getEvaluatedPear(pearId)
                        //  Viewの書き換えはメインプロセスの中でしか出来ないため
                        mainHandler.post {
                            if (evaluatedPearModel.evaluateCode != EvaluateCode.Not_Yet) {
                                setPearImage(evaluatedPearModel)
                                setDeteriorationTable(evaluatedPearModel.deteriorations)

                                evaluateCodeTextView.text = transformEvaluateCode(
                                    evaluatedPearModel.evaluateCode
                                )
                                evaluateCodeTextView.setTextColor(
                                    ContextCompat.getColor(
                                        applicationContext,
                                        getEvaluateCodeColor(evaluatedPearModel.evaluateCode)
                                    )
                                )

                                progressBar.visibility = View.GONE
                                progressEvaluateText.visibility = View.GONE
                                evaluateDescription.visibility = View.VISIBLE
                                evaluateCodeTextView.visibility = View.VISIBLE
                                evaluateCodeEnd.visibility = View.VISIBLE
                                tableLayout.visibility = View.VISIBLE
                            } else {
                                progressEvaluateText.visibility = View.VISIBLE
                            }
                        }
                        if (evaluatedPearModel.evaluateCode != EvaluateCode.Not_Yet) {
                            break
                        }
                        Thread.sleep(100)
                    }

                } catch (e: Exception) {
                    // エラーが発生した場合はとりあえずメイン画面に戻す
                    val intent = Intent(applicationContext, MainActivity::class.java)
                    startActivity(intent)
                }

            }
        }
    }

    /**
     * 画像のデータを表示させる
     */
    private fun setPearImage(evaluatedPearModel: EvaluatedPearModel) {
        val pearImageListView: CustomListView = findViewById(R.id.pear_image_list)
        val adapter: BaseAdapter = PearImageAdapter(
            applicationContext,
            R.layout.pear_image_list,
            evaluatedPearModel
        )
        pearImageListView.adapter = adapter
    }

    /**
     * 汚損データを表の形式で表示させる
     */
    private fun setDeteriorationTable(deteriorations: ArrayList<JSONObject>) {
        val vg = findViewById<ViewGroup>(R.id.pear_deterioration_table_row)
        for (i in 0 until deteriorations.size) {
            layoutInflater.inflate(R.layout.pear_deterioration_row, vg)
            val tr = vg.getChildAt(i) as TableRow
            val pearDeteriorationName: TextView = tr.findViewById(R.id.pear_deterioration_name)
            val pearDeteriorationRatio: TextView = tr.findViewById(R.id.pear_deterioration_ratio)

            pearDeteriorationName.text = deteriorations[i]["name"].toString()
            pearDeteriorationRatio.text = deteriorations[i]["ratio"].toString()
        }
    }

    /**
     * EnumのEvaluateCodeから当該の等級の文字列に変換する
     */
    private fun transformEvaluateCode(evaluateCode: EvaluateCode): String {
        return when (evaluateCode) {
            EvaluateCode.Good -> {
                "良"
            }
            EvaluateCode.Blue -> {
                "青秀"
            }
            EvaluateCode.Red -> {
                "赤秀"
            }
            else -> {
                "無印"
            }
        }
    }

    /**
     * EnumのEvaluateCodeから当該の等級の色に変換する
     */
    private fun getEvaluateCodeColor(evaluateCode: EvaluateCode): Int {
        return when (evaluateCode) {
            EvaluateCode.Good -> {
                R.color.evaluate_good
            }
            EvaluateCode.Blue -> {
                R.color.evaluate_blue
            }
            EvaluateCode.Red -> {
                R.color.evaluate_red
            }
            else -> {
                R.color.black
            }
        }
    }

}
