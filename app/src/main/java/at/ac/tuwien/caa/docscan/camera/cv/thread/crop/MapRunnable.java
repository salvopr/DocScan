package at.ac.tuwien.caa.docscan.camera.cv.thread.crop;

import android.graphics.PointF;

import java.util.ArrayList;

public class MapRunnable extends CropRunnable{

    private static final String CLASS_NAME = "MapRunnable";

    public MapRunnable(MapTask mapTask) {
        super(mapTask);
    }

    @Override
    protected void performTask(String fileName) {

        ArrayList<PointF> points = PageDetector.getNormedCropPoints(fileName);

        if (Mapper.replaceWithMappedImage(fileName, points))
            PageDetector.saveAsCropped(fileName);

    }

}
