package pt.iscte.daam.pinpointhint;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import pt.iscte.daam.pinpointhint.model.Pin;

public class ListActivity extends AppCompatActivity {

    private ListView pinsLV;
    private final static String mLogTag = "pin point log";

    public List<Pin> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        pinsLV = (ListView) findViewById(R.id.listView);

        pinsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Pin pin = items.get(position);
                Intent i = new Intent(ListActivity.this, DetailsActivity.class);
                i.putExtra("ID", ""+pin.getIdent()+"");
                i.putExtra("DESCR", pin.getDescr());
                startActivity(i);
            }
        });


        items = PinGeoJonReader.getPinInstances();

        String [] values = new String[items.size()];
        Integer n = 0;
        for (Pin pin : items) {
            values[n] = pin.getDescr();
            n = n + 1;
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(),
                android.R.layout.simple_list_item_1, values);

        pinsLV.setAdapter(adapter);

    }
}
