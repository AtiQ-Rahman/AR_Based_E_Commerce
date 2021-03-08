
package com.augmented_reality.AR_Based_E_Commerce.googleMlKit.posedetector;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;

import com.augmented_reality.AR_Based_E_Commerce.StaticVar;
import com.augmented_reality.AR_Based_E_Commerce.googleMlKit.CameraSource;
import com.augmented_reality.AR_Based_E_Commerce.googleMlKit.GraphicOverlay;
import com.augmented_reality.AR_Based_E_Commerce.googleMlKit.segmenter.SegmenterProcessor;
import com.google.mlkit.vision.pose.Pose;
import com.google.mlkit.vision.pose.PoseLandmark;

import java.nio.ByteBuffer;
import java.util.List;

import static java.lang.Math.atan2;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class PoseGraphic extends GraphicOverlay.Graphic {

  private static final float DOT_RADIUS = 8.0f;
  private static final float IN_FRAME_LIKELIHOOD_TEXT_SIZE = 30.0f;
  private static final float STROKE_WIDTH = 10.0f;
  private static final float POSE_CLASSIFICATION_TEXT_SIZE = 60.0f;

  private final Pose pose;
  private final boolean showInFrameLikelihood;
  private final boolean visualizeZ;
  private final boolean rescaleZForVisualization;
  private float zMin = Float.MAX_VALUE;
  private float zMax = Float.MIN_VALUE;

  private final List<String> poseClassification;
  private final Paint classificationTextPaint;
  private final Paint leftPaint;
  private final Paint rightPaint;
  private final Paint whitePaint;

  CameraSource cameraSource;
  Bitmap frontTshirt;
  Bitmap backTshirt;

  PoseGraphic(
          GraphicOverlay overlay,
          Pose pose,
          boolean showInFrameLikelihood,
          boolean visualizeZ,
          boolean rescaleZForVisualization,
          List<String> poseClassification,
          CameraSource cameraSource
          ) {
    super(overlay);
    this.pose = pose;
    this.showInFrameLikelihood = showInFrameLikelihood;
    this.visualizeZ = visualizeZ;
    this.rescaleZForVisualization = rescaleZForVisualization;

    this.poseClassification = poseClassification;
    this.cameraSource = cameraSource;
    classificationTextPaint = new Paint();
    classificationTextPaint.setColor(Color.WHITE);
    classificationTextPaint.setTextSize(POSE_CLASSIFICATION_TEXT_SIZE);

    whitePaint = new Paint();
    whitePaint.setStrokeWidth(STROKE_WIDTH);
    whitePaint.setColor(Color.WHITE);
    whitePaint.setTextSize(IN_FRAME_LIKELIHOOD_TEXT_SIZE);
    leftPaint = new Paint();
    leftPaint.setStrokeWidth(STROKE_WIDTH);
    leftPaint.setColor(Color.GREEN);
    rightPaint = new Paint();
    rightPaint.setStrokeWidth(STROKE_WIDTH);
    rightPaint.setColor(Color.YELLOW);

    frontTshirt = new DressTrial(getApplicationContext()).getImage(5);
    backTshirt = new DressTrial(getApplicationContext()).getImage(6);
  }

  @RequiresApi(api = Build.VERSION_CODES.Q)
  @Override
  public void draw(Canvas canvas) {
    List<PoseLandmark> landmarks = pose.getAllPoseLandmarks();
    if (landmarks.isEmpty()) {
      return;
    }

    // Draw pose classification text.
    float classificationX = POSE_CLASSIFICATION_TEXT_SIZE * 0.5f;
    for (int i = 0; i < poseClassification.size(); i++) {
      float classificationY = (canvas.getHeight() - POSE_CLASSIFICATION_TEXT_SIZE * 1.5f
              * (poseClassification.size() - i));
    }

    // Draw all the points
    for (PoseLandmark landmark : landmarks) {
      drawPoint(canvas, landmark, whitePaint);
      if (visualizeZ && rescaleZForVisualization) {
        zMin = min(zMin, landmark.getPosition3D().getZ());
        zMax = max(zMax, landmark.getPosition3D().getZ());
      }
    }

    PoseLandmark leftShoulder = pose.getPoseLandmark(PoseLandmark.LEFT_SHOULDER);
    PoseLandmark rightShoulder = pose.getPoseLandmark(PoseLandmark.RIGHT_SHOULDER);
    PoseLandmark leftHip = pose.getPoseLandmark(PoseLandmark.LEFT_HIP);
    PoseLandmark rightHip = pose.getPoseLandmark(PoseLandmark.RIGHT_HIP);
    PoseLandmark rightElbow = pose.getPoseLandmark(PoseLandmark.RIGHT_ELBOW);
    PoseLandmark rightWrist = pose.getPoseLandmark(PoseLandmark.RIGHT_WRIST);
    PoseLandmark leftElbow = pose.getPoseLandmark(PoseLandmark.LEFT_ELBOW);
    PoseLandmark leftWrist = pose.getPoseLandmark(PoseLandmark.LEFT_WRIST);

    double avgLiklihood= (
                    leftShoulder.getInFrameLikelihood()+rightShoulder.getInFrameLikelihood()+
                    leftHip.getInFrameLikelihood()+rightHip.getInFrameLikelihood()+
                    rightElbow.getInFrameLikelihood()+leftElbow.getInFrameLikelihood()+
                    rightWrist.getInFrameLikelihood()+leftWrist.getInFrameLikelihood()
                    )/8.000f;

    if(avgLiklihood>= 0.997){
      StaticVar.isPoseDetecd = true;
    }
    else  {
      StaticVar.isPoseDetecd = false;
    }

    System.out.println("Now Accuracy--->"+avgLiklihood);


    drawDress(canvas, leftShoulder, rightShoulder, leftHip, rightHip,leftElbow, rightElbow);
    takeScreenshot(rightShoulder, rightElbow, rightWrist);

  }

  public void takeScreenshot(PoseLandmark rightShoulder, PoseLandmark rightElbow, PoseLandmark rightWrist)
  {
    double angle = getAngle(rightShoulder, rightElbow, rightWrist);

    if(angle < 30)
      StaticVar.isGastured = true;
    else
    {
      StaticVar.isGastured = false;
      StaticVar.isTakeScreenshot = false;
    }
  }

  static double getAngle(PoseLandmark firstPoint, PoseLandmark midPoint, PoseLandmark lastPoint) {
    double result =
            Math.toDegrees(
                    atan2(lastPoint.getPosition().y - midPoint.getPosition().y,
                            lastPoint.getPosition().x - midPoint.getPosition().x)
                            - atan2(firstPoint.getPosition().y - midPoint.getPosition().y,
                            firstPoint.getPosition().x - midPoint.getPosition().x));
    result = Math.abs(result); // Angle should never be negative
    if (result > 180) {
      result = (360.0 - result); // Always get the acute representation of the angle
    }
    return result;
  }

  void drawPoint(Canvas canvas, PoseLandmark landmark, Paint paint) {
  }

  public static PointF getMiddlePoint(PointF p1, PointF p2) {
    return new PointF((p1.x + p2.x) / 2.0f, (p1.y + p2.y) / 2.0f);
  }

  double getAngleFromPoint(double P1X, double P1Y, double P2X, double P2Y,
                        double P3X, double P3Y){

    double numerator = P2Y*(P1X-P3X) + P1Y*(P3X-P2X) + P3Y*(P2X-P1X);
    double denominator = (P2X-P1X)*(P1X-P3X) + (P2Y-P1Y)*(P1Y-P3Y);
    double ratio = numerator/denominator;

    double angleRad = Math.atan(ratio);
    double angleDeg = (angleRad*180)/Math.PI;

    if(angleDeg<0){
      angleDeg = 180+angleDeg;
    }

    return angleDeg;
  }

  @RequiresApi(api = Build.VERSION_CODES.Q)
  void drawDress(Canvas canvas, PoseLandmark leftSHoulder, PoseLandmark rightShoulder, PoseLandmark leftHip, PoseLandmark rightHip, PoseLandmark leftElbow, PoseLandmark rightElbow)
  {
    float constrain = 16.0f;
    Bitmap image;

    PointF p1 = leftSHoulder.getPosition();
    PointF p2 = rightShoulder.getPosition();
    PointF p3 = leftHip.getPosition();
    PointF p4 = rightHip.getPosition();



    if(p1.x > p2.x)
    {
      // front logic

      if(StaticVar.isSTillImage)
        constrain = -45f;
      else
        constrain = -10.0f;

      image = frontTshirt;

      p1 = new PointF((p1.x-constrain), (p1.y+constrain-20));
      p2 = new PointF((p2.x+constrain), (p2.y+constrain-20));
      p3 = new PointF((p3.x-constrain), (p3.y-constrain));
      p4 = new PointF((p4.x+constrain), (p4.y-constrain));

    }
    else
    {
      //back logic
      if(StaticVar.isSTillImage)
        constrain = 45f;
      else
        constrain = 10.0f;

      image = backTshirt;

      p1 = new PointF((p1.x-constrain), (p1.y-constrain-5));
      p2 = new PointF((p2.x+constrain), (p2.y-constrain-5));
      p3 = new PointF((p3.x-constrain), (p3.y+constrain));
      p4 = new PointF((p4.x+constrain), (p4.y+constrain));

    }

    PointF p5 = leftElbow.getPosition();
    PointF p6 = rightElbow.getPosition();

    PointF x2 = p1;
    PointF x4 = p2;
    PointF x22 = p3;
    PointF x24 = p4;

    PointF x3 = getMiddlePoint(x2, x4);
    PointF x23 = getMiddlePoint(x22, x24);

    PointF x14 = getMiddlePoint(x4, x24);
    PointF x9 = getMiddlePoint(x4, x14);
    PointF x19 = getMiddlePoint(x14, x24);

    PointF x13 = getMiddlePoint(x3, x23);
    PointF x8 = getMiddlePoint(x3, x13);
    PointF x18 = getMiddlePoint(x13, x23);

    PointF x12 = getMiddlePoint(x2, x22);
    PointF x7 = getMiddlePoint(x2, x12);
    PointF x17 = getMiddlePoint(x12, x22);

    float thresholdXP6 = 0f;
    float thresholdYP6 = 0f;

    PointF x5;

    double angleRight = getAngle(rightShoulder, rightElbow, rightHip);
    //double angleRight = getAngleFromPoint(p6.x, p6.y, x4.x, x4.y, x14.x, x14.y);


    if(angleRight > 100)
    {
      x5 = new PointF((p6.x-thresholdXP6)-30, x9.y + ((x9.y-x4.y)/2.0f) - 30);

    }
    else
    {
      x5 = new PointF((p6.x-thresholdXP6-5), (p6.y-thresholdYP6-20));

    }

    PointF x10 = new PointF(x5.x+10, ((x9.y-x4.y)+x5.y));
    PointF x15 = new PointF(x5.x, x14.y);
    PointF x20 = new PointF(x5.x, x19.y);
    PointF x25 = new PointF(x5.x, x24.y);

    float thresholdXP5 = 0.0f;
    float thresholdYP5 = 0.0f;

    double angleLeft = getAngle(leftSHoulder, leftElbow, leftHip);

    PointF x1 = new PointF((p5.x-thresholdXP5), (p5.y-thresholdYP5));

    //System.out.println("Now angle is: " + angleRight);


    PointF x6;

    if(angleLeft > 100)
    {
      x1 = new PointF((p5.x-thresholdXP5)+45, x7.y + ((x7.y-x2.y)/2.0f)-30);
      x6 = new PointF(x1.x-20, (x7.y-x2.y)+x1.y+10);
      x2 = new PointF(x2.x+10, x2.y);

    }
    else
    {
      x1 = new PointF((p5.x-thresholdXP5+15), (p5.y-thresholdYP5)-20);
      x6 = new PointF(x1.x-10, (x7.y-x2.y)+x1.y);
    }

    PointF x11 = new PointF(x1.x, x12.y);
    PointF x16 = new PointF(x1.x, x17.y);
    PointF x21 = new PointF(x1.x, x22.y);

    float meshPoint[] = new float[50];

      meshPoint[0] = translateX(x5.x);
      meshPoint[1] = translateY(x5.y);

      meshPoint[2] = translateX(x4.x);
      meshPoint[3] = translateY(x4.y);

      meshPoint[4] = translateX(x3.x);
      meshPoint[5] = translateY(x3.y);

      meshPoint[6] = translateX(x2.x);
      meshPoint[7] = translateY(x2.y);

      meshPoint[8] = translateX(x1.x);
      meshPoint[9] = translateY(x1.y);

      meshPoint[10] = translateX(x10.x);
      meshPoint[11] = translateY(x10.y);

      meshPoint[12] = translateX(x9.x);
      meshPoint[13] = translateY(x9.y);

      meshPoint[14] = translateX(x8.x);
      meshPoint[15] = translateY(x8.y);

      meshPoint[16] = translateX(x7.x);
      meshPoint[17] = translateY(x7.y);

      meshPoint[18] = translateX(x6.x);
      meshPoint[19] = translateY(x6.y);

      meshPoint[20] = translateX(x15.x);
      meshPoint[21] = translateY(x15.y);

      meshPoint[22] = translateX(x14.x);
      meshPoint[23] = translateY(x14.y);

      meshPoint[24] = translateX(x13.x);
      meshPoint[25] = translateY(x13.y);

      meshPoint[26] = translateX(x12.x);
      meshPoint[27] = translateY(x12.y);

      meshPoint[28] = translateX(x11.x);
      meshPoint[29] = translateY(x11.y);


      meshPoint[30] = translateX(x20.x);
      meshPoint[31] = translateY(x20.y);

      meshPoint[32] = translateX(x19.x);
      meshPoint[33] = translateY(x19.y);

      meshPoint[34] = translateX(x18.x);
      meshPoint[35] = translateY(x18.y);

      meshPoint[36] = translateX(x17.x);
      meshPoint[37] = translateY(x17.y);

      meshPoint[38] = translateX(x16.x);
      meshPoint[39] = translateY(x16.y);

      meshPoint[40] = translateX(x25.x);
      meshPoint[41] = translateY(x25.y);

      meshPoint[42] = translateX(x24.x);
      meshPoint[43] = translateY(x24.y);

      meshPoint[44] = translateX(x23.x);
      meshPoint[45] = translateY(x23.y);

      meshPoint[46] = translateX(x22.x);
      meshPoint[47] = translateY(x22.y);

      meshPoint[48] = translateX(x21.x);
      meshPoint[49] = translateY(x21.y);


    canvas.drawBitmapMesh(image, 4, 4, meshPoint, 0, null, 0, null);

    canvas.drawText("x5", meshPoint[0], meshPoint[1], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x4", meshPoint[2], meshPoint[3], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x3", meshPoint[4], meshPoint[5], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x2", meshPoint[6], meshPoint[7], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x1", meshPoint[8], meshPoint[9], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x10", meshPoint[10], meshPoint[11], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x9", meshPoint[12], meshPoint[13], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x8", meshPoint[14], meshPoint[15], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x7", meshPoint[16], meshPoint[17], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x6", meshPoint[18], meshPoint[19], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x15", meshPoint[20], meshPoint[21], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x14", meshPoint[22], meshPoint[23], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x13", meshPoint[24], meshPoint[25], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x12", meshPoint[26], meshPoint[27], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x11", meshPoint[28], meshPoint[29], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x20", meshPoint[30], meshPoint[31], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x19", meshPoint[32], meshPoint[33], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x18", meshPoint[34], meshPoint[35], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x17", meshPoint[36], meshPoint[37], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x16", meshPoint[38], meshPoint[39], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x25", meshPoint[40], meshPoint[41], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x24", meshPoint[42], meshPoint[43], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x23", meshPoint[44], meshPoint[45], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x22", meshPoint[46], meshPoint[47], new Paint(Color.argb(255,0,0,0)));
    canvas.drawText("x21", meshPoint[48], meshPoint[49], new Paint(Color.argb(255,0,0,0)));



    SegmenterProcessor segmenterProcessor = (SegmenterProcessor) cameraSource.selfie;

    Bitmap trialRoomBGBM =  new DressTrial(getApplicationContext()).getImage(4);

    if(StaticVar.isVirtualBgOn)
    {
      Bitmap bitmap =
              Bitmap.createBitmap(
                      maskColorsFromByteBuffer(segmenterProcessor.mask, segmenterProcessor.maskWidth, segmenterProcessor.maskHeight, trialRoomBGBM), segmenterProcessor.maskWidth, segmenterProcessor.maskHeight, Bitmap.Config.ARGB_8888);

      if (segmenterProcessor.isRawSizeMaskEnabled) {
        Matrix matrix = new Matrix(getTransformationMatrix());
        matrix.preScale(segmenterProcessor.scaleX, segmenterProcessor.scaleY);
        canvas.drawBitmap(bitmap, matrix, null);
      } else {

        //Draw Selfie Segmentation here
        canvas.drawBitmap(bitmap, getTransformationMatrix(), null);

      }
      bitmap.recycle();
      segmenterProcessor.mask.rewind();
    }

  }

  @RequiresApi(api = Build.VERSION_CODES.Q)
  @ColorInt
  private int[] maskColorsFromByteBuffer(ByteBuffer byteBuffer, int maskWidth, int maskHeight, Bitmap trialRoomBG) {

    @ColorInt int[] colors = new int[maskWidth * maskHeight];
    int k=0;

    for(int y=0; y<maskHeight; y++)
    {
      for(int x=0; x<maskWidth; x++)
      {
        float backgroundLikelihood = 1 - byteBuffer.getFloat();

        if (backgroundLikelihood > 0.9) {
          colors[k] = trialRoomBG.getColor(x,y).toArgb();
        } else if (backgroundLikelihood > 0.2) {
          colors[k] = Color.argb(0, 0, 0, 0);
        }
        k++;
      }
    }

    return colors;
  }

}
