package org.maoxin.zkapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import lecho.lib.hellocharts.gesture.ContainerScrollType;
import lecho.lib.hellocharts.gesture.ZoomType;
import lecho.lib.hellocharts.listener.LineChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.LineChartView;

public class hrv_analysis extends AppCompatActivity {
    private LineChartView mChartView;
    private List<PointValue> values;
    private List<Line> lines;
    private LineChartData lineChartData;
    private LineChartView lineChartView;
    private List<Line> linesList;
    private List<PointValue> pointValueList;
    private List<PointValue> points;
    private int position = 0;
    private Timer timer;
    private boolean isFinish = true;
    private Axis axisY, axisX;
    private Random random = new Random();
    private ArrayList<Integer> HRV;
    private ArrayList<Integer> SDA;
    private ArrayList<Integer> rMSSDA;

    private LineChartView mChart;
    private LineChartView mChart1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hrv_analysis_layout);
        Intent intent=getIntent();

        HRV=intent.getIntegerArrayListExtra("HR");
        SDA=intent.getIntegerArrayListExtra("SD");
        rMSSDA=intent.getIntegerArrayListExtra("rMSSD");

        int HRV_control=HRV.size();
        int SD_control=SDA.size();
        double SDNN=0;
        double rMSSD=0;
        int SDNN_show=0;
        int rMSSD_show=0;
        if (HRV_control>1){
            SDNN=(SDA.get(SDA.size()-1))/10;
            //SDNN=45;
            SDNN_show=(int)(Math.abs(SDNN-30))*2;
            rMSSD=(rMSSDA.get(rMSSDA.size()-1))/10;
            //SDNN=60;
            rMSSD_show=(int)(Math.abs(rMSSD-42))*3;
        }

        TextView HR=findViewById(R.id.hr);
        TextView SDNN_text=findViewById(R.id.SDNN);
        TextView SDNN1_text=findViewById(R.id.SDNN1);

        DecimalFormat decimalFormat=new DecimalFormat(".00");
        HR.setText("平均心率："+HRV.get(HRV.size()-1)+" bpm   SDNN："+(decimalFormat.format(SDNN))+" ms   rMSSD："+(decimalFormat.format(rMSSD))+" ms");
        //HR.setText("平均心率："+HRV.get(HRV.size()-1)+" bpm   SDNN："+(SDNN)+" ms   rMSSD："+(rMSSD)+" ms");
        SDNN_text.setText("精神压力： "+SDNN_show);
        SDNN1_text.setText("疲劳指数： "+rMSSD_show);

        ArrayList<PointValue> values = new ArrayList<PointValue>();
        for(int i=0;i<HRV.size();i++){
            float temp=HRV.get(i);
            BigDecimal bd   =   new   BigDecimal((double)temp);
            bd=bd.setScale(2,4);
            temp = bd.floatValue();
            values.add(new PointValue(i+1,temp+1-1));
        }

        Line line = new Line(values);
        line.setColor(ChartUtils.COLOR_BLUE);
        line.setShape(ValueShape.CIRCLE);
        line.setPointRadius(1);
        line.setStrokeWidth(0);
        line.setHasPoints(true);
        line.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据

        line.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        //line.setHasLabels(true);

        ArrayList<Line> lines = new ArrayList<Line>();lines.add(line);

        LineChartData data = new LineChartData(lines);

        Axis axisX = new Axis();
        //axisX.setName("");
        axisX.setTextColor(Color.BLACK);
        axisX.setTextSize(6);
        Axis axisY = new Axis();
        axisY.setName("RR间期/ms");
        axisY.setTextSize(7);

        //axisY.setHasLines(false);

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);
        mChart =  findViewById(R.id.chart);
        //mChart.setInteractive(true);//设置图表是可以交互的（拖拽，缩放等效果的前提）
        //mChart.setZoomEnabled(true);
        mChart.setLineChartData(data);
        Viewport port=new Viewport(mChart.getMaximumViewport());
        port.bottom = 20f;
        port.top = 120f;
        port.left = 1f;
        port.right = HRV_control;
        mChart.setMaximumViewport(port);
        mChart.setCurrentViewport(port);
        mChart.setOnValueTouchListener(new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {

                Toast toast=Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
                toast.setText(""+value.getX()+","+value.getY());
                toast.setGravity(Gravity.CENTER,0,-10);
                toast.show();

            }

            @Override
            public void onValueDeselected() {

            }
        });

        ArrayList<PointValue> values1 = new ArrayList<PointValue>();
        for(int i=0;i<SDA.size();i++){
            float temp=SDA.get(i);
            BigDecimal bd   =   new   BigDecimal((double)temp);
            bd=bd.setScale(2,4);
            temp = bd.floatValue();
            values1.add(new PointValue(i+1,temp+1-1));
        }
        /*values1.add(new PointValue(1,42));
        values1.add(new PointValue(2,45));
        values1.add(new PointValue(3,41));
        values1.add(new PointValue(4,45));*/
        ArrayList<PointValue> values2 = new ArrayList<PointValue>();
        for(int i=0;i<rMSSDA.size();i++){
            float temp=rMSSDA.get(i);
            BigDecimal bd   =   new   BigDecimal((double)temp);
            bd=bd.setScale(2,4);
            temp = bd.floatValue();
            values2.add(new PointValue(i+1,temp+1-1));
        }
        /*values2.add(new PointValue(1,52));
        values2.add(new PointValue(2,50));
        values2.add(new PointValue(3,46));
        values2.add(new PointValue(4,60));*/
        Line line1 = new Line(values1);
        Line line2 = new Line(values2);
        line1.setColor(ChartUtils.COLOR_BLUE);
        line2.setColor(ChartUtils.COLOR_RED);
        line1.setShape(ValueShape.CIRCLE);
        line1.setPointRadius(1);
        line1.setStrokeWidth(0);
        line1.setHasPoints(true);
        line1.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据
        line1.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        line2.setShape(ValueShape.CIRCLE);
        line2.setPointRadius(1);
        line2.setStrokeWidth(0);
        line2.setHasPoints(true);
        line2.setHasLabelsOnlyForSelected(true);//点击数据坐标提示数据
        line2.setHasLines(true);//是否用线显示。如果为false 则没有曲线只有点显示
        //line.setHasLabels(true);

        ArrayList<Line> lines1 = new ArrayList<Line>();lines1.add(line1);lines1.add(line2);

        LineChartData data1 = new LineChartData(lines1);

        Axis axisX1 = new Axis();
        //axisX.setName("");
        axisX1.setTextColor(Color.BLACK);
        axisX1.setTextSize(6);
        Axis axisY1 = new Axis();
        axisY1.setName("HRV参数/ms");
        axisY1.setTextSize(7);


        data1.setAxisXBottom(axisX1);
        data1.setAxisYLeft(axisY1);
        mChart1 =  findViewById(R.id.chart2);
        //mChart.setInteractive(true);//设置图表是可以交互的（拖拽，缩放等效果的前提）
        //mChart.setZoomEnabled(true);
        mChart1.setLineChartData(data1);
        Viewport port1=new Viewport(mChart1.getMaximumViewport());
        port1.bottom = 20f;
        port1.top = 80f;
        port1.left = 1f;
        port1.right = SD_control;
        mChart1.setMaximumViewport(port1);
        mChart1.setCurrentViewport(port1);
        mChart1.setOnValueTouchListener(new LineChartOnValueSelectListener() {
            @Override
            public void onValueSelected(int lineIndex, int pointIndex, PointValue value) {

                Toast toast=Toast.makeText(getApplicationContext(), null, Toast.LENGTH_SHORT);
                toast.setText(""+value.getX()+","+value.getY());
                toast.setGravity(Gravity.CENTER,0,-10);
                toast.show();

            }

            @Override
            public void onValueDeselected() {

            }
        });

        Button bt=(Button) findViewById(R.id.button);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        GoodProgressView good_progress_view = (GoodProgressView)findViewById(R.id.good_progress_view);
        GoodProgressView good_progress_view1 = (GoodProgressView)findViewById(R.id.good_progress_view1);
        good_progress_view.setProgressValue(SDNN_show);
        good_progress_view1.setProgressValue(rMSSD_show);

    }



}
