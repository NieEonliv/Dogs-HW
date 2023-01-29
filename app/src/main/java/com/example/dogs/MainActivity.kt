package com.example.dogs

import android.R
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.denzcoskun.imageslider.models.SlideModel
import com.example.dogs.databinding.ActivityMainBinding
import `in`.galaxyofandroid.spinerdialog.OnSpinerItemClick
import `in`.galaxyofandroid.spinerdialog.SpinnerDialog
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var breeds: ArrayList<String>
    private lateinit var queue: RequestQueue
    private lateinit var spinnerDialog: SpinnerDialog
    private val api: String = "https://dog.ceo/api"
    private var slideModels = ArrayList<SlideModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        queue = Volley.newRequestQueue(binding.root.context)
        setContentView(binding.root)
        getAllCategory()
        slideModels.add(SlideModel("https://kartinkof.club/uploads/posts/2022-05/1653248217_1-kartinkof-club-p-kartinki-privet-sobaka-1.jpg"))
        binding.slidebar.setImageList(slideModels)
    }

    private fun getAllCategory() {
        val request = StringRequest(
            Request.Method.GET,
            "$api/breeds/list/all",
            { response ->
                parseAllCategoryData(response)
            },
            { error ->
                Log.d("ErrorAllCategory", "error $error")
            }
        )
        queue.add(request);
    }

    private fun parseAllCategoryData(response: String) {
        val mainObject = JSONObject(response).getJSONObject("message")
        val names = mainObject.names() as JSONArray

        breeds = ArrayList<String>()
        for (i in 0 until names.length()) {
            val tmp = mainObject.getJSONArray(names[i].toString())

            if (tmp.length() > 0) {
                for (j in 0 until tmp.length()) {
                    breeds.add("${names[i]}/${tmp[j]}")
                }
            } else {
                breeds.add("${names[i]}")
            }
        }

        spinnerDialog = SpinnerDialog(
            this@MainActivity,
            breeds,
            "Select or Search breeds",
            "Close"
        ) // With No Animation
        spinnerDialog.setCancellable(true) // for cancellable
        spinnerDialog.setShowKeyboard(false) // for open keyboard by default
        spinnerDialog.bindOnSpinerListener(OnSpinerItemClick { item, _ ->
            getCategoryImages(item)
            binding.selectedBreed.text = "You are currently viewing\n\"$item\""
        })
        binding.button.setOnClickListener { spinnerDialog.showSpinerDialog() }
    }

    private fun getCategoryImages(breed: String) {
        val request = StringRequest(
            Request.Method.GET,
            "$api/breed/$breed/images",
            { response ->
                setImagesJSON(response)
            },
            { error ->
                Log.d("ErrorSingleCategoryImages", "error $error")
            }
        )
        queue.add(request);
    }

    private fun setImagesJSON(response: String) {
        val images = JSONObject(response).getJSONArray("message")
        slideModels.clear()
        for (i in 0 until images.length())
            slideModels.add(SlideModel("${images[i]}"))
        binding.slidebar.setImageList(slideModels)
    }
}