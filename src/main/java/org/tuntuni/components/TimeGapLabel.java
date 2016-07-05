/*
 * Copyright 2016 Tuntuni.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tuntuni.components;

import java.util.Date;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Label;
import javafx.util.Duration;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Label that shows difference of given time from current time, like-
 * {@code 3 hours ago} or {@code 29 minutes from now}.
 */
public class TimeGapLabel extends Label {

    private Date mDate;
    private final PrettyTime mPrettyTime;
    private final Timeline mTimeline;

    /**
     * creates a new labe and starts off the timer immediately.
     */
    public TimeGapLabel() {
        mDate = new Date();
        mPrettyTime = new PrettyTime();

        mTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1), (evt) -> updateTime()));
        mTimeline.setCycleCount(Animation.INDEFINITE);
        mTimeline.play();
    }

    // updates the view of time
    private void updateTime() {
        setText(mPrettyTime.format(mDate));
    }

    /**
     * Get the time set to this label
     *
     * @param date
     */
    public void setTime(Date date) {
        mDate = date;
    }

    public Date getTime() {
        return mDate;
    }

}
