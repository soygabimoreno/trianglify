package com.manolovn.sample

import android.Manifest
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.SeekBar
import android.widget.Toast
import butterknife.ButterKnife
import butterknife.OnClick
import com.manolovn.colorbrewer.ColorBrewer
import com.manolovn.sample.color.BrewerColorGenerator
import com.manolovn.sample.exporter.ImageExporter
import com.manolovn.sample.point.PointGeneratorFactory
import com.manolovn.sample.util.PermissionRequester
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import java.util.*

class SampleActivity : AppCompatActivity() {

    private lateinit var exporter: ImageExporter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ButterKnife.bind(this)
        initCellSizeControl()
        initVarianceControl()
        initColorControl()
        initPointsControl()

        initSaveToGallery()
        initToggleFullscreen()
        trianglifyView.isDrawingCacheEnabled = true
        exporter = ImageExporter()
    }

    private fun initSaveToGallery() {
        saveToGalleryButton.setOnClickListener {
            PermissionRequester(this, Manifest.permission.WRITE_EXTERNAL_STORAGE).request { granted: Boolean ->
                if (granted) {
                    exportViewToImage()
                } else {
                    Toast.makeText(this@SampleActivity, R.string.permission_not_granted_files, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initToggleFullscreen() {
        toggleFullscreen.setOnClickListener {
            if (controlsContainer.visibility == View.GONE) {
                controlsContainer.visibility = View.VISIBLE
            } else if (controlsContainer.visibility == View.VISIBLE) {
                controlsContainer.visibility = View.GONE
            }
        }
    }

    private fun exportViewToImage() {
        try {
            exporter.exportFromView(this, trianglifyView)
            Toast.makeText(this, R.string.image_generated_success, Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            Toast.makeText(this, R.string.image_generated_failed, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initCellSizeControl() {
        cellSizeControl.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var progress = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                this.progress = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (progress > 0) {
                    trianglifyView.isDrawingCacheEnabled = false
                    trianglifyView.drawable.setCellSize(progress * 10)
                    trianglifyView.isDrawingCacheEnabled = true
                }
            }
        })
    }

    private fun initVarianceControl() {
        varianceControl.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var progress = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                this.progress = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                trianglifyView.isDrawingCacheEnabled = false
                trianglifyView.drawable.setVariance(progress + 2)
                trianglifyView.isDrawingCacheEnabled = true
            }
        })
    }

    private fun initColorControl() {
        val list: MutableList<String> = ArrayList(ColorBrewer.values().size)
        val colors = ColorBrewer.values()
        for (color in colors) {
            list.add(color.name)
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        colorControl.adapter = adapter
        colorControl.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                trianglifyView.isDrawingCacheEnabled = false
                trianglifyView.drawable
                    .setColorGenerator(BrewerColorGenerator(colors[position]))
                trianglifyView.isDrawingCacheEnabled = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun initPointsControl() {
        val list: MutableList<String> = ArrayList(PointGeneratorFactory.Type.values().size)
        for (type in PointGeneratorFactory.Type.values()) {
            list.add(type.toString())
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        pointsControl.adapter = adapter
        pointsControl.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                trianglifyView.isDrawingCacheEnabled = false
                trianglifyView.drawable.setPointGenerator(
                    PointGeneratorFactory.from(list[position])
                )
                trianglifyView.isDrawingCacheEnabled = true
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}