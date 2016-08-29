/*
 * Copyright 2014 Niek Haarman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.daycle.daycleapp.custom.swipelistview.itemmanipulation.dragdrop;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.daycle.daycleapp.R;

class BitmapUtils {

    private BitmapUtils() {
    }

    /**
     * Returns a bitmap showing a screenshot of the view passed in.
     */
    @NonNull
    static Bitmap getBitmapFromView(@NonNull final View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint p = new Paint();
        p.setColor(ContextCompat.getColor(v.getContext(), R.color.colorAccent));
        p.setAlpha(50);
        //v.setBackgroundColor(ContextCompat.getColor(v.getContext(), android.R.color.transparent));
        v.draw(canvas);
        //canvas.drawCircle((float)(v.getWidth() / 2), (float)(v.getHeight() / 2), 10, p);
        canvas.drawRect(new Rect(0, 0, v.getWidth(), v.getHeight()), p);
        return bitmap;
    }

}
