package edu.simpleinventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import edu.simpleinventoryapp.data.ItemContract.ItemEntry;

/**
 * Created by edu on 7/19/2017.
 */

public class AdapterItem extends CursorAdapter {
    private static final String TAG = "MyActivity";

    public AdapterItem(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView tvName = (TextView) view.findViewById(R.id.name);
        TextView tvPrice = (TextView) view.findViewById(R.id.price);
        TextView tvCount = (TextView) view.findViewById(R.id.count);

        final int itemId = cursor.getInt(cursor.getColumnIndex(ItemEntry._ID));
        String name = cursor.getString(cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME));
        Double price = cursor.getDouble(cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE));
        final int count = cursor.getInt(cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_COUNT));

        tvName.setText(name);
        tvPrice.setText("Cost: $" + String.format("%.2f", price));
        tvCount.setText("Availability: " + String.valueOf(count));


        Button btnBuy = (Button) view.findViewById(R.id.buy_button);
        btnBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri itemUri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, itemId);
                buyItem(context, itemUri, count);
            }
        });
    }


    private void buyItem(Context context, Uri itemUri, int currentCount) {
        int newCount = (currentCount >= 1) ? currentCount - 1 : 0;
        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_COUNT, newCount);
        int numRowsUpdated = context.getContentResolver().update(itemUri, values, null, null);

        if (numRowsUpdated > 0) {
            Log.i(TAG, "Buy item successful");
        } else {
            Log.i(TAG, "Could not update buy item");
        }
    }

}
