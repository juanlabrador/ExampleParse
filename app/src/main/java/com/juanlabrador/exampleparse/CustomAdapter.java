package com.juanlabrador.exampleparse;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by juanlabrador on 19/10/15.
 */
public class CustomAdapter extends ArrayAdapter {


    private final Context context;
    private List<Player> players;
    private LayoutInflater inflater = null;
    private int cantidad = 0;


    public CustomAdapter(Context context) {
        super(context, R.layout.custom_item);
        this.context = context;
        inflater = LayoutInflater.from(context);
        players = new ArrayList<>();
    }

    class ViewHolder {
        ImageView image;
        TextView id;
        TextView name;
        TextView score;
        TextView uuid;
    }

    public void add(Player object) {
        players.add(object);
    }

    @Override
    public void addAll(Collection collection) {
        players = new ArrayList<>(collection);
    }

    @Override
    public Object getItem(int position) {
        return players.get(position);
    }

    @Override
    public int getCount() {
        Log.i("TAG SIZE!", ""+ players.size());
        return players.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.custom_item, null);

            holder = new ViewHolder();

            holder.image = (ImageView) convertView.findViewById(R.id.photo);
            holder.id = (TextView) convertView
                    .findViewById(R.id.id);
            holder.uuid = (TextView) convertView.findViewById(R.id.uuid);
            holder.name = (TextView) convertView
                    .findViewById(R.id.name);
            holder.score = (TextView) convertView.findViewById(R.id.score);

            convertView.setTag(holder);

        } else
            holder = (ViewHolder) convertView.getTag();


        Player d = (Player) getItem(position);

        Log.i("tag TIMES", "times: " + cantidad++);
        ParseFile file = d.getPhoto();
        if (file != null) {
            file.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory
                                .decodeByteArray(
                                        data, 0,
                                        data.length);

                        Log.i("TAG", "inside!");
                        holder.image.setImageBitmap(bmp);
                    }
                }
            });
        } else
            holder.image.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        holder.id.setText(d.getObjectId());

        JSONArray array = d.getCars();
        Log.i("TAG array", array.toString());
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < array.length(); i++) {
            Gson gson = new Gson();
            try {
                Log.i("TAG Object", array.get(i).toString());
                Car car = gson.fromJson(array.get(i).toString(), Car.class);
                //JSONObject jsonobject = array.getJSONObject(i);
                str.append(car.getModel());
                str.append("\n");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        holder.uuid.setText(str.toString());


        holder.name.setText(d.getName());
        holder.score.setText(String.valueOf(d.getScore()));

        return convertView;
    }
}
