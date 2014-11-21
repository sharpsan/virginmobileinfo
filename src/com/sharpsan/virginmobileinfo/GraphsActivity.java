package com.sharpsan.virginmobileinfo;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.jjoe64.graphview.BarGraphView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.sharpsan.virginmobileinfo.profile.AccountAdapter;
import com.sharpsan.virginmobileinfo.profile.LoginAdapter;

public class GraphsActivity extends Activity {

	// database vars
	private LoginAdapter mLoginAdapter;
	private AccountAdapter mAccountAdapter;
	private Cursor mLoginCursor;
	private Cursor mAccountCursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_graphs);
		showGraph();
	}

	public void showGraph() {
		// init example series data
		GraphViewSeries exampleSeries = new GraphViewSeries(new GraphViewData[] { new GraphViewData(1, 2.0d), new GraphViewData(2, 1.5d),
				new GraphViewData(3, 2.5d), new GraphViewData(4, 1.7d), new GraphViewData(5, 2.3d) });

		GraphView graphView = new BarGraphView(this // context
				, "Recent Minutes Useage" // heading
		);
		graphView.addSeries(exampleSeries); // data

		LinearLayout layout = (LinearLayout) findViewById(R.id.layout_graphs);
		layout.addView(graphView);
	}

}