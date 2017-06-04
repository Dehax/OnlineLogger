package org.dehaxsoft.vk.onlinelogger.data;

import android.graphics.Bitmap;
import android.graphics.drawable.Icon;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by Dehax on 14.11.2015.
 */
public class Person {
    public long userId;
    public String firstName;
    public String lastName;
    public boolean isMale;
    public String photo50url;
    public boolean isOnline;
    public long lastSeen;

    public Bitmap icon;

    public String getLastSeenTime() {
        return DateFormat.getDateTimeInstance().format(new Date(lastSeen * 1000));
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (firstName != null) {
            sb.append(firstName);
        }
        sb.append(' ');
        if (lastName != null) {
            sb.append(lastName);
        }

        return sb.toString();
    }
}
