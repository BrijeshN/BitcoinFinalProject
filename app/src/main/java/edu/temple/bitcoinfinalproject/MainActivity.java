package edu.temple.bitcoinfinalproject;

import android.Manifest;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.zxing.Result;

import java.util.ArrayList;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class MainActivity extends AppCompatActivity implements ListViewFragment.ListInterface, GraphFragment.ChartInterface, BlockFragment.BlockInterface,
        ZXingScannerView.ResultHandler, BalanceFragment.BalanceInterface {

    // Setting permission to 1
    final int PERMISSION = 1;

    // index set to 0
    int index = 0;

    // Arraylist that holds main list
    ArrayList<BlockFragment> mainList;

    // List view adapter
    ListViewAdapter listAdapter;

    // viewPager view
    ViewPager pagerView;

    // QR code scanner
    private ZXingScannerView getAddressFromQRCode;

    // Broadcast bCast
    private BroadcastReceiver bCast;

    // Initilize graph fragment
    GraphFragment graphFragment;

    // set twopane - portrait and landscape to false as well as ser
    boolean twoPanes, ser = false;

    // Initilize list view fragment
    ListViewFragment listViewFragment;

    // Initilize priceFragment
    PriceFragment priceFragment;

    // Initilize balanceFragment
    BalanceFragment balanceFragment;

    // Initilize block fragment
    BlockFragment blockFragment;

    // Default position set to 0
    int position = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stopService(new Intent(this, ServiceBG.class));
        ser = false;
        if (bCast != null) {
            unregisterReceiver(bCast);
            bCast = null;
        }

        twoPanes = (findViewById(R.id.display_list) != null);


        if (listViewFragment == null)
            listViewFragment = new ListViewFragment();

        if (graphFragment == null)
            graphFragment = new GraphFragment();

        if (priceFragment == null)
            priceFragment = new PriceFragment();

        if (balanceFragment == null)
            balanceFragment = new BalanceFragment();

        if (blockFragment == null) {
            blockFragment = new BlockFragment();
            Bundle args = new Bundle();
            args.putString("apiBlocknumber", String.valueOf(12345));
            blockFragment.setArguments(args);
        }

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main_list, listViewFragment)
                .commit();

        if (twoPanes) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.display_list, priceFragment)
                    .commit();
        } else {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_list, priceFragment)
                    .commit();
        }
        getFragmentManager().executePendingTransactions();

        position = getIntent().getIntExtra("position", 0);
        if (position == 3) {
            Log.d("bcAddress", getIntent().getStringExtra("bcAddress"));
            if (!twoPanes && getIntent().getStringExtra("bcAddress").length() == 0) {
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.main_list, listViewFragment)
                        .commit();
            } else {
                balanceFragment.blockAddress = getIntent().getStringExtra("bcAddress");
                loadList(position);
            }
        }
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt("position");

            if (position == 1) {
                graphFragment.url = savedInstanceState.getString("url");
                graphFragment.text = savedInstanceState.getString("text");
                graphFragment.tap = savedInstanceState.getBoolean("tap");
                if (graphFragment.tap) {
                    graphFragment.updateChart();
                    startService();
                }
            }

            if (position == 2) {
                mainList = savedInstanceState.getParcelableArrayList("fragArray");
            }

            if (position == 3) {
                balanceFragment.balance = savedInstanceState.getDouble("balance");
                balanceFragment.blockAddress = savedInstanceState.getString("bcAddress");
            }

            loadList(position);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("position", position);
        if (position == 1) {
            outState.putString("url", graphFragment.url);
            outState.putString("text", graphFragment.getDayFromUser.getText().toString());
            outState.putBoolean("tap", graphFragment.tap);
        }

        if (position == 2) {
            outState.putParcelableArrayList("fragArray", mainList);
        }

        if (position == 3) {
            outState.putString("bcAddress", balanceFragment.blockAddress);
            outState.putDouble("balance", balanceFragment.balance);
        }

        stopService(new Intent(this, ServiceBG.class));
        ser = false;
        if (bCast != null) {
            unregisterReceiver(bCast);
            bCast = null;
        }

        super.onSaveInstanceState(outState);
    }


    @Override
    public void onPause() {
        super.onPause();
        if (getAddressFromQRCode != null)
            getAddressFromQRCode.stopCamera();           // Stop camera on pause
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.menu) {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }

            stopService(new Intent(this, ServiceBG.class));
            ser = false;
            if (bCast != null) {
                unregisterReceiver(bCast);
                bCast = null;
            }

            if (getAddressFromQRCode != null) {
                getAddressFromQRCode.stopCamera();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("position", 3);
                intent.putExtra("bcAddress", "");
                startActivity(intent);
                return true;
            }

            if (position == 2 && !twoPanes) {
                if (pagerView != null) {
                    index = pagerView.getCurrentItem();
                    listAdapter = new ListViewAdapter(getSupportFragmentManager(), new ArrayList<BlockFragment>());
                    pagerView = (ViewPager) findViewById(R.id.viewPager);
                    pagerView.setAdapter(listAdapter);
                    pagerView = null;
                }
            }

            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_list, listViewFragment)
                    .commit();

            getFragmentManager().executePendingTransactions();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if (bCast != null) {
            unregisterReceiver(bCast);
            bCast = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopService(new Intent(this, ServiceBG.class));
        ser = false;
        if (bCast != null) {
            unregisterReceiver(bCast);
            bCast = null;
        }
    }

    @Override
    public void loadList(int position) {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

        if (position == 0) {
            if (pagerView != null) {
                index = pagerView.getCurrentItem();
                listAdapter = new ListViewAdapter(getSupportFragmentManager(), new ArrayList<BlockFragment>());
                pagerView = (ViewPager) findViewById(R.id.viewPager);
                pagerView.setAdapter(listAdapter);
                pagerView = null;
            }
            this.position = 0;
            stopService(new Intent(this, ServiceBG.class));
            ser = false;
            if (bCast != null) {
                unregisterReceiver(bCast);
                bCast = null;
            }

            Fragment newFragment;
            if (!twoPanes)
                newFragment = getFragmentManager().findFragmentById(R.id.main_list);
            else
                newFragment = getFragmentManager().findFragmentById(R.id.display_list);
            if (newFragment == null || !newFragment.getClass().equals(PriceFragment.class)) {
                if (!twoPanes) {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_list, priceFragment)
                            .addToBackStack(null)
                            .commit();
                    getFragmentManager().executePendingTransactions();
                } else {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.display_list, priceFragment)
                            .commit();
                    getFragmentManager().executePendingTransactions();
                }
            }
        }

        if (position == 1) {
            this.position = 1;
            if (pagerView != null) {
                index = pagerView.getCurrentItem();
                listAdapter = new ListViewAdapter(getSupportFragmentManager(), new ArrayList<BlockFragment>());
                pagerView = (ViewPager) findViewById(R.id.viewPager);
                pagerView.setAdapter(listAdapter);
                pagerView = null;
            }
            Fragment existingFragment;
            if (!twoPanes)
                existingFragment = getFragmentManager().findFragmentById(R.id.main_list);
            else
                existingFragment = getFragmentManager().findFragmentById(R.id.display_list);
            if (existingFragment == null || !existingFragment.getClass().equals(GraphFragment.class)) {
                if (!twoPanes) {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_list, graphFragment)
                            .addToBackStack(null)
                            .commit();
                    getFragmentManager().executePendingTransactions();
                } else {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.display_list, graphFragment)
                            .commit();
                    getFragmentManager().executePendingTransactions();
                }
            }
            if (graphFragment.tap) {
                graphFragment.updateChart();
                startService();
            }
        }

        if (position == 2) {
            this.position = 2;
            stopService(new Intent(this, ServiceBG.class));
            ser = false;
            if (bCast != null) {
                unregisterReceiver(bCast);
                bCast = null;
            }

            if (mainList == null) {
                mainList = new ArrayList<>();
            }

            Fragment existingFragment;
            if (!twoPanes)
                existingFragment = getFragmentManager().findFragmentById(R.id.main_list);
            else
                existingFragment = getFragmentManager().findFragmentById(R.id.display_list);

            if (existingFragment != null) {
                getFragmentManager()
                        .beginTransaction().remove(existingFragment).commit();
            }
            if (!mainList.contains(blockFragment) && mainList.size() == 0)
                mainList.add(blockFragment);
            listAdapter = new ListViewAdapter(getSupportFragmentManager(), mainList);
            pagerView = (ViewPager) findViewById(R.id.viewPager);
            pagerView.setAdapter(listAdapter);
            pagerView.setCurrentItem(index);
        }

        if (position == 3) {
            this.position = 3;
            if (pagerView != null) {
                index = pagerView.getCurrentItem();
                listAdapter = new ListViewAdapter(getSupportFragmentManager(), new ArrayList<BlockFragment>());
                pagerView = (ViewPager) findViewById(R.id.viewPager);
                pagerView.setAdapter(listAdapter);
                pagerView = null;
            }
            stopService(new Intent(this, ServiceBG.class));
            ser = false;
            if (bCast != null) {
                unregisterReceiver(bCast);
                bCast = null;
            }

            Fragment existingFragment;
            if (!twoPanes)
                existingFragment = getFragmentManager().findFragmentById(R.id.main_list);
            else
                existingFragment = getFragmentManager().findFragmentById(R.id.display_list);
            if (existingFragment == null || !existingFragment.getClass().equals(BalanceFragment.class)) {
                if (!twoPanes) {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.main_list, balanceFragment)
                            .addToBackStack(null)
                            .commit();

                    getFragmentManager().executePendingTransactions();
                } else {
                    getFragmentManager()
                            .beginTransaction()
                            .replace(R.id.display_list, balanceFragment)
                            .commit();
                    getFragmentManager().executePendingTransactions();
                }
            }

        }


    }

    @Override
    public void startService() {

        if (!ser) {
            startService(new Intent(this, ServiceBG.class));
            ser = true;

            IntentFilter filter = new IntentFilter();
            filter.addAction("update");
            bCast = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    graphFragment.updateChart();
                }
            };
            registerReceiver(bCast, filter);
        }
    }

    @Override
    public void previous_block(int blockNum) {
        //index = pagerView.getCurrentItem() - 1;
        Log.d("index", index + "");
        if (pagerView.getCurrentItem() != 0) {
            pagerView.setCurrentItem(pagerView.getCurrentItem() - 1);
            return;
        }

        BlockFragment fragment = new BlockFragment();
        Bundle args = new Bundle();
        args.putString("apiBlocknumber", String.valueOf(blockNum));
        fragment.setArguments(args);
        mainList.add(0, fragment);
        listAdapter = new ListViewAdapter(getSupportFragmentManager(), mainList);
        pagerView = (ViewPager) findViewById(R.id.viewPager);
        pagerView.setAdapter(listAdapter);
        pagerView.setCurrentItem(mainList.indexOf(fragment));
    }


    @Override
    public void next_block(int blockNum) {
        // index = pagerView.getCurrentItem() + 1;
        Log.d("index", index + "");
        if (pagerView.getCurrentItem() != mainList.size() - 1) {
            pagerView.setCurrentItem(pagerView.getCurrentItem() + 1);
            return;
        }

        BlockFragment fragment = new BlockFragment();
        Bundle args = new Bundle();
        args.putString("apiBlocknumber", String.valueOf(blockNum));
        fragment.setArguments(args);
        mainList.add(fragment);
        listAdapter = new ListViewAdapter(getSupportFragmentManager(), mainList);
        pagerView = (ViewPager) findViewById(R.id.viewPager);
        pagerView.setAdapter(listAdapter);
        pagerView.setCurrentItem(mainList.indexOf(fragment));
    }


    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here

        Log.e("handler", rawResult.getText()); // Prints scan results
        Log.e("handler", rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode)

        // show the scanner result into dialog box.
        getAddressFromQRCode.stopCamera();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("position", 3);
        intent.putExtra("bcAddress", rawResult.getText());
        startActivity(intent);

        // If you would like to resume scanning, call this method below:
        // getAddressFromQRCode.resumeCameraPreview(this);
    }

    @Override
    public void scanQR() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISSION);
        } else {
            getAddressFromQRCode = new ZXingScannerView(this);   // Programmatically initialize the scanner view
            setContentView(getAddressFromQRCode);

            getAddressFromQRCode.setResultHandler(this); // Register ourselves as a handler for scan results.
            getAddressFromQRCode.startCamera();         // Start camera
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getAddressFromQRCode = new ZXingScannerView(this);   // Programmatically initialize the scanner view
                    setContentView(getAddressFromQRCode);

                    getAddressFromQRCode.setResultHandler(this); // Register ourselves as a handler for scan results.
                    getAddressFromQRCode.startCamera();         // Start camera
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}

