package edu.simpleinventoryapp;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import edu.simpleinventoryapp.data.ItemContract.ItemEntry;

/**
 * Created by edu on 7/19/2017.
 */

public class Detail extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final int ITEM_LOADER = 1;

    private Uri mCurrentItemUri;

    TextView mNameTextView;
    TextView mCountTextView;
    TextView mPriceTextView;
    Button mDecreaseButton;
    Button mIncreaseButton;
    Button mOrderSupplyButton;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail);

        mNameTextView = (TextView) findViewById(R.id.item_name);
        mCountTextView = (TextView) findViewById(R.id.stock);
        mPriceTextView = (TextView) findViewById(R.id.item_price);
        mDecreaseButton = (Button) findViewById(R.id.decrease);
        mIncreaseButton = (Button) findViewById(R.id.increase);
        mOrderSupplyButton = (Button) findViewById(R.id.order);
        mImageView = (ImageView) findViewById(R.id.item_image);

        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        if (mCurrentItemUri != null) {
            setTitle("Item detail");
            getSupportLoaderManager().initLoader(ITEM_LOADER, null, this);
        }
    }

    private int adjustAvailability(Uri itemUri, int newCount) {
        if (newCount < 0) return 0;

        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_COUNT, newCount);
        int noOfRowsUpd = getContentResolver().update(itemUri, values, null, null);

        return noOfRowsUpd;
    }

    private int deleteItem(Uri itemUri) {
        return getContentResolver().delete(itemUri, null, null);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                mCurrentItemUri,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
            int countColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_COUNT);
            int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
            int imageColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_IMAGE);


            final String name = cursor.getString(nameColumnIndex);
            final int count = cursor.getInt(countColumnIndex);
            double price = cursor.getDouble(priceColumnIndex);
            byte[] imageByteArray = cursor.getBlob(imageColumnIndex);

            mNameTextView.setText(name);
            mCountTextView.setText(Integer.toString(count));
            mPriceTextView.setText("$" + String.format("%.2f", price));

            Bitmap bmp = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
            mImageView.setImageBitmap(bmp);

            mDecreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adjustAvailability(mCurrentItemUri, count - 1);
                }
            });

            mIncreaseButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adjustAvailability(mCurrentItemUri, count + 1);
                }
            });

            mOrderSupplyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    composeEmail(new String[]{"abc@gmail.com"}, "Supply Order for " + name);
                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(Detail.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete all item information?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteItem(mCurrentItemUri);
                finish();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });


        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public void composeEmail(String[] addresses, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
