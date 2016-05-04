package com.aquilaeye.android.mosaicgrid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        List<ImageItem> gaggeredList = getListItemData();

        MosaicRecyclerViewAdapter rcAdapter = new MosaicRecyclerViewAdapter(MainActivity.this, gaggeredList);
        recyclerView.setLayoutManager(rcAdapter.getLayoutManager());
        recyclerView.setAdapter(rcAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private List<ImageItem> getListItemData() {
        List<ImageItem> listViewItems = new ArrayList<ImageItem>();
        listViewItems.add(new ImageItem(R.drawable.small));
        listViewItems.add(new ImageItem(R.drawable.small2));
        listViewItems.add(new ImageItem(R.drawable.small1));
        listViewItems.add(new ImageItem(R.drawable.small3));
        listViewItems.add(new ImageItem(R.drawable.small4));
        listViewItems.add(new ImageItem(R.drawable.small5));
        listViewItems.add(new ImageItem(R.drawable.small6));
        listViewItems.add(new ImageItem(R.drawable.small7));
        listViewItems.add(new ImageItem(R.drawable.small8));
        listViewItems.add(new ImageItem(R.drawable.small9));
        listViewItems.add(new ImageItem(R.drawable.small10));
        return listViewItems;
    }


}
