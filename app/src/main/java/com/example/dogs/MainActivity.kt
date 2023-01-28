package com.example.dogs

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.denzcoskun.imageslider.models.SlideModel
import com.example.dogs.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var breeds: Array<String?>
    private lateinit var breeds_sub: Array<String?>
    private lateinit var queue: RequestQueue
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
        val mainObject = JSONObject(response)
        val names = mainObject.getJSONObject("message").names() as JSONArray

        breeds = arrayOfNulls(names.length())
        for (i in 0 until names.length())
            breeds[i] = names[i].toString();

        val autoAdapter = getAdapter(breeds)

        binding.spinnerBreeds.adapter = autoAdapter
        binding.spinnerBreeds.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                getCategoryList(parent?.getItemAtPosition(position) as String)
            }
        }
    }

    private fun getCategoryList(breed: String) {
        val request = StringRequest(
            Request.Method.GET,
            "$api/breed/$breed/list",
            { response ->
                categoryController(response, breed)
            },
            { error ->
                Log.d("ErrorListCategory", "error $error")
            }
        )
        queue.add(request);
    }

    private fun getCategoryImages(breed: String, subBreed: String = "") {
        val query: String = if (subBreed.isEmpty()) { breed } else { "$breed/$subBreed" }
        val request = StringRequest(
            Request.Method.GET,
            "$api/breed/$query/images",
            { response ->
                setImagesJSON(response)
            },
            { error ->
                Log.d("ErrorSingleCategoryImages", "error $error")
            }
        )
        queue.add(request);
    }

    private fun categoryController(response: String, breed: String) {

        val mainSub = JSONObject(response).getJSONObject("message").getJSONArray(breed)
        if (mainSub.length() == 0) {
            getCategoryImages(breed)
            binding.spinnerBreeds2.visibility = View.INVISIBLE
        } else {
            breeds_sub = emptyArray<String?>()
            breeds_sub = arrayOfNulls(mainSub.length())
            for (i in 0 until mainSub.length()) {
                breeds_sub[i] = mainSub[i].toString()
            }
            val autoAdapter = getAdapter(breeds_sub)

            binding.spinnerBreeds2.adapter = autoAdapter
            binding.spinnerBreeds2.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        getCategoryImages(binding.spinnerBreeds.selectedItem.toString(), parent?.getItemAtPosition(position) as String)
                    }
                }
            binding.spinnerBreeds2.visibility = View.VISIBLE
        }
    }

    private fun setImagesJSON(response: String) {
        val images = JSONObject(response).getJSONArray("message")
        slideModels.clear()
        for (i in 0 until images.length())
            slideModels.add(SlideModel("${images[i]}"))
        binding.slidebar.setImageList(slideModels)
    }

    private fun getAdapter(array: Array<String?>): ArrayAdapter<String> {
        val autoAdapter: ArrayAdapter<String> =
            ArrayAdapter(binding.root.context, android.R.layout.simple_spinner_item, array)
        autoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return autoAdapter
    }
}
