package com.example.pearappearanceevaluatesystemandroid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.example.pearappearanceevaluatesystemandroid.adapter.ItemAdapter
import com.example.pearappearanceevaluatesystemandroid.model.PearListItem
import com.example.pearappearanceevaluatesystemandroid.repository.PearListRepository
import kotlinx.coroutines.*
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.View


class PearListActivity : AppCompatActivity() {

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)
    private val pearListRepository = PearListRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pear_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        scope.launch {
            coroutineScope {
                val pearList: ArrayList<PearListItem> = pearListRepository.getPearList()
                println("REQUEST SUCCESS: ")
                val itemAdapter = ItemAdapter(applicationContext, pearList)
                println("SUCCESS ADAPTER")
                val mainHandler = Handler(Looper.getMainLooper())
                mainHandler.post {
                    recyclerView.adapter = itemAdapter
                    itemAdapter.setOnItemClickListener(
                        View.OnClickListener {
                            val pearId: Int? = itemAdapter.getPearItemId()
                            val intent = Intent(applicationContext, ViewPastPearActivity::class.java)
                            intent.putExtra("pearId", pearId)
                            startActivity(intent)
                        }
                    )
                    recyclerView.setHasFixedSize(true)
                }
            }
        }
    }
}
