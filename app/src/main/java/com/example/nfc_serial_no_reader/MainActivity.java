package com.example.nfc_serial_no_reader;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private TextView nfcIdTextView;
    private NfcAdapter nfcAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nfcIdTextView = findViewById(R.id.nfcIdTextView);
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Check if NFC is available and enabled on the device
        if (nfcAdapter == null || !nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            // Create a pending intent that will be used to capture NFC events
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                    new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

            // Define the intent filters to specify which NFC events to handle
            IntentFilter[] intentFiltersArray = new IntentFilter[]{
                    new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED),
                    new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED),
                    new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
            };

            // Enable foreground dispatch to capture NFC events
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFiltersArray, null);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (nfcAdapter != null) {
            // Disable foreground dispatch when the activity is paused
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            // Retrieve the NFC tag from the intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                // Get the ID byte array from the tag
                byte[] id = tag.getId();
                // Convert the byte array to a readable string
                String idString = bytesToHexString(id);
                // Update the UI to display the ID
                nfcIdTextView.setText(idString);
            }
        }
    }

    // Helper method to convert a byte array to a hexadecimal string
    private String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(String.format("%02x", aByte));
        }
        return sb.toString();
    }

    // The following methods are not used in the provided code snippet:

    // Enable NFC foreground dispatch with intent filters and tech lists
    private void enableNfcForegroundDispatch() {
        // Create an intent to launch the current activity when NFC tag is discovered
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_RECEIVER_REPLACE_PENDING);

        // Create a pending intent for the NFC foreground dispatch
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, 0);

        // Define the intent filters for NFC events
        IntentFilter[] intentFilters = new IntentFilter[] {
                new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
        };

        // Define the supported NFC tech lists
        String[][] techList = new String[][] {
                new String[] { NfcA.class.getName() }
        };

        // Enable the NFC foreground dispatch
        nfcAdapter.enableForegroundDispatch(
                this, pendingIntent, intentFilters, techList);
    }

    // Disable NFC foreground dispatch
    private void disableNfcForegroundDispatch() {
        // Disable the NFC foreground dispatch
        nfcAdapter.disableForegroundDispatch(this);
    }

    // Handle the NFC intent and extract the NFC ID
    private void handleNfcIntent(Intent intent) {
        // Check if the intent is for a tag discovered event
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            // Retrieve the NFC tag from the intent
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tag != null) {
                // Get the ID byte array from the tag
                byte[] idBytes = tag.getId();
                // Convert the byte array to a hexadecimal string
                String nfcId = byteArrayToHexString(idBytes);
                // Display the NFC ID
                displayNfcId(nfcId);
            }
        }
    }

    // Helper method to convert a byte array to a hexadecimal string
    private String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(String.format("%02x", aByte & 0xFF));
        }
        return sb.toString();
    }

    // Update the UI to display the NFC ID
    private void displayNfcId(String nfcId) {
        nfcIdTextView.setText(nfcId);
    }
}