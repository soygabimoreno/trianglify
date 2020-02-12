package com.manolovn.sample;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.manolovn.colorbrewer.ColorBrewer;
import com.manolovn.sample.color.BrewerColorGenerator;
import com.manolovn.sample.exporter.ImageExporter;
import com.manolovn.sample.point.PointGeneratorFactory;
import com.manolovn.sample.point.PointGeneratorFactory.Type;
import com.manolovn.sample.util.PermissionRequester;
import com.manolovn.trianglify.TrianglifyView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class SampleActivity extends AppCompatActivity {

    @BindView(R.id.trianglify)
    TrianglifyView trianglifyView;
    @BindView(R.id.cellSizeControl)
    SeekBar cellSizeControl;
    @BindView(R.id.varianceControl)
    SeekBar varianceControl;
    @BindView(R.id.colorControl)
    Spinner colorControl;
    @BindView(R.id.pointsControl)
    Spinner pointsControl;
    @BindView(R.id.controlsContainer)
    ViewGroup controlsContainer;

    private ImageExporter exporter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initCellSizeControl();
        initVarianceControl();
        initColorControl();
        initPointsControl();

        trianglifyView.setDrawingCacheEnabled(true);
        exporter = new ImageExporter();
    }

    @OnClick(R.id.saveToGalleryButton)
    void saveToGallery() {
        new PermissionRequester(this, Manifest.permission.WRITE_EXTERNAL_STORAGE).request(new Function1<Boolean, Unit>() {
            @Override
            public Unit invoke(Boolean granted) {
                if (granted) {
                    exportViewToImage();
                } else {
                    Toast.makeText(SampleActivity.this, R.string.permission_not_granted_files, Toast.LENGTH_SHORT).show();
                }
                return null;
            }
        });
    }

    @OnClick(R.id.toggleFullscreen)
    void toggleFullScreen() {
        if (controlsContainer.getVisibility() == View.GONE) {
            controlsContainer.setVisibility(View.VISIBLE);
        } else if (controlsContainer.getVisibility() == View.VISIBLE) {
            controlsContainer.setVisibility(View.GONE);
        }
    }

    private void exportViewToImage() {
        try {
            exporter.exportFromView(this, trianglifyView);
            Toast.makeText(this, R.string.image_generated_success, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, R.string.image_generated_failed, Toast.LENGTH_SHORT).show();
        }
    }

    private void initCellSizeControl() {
        cellSizeControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                this.progress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (progress > 0) {
                    trianglifyView.setDrawingCacheEnabled(false);
                    trianglifyView.getDrawable().setCellSize(progress * 10);
                    trianglifyView.setDrawingCacheEnabled(true);
                }
            }
        });
    }

    private void initVarianceControl() {
        varianceControl.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                this.progress = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                trianglifyView.setDrawingCacheEnabled(false);
                trianglifyView.getDrawable().setVariance(progress + 2);
                trianglifyView.setDrawingCacheEnabled(true);
            }
        });
    }

    private void initColorControl() {
        final List<String> list = new ArrayList<>(ColorBrewer.values().length);
        final ColorBrewer[] colors = ColorBrewer.values();
        for (ColorBrewer color : colors) {
            list.add(color.name());
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorControl.setAdapter(adapter);

        colorControl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                trianglifyView.setDrawingCacheEnabled(false);
                trianglifyView.getDrawable()
                        .setColorGenerator(new BrewerColorGenerator(colors[position]));
                trianglifyView.setDrawingCacheEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initPointsControl() {
        final List<String> list = new ArrayList<>(Type.values().length);
        for (Type type : Type.values()) {
            list.add(type.toString());
        }

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pointsControl.setAdapter(adapter);

        pointsControl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                trianglifyView.setDrawingCacheEnabled(false);
                trianglifyView.getDrawable().setPointGenerator(
                        PointGeneratorFactory.from(list.get(position)));
                trianglifyView.setDrawingCacheEnabled(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
