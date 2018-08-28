package barcode.along.barcode.fragment;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.google.gson.Gson;

import org.apache.log4j.chainsaw.Main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import barcode.along.barcode.Api.ApiService;
import barcode.along.barcode.Api.HttpObserver;
import barcode.along.barcode.MainActivity;
import barcode.along.barcode.R;
import barcode.along.barcode.Utils.App;
import barcode.along.barcode.bean.ComMessageBean;
import barcode.along.barcode.bean.FailedMessage;
import barcode.along.barcode.bean.HttpResult;
import barcode.along.barcode.bean.QueryResultBean;
import barcode.along.barcode.bean.TBodyBean;
import io.reactivex.disposables.Disposable;

/**
 * A simple {@link Fragment} subclass.
 */
public class StatisticFragment extends Fragment {
    private View view;
    private PieChart mChart;
    private EditText cvstart;
    private EditText cvend;
    private ImageView iv_start;
    private ImageView iv_end;
    private Button btn_statistic;

    public static StatisticFragment newInstance() {
        return new StatisticFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_statistic, container, false);
        // Inflate the layout for this fragment
        //transitionsContainer = (ViewGroup) view.findViewById(R.id.search_container);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cvstart = (EditText)view.findViewById(R.id.cld_start);
        cvend = (EditText)view.findViewById(R.id.cld_end);
        iv_start = (ImageView) view.findViewById(R.id.qstart);
        iv_end = (ImageView)view.findViewById(R.id.qend);
        btn_statistic = (Button) view.findViewById(R.id.btn_query);

        mChart = view.findViewById(R.id.chart1);
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 10, 5, 5);

        mChart.setDragDecelerationFrictionCoef(0.95f);

        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(58f);
        mChart.setTransparentCircleRadius(61f);

        mChart.setDrawCenterText(true);

        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);



        iv_start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showDatePickDlg(cvstart);
            }
        });

        iv_end.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                showDatePickDlg(cvend);
            }
        });

        btn_statistic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String querystr = cvstart.getText() + "|" + cvend.getText();
                setData(querystr);
            }
        });

        //

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        // entry label styling
        mChart.setEntryLabelColor(Color.BLACK);
        //mChart.setEntryLabelTypeface(mTfRegular);
        mChart.setEntryLabelTextSize(12f);
    }

    private void setData(String querystr) {

        ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

        Gson gson = new Gson();
        TBodyBean tbody = new TBodyBean();

        tbody.setData(querystr);
        ApiService.getApiService().getStatistic(new HttpObserver<List>() {
            @Override
            public void onNext(List result) {
                Float total = 0f;
                for (int i = 0; i < 3 ; i++) {
                    if(i ==0){
                        if(Float.parseFloat(result.get(0).toString()) > 0.01){
                            entries.add(new PieEntry(Float.parseFloat(result.get(0).toString()),
                                    "万隆",
                                    getResources().getDrawable(R.drawable.persons)));
                            total += Float.parseFloat(result.get(0).toString());
                        }
                    }else if(i == 1){
                        if(Float.parseFloat(result.get(1).toString()) > 0.01){
                            entries.add(new PieEntry(Float.parseFloat(result.get(1).toString()),
                                    "格林",
                                    getResources().getDrawable(R.drawable.persons)));
                            total += Float.parseFloat(result.get(1).toString());
                        }
                    }else if(i == 2){
                        if(Float.parseFloat(result.get(2).toString()) > 0.01){
                            entries.add(new PieEntry(Float.parseFloat(result.get(2).toString()),
                                    "其它",
                                    getResources().getDrawable(R.drawable.persons)));
                            total += Float.parseFloat(result.get(2).toString());
                        }
                    }
                }
                mChart.setCenterText(generateCenterSpannableText(total));
                PieDataSet dataSet = new PieDataSet(entries, "占有公司");

                dataSet.setDrawIcons(false);

                dataSet.setSliceSpace(3f);
                dataSet.setIconsOffset(new MPPointF(0, 40));
                dataSet.setSelectionShift(5f);

                // add a lot of colors

                ArrayList<Integer> colors = new ArrayList<Integer>();

                for (int c : ColorTemplate.VORDIPLOM_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.JOYFUL_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.COLORFUL_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.LIBERTY_COLORS)
                    colors.add(c);

                for (int c : ColorTemplate.PASTEL_COLORS)
                    colors.add(c);

                colors.add(ColorTemplate.getHoloBlue());

                dataSet.setColors(colors);
                //dataSet.setSelectionShift(0f);

                PieData data = new PieData(dataSet);
                data.setValueFormatter(new PercentFormatter());
                data.setValueTextSize(11f);
                data.setValueTextColor(Color.BLACK);

                //data.setValueTypeface(mTfLight);
                mChart.setData(data);

                // undo all highlights
                mChart.highlightValues(null);

                mChart.invalidate();
            }

            @Override
            public void onFinished() {
                //不做任何处理
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
            }

            @Override
            public void getDisposable(Disposable disposable) {
                //addDisposable(disposable);
            }
        },gson.toJson(tbody));


    }

    private SpannableString generateCenterSpannableText(Float total) {

        SpannableString s = new SpannableString("出库总计: " + total.toString().substring(0,total.toString().indexOf(".")));
        s.setSpan(new RelativeSizeSpan(1.7f), 0, 5, 0);
        s.setSpan(new StyleSpan(Typeface.NORMAL), 5, s.length(), 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 5, s.length(), 0);
        return s;
    }

    protected void showDatePickDlg(EditText edText) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), DatePickerDialog.THEME_HOLO_DARK, new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                edText.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();

    }

}
