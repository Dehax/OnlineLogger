package org.dehaxsoft.vk.onlinelogger;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Parcel;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.dehaxsoft.vk.onlinelogger.data.Person;
import org.dehaxsoft.vk.onlinelogger.data.PersonAdapter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        Person person;
        Context context;

        public DownloadImageTask(Context context, Person person) {
            this.context = context;
            this.person = person;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = person.photo50url;
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            person.icon = result;

            if (person.isOnline) {
                Notification.Builder nb = new Notification.Builder(context);
                //URL url;
                //Bitmap bitmap;
                try {
                    //url = new URL(newFriends[i].photo50url);
                    //bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    nb.setSmallIcon(R.drawable.ic_ab_app);
                    nb.setLargeIcon(person.icon);
                    nb.setContentTitle("Вошёл в сеть");
                    nb.setContentText(person.firstName + " " + person.lastName);
                    nb.setTicker(person.firstName + " " + person.lastName + " в сети");
                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    nb.setSound(alarmSound);
                    mNotificationManager.notify(mNotificationId, nb.build());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Notification.Builder nb = new Notification.Builder(MainActivity.this);
                //URL url;
                //Bitmap bitmap;
                try {
                    //url = new URL(newFriends[i].photo50url);
                    //bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());

                    nb.setSmallIcon(R.drawable.ic_ab_app);
                    nb.setLargeIcon(person.icon);
                    nb.setContentTitle("Вышел из сети");
                    nb.setContentText(person.firstName + " " + person.lastName + " - " + person.getLastSeenTime());
                    nb.setTicker(person.firstName + " " + person.lastName + " вышел из сети");
                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    nb.setSound(alarmSound);
                    mNotificationManager.notify(mNotificationId, nb.build());

//                    Handler h = new Handler();
//                    long delayInMilliseconds = 5000;
//                    h.postDelayed(new Runnable() {
//                        public void run() {
//                            mNotificationManager.cancel(mNotificationId);
//                        }
//                    }, delayInMilliseconds);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //public static final String PREFS_NAME = "OnlineLoggerMainSettings";

    private JSONObject mFriendsOldLog, mFriendsNewLog;

    //private SharedPreferences mSharedPreferences;

    private Button mLoginButton, mGetFriendsButton;

    private ListView mOnlineListView, mOfflineListView;
    private PersonAdapter mOnlineListAdapter, mOfflineListAdapter;
    private ArrayList<Person> mOnlineListStrings, mOfflineListStrings;
    private NotificationManager mNotificationManager;
    private int mNotificationId;
    private CountDownTimer mCountDownTimer;
    private boolean mIsTimerRunning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (savedInstanceState != null) {
            //mSharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotificationId = 1;

            mLoginButton = (Button) findViewById(R.id.loginButton);
            mGetFriendsButton = (Button) findViewById(R.id.getFriendsButton);

            mOnlineListStrings = new ArrayList<>();
            mOfflineListStrings = new ArrayList<>();
            mOnlineListAdapter = new PersonAdapter(this, R.layout.small_list_item, mOnlineListStrings);
            mOfflineListAdapter = new PersonAdapter(this, R.layout.small_list_item, mOfflineListStrings);

            mOnlineListView = (ListView) findViewById(R.id.onlineListView);
            mOnlineListView.setAdapter(mOnlineListAdapter);
            mOfflineListView = (ListView) findViewById(R.id.offlineListView);
            mOfflineListView.setAdapter(mOfflineListAdapter);

            mOnlineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListView listView = mOnlineListView;
                    PersonAdapter adapter = (PersonAdapter) listView.getAdapter();
                    Person person = adapter.getItem(position);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("vkontakte://profile/%d", person.userId)));
                    startActivity(intent);
                }
            });
            mOfflineListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ListView listView = mOfflineListView;
                    PersonAdapter adapter = (PersonAdapter) listView.getAdapter();
                    Person person = adapter.getItem(position);
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("vkontakte://profile/%d", person.userId)));
                    startActivity(intent);
                }
            });
            mCountDownTimer = new CountDownTimer(Long.MAX_VALUE, 10000) {

                public void onTick(long millisUntilFinished) {
                    updateFriends();
                }

                public void onFinish() {
                    //updateFriends();
                    //Toast.makeText(MainActivity.this, "Таймер завершил работу!", Toast.LENGTH_LONG).show();
                    this.start();
                }
            };
            mIsTimerRunning = false;
        }

        if (VKSdk.isLoggedIn()) {
            mLoginButton.setText(R.string.logout_button_text);
            mGetFriendsButton.setEnabled(true);
        } else {
            mLoginButton.setText(R.string.login_button_text);
            mGetFriendsButton.setEnabled(false);
        }

        mGetFriendsButton.setText(R.string.get_friends_button_on_text);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mCountDownTimer.cancel();
    }

    @Override
    public void onBackPressed() {
        if (mIsTimerRunning) {
            moveTaskToBack(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Toast.makeText(MainActivity.this, "Успешно вошёл!", Toast.LENGTH_LONG).show();
                VKAccessToken.currentToken().save();
                mLoginButton.setText(R.string.logout_button_text);
                mGetFriendsButton.setEnabled(true);
            }

            @Override
            public void onError(VKError error) {
                Toast.makeText(MainActivity.this, "Ошибка входа!", Toast.LENGTH_LONG).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void loginButtonClick(View view) {
        if (!VKSdk.isLoggedIn()) {
            VKSdk.login(this, new String[]{VKScope.FRIENDS});
        } else {
            mCountDownTimer.cancel();
            mIsTimerRunning = !mIsTimerRunning;
            mGetFriendsButton.setText(R.string.get_friends_button_on_text);
            VKSdk.logout();

            mLoginButton.setText(R.string.login_button_text);
            mGetFriendsButton.setEnabled(false);
        }
    }

    public void getFriendsButtonClick(View view) {
        if (mIsTimerRunning) {
            mCountDownTimer.cancel();
            mIsTimerRunning = !mIsTimerRunning;
            mGetFriendsButton.setText(R.string.get_friends_button_on_text);
        } else {
            mCountDownTimer.start();
            mIsTimerRunning = !mIsTimerRunning;
            mGetFriendsButton.setText(R.string.get_friends_button_off_text);
        }
    }

    private void updateFriends() {
        VKRequest request = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "online,last_seen,sex,photo_50"));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                //Toast.makeText(MainActivity.this, "Обновлено!", Toast.LENGTH_SHORT).show();
                mFriendsOldLog = mFriendsNewLog;
                mFriendsNewLog = response.json;

                mOnlineListAdapter.clear();
                mOfflineListAdapter.clear();

                try {
                    CheckDifferences();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                Toast.makeText(MainActivity.this, "Ошибка запроса: " + attemptNumber + totalAttempts, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(VKError error) {
                Person personOnline = new Person();
                personOnline.firstName = getString(R.string.connection_error_text);
                Person personOffline = new Person();
                personOffline.firstName = getString(R.string.connection_error_text);
                mOnlineListAdapter.add(personOnline);
                mOfflineListAdapter.add(personOffline);
                mOnlineListAdapter.notifyDataSetChanged();
                mOnlineListAdapter.notifyDataSetChanged();
                Toast.makeText(MainActivity.this, "Итоговая ошибка всего запроса: " + error.errorMessage + error.errorReason, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void CheckDifferences() throws JSONException {
        if (mFriendsOldLog == null || mFriendsNewLog == null) {
            Person personOnline = new Person();
            personOnline.firstName = getString(R.string.wait_for_update_text);
            Person personOffline = new Person();
            personOffline.firstName = getString(R.string.wait_for_update_text);
            mOnlineListAdapter.add(personOnline);
            mOfflineListAdapter.add(personOffline);
            mOnlineListAdapter.notifyDataSetChanged();
            mOnlineListAdapter.notifyDataSetChanged();
            return;
        }

        JSONObject friendsGetResponse = mFriendsNewLog.getJSONObject("response");
        int count = friendsGetResponse.getInt("count");
        JSONArray friendsItems = friendsGetResponse.getJSONArray("items");
        Person[] newFriends = new Person[count];

        for (int i = 0; i < count; i++) {
            StringBuilder sb = new StringBuilder();

            JSONObject friend = friendsItems.getJSONObject(i);
            Person person = new Person();
            person.userId = friend.getLong("id");
            person.firstName = friend.getString("first_name");
            person.lastName = friend.getString("last_name");
            person.isMale = (friend.getInt("sex") == 2);
            person.photo50url = friend.getString("photo_50");
            person.isOnline = (friend.getInt("online") == 1);
            JSONObject lastSeen = friend.getJSONObject("last_seen");
            person.lastSeen = lastSeen.getLong("time");
            //new DownloadImageTask(this, person).execute(person.photo50url);
            newFriends[i] = person;

            sb.append(person.firstName);
            sb.append(' ');
            sb.append(person.lastName);
            sb.append(" - ");
            sb.append(person.getLastSeenTime());

            if (person.isOnline) {
                mOnlineListAdapter.add(person);
            } else {
                mOfflineListAdapter.add(person);
            }
        }

        mOnlineListAdapter.notifyDataSetChanged();
        mOnlineListAdapter.notifyDataSetChanged();

        friendsGetResponse = mFriendsOldLog.getJSONObject("response");
        count = friendsGetResponse.getInt("count");
        friendsItems = friendsGetResponse.getJSONArray("items");
        Person[] oldFriends = new Person[count];

        for (int i = 0; i < count; i++) {
            JSONObject friend = friendsItems.getJSONObject(i);
            Person person = new Person();
            person.userId = friend.getLong("id");
            person.firstName = friend.getString("first_name");
            person.lastName = friend.getString("last_name");
            person.isMale = (friend.getInt("sex") == 2);
            person.photo50url = friend.getString("photo_50");
            person.isOnline = (friend.getInt("online") == 1);
            JSONObject lastSeen = friend.getJSONObject("last_seen");
            person.lastSeen = lastSeen.getLong("time");
            //new DownloadImageTask(this, person).execute(person.photo50url);
            oldFriends[i] = person;
        }

        int newIndex = -1;
        for (int i = 0; i < oldFriends.length; i++) {
            for (int j = 0; j < newFriends.length; j++) {
                if (oldFriends[i].userId == newFriends[j].userId) {
                    newIndex = j;
                    break;
                }
            }

            if (newIndex > 0) {
                if (!oldFriends[i].isOnline && newFriends[newIndex].isOnline) {
                    new DownloadImageTask(this, newFriends[newIndex]).execute();
                } else if (oldFriends[i].isOnline && !newFriends[newIndex].isOnline) {
                    new DownloadImageTask(this, newFriends[newIndex]).execute();
                }
            }

            newIndex = -1;
        }
    }
}
