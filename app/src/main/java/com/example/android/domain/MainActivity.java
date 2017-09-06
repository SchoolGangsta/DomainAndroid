package com.example.android.domain;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.GridLabelRenderer;
import java.util.SortedSet;
import java.util.TreeSet;
import org.mariuszgromada.math.mxparser.*;

public class MainActivity extends AppCompatActivity {

    public EditText func;
    public Button start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        func = (EditText) findViewById(R.id.function);
        final Button start = (Button) findViewById(R.id.start);

        final GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.setVisibility(View.INVISIBLE);
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinY(-1);
        graph.getViewport().setMaxY(1);
        graph.getViewport().setScalable(true);
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setHorizontalAxisTitle("x");
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);

        start.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                SortedSet<Double> numberSet = new TreeSet<>();
                MathParser parser = new MathParser();
                String expression = func.getText().toString();
                Expression e;
                double lowBound = -50;
                double highBound = 50;
                double numSteps = 1000;
                double step = (highBound-lowBound)/numSteps;
                try {
                    parser.Parse(expression);
                    System.out.println(MathParser.solvable.toString());
                    if (!MathParser.solvable.isEmpty()) {
                        for (String i : MathParser.solvable) {
                            Toast.makeText(getApplicationContext(), i.toString(), Toast.LENGTH_LONG).show();
                            for (int j = 0; j < 1000; ++j) {
                                highBound=lowBound+step;
                                e = new Expression("solve(" + i + ",x,"+lowBound+","+highBound+")");
                                lowBound+=step;
                                double k = e.calculate();
                                numberSet.add(k);
                            }
                        }
                        if (numberSet.contains(Double.NaN))
                            numberSet.remove(Double.NaN);
                        System.out.println(numberSet.toString());

                    }
                } catch (Exception k) {
                    System.out.println(k.getMessage());
                }
                if(numberSet.size()!=0) {
                    graph.setVisibility(View.VISIBLE);
                    graph.getViewport().setMinX(numberSet.first() * 1.3);
                    graph.getViewport().setMaxX(numberSet.last() * 1.3);
                    if (numberSet.size() == 1) {
                        graph.getViewport().setMinX(numberSet.first() - 1);
                        graph.getViewport().setMaxX(numberSet.first() + 1);
                    }
                }
                PointsGraphSeries<DataPoint> series4 = new PointsGraphSeries<>(new DataPoint[]{
                });
                series4.setColor(Color.RED);
                series4.setCustomShape(new PointsGraphSeries.CustomShape() {
                    @Override
                    public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                        paint.setStrokeWidth(5);
                        canvas.drawLine(x - 10, y - 10, x + 10, y + 10, paint);
                        canvas.drawLine(x + 10, y - 10, x - 10, y + 10, paint);
                    }
                });
                for(Double i : numberSet) {
                    Toast.makeText(getApplicationContext(), i.toString(), Toast.LENGTH_LONG).show();
                    series4.appendData(new DataPoint (i, 0),false,Integer.MAX_VALUE,false);
                    }
                    graph.addSeries(series4);
                }


        });



    }

}
