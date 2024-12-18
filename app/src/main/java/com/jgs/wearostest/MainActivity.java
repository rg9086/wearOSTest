package com.jgs.wearostest;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.wearable.DataClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;



public class MainActivity extends AppCompatActivity implements DataClient.OnDataChangedListener {

    private TextView heartRateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        heartRateTextView = findViewById(R.id.heart_rate);
        Log.d("Heart rate data sent successfully: ", String.valueOf(heartRateTextView));

        // Wearable 데이터 수신 리스너 등록
        Wearable.getNodeClient(this).getConnectedNodes()
                .addOnSuccessListener(nodes -> {
                    if (!nodes.isEmpty()) {
                        for (Node node : nodes) {
                            Log.d("Node Info", "Connected node: " + node.getDisplayName());
                        }
                    } else {
                        Log.d("Node Info", "No connected wearable devices.");
                    }
                })
                .addOnFailureListener(e -> Log.e("Node Info", "Failed to get connected nodes", e));
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEventBuffer) {
        for (DataEvent event : dataEventBuffer) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                String path = event.getDataItem().getUri().getPath();
                System.out.println("Received data at path: " + path);
                if ("/heart_rate".equals(path)) {
                    DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                    int heartRate = dataMapItem.getDataMap().getInt("heart_rate");



                    // UI 업데이트
                    runOnUiThread(() -> heartRateTextView.setText("Heart Rate: " + heartRate));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Wearable.getDataClient(this).removeListener(this);
    }
}