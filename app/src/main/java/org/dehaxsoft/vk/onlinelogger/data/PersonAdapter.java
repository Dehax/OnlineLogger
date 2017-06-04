package org.dehaxsoft.vk.onlinelogger.data;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Dehax on 02.01.2016.
 */
public class PersonAdapter extends ArrayAdapter<Person> {

    public PersonAdapter(Context context, int resource) {
        super(context, resource);
    }

    public PersonAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public PersonAdapter(Context context, int resource, Person[] objects) {
        super(context, resource, objects);
    }

    public PersonAdapter(Context context, int resource, int textViewResourceId, Person[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public PersonAdapter(Context context, int resource, int textViewResourceId, List<Person> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public PersonAdapter(Context context, int resource, List<Person> objects) {
        super(context, resource, objects);
    }
}
