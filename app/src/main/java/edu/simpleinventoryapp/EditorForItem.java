package edu.simpleinventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;

import edu.simpleinventoryapp.data.ItemContract.ItemEntry;

/**
 * Created by edu on 7/19/2017.
 */

public class EditorForItem extends AppCompatActivity {
    private static final int FILE_SELECT_CODE = 2;
    private Uri mCurrentItemUri;

    EditText mNameEditText;
    EditText mCountEditText;
    EditText mPriceEditText;
    Button mImageButton;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editor);

        mNameEditText = (EditText) findViewById(R.id.etName);
        mCountEditText = (EditText) findViewById(R.id.etCount);
        mPriceEditText = (EditText) findViewById(R.id.etPrice);
        mImageButton = (Button) findViewById(R.id.item_image);
        mImageView = (ImageView) findViewById(R.id.image_view);


        mImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonImageClick();
            }
        });


        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        if (mCurrentItemUri == null) {
            setTitle("Add a item");
        }
    }

    private void buttonImageClick() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), FILE_SELECT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_SELECT_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    Uri imageUri = data.getData();
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);

                    mImageView = (ImageView) findViewById(R.id.image_view);
                    mImageView.setImageBitmap(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void saveItem() {
        String nameString = mNameEditText.getText().toString().trim();
        String countString = mCountEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(countString)
                || TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, "Please fill out all values", Toast.LENGTH_SHORT).show();
            return;
        }

        int count = Integer.valueOf(countString);
        double price = Double.valueOf(priceString);

        if (count < 0) {
            Toast.makeText(this, "Real number required for the count field", Toast.LENGTH_SHORT).show();
            return;
        }

        if (price < 0.0) {
            Toast.makeText(this, "Price must be a real number", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mImageView.getDrawable() == null) {
            Toast.makeText(this, "Image required", Toast.LENGTH_SHORT).show();
            return;
        }

        Bitmap imageBitMap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        imageBitMap.compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] imageByteArray = bos.toByteArray();


        ContentValues values = new ContentValues();
        values.put(ItemEntry.COLUMN_ITEM_NAME, nameString);
        values.put(ItemEntry.COLUMN_ITEM_COUNT, count);
        values.put(ItemEntry.COLUMN_ITEM_PRICE, price);
        values.put(ItemEntry.COLUMN_ITEM_IMAGE, imageByteArray);

        Uri newUri = getContentResolver().insert(ItemEntry.CONTENT_URI, values);


        if (newUri == null) {

            Toast.makeText(this, "Failed to insert a new item.", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, "Successfully inserted new item.", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:
                saveItem();
                return true;

            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(EditorForItem.this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
