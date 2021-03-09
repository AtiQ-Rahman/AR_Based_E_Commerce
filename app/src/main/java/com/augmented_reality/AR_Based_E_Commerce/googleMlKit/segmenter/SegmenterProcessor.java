/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.augmented_reality.AR_Based_E_Commerce.googleMlKit.segmenter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.augmented_reality.AR_Based_E_Commerce.googleMlKit.GraphicOverlay;
import com.augmented_reality.AR_Based_E_Commerce.googleMlKit.VisionProcessorBase;
import com.augmented_reality.AR_Based_E_Commerce.googleMlKit.preference.PreferenceUtils;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.segmentation.Segmentation;
import com.google.mlkit.vision.segmentation.SegmentationMask;
import com.google.mlkit.vision.segmentation.Segmenter;
import com.google.mlkit.vision.segmentation.selfie.SelfieSegmenterOptions;

import java.nio.ByteBuffer;

/** A processor to run Segmenter. */
public class SegmenterProcessor extends VisionProcessorBase<SegmentationMask> {

  private static final String TAG = "SegmenterProcessor";

  public final Segmenter segmenter;

 public GraphicOverlay overlay;

  public ByteBuffer mask;
  public int maskWidth;
  public int maskHeight;
  public boolean isRawSizeMaskEnabled;
  public float scaleX;
  public float scaleY;


  public SegmenterProcessor(Context context) {
    this(context, /* isStreamMode= */ true);
  }

  public SegmenterProcessor(Context context, boolean isStreamMode) {
    super(context);
    SelfieSegmenterOptions.Builder optionsBuilder = new SelfieSegmenterOptions.Builder();
    optionsBuilder.setDetectorMode(
      isStreamMode ? SelfieSegmenterOptions.STREAM_MODE : SelfieSegmenterOptions.SINGLE_IMAGE_MODE);
    if (PreferenceUtils.shouldSegmentationEnableRawSizeMask(context)) {
      optionsBuilder.enableRawSizeMask();
    }

    SelfieSegmenterOptions options = optionsBuilder.build();
    segmenter = Segmentation.getClient(options);
    //Log.d(TAG, "SegmenterProcessor created with option: " + options);
  }

  @Override
  protected Task<SegmentationMask> detectInImage(InputImage image) {
    return segmenter.process(image);
  }

  @Override
  protected void onSuccess(
      @NonNull SegmentationMask segmentationMask, @NonNull GraphicOverlay graphicOverlay) {

    mask = segmentationMask.getBuffer();
    maskWidth = segmentationMask.getWidth();
    maskHeight = segmentationMask.getHeight();

    isRawSizeMaskEnabled =
            maskWidth != overlay.getImageWidth()
                    || maskHeight != overlay.getImageHeight();
    scaleX = overlay.getImageWidth() * 1f / maskWidth;
    scaleY = overlay.getImageHeight() * 1f / maskHeight;

    graphicOverlay.add(new SegmentationGraphic(graphicOverlay, segmentationMask));
  }

  @Override
  protected void onFailure(@NonNull Exception e) {
    Log.e(TAG, "Segmentation failed: " + e);
  }
}
